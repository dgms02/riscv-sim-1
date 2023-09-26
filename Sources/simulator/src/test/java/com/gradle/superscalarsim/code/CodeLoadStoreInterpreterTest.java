package com.gradle.superscalarsim.code;

import com.gradle.superscalarsim.blocks.base.UnifiedRegisterFileBlock;
import com.gradle.superscalarsim.builders.*;
import com.gradle.superscalarsim.enums.DataTypeEnum;
import com.gradle.superscalarsim.loader.InitLoader;
import com.gradle.superscalarsim.models.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

public class CodeLoadStoreInterpreterTest
{
  @Mock
  private InitLoader initLoader;

  private CodeLoadStoreInterpreter codeLoadStoreInterpreter;

  @Before
  public void setUp()
  {
    MockitoAnnotations.openMocks(this);
    RegisterModel integer1 = new RegisterModelBuilder().hasName("x1").HasValue(0).IsConstant(false).build();
    RegisterModel integer2 = new RegisterModelBuilder().hasName("x2").HasValue(25).IsConstant(false).build();
    RegisterModel integer3 = new RegisterModelBuilder().hasName("x3").HasValue(6).IsConstant(false).build();
    RegisterModel integer4 = new RegisterModelBuilder().hasName("x4").HasValue(-1000).IsConstant(false).build();
    RegisterModel integer5 = new RegisterModelBuilder().hasName("x5").HasValue(-65535).IsConstant(false).build();
    RegisterModel integer6 = new RegisterModelBuilder().hasName("x6").HasValue(-4294967295L).IsConstant(false).build();
    RegisterFileModel integerFile = new RegisterFileModelBuilder().hasName("integer")
      .hasDataType(DataTypeEnum.kLong)
      .hasRegisterList(Arrays.asList(integer1,integer2,integer3,integer4,integer5,integer6))
      .build();

    RegisterModel float1 = new RegisterModelBuilder().hasName("f1").HasValue(0.0).IsConstant(false).build();
    RegisterModel float2 = new RegisterModelBuilder().hasName("f2").HasValue(25.0).IsConstant(false).build();
    RegisterModel float3 = new RegisterModelBuilder().hasName("f3").HasValue(6.0).IsConstant(false).build();
    RegisterFileModel floatFile = new RegisterFileModelBuilder().hasName("float")
      .hasDataType(DataTypeEnum.kDouble)
      .hasRegisterList(Arrays.asList(float1,float2,float3))
      .build();

    Mockito.when(initLoader.getRegisterFileModelList()).thenReturn(Arrays.asList(integerFile,floatFile));
    Mockito.when(initLoader.getInstructionFunctionModelList()).thenReturn(setUpInstructions());

    this.codeLoadStoreInterpreter = new CodeLoadStoreInterpreter(initLoader, new MemoryModel(new SimulatedMemory()), new UnifiedRegisterFileBlock(initLoader));
  }

  @Test
  public void storeByte_loadByte_returnsExactValue()
  {
    InputCodeArgument argument1 = new InputCodeArgumentBuilder().hasName("rs2").hasValue("x3").build();
    InputCodeArgument argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    InputCodeArgument argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    InputCodeModel inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("sb")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);

