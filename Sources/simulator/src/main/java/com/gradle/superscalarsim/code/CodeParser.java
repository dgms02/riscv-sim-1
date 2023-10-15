/**
 * @file CodeParser.java
 * @author Michal Majer
 * Faculty of Information Technology
 * Brno University of Technology
 * xmajer21@stud.fit.vutbr.cz
 * @brief File contains parser for parsing and validating ASM code
 * @date 10 October  2023 18:00 (created)
 * @section Licence
 * This file is part of the Superscalar simulator app
 * <p>
 * Copyright (C) 2020  Jan Vavra
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gradle.superscalarsim.code;

import com.gradle.superscalarsim.enums.DataTypeEnum;
import com.gradle.superscalarsim.enums.InstructionTypeEnum;
import com.gradle.superscalarsim.loader.InitLoader;
import com.gradle.superscalarsim.models.InputCodeArgument;
import com.gradle.superscalarsim.models.InputCodeModel;
import com.gradle.superscalarsim.models.InstructionFunctionModel;
import com.gradle.superscalarsim.models.register.RegisterFileModel;
import com.gradle.superscalarsim.models.register.RegisterModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @brief Transforms tokens from the lexer into instructions and labels
 * @details Class which provides parsing logic for code written in assembly. Code shall contain instructions loaded
 * by InitLoader. Class also verifies parsed code and in case of failure, error message will be provided with
 * line number and cause. Conforms to GCC RISC-V syntax.
 */
public class CodeParser
{
  /**
   * Descriptions of all instructions
   */
  Map<String, InstructionFunctionModel> instructionModels;
  
  /**
   * Descriptions of all register files
   */
  List<RegisterFileModel> registerFileModelList;
  
  /**
   * Lexer for parsing the code
   */
  Lexer lexer;
  
  /**
   * Current token
   */
  CodeToken currentToken;
  
  /**
   * Peek token
   */
  CodeToken peekToken;
  
  /**
   * Result of the parsing - list of instructions.
   */
  List<InputCodeModel> instructions;
  
  /**
   * Result of the parsing - list of labels.
   */
  Map<String, Integer> labels;
  
  /**
   * Unconfirmed labels
   */
  List<CodeToken> unconfirmedLabels;
  
  /**
   * Error messages from parsing ASM code.
   *
   * @brief List of error messages
   */
  List<ParseError> errorMessages;
  
  Pattern decimalPattern;
  Pattern hexadecimalPattern;
  Pattern registerPattern;
  Pattern immediatePattern;
  
  /**
   * The aliases between registers.
   * The key is the architecture name (x0), the value is the alias (zero).
   * Must be a list - register x8 has two aliases (s0 and fp).
   */
  private List<InitLoader.RegisterMapping> registerAliases;
  
  /**
   * @brief Constructor when initloader is available
   */
  public CodeParser(InitLoader initLoader)
  {
    this(initLoader.getInstructionFunctionModels(), initLoader.getRegisterFileModelList(),
         initLoader.getRegisterAliases());
  }
  
  /**
   * @brief Constructor
   */
  public CodeParser(Map<String, InstructionFunctionModel> instructionModels,
                    List<RegisterFileModel> registerFileModelList,
                    List<InitLoader.RegisterMapping> registerAliases)
  {
    this.registerFileModelList = registerFileModelList;
    this.instructionModels     = instructionModels;
    this.registerAliases       = registerAliases;
    this.lexer                 = null;
    this.currentToken          = null;
    this.peekToken             = null;
    this.errorMessages         = new ArrayList<>();
    this.unconfirmedLabels     = new ArrayList<>();
    
    this.decimalPattern     = Pattern.compile("-?\\d+(\\.\\d+)?");
    this.hexadecimalPattern = Pattern.compile("0x\\p{XDigit}+");
    this.registerPattern    = Pattern.compile("r[d,s]\\d*");
    this.immediatePattern   = Pattern.compile("imm\\d*");
    
  }
  
  /**
   * After calling this method, the results can be collected from the instance.
   *
   * @param code ASM Code to parse
   */
  public void parseCode(String code)
  {
    this.lexer             = new Lexer(code);
    this.currentToken      = this.lexer.nextToken();
    this.peekToken         = this.lexer.nextToken();
    this.errorMessages     = new ArrayList<>();
    this.instructions      = new ArrayList<>();
    this.labels            = new HashMap<>();
    this.unconfirmedLabels = new ArrayList<>();
    parse();
    
    // Delete code if errors
    if (!this.errorMessages.isEmpty())
    {
      this.instructions = new ArrayList<>();
      this.labels       = new HashMap<>();
    }
  }
  
