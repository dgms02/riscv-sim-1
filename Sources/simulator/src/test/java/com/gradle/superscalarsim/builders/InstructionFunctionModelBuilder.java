package com.gradle.superscalarsim.builders;

import com.gradle.superscalarsim.enums.DataTypeEnum;
import com.gradle.superscalarsim.enums.InstructionTypeEnum;
import com.gradle.superscalarsim.models.InstructionFunctionModel;

public class InstructionFunctionModelBuilder
{
  private String name;
  private InstructionTypeEnum instructionType;
  private DataTypeEnum inputDataType;
  private DataTypeEnum outputDataType;
  private String arguments;
  private String interpretableAs;
  
  public InstructionFunctionModelBuilder()
  {
    this.name            = "";
    this.instructionType = null;
    this.inputDataType   = null;
    this.outputDataType  = null;
    this.arguments       = "";
    this.interpretableAs = "";
  }
  
  public InstructionFunctionModelBuilder hasName(String name)
  {
    this.name = name;
    return this;
  }
  
  public InstructionFunctionModelBuilder hasType(InstructionTypeEnum instructionType)
  {
    this.instructionType = instructionType;
    return this;
  }
  
  public InstructionFunctionModelBuilder hasInputDataType(DataTypeEnum dataType)
  {
    this.inputDataType = dataType;
    return this;
  }
  
  public InstructionFunctionModelBuilder hasOutputDataType(DataTypeEnum dataType)
  {
    this.outputDataType = dataType;
    return this;
  }
  
  public InstructionFunctionModelBuilder hasArguments(String arguments)
  {
    this.arguments = arguments;
    return this;
  }
  
  public InstructionFunctionModelBuilder isInterpretedAs(String interpretableAs)
  {
    this.interpretableAs = interpretableAs;
    return this;
  }
  
  public InstructionFunctionModel build()
  {
    return new InstructionFunctionModel(this.name, this.instructionType, this.inputDataType.toString(),
                                        this.outputDataType.toString(), this.arguments, this.interpretableAs);
  }
}
