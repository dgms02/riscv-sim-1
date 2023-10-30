/**
 * @file SimCodeModel.java
 * @author Jan Vavra \n
 * Faculty of Information Technology \n
 * Brno University of Technology \n
 * xvavra20@fit.vutbr.cz
 * @author Michal Majer
 * Faculty of Information Technology
 * Brno University of Technology
 * xmajer21@stud.fit.vutbr.cz
 * @brief File contains extended container for processed line of code
 * @date 3  February   2021 16:00 (created) \n
 * 10 March      2021 18:00 (revised)
 * 26 Sep      2023 10:00 (revised)
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
package com.gradle.superscalarsim.models;

import com.gradle.superscalarsim.blocks.base.UnifiedRegisterFileBlock;
import com.gradle.superscalarsim.code.Expression;
import com.gradle.superscalarsim.enums.DataTypeEnum;
import com.gradle.superscalarsim.enums.InstructionTypeEnum;
import com.gradle.superscalarsim.models.register.RegisterDataContainer;
import com.gradle.superscalarsim.models.register.RegisterModel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @class SimCodeModel
 * @brief Instruction execution data (renaming)
 * Ids are zero if not yet processed
 */
public class SimCodeModel implements IInputCodeModel, Comparable<SimCodeModel>
{
  /**
   * Reference to original code model
   */
  private final InputCodeModel inputCodeModel;
  /**
   * Id of order of instructions processed by the fetch
   */
  private final int id;
  /**
   * A copy of arguments, which are used for renaming.
   * The order of arguments is the same as in the original code line and tests depend on this order.
   */
  private final List<InputCodeArgument> renamedArguments;
  /**
   * Number marking bulk of instructions, which was fetched together
   */
  private int instructionBulkNumber;
  /**
   * Id, when was instructions accepted by the issue window
   */
  private int issueWindowId;
  /**
   * Id of the function block, which processed this instruction
   */
  private int functionUnitId;
  /**
   * Id marking when was result ready
   */
  private int readyId;
  /**
   * Id marking when was instruction committed from ROB
   */
  private int commitId;
  /**
   * True if simcodemodel has left the system (committed, flushed).
   * A finished simcodemodel can be safely deleted.
   */
  private boolean isFinished;
  /**
   * Bit value marking failure due to wrong branch prediction
   */
  private boolean hasFailed;
  /**
   * Saved value of the PC, when instruction was fetched
   * Used for load/store and branch instructions
   * TODO: Optional
   */
  private int savedPc;
  /**
   * Prediction made by branch predictor at the time of fetching.
   * Used for branch instructions.
   */
  private boolean branchPredicted;
  /**
   * Result of the branch computation.
   * Used to check for mispredictions.
   * True means branch was taken.
   */
  private boolean branchLogicResult;
  /**
   * Target of the branch instruction (offset from the savedPc).
   * Used to fix BTB and PC in misprediction.
   */
  private int branchTargetOffset;
  
  /**
   * @param [in] instructionFunctionModel - Instruction function model
   * @param [in] instructionName       - Name of the parsed instruction
   * @param [in] codeLine              - Unparsed line of code
   * @param [in] arguments             - Arguments of the instruction
   * @param [in] instructionBulkNumber - Id marking when was code accepted
   *
   * @brief Constructor - not used anymore
   */
  public SimCodeModel(InstructionFunctionModel instructionFunctionModel,
                      String instructionName,
                      InstructionTypeEnum instructionTypeEnum,
                      DataTypeEnum dataTypeEnum,
                      List<InputCodeArgument> arguments,
                      int id,
                      int instructionBulkNumber)
  {
    this.inputCodeModel        = new InputCodeModel(instructionFunctionModel, instructionName, arguments,
                                                    instructionTypeEnum, 0);
    this.id                    = id;
    this.commitId              = -1;
    this.isFinished            = false;
    this.instructionBulkNumber = instructionBulkNumber;
    this.hasFailed             = false;
    // Copy arguments
    this.renamedArguments = new ArrayList<>();
    for (InputCodeArgument argument : arguments)
    {
      this.renamedArguments.add(new InputCodeArgument(argument));
    }
  }// end of Constructor
  //------------------------------------------------------
  
  /**
   * @param [in] inputCodeModel - Original code model
   * @param [in] id             - Number marking when was code accepted
   *
   * @brief Constructor which copies original InputCodeModel
   * This constructor can be used only through the SimCodeModelAllocator
   */
  public SimCodeModel(InputCodeModel inputCodeModel, int id, int instructionBulkNumber)
  {
    this.inputCodeModel        = inputCodeModel;
    this.id                    = id;
    this.commitId              = -1;
    this.isFinished            = false;
    this.instructionBulkNumber = instructionBulkNumber;
    this.hasFailed             = false;
    // Copy arguments
    this.renamedArguments = new ArrayList<>();
    for (InputCodeArgument argument : inputCodeModel.getArguments())
    {
      this.renamedArguments.add(new InputCodeArgument(argument));
    }
  }// end of Constructor
  //------------------------------------------------------
  