  /**
   * Creates list of instructions, labels, errors (mutates the state of the parser).
   *
   * @brief Parses the code
   */
  private void parse()
  {
    assert lexer != null;
    assert currentToken != null;
    
    while (!currentToken.type().equals(CodeToken.Type.EOF))
    {
      // Label
      if (peekToken.type().equals(CodeToken.Type.COLON))
      {
        parseLabel();
        // Allow multiple labels on a line. Start over
        continue;
      }
      
      // Directive or instruction
      if (currentToken.type().equals(CodeToken.Type.SYMBOL))
      {
        parseDirectiveOrInstruction();
      }
      
      // Skip comment
      if (currentToken.type().equals(CodeToken.Type.COMMENT))
      {
        nextToken();
      }
      
      // If instruction parsing ended early, skip to the next line
      if (!currentToken.type().equals(CodeToken.Type.NEWLINE) && !currentToken.type().equals(CodeToken.Type.EOF))
      {
        addError(currentToken, "Expected newline, got " + currentToken.type());
      }
      
      while (!currentToken.type().equals(CodeToken.Type.NEWLINE) && !currentToken.type().equals(CodeToken.Type.EOF))
      {
        nextToken();
      }
      nextToken();
    }
    
    // Check if all labels are defined
    for (CodeToken unconfirmedLabel : unconfirmedLabels)
    {
      addError(unconfirmedLabel, "Label '" + unconfirmedLabel.text() + "' is not defined");
    }
  }
  
  /**
   * @brief Parse label. Current token is the label name, peek token is colon.
   */
  private void parseLabel()
  {
    assert currentToken.type().equals(CodeToken.Type.SYMBOL);
    assert peekToken.type().equals(CodeToken.Type.COLON);
    
    String labelName = currentToken.text();
    
    // Check label syntax
    if (!labelName.matches("^[a-zA-Z0-9_\\.]+$"))
    {
      addError(currentToken, "Invalid label name '" + labelName + "'");
    }
    
    // Check if label is already defined
    if (labels.containsKey(labelName))
    {
      addError(currentToken, "Label '" + labelName + "' already defined");
    }
    else
    {
      // Add label, this is index to the array of instructions, not "bytes"
      labels.put(labelName, instructions.size());
      // Clear unconfirmed labels
      unconfirmedLabels.removeIf(token -> token.text().equals(labelName));
    }
    
    // Consume label name and colon
    nextToken();
    nextToken();
  }
  
  /**
   * @brief Parse directive or instruction. Current token is a symbol.
   */
  private void parseDirectiveOrInstruction()
  {
    assert currentToken.type().equals(CodeToken.Type.SYMBOL);
    
    String symbolName = currentToken.text();
    
    // Check if symbol is a directive
    if (symbolName.startsWith("."))
    {
      parseDirective();
    }
    else
    {
      parseInstruction();
      
      // Skip to the next line
      while (!currentToken.type().equals(CodeToken.Type.NEWLINE) && !currentToken.type().equals(CodeToken.Type.EOF))
      {
        nextToken();
      }
    }
  }
  
  /**
   * @brief Load next token
   */
  private void nextToken()
  {
    this.currentToken = this.peekToken;
    this.peekToken    = this.lexer.nextToken();
  }
  
  /**
   * @param token   Token where the error occurred
   * @param message Error message
   *
   * @brief Adds a single-line error message to the list of errors
   */
  private void addError(CodeToken token, String message)
  {
    this.errorMessages.add(new ParseError("error", message, token.line(), token.columnStart(), token.columnEnd()));
  }
  
  private void parseDirective()
  {
    // TODO: Implement
  }
  
