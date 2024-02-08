package com.gradle.superscalarsim.builders;

import com.gradle.superscalarsim.enums.DataTypeEnum;
import com.gradle.superscalarsim.enums.InstructionTypeEnum;
import com.gradle.superscalarsim.loader.IDataProvider;
import com.gradle.superscalarsim.models.instruction.InputCodeArgument;
import com.gradle.superscalarsim.models.instruction.InputCodeModel;
import com.gradle.superscalarsim.models.instruction.InstructionFunctionModel;

import java.util.ArrayList;
import java.util.List;

public class InputCodeModelBuilder
{
  /// ID - the index of the instruction in the code
  private int id;
  private String instructionName;
  private String codeLine;
  private InstructionTypeEnum instructionTypeEnum;
  private List<InputCodeArgument> arguments;
  private DataTypeEnum dataTypeEnum;
  private InstructionFunctionModel instructionFunctionModel;
  private IDataProvider loader;
  
  public InputCodeModelBuilder()
  {
    this.id                  = 0; // TODO: this ruins per instruction statistics
    this.instructionName     = "";
    this.codeLine            = "";
    this.arguments           = new ArrayList<>();
    this.instructionTypeEnum = null;
    this.dataTypeEnum        = null;
  }
  
  public InputCodeModelBuilder hasLoader(IDataProvider loader)
  {
    this.loader = loader;
    return this;
  }
  
  public InputCodeModelBuilder hasInstructionFunctionModel(InstructionFunctionModel instructionFunctionModel)
  {
    this.instructionFunctionModel = instructionFunctionModel;
    return this;
  }
  
  public InputCodeModelBuilder hasInstructionName(String instructionName)
  {
    this.instructionName = instructionName;
    return this;
  }
  
  public InputCodeModelBuilder hasCodeLine(String codeLine)
  {
    this.codeLine = codeLine;
    return this;
  }
  
  public InputCodeModelBuilder hasArguments(List<InputCodeArgument> arguments)
  {
    this.arguments = arguments;
    return this;
  }
  
  public InputCodeModelBuilder hasInstructionTypeEnum(InstructionTypeEnum instructionTypeEnum)
  {
    this.instructionTypeEnum = instructionTypeEnum;
    return this;
  }
  
  public InputCodeModelBuilder hasDataTypeEnum(DataTypeEnum dataTypeEnum)
  {
    this.dataTypeEnum = dataTypeEnum;
    return this;
  }
  
  public InputCodeModelBuilder hasId(int id)
  {
    this.id = id;
    return this;
  }
  
  public InputCodeModel build()
  {
    InstructionFunctionModel model;
    if (this.loader != null)
    {
      model = this.loader.getInstructionFunctionModel(this.instructionName);
      if (model == null)
      {
        throw new RuntimeException("Instruction " + this.instructionName + ": model not found");
      }
    }
    else
    {
      model = this.instructionFunctionModel;
    }
    
    // Patch: add argument to labels called labelName
    if (this.instructionName.equals("label"))
    {
      ArrayList<InputCodeArgument> temp = new ArrayList<>();
      // Copy all
      for (InputCodeArgument argument : this.arguments)
      {
        temp.add(new InputCodeArgument(argument));
      }
      temp.add(new InputCodeArgument("labelName", this.codeLine));
      this.arguments = temp;
    }
    return new InputCodeModel(model, this.instructionName, this.arguments, this.instructionTypeEnum, this.id);
  }
  
  
}