    argument1 = new InputCodeArgumentBuilder().hasName("rd").hasValue("x1").build();
    argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("lbu")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();

    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);
  }

  @Test
  public void storeShort_loadShort_returnsExactValue()
  {
    InputCodeArgument argument1 = new InputCodeArgumentBuilder().hasName("rs2").hasValue("x3").build();
    InputCodeArgument argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    InputCodeArgument argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    InputCodeModel inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("sh")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);

    argument1 = new InputCodeArgumentBuilder().hasName("rd").hasValue("x1").build();
    argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("lhu")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();

    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);
  }

  @Test
  public void storeInt_loadInt_returnsExactValue()
  {
    InputCodeArgument argument1 = new InputCodeArgumentBuilder().hasName("rs2").hasValue("x3").build();
    InputCodeArgument argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    InputCodeArgument argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    InputCodeModel inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("sw")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);

    argument1 = new InputCodeArgumentBuilder().hasName("rd").hasValue("x1").build();
    argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("lwu")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();

    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);
  }

  @Test
  public void storeLong_loadLong_returnsExactValue()
  {
    InputCodeArgument argument1 = new InputCodeArgumentBuilder().hasName("rs2").hasValue("x3").build();
    InputCodeArgument argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    InputCodeArgument argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    InputCodeModel inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("sd")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);

    argument1 = new InputCodeArgumentBuilder().hasName("rd").hasValue("x1").build();
    argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("ld")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();

    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);
  }

  @Test
  public void storeFloat_loadFloat_returnsExactValue()
  {
    InputCodeArgument argument1 = new InputCodeArgumentBuilder().hasName("rs2").hasValue("f3").build();
    InputCodeArgument argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    InputCodeArgument argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    InputCodeModel inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("fsw")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);

    argument1 = new InputCodeArgumentBuilder().hasName("rd").hasValue("f1").build();
    argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("flw")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();

    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);
  }

  @Test
  public void storeDouble_loadDouble_returnsExactValue()
  {
    InputCodeArgument argument1 = new InputCodeArgumentBuilder().hasName("rs2").hasValue("f3").build();
    InputCodeArgument argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    InputCodeArgument argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    InputCodeModel inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("fsd")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);

    argument1 = new InputCodeArgumentBuilder().hasName("rd").hasValue("f1").build();
    argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("fld")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();

    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);
  }

  @Test
  public void storeDouble_loadFloat_returnsDifferentValue()
  {
    InputCodeArgument argument1 = new InputCodeArgumentBuilder().hasName("rs2").hasValue("f3").build();
    InputCodeArgument argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    InputCodeArgument argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    InputCodeModel inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("fsd")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);

    argument1 = new InputCodeArgumentBuilder().hasName("rd").hasValue("f1").build();
    argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("flw")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();

    Assert.assertNotEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);
  }

  @Test
  public void storeFloat_loadDouble_returnsDifferentValue()
  {
    InputCodeArgument argument1 = new InputCodeArgumentBuilder().hasName("rs2").hasValue("f3").build();
    InputCodeArgument argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    InputCodeArgument argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    InputCodeModel inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("fsw")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);

    argument1 = new InputCodeArgumentBuilder().hasName("rd").hasValue("f1").build();
    argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("fld")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();

    Assert.assertNotEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);
  }

  @Test
  public void storeInt_loadSignedByte_returnsRightSign()
  {
    InputCodeArgument argument1 = new InputCodeArgumentBuilder().hasName("rs2").hasValue("x4").build();
    InputCodeArgument argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    InputCodeArgument argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    InputCodeModel inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("sw")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), -1000, 0.01);

    argument1 = new InputCodeArgumentBuilder().hasName("rd").hasValue("x1").build();
    argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("lb")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();

    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 24, 0.01);
  }

  @Test
  public void storeLong_loadSignedInt_returnsRightSign()
  {
    InputCodeArgument argument1 = new InputCodeArgumentBuilder().hasName("rs2").hasValue("x6").build();
    InputCodeArgument argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    InputCodeArgument argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    InputCodeModel inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("sd")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), -4294967295L, 0.01);

    argument1 = new InputCodeArgumentBuilder().hasName("rd").hasValue("x1").build();
    argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("lw")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();

    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 1, 0.01);
  }

  @Test
  public void storeInt_loadSignedHalf_returnsRightSign()
  {
    InputCodeArgument argument1 = new InputCodeArgumentBuilder().hasName("rs2").hasValue("x5").build();
    InputCodeArgument argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    InputCodeArgument argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    InputCodeModel inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("sw")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), -65535, 0.01);

    argument1 = new InputCodeArgumentBuilder().hasName("rd").hasValue("x1").build();
    argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("lh")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();

    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 1, 0.01);
  }

  @Test
  public void loadWord_dataUnknown_returns0()
  {
    InputCodeArgument argument1 = new InputCodeArgumentBuilder().hasName("rd").hasValue("x1").build();
    InputCodeArgument argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    InputCodeArgument argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    InputCodeModel inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("lw")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    double result1 = this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond();
    double result2 = this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond();
    Assert.assertEquals(0, result1, 0.0001);
    Assert.assertEquals(0, result2, 0.0001);
  }

  @Test
  public void loadWord_storeItBefore_returnsExactData()
  {
    InputCodeArgument argument1 = new InputCodeArgumentBuilder().hasName("rs2").hasValue("x2").build();
    InputCodeArgument argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    InputCodeArgument argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    InputCodeModel inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("sw")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 25, 0.01);

    argument1 = new InputCodeArgumentBuilder().hasName("rd").hasValue("x1").build();
    argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("lw")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    Assert.assertNotEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(),25);
  }

  @Test
  public void storeTwoBytes_loadOneShort_getByteCombinedValue()
  {
    InputCodeArgument argument1 = new InputCodeArgumentBuilder().hasName("rs2").hasValue("x3").build();
    InputCodeArgument argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x1").build();
    InputCodeArgument argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    InputCodeModel inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("sb")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);

    argument1 = new InputCodeArgumentBuilder().hasName("rs2").hasValue("x3").build();
    argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x1").build();
    argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("1").build();
    inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("sb")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);

    argument1 = new InputCodeArgumentBuilder().hasName("rd").hasValue("x2").build();
    argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x1").build();
    argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("lhu")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();

    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 1542, 0.01);
  }

  @Test
  public void storeTwoShort_loadOneInt_getShortCombinedValue()
  {
    InputCodeArgument argument1 = new InputCodeArgumentBuilder().hasName("rs2").hasValue("x3").build();
    InputCodeArgument argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    InputCodeArgument argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    InputCodeModel inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("sh")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);

    argument1 = new InputCodeArgumentBuilder().hasName("rs2").hasValue("x3").build();
    argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("2").build();
    inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("sh")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);

    argument1 = new InputCodeArgumentBuilder().hasName("rd").hasValue("x1").build();
    argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("lwu")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();

    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 393222, 0.01);
  }

  @Test
  public void storeOneShort_loadOneInt_getRandomValues()
  {
    InputCodeArgument argument1 = new InputCodeArgumentBuilder().hasName("rs2").hasValue("x3").build();
    InputCodeArgument argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    InputCodeArgument argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    InputCodeModel inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("sh")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();
    Assert.assertEquals(this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond(), 6, 0.01);

    argument1 = new InputCodeArgumentBuilder().hasName("rd").hasValue("x1").build();
    argument2 = new InputCodeArgumentBuilder().hasName("rs1").hasValue("x2").build();
    argument3 = new InputCodeArgumentBuilder().hasName("imm").hasValue("0").build();
    inputCodeModel = new InputCodeModelBuilder().hasLoader(initLoader).hasInstructionName("lwu")
      .hasArguments(Arrays.asList(argument1, argument2, argument3))
      .build();

    double result1 = this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond();
    double result2 = this.codeLoadStoreInterpreter.interpretInstruction(new SimCodeModel(inputCodeModel, -1,-1),0).getSecond();

    Assert.assertEquals(6.0, result1, 0.0001);
    Assert.assertEquals(6.0, result2, 0.0001);
  }

  private List<InstructionFunctionModel> setUpInstructions()
  {
    InstructionFunctionModel instructionLoadByte = new InstructionFunctionModelBuilder().hasName("lb")
      .hasInputDataType(DataTypeEnum.kInt)
      .hasOutputDataType(DataTypeEnum.kInt)
      .isInterpretedAs("load byte:signed rd rs1 imm")
      .hasSyntax("lb rd rs1 imm")
      .build();

    InstructionFunctionModel instructionLoadByteUnsigned = new InstructionFunctionModelBuilder().hasName("lbu")
      .hasInputDataType(DataTypeEnum.kInt)
      .hasOutputDataType(DataTypeEnum.kInt)
      .isInterpretedAs("load byte:unsigned rd rs1 imm")
      .hasSyntax("lbu rd rs1 imm")
      .build();

    InstructionFunctionModel instructionLoadHigh = new InstructionFunctionModelBuilder().hasName("lh")
      .hasInputDataType(DataTypeEnum.kInt)
      .hasOutputDataType(DataTypeEnum.kInt)
      .isInterpretedAs("load half:signed rd rs1 imm")
      .hasSyntax("lh rd rs1 imm")
      .build();

    InstructionFunctionModel instructionLoadHighUnsigned = new InstructionFunctionModelBuilder().hasName("lhu")
      .hasInputDataType(DataTypeEnum.kInt)
      .hasOutputDataType(DataTypeEnum.kInt)
      .isInterpretedAs("load half:unsigned rd rs1 imm")
      .hasSyntax("lhu rd rs1 imm")
      .build();

    InstructionFunctionModel instructionLoadWord = new InstructionFunctionModelBuilder().hasName("lw")
      .hasInputDataType(DataTypeEnum.kInt)
      .hasOutputDataType(DataTypeEnum.kInt)
      .isInterpretedAs("load word:signed rd rs1 imm")
      .hasSyntax("lw rd rs1 imm")
      .build();

    InstructionFunctionModel instructionLoadWordUnsigned = new InstructionFunctionModelBuilder().hasName("lwu")
      .hasInputDataType(DataTypeEnum.kLong)
      .hasOutputDataType(DataTypeEnum.kLong)
      .isInterpretedAs("load word:unsigned rd rs1 imm")
      .hasSyntax("lwu rd rs1 imm")
      .build();

    InstructionFunctionModel instructionLoadDoubleWord = new InstructionFunctionModelBuilder().hasName("ld")
      .hasInputDataType(DataTypeEnum.kLong)
      .hasOutputDataType(DataTypeEnum.kLong)
      .isInterpretedAs("load doubleword:signed rd rs1 imm")
      .hasSyntax("ld rd rs1 imm")
      .build();

    InstructionFunctionModel instructionLoadFloat = new InstructionFunctionModelBuilder().hasName("flw")
      .hasInputDataType(DataTypeEnum.kFloat)
      .hasOutputDataType(DataTypeEnum.kFloat)
      .isInterpretedAs("load float:unsigned rd rs1 imm")
      .hasSyntax("flw rd rs1 imm")
      .build();

    InstructionFunctionModel instructionLoadDouble = new InstructionFunctionModelBuilder().hasName("fld")
      .hasInputDataType(DataTypeEnum.kDouble)
      .hasOutputDataType(DataTypeEnum.kDouble)
      .isInterpretedAs("load double:unsigned rd rs1 imm")
      .hasSyntax("fld rd rs1 imm")
      .build();

    InstructionFunctionModel instructionStoreByte = new InstructionFunctionModelBuilder().hasName("sb")
      .hasInputDataType(DataTypeEnum.kInt)
      .hasOutputDataType(DataTypeEnum.kInt)
      .isInterpretedAs("store byte rs2 rs1 imm")
      .hasSyntax("sb rs2 rs1 imm")
      .build();

    InstructionFunctionModel instructionStoreHigh = new InstructionFunctionModelBuilder().hasName("sh")
      .hasInputDataType(DataTypeEnum.kInt)
      .hasOutputDataType(DataTypeEnum.kInt)
      .isInterpretedAs("store half rs2 rs1 imm")
      .hasSyntax("sh rs2 rs1 imm")
      .build();

    InstructionFunctionModel instructionStoreWord = new InstructionFunctionModelBuilder().hasName("sw")
      .hasInputDataType(DataTypeEnum.kInt)
      .hasOutputDataType(DataTypeEnum.kInt)
      .isInterpretedAs("store word rs2 rs1 imm")
      .hasSyntax("sw rs2 rs1 imm")
      .build();

    InstructionFunctionModel instructionStoreDoubleWord = new InstructionFunctionModelBuilder().hasName("sd")
      .hasInputDataType(DataTypeEnum.kLong)
      .hasOutputDataType(DataTypeEnum.kLong)
      .isInterpretedAs("store doubleword rs2 rs1 imm")
      .hasSyntax("sd rs2 rs1 imm")
      .build();

    InstructionFunctionModel instructionStoreFloat = new InstructionFunctionModelBuilder().hasName("fsw")
      .hasInputDataType(DataTypeEnum.kFloat)
      .hasOutputDataType(DataTypeEnum.kFloat)
      .isInterpretedAs("store float rs2 rs1 imm")
      .hasSyntax("fsw rs2 rs1 imm")
      .build();

    InstructionFunctionModel instructionStoreDouble = new InstructionFunctionModelBuilder().hasName("fsd")
      .hasInputDataType(DataTypeEnum.kFloat)
      .hasOutputDataType(DataTypeEnum.kFloat)
      .isInterpretedAs("store double rs2 rs1 imm")
      .hasSyntax("fsd rs2 rs1 imm")
      .build();

    return Arrays.asList(
      instructionLoadByte,
      instructionLoadByteUnsigned,
      instructionLoadHigh,
      instructionLoadHighUnsigned,
      instructionLoadWord,
      instructionLoadWordUnsigned,
      instructionLoadDoubleWord,
      instructionLoadFloat,
      instructionLoadDouble,
      instructionStoreByte,
      instructionStoreHigh,
      instructionStoreWord,
      instructionStoreDoubleWord,
      instructionStoreFloat,
      instructionStoreDouble
    );
  }
}