  /**
   * @brief Parse instruction. Current token is a symbol with the name of the instruction.
   */
  private void parseInstruction()
  {
    assert currentToken.type().equals(CodeToken.Type.SYMBOL);
    
    CodeToken                instructionNameToken = currentToken;
    String                   instructionName      = instructionNameToken.text();
    InstructionFunctionModel instructionModel     = getInstructionFunctionModel(instructionName);
    
    // Check if instruction is valid
    if (instructionModel == null)
    {
      addError(currentToken, "Unknown instruction '" + instructionName + "'");
      return;
    }
    
    // Consume instruction name
    nextToken();
    
    // Collect arguments
    List<CodeToken> collectedArgs = collectInstructionArgs();
    if (collectedArgs == null)
    {
      return;
    }
    
    // Collect args, fill with defaults if needed
    Map<String, CodeToken> args               = new HashMap<>();
    int                    numArguments       = collectedArgs.size();
    int                    collectedArgsIndex = 0;
    boolean useDefaultArgs = numArguments < instructionModel.getArguments()
            .size() && instructionModel.hasDefaultArguments();
    for (InstructionFunctionModel.Argument argument : instructionModel.getArguments())
    {
      String  key        = argument.name();
      boolean hasDefault = argument.defaultValue() != null;
      if (useDefaultArgs && hasDefault)
      {
        // Use default argument
        args.put(key, new CodeToken(0, 0, argument.defaultValue(), CodeToken.Type.EOF));
      }
      else if (collectedArgsIndex < collectedArgs.size())
      {
        // Use collected argument
        args.put(key, collectedArgs.get(collectedArgsIndex));
        collectedArgsIndex++;
      }
      else
      {
        // Missing argument
        addError(instructionNameToken, "Missing argument " + argument.name());
        return;
      }
    }
    
    if (collectedArgsIndex < collectedArgs.size())
    {
      // Not all arguments were used
      addError(instructionNameToken, "Too many arguments");
      return;
    }
    
    // Validate arguments
    List<InputCodeArgument> codeArguments = new ArrayList<>();
    for (Map.Entry<String, CodeToken> entry : args.entrySet())
    {
      String            argumentName        = entry.getKey();
      CodeToken         argumentToken       = entry.getValue();
      InputCodeArgument inputCodeArgument   = new InputCodeArgument(argumentName, argumentToken.text());
      boolean           isLValue            = argumentName.equals("rd");
      DataTypeEnum      instructionDataType = isLValue ? instructionModel.getOutputDataType() : instructionModel.getInputDataType();
      boolean isValid = validateArgument(inputCodeArgument, instructionModel.getInstructionType(), instructionDataType,
                                         isLValue, argumentToken);
      if (isValid)
      {
        codeArguments.add(inputCodeArgument);
      }
    }
    
    InputCodeModel inputCodeModel = new InputCodeModel(instructionModel, codeArguments, instructions.size());
    instructions.add(inputCodeModel);
  }
  
  private InstructionFunctionModel getInstructionFunctionModel(String instructionName)
  {
    return instructionModels.get(instructionName);
  }
  
  /**
   * @return List of tokens representing arguments, null in case of error
   * @brief Collects arguments of an instruction from the token stream
   */
  private List<CodeToken> collectInstructionArgs()
  {
    // TODO: bugs like: addi x1(x2(x3
    boolean         openParen         = false;
    boolean         expectingArgState = true;
    List<CodeToken> collectedArgs     = new ArrayList<>();
    while (true)
    {
      // instructions with parentheses can also be written with commas
      // example: flw rd,imm(rs1) also flw rd,imm,rs1
      
      if (expectingArgState)
      {
        collectedArgs.add(currentToken);
        nextToken();
        expectingArgState = false; // Now a comma
        continue;
      }
      
      // Not expecting argument
      
      boolean isComma      = currentToken.type().equals(CodeToken.Type.COMMA);
      boolean isParen      = currentToken.type().equals(CodeToken.Type.L_PAREN);
      boolean isCloseParen = currentToken.type().equals(CodeToken.Type.R_PAREN);
      boolean isEnd = currentToken.type().equals(CodeToken.Type.NEWLINE) || currentToken.type()
              .equals(CodeToken.Type.EOF) || currentToken.type().equals(CodeToken.Type.COMMENT);
      
      if (isEnd)
      {
        break;
      }
      
      if (openParen && isCloseParen)
      {
        openParen = false;
      }
      else if (isComma || isParen)
      {
        expectingArgState = true;
        if (isParen)
        {
          openParen = true;
        }
      }
      else
      {
        // Give up, next line
        addError(currentToken, "Expected comma or parenthesis, got " + currentToken.type());
        return null;
      }
      nextToken();
    }
    return collectedArgs;
  }
  
  /**
   * @param inputCodeArgument   Argument to be verified
   * @param instructionType     Type of the instruction
   * @param instructionDataType Data type of argument according to instruction template
   * @param isLValue            True, if the currently verified argument lvalue, false otherwise
   * @param token               Token of the argument, for error reporting
   *
   * @return True in case of valid argument, false otherwise
   * @brief Validates argument's value and data type
   */
  private boolean validateArgument(final InputCodeArgument inputCodeArgument,
                                   final InstructionTypeEnum instructionType,
                                   final DataTypeEnum instructionDataType,
                                   final boolean isLValue,
                                   CodeToken token)
  {
    String  argumentName  = inputCodeArgument.getName();
    String  argumentValue = inputCodeArgument.getValue();
    boolean isDirectValue = isNumeralLiteral(argumentValue);
    
    if (this.immediatePattern.matcher(argumentName).matches())
    {
      //if instruction is jump/branch, expected imm value can be a label or a direct value in the +-1MiB range
      // numeral values are already checked in the previous step
      boolean isBranch = instructionType == InstructionTypeEnum.kJumpbranch;
      return checkImmediateArgument(argumentValue, isLValue, isDirectValue, isBranch, token);
    }
    else if (this.registerPattern.matcher(argumentName).matches())
    {
      // A register (rd, rs1, ...)
      return checkRegisterArgument(argumentValue, instructionDataType, isLValue, isDirectValue, token);
    }
    return false;
  }// end of validateArgument
  