  /**
   * @param [in] codeModel - Model to be compared to
   *
   * @return -1 if <, 0 if ==, > if 1
   * @brief Comparator function for assigning to priorityQueue
   */
  @Override
  public int compareTo(@NotNull SimCodeModel codeModel)
  {
    return -Integer.compare(codeModel.getId(), this.id);
  }// end of compareTo
  //------------------------------------------------------
  
  /**
   * @return Id of the model
   * @brief Gets Id of the model
   */
  public int getId()
  {
    return id;
  }// end of getId
  //------------------------------------------------------
  
  /**
   * @return Integer value of bulk number
   * @brief Gets number marking when was code processed
   */
  public int getInstructionBulkNumber()
  {
    return instructionBulkNumber;
  }// end of getId
  //------------------------------------------------------
  
  /**
   * @return Id, when was instruction accepted to issue window, 0 if not yet processed
   * @brief Gets accepted id to issue window
   */
  public int getIssueWindowId()
  {
    return issueWindowId;
  }// end of getIssueWindowId
  //------------------------------------------------------
  
  /**
   * @param [in] windowId - Id, when was instruction accepted to issue window
   *
   * @brief Sets accepted id to issue window
   */
  public void setIssueWindowId(int windowId)
  {
    this.issueWindowId = windowId;
  }// end of setIssueWindowId
  //------------------------------------------------------
  
  /**
   * @return Id of function block, which processed this instruction, 0 if not yet processed
   * @brief Gets id of function block, which processed this instruction
   */
  public int getFunctionUnitId()
  {
    return functionUnitId;
  }// end of getFunctionUnitId
  //------------------------------------------------------
  
  /**
   * @param [in] functionUnitId - Id of function block, which processed this instruction
   *
   * @brief Sets id of function block, which processed this instruction
   */
  public void setFunctionUnitId(int functionUnitId)
  {
    this.functionUnitId = functionUnitId;
  }// end of setFunctionUnitId
  //------------------------------------------------------
  
  /**
   * @return Id of when was instruction's result ready, 0 if not yet processed
   * @brief Gets id of when was instruction's result ready
   */
  public int getReadyId()
  {
    return readyId;
  }// end of getReadyId
  
  /**
   * @param [in] readyId - Id of when was instruction's result ready
   *
   * @brief Sets id of when was instruction's result ready
   */
  public void setReadyId(int readyId)
  {
    this.readyId = readyId;
  }// end of setReadyId
  //------------------------------------------------------
  
  /**
   * @return Id of when was instruction committed, 0 if not yet processed
   * @brief Gets id of when was instruction committed
   */
  public int getCommitId()
  {
    return commitId;
  }// end of getCommitId
  //------------------------------------------------------
  
  /**
   * @param [in] commitId - Id of when was instruction committed
   *
   * @brief Sets id of when was instruction's result ready
   */
  public void setCommitId(int commitId)
  {
    this.commitId = commitId;
  }// end of setCommitId
  //------------------------------------------------------
  
  /**
   * @return Boolean value marking failure to finish
   * @brief Get bit value corresponding to failure due to wrong prediction
   */
  public boolean hasFailed()
  {
    return hasFailed;
  }// end of hasFailed
  //------------------------------------------------------
  
  /**
   * @param hasFailed - Has instruction failed to finish due to missprediction?
   *
   * @brief Set bit marking to failure due to wrong prediction
   */
  public void setHasFailed(boolean hasFailed)
  {
    this.hasFailed = hasFailed;
  }// end of setHasFailed
  //------------------------------------------------------
  
  public boolean isBranchPredicted()
  {
    return branchPredicted;
  }
  
  public void setBranchPredicted(boolean branchPredicted)
  {
    this.branchPredicted = branchPredicted;
  }
  
  public boolean isBranchLogicResult()
  {
    return branchLogicResult;
  }
  
  public void setBranchLogicResult(boolean branchLogicResult)
  {
    this.branchLogicResult = branchLogicResult;
  }
  
  public int getBranchTargetOffset()
  {
    return branchTargetOffset;
  }
  
  public void setBranchTargetOffset(int branchTargetOffset)
  {
    this.branchTargetOffset = branchTargetOffset;
  }
  
  /**
   * String representation of the object
   */
  @Override
  public String toString()
  {
    return this.getRenamedCodeLine();
  }
  