  private boolean isNumeralLiteral(String argValue)
  {
    return this.hexadecimalPattern.matcher(argValue).matches() || this.decimalPattern.matcher(argValue).matches();
  }
  
  /**
   * @param argumentValue Value of an argument in string format
   * @param isLValue      True, if the currently verified argument lvalue (destination), false otherwise
   * @param isDirectValue True, if argument is decimal or hexadecimal , false otherwise
   * @param isBranch      True, if instruction is branch/jump, false otherwise
   * @param token         Token of the argument, for error reporting
   *
   * @return True if argument is valid immediate value, otherwise false
   * @brief Verifies if argument is immediate value
   */
  private boolean checkImmediateArgument(final String argumentValue,
                                         final boolean isLValue,
                                         final boolean isDirectValue,
                                         boolean isBranch,
                                         CodeToken token)
  {
    if (isLValue)
    {
      this.addError(token, "LValue cannot be immediate value.");
      return false;
    }
    
    if (!isDirectValue && !isBranch)
    {
      this.addError(token, "Expecting immediate value, got : \"" + argumentValue + "\".");
      return false;
    }
    
    if (!isDirectValue && !this.labels.containsKey(argumentValue))
    {
      this.unconfirmedLabels.add(token);
    }
    
    return true;
  }// end of checkImmediateArgument
  
  /**
   * @param argumentValue    Value of an argument in string format
   * @param argumentDataType Expected data type of argument
   * @param isLValue         True, if the currently verified argument lvalue, false otherwise
   * @param isDirectValue    True, if argument is decimal or hexadecimal , false otherwise
   * @param token            Token of the argument, for error reporting
   *
   * @return True if argument is valid register, otherwise false
   * @brief Verifies if argument is register
   */
  private boolean checkRegisterArgument(final String argumentValue,
                                        final DataTypeEnum argumentDataType,
                                        final boolean isLValue,
                                        final boolean isDirectValue,
                                        CodeToken token)
  {
    if (isDirectValue && isLValue)
    {
      this.addError(token, "LValue cannot be immediate value.");
      return false;
    }
    if (isDirectValue)
    {
      this.addError(token, "Expected register, got : \"" + argumentValue + "\".");
      return false;
    }
    
    // Lookup all register files and aliases, check if the register exists
    // Assumes that the register names and aliases are unique
    // First try to find alias
    String registerToLookFor = argumentValue;
    for (InitLoader.RegisterMapping alias : registerAliases)
    {
      if (alias.alias.equals(argumentValue))
      {
        // Look for this register instead of the alias
        registerToLookFor = alias.register;
        break;
      }
    }
    for (RegisterFileModel registerFileModel : registerFileModelList)
    {
      if (!checkDatatype(argumentDataType, registerFileModel.getDataType()))
      {
        // Incorrect data type in this register file, skip
        continue;
      }
      RegisterModel reg = registerFileModel.getRegister(registerToLookFor);
      if (reg != null)
      {
        return true;
      }
    }
    this.addError(token, "Argument \"" + argumentValue + "\" is not a register nor a value.");
    return false;
  }// end of checkRegisterArgument
  //-------------------------------------------------------------------------------------------
  
  /**
   * @param argumentDataType The datatype of an argument
   * @param registerDataType The datatype of a register file
   *
   * @return True if argument can fit inside the register, false otherwise
   * @brief Check argument and register data types if they fit within each other
   */
  private boolean checkDatatype(final DataTypeEnum argumentDataType, final DataTypeEnum registerDataType)
  {
    return switch (argumentDataType)
    {
      case kInt -> registerDataType == DataTypeEnum.kInt || registerDataType == DataTypeEnum.kLong;
      case kLong -> registerDataType == DataTypeEnum.kLong;
      case kFloat -> registerDataType == DataTypeEnum.kFloat || registerDataType == DataTypeEnum.kDouble;
      case kDouble -> registerDataType == DataTypeEnum.kDouble;
      case kSpeculative -> false;
    };
  }// end of checkDatatype
  //-------------------------------------------------------------------------------------------
  
  /**
   * @return True if the parsing was successful, false otherwise
   */
  public boolean success()
  {
    return this.errorMessages.isEmpty();
  }
  
  public List<InputCodeModel> getInstructions()
  {
    return instructions;
  }
  
  public Map<String, Integer> getLabels()
  {
    return labels;
  }
  
  public List<ParseError> getErrorMessages()
  {
    return errorMessages;
  }
}