  /**
   * Used for testing
   *
   * @return String with renamed code line
   * @brief Gets renamed code line
   */
  public String getRenamedCodeLine()
  {
    StringBuilder genericLine = new StringBuilder(getInstructionName());
    genericLine.append(" ");
    List<InstructionFunctionModel.Argument> args = inputCodeModel.getInstructionFunctionModel().getAsmArguments();
    for (int i = 0; i < args.size(); i++)
    {
      boolean wrapInParens = inputCodeModel.getInstructionFunctionModel().getInstructionType()
              .equals(InstructionTypeEnum.kLoadstore) && i == args.size() - 1;
      if (i != 0)
      {
        if (wrapInParens)
        {
          genericLine.append("(");
        }
        else
        {
          genericLine.append(",");
        }
      }
      InstructionFunctionModel.Argument arg        = args.get(i);
      InputCodeArgument                 renamedArg = getArgumentByName(arg.name());
      if (renamedArg == null)
      {
        throw new RuntimeException("Argument " + arg.name() + " not found in " + this);
      }
      genericLine.append(renamedArg.getValue());
      if (wrapInParens)
      {
        genericLine.append(")");
      }
    }
    return genericLine.toString();
  }// end of getRenamedCodeLine
  
  @Override
  public String getInstructionName()
  {
    return inputCodeModel.getInstructionName();
  }
  
  /**
   * @return Renamed arguments of the instruction
   * @brief Gets arguments of the instruction, copied so they are rewritable
   */
  @Override
  public List<InputCodeArgument> getArguments()
  {
    return renamedArguments;
  }
  
  /**
   * @param name Name of the argument
   *
   * @return An argument by its name
   */
  @Override
  public InputCodeArgument getArgumentByName(String name)
  {
    return renamedArguments.stream().filter(argument -> argument.getName().equals(name)).findFirst().orElse(null);
  }// end of getArgumentByName
  
  @Override
  public InstructionTypeEnum getInstructionTypeEnum()
  {
    return inputCodeModel.getInstructionTypeEnum();
  }
  
  @Override
  public DataTypeEnum getDataType()
  {
    return inputCodeModel.getDataType();
  }
  
  @Override
  public int getCodeId()
  {
    return inputCodeModel.getCodeId();
  }// end of getId
  
  @Override
  public InstructionFunctionModel getInstructionFunctionModel()
  {
    return inputCodeModel.getInstructionFunctionModel();
  }
  
  /**
   * @return Original arguments of the instruction (without renaming)
   */
  public List<InputCodeArgument> getOriginalArguments()
  {
    return inputCodeModel.getArguments();
  }
  
  /**
   * @return True if simcodemodel has left the system (committed, flushed)
   */
  public boolean isFinished()
  {
    return isFinished;
  }
  
  /**
   * @param finished True if simcodemodel has left the system (committed, flushed)
   */
  public void setFinished(boolean finished)
  {
    isFinished = finished;
  }
  
  /**
   * @return Variables used in the instruction in the form for expression evaluation
   * @brief reads current register values (including speculative values), the PC, constants
   */
  public List<Expression.Variable> getVariables(List<String> variableNames, UnifiedRegisterFileBlock registerFileBlock)
  {
    List<Expression.Variable> variables                = new ArrayList<>();
    InstructionFunctionModel  instructionFunctionModel = getInstructionFunctionModel();
    
    for (String varName : variableNames)
    {
      if (varName.equals("pc"))
      {
        variables.add(new Expression.Variable("pc", DataTypeEnum.kInt, RegisterDataContainer.fromValue(getSavedPc())));
        continue;
      }
      
      InstructionFunctionModel.Argument argument = instructionFunctionModel.getArgumentByName(varName);
      if (argument == null)
      {
        throw new IllegalStateException("Argument " + varName + " not found in " + instructionFunctionModel.getName());
      }
      
      // Variable is an argument of the instruction (rs1/imm)
      String name        = argument.name();
      String renamedName = getArgumentByName(name).getValue();
      // Check if value is register
      RegisterModel register = registerFileBlock.getRegister(renamedName);
      if (register != null)
      {
        // Register found (or its speculative rename)
        variables.add(new Expression.Variable(name, argument.type(), register.getValueContainer()));
      }
      else
      {
        //It is an immediate - constant or a label
        Expression.Variable parsed = Expression.parseConstant(renamedName);
        if (parsed != null)
        {
          parsed.tag  = name;
          parsed.type = argument.type();
          variables.add(parsed);
        }
        else
        {
          // TODO: Handle labels: load their value or extract it in parse. Skip for now.
          //throw new IllegalStateException("Could not parse " + renamedName + " as constant");
        }
      }
    }
    
    return variables;
  }
  
  /**
   * @return Saved value of the PC, when instruction was fetched
   */
  public int getSavedPc()
  {
    return savedPc;
  }
  
  public void setSavedPc(int savedPc)
  {
    this.savedPc = savedPc;
  }
}
