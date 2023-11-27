package com.gradle.superscalarsim.code;

import com.gradle.superscalarsim.cpu.MemoryInitializer;
import com.gradle.superscalarsim.cpu.MemoryLocation;
import com.gradle.superscalarsim.enums.DataTypeEnum;
import com.gradle.superscalarsim.loader.InitLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CodeParserTest
{
  private CodeParser codeParser;
  
  @Before
  public void setUp()
  {
    InitLoader initLoader = new InitLoader();
    this.codeParser = new CodeParser(initLoader);
  }
  
  @Test
  public void parseCode_storeSyntax()
  {
    String code = "sw x3, 0(x2)";
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(1, codeParser.getInstructions().size());
    Assert.assertTrue(codeParser.getErrorMessages().isEmpty());
    
    Assert.assertEquals("sw", codeParser.getInstructions().get(0).getInstructionName());
    Assert.assertEquals("x3", codeParser.getInstructions().get(0).getArgumentByName("rs2").getValue());
    Assert.assertEquals("0", codeParser.getInstructions().get(0).getArgumentByName("imm").getValue());
    Assert.assertEquals("x2", codeParser.getInstructions().get(0).getArgumentByName("rs1").getValue());
  }
  
  @Test
  public void parseCode_codeValid_returnTrueAndParsedCodeHasThreeInstr()
  {
    String code = """
            add x1, x2, x3
            fcvt.w.s x1, f3
            fadd.s f1, f2, f3
            """;
    codeParser.parseCode(code);
    
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(3, codeParser.getInstructions().size());
    Assert.assertTrue(codeParser.getErrorMessages().isEmpty());
    
    Assert.assertEquals("add", codeParser.getInstructions().get(0).getInstructionName());
    Assert.assertEquals("fcvt.w.s", codeParser.getInstructions().get(1).getInstructionName());
    Assert.assertEquals("fadd.s", codeParser.getInstructions().get(2).getInstructionName());
    
    Assert.assertEquals("x1", codeParser.getInstructions().get(0).getArgumentByName("rd").getValue());
    Assert.assertEquals("x2", codeParser.getInstructions().get(0).getArgumentByName("rs1").getValue());
    Assert.assertEquals("x3", codeParser.getInstructions().get(0).getArgumentByName("rs2").getValue());
  }
  
  @Test
  public void parseCode_codeWithLabel_returnTrueAndParsedCodeHasFourInstr()
  {
    String code = """
            one:   add x1, x2, x3
            two:   fcvt.w.s x1, f3
            ret
            three: fadd.s f1, f2, f3
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(4, codeParser.getInstructions().size());
    Assert.assertTrue(codeParser.getErrorMessages().isEmpty());
    
    Assert.assertEquals("add", codeParser.getInstructions().get(0).getInstructionName());
    Assert.assertEquals("fcvt.w.s", codeParser.getInstructions().get(1).getInstructionName());
    Assert.assertEquals("ret", codeParser.getInstructions().get(2).getInstructionName());
    Assert.assertEquals("fadd.s", codeParser.getInstructions().get(3).getInstructionName());
    
    // Parser does not deal in bytes, but index offsets
    Assert.assertEquals(0, codeParser.getLabels().get("one").address);
    Assert.assertEquals(4, codeParser.getLabels().get("two").address);
    Assert.assertEquals(12, codeParser.getLabels().get("three").address);
  }
  
  @Test
  public void parseCode_codeWithLabelOnEmptyLine_returnTrueAndParsedCodeHasSixInstr()
  {
    String code = """
            one:
            add x1, x2, x3
            two:
            fcvt.w.s x1, f3
            three:
            fadd.s f1, f2, f3
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(3, codeParser.getInstructions().size());
    Assert.assertTrue(codeParser.getErrorMessages().isEmpty());
    
    Assert.assertEquals("add", codeParser.getInstructions().get(0).getInstructionName());
    Assert.assertEquals("fcvt.w.s", codeParser.getInstructions().get(1).getInstructionName());
    Assert.assertEquals("fadd.s", codeParser.getInstructions().get(2).getInstructionName());
    
    Assert.assertEquals(0, codeParser.getLabels().get("one").address);
    Assert.assertEquals(4, codeParser.getLabels().get("two").address);
    Assert.assertEquals(8, codeParser.getLabels().get("three").address);
  }
  
  @Test
  public void parseCode_codeWithRepeatingLabels_returnFalse()
  {
    String code = """
            one:
            add x1, x2, x3
            one:
            fcvt.w.s x1, f3
            one:
            fadd.s f1, f2, f3
            """;
    codeParser.parseCode(code);
    
    Assert.assertFalse(codeParser.success());
    
    Assert.assertEquals(2, this.codeParser.getErrorMessages().size());
    Assert.assertEquals(3, this.codeParser.getErrorMessages().get(0).line);
    Assert.assertEquals(5, this.codeParser.getErrorMessages().get(1).line);
  }
  
  @Test
  public void parseCode_codeWithMissingLabel_returnFalseAndErrorMessageIsSet()
  {
    String code = """
            one:
            add x1, x2, x3
            fcvt.w.s x1, f3
            beq x1, x2, two
            fadd.s f1, f2, f3
            """;
    codeParser.parseCode(code);
    
    Assert.assertFalse(codeParser.success());
    Assert.assertEquals(0, codeParser.getInstructions().size());
    Assert.assertEquals(1, codeParser.getErrorMessages().size());
    ParseError firstError = codeParser.getErrorMessages().get(0);
    Assert.assertEquals(4, firstError.line);
    Assert.assertEquals(13, firstError.columnStart);
    Assert.assertEquals(15, firstError.columnEnd);
  }
  
  @Test
  public void parseCode_multipleLabels_perInstruction()
  {
    String code = """
            one:
            two: three: add x1, x2, x3
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(1, codeParser.getInstructions().size());
    Assert.assertEquals(0, codeParser.getLabels().get("one").address);
    Assert.assertEquals(0, codeParser.getLabels().get("two").address);
    Assert.assertEquals(0, codeParser.getLabels().get("three").address);
  }
  
  @Test
  public void parseCode_lessArgumentsThatExpected_returnsFalseAndErrorMessageIsSet()
  {
    String code = """
            add x1, x2
            fcvt.w.s x1, f3
            fadd.s f1, f2, f3
            """;
    codeParser.parseCode(code);
    
    Assert.assertFalse(codeParser.success());
    Assert.assertEquals(0, codeParser.getInstructions().size());
    Assert.assertEquals(1, codeParser.getErrorMessages().size());
  }
  
  @Test
  public void parseCode_moreArgumentsThatExpected_returnsFalseAndErrorMessageIsSet()
  {
    String code = """
            add x1, x2, x3
            fcvt.w.s f3, x1, x1
            fadd.s f1, f2, f3
            """;
    codeParser.parseCode(code);
    
    Assert.assertFalse(codeParser.success());
    Assert.assertEquals(0, codeParser.getInstructions().size());
    
    Assert.assertEquals(1, codeParser.getErrorMessages().size());
    Assert.assertEquals(2, codeParser.getErrorMessages().get(0).line);
  }
  
  @Test
  public void parseCode_invalidInstruction_returnsFalseAndErrorMessageIsSet()
  {
    String code = """
            add x1, x2, x3
            someRandomInstruction f3, x1 # a comment here
            fadd.s f1, f2, f3
            """;
    codeParser.parseCode(code);
    
    Assert.assertFalse(codeParser.success());
    Assert.assertEquals(0, codeParser.getInstructions().size());
    
    Assert.assertEquals(1, codeParser.getErrorMessages().size());
    Assert.assertEquals(2, codeParser.getErrorMessages().get(0).line);
  }
  
  @Test
  public void parseCode_lValueIsDecimal_returnsFalseAndErrorMessageIsSet()
  {
    String code = """
            add x1, x2, x3 # commas required
            fcvt.w.s x1, f3
            fadd.s 20, f2, f3
            """;
    codeParser.parseCode(code);
    
    Assert.assertFalse(codeParser.success());
    Assert.assertEquals(0, codeParser.getInstructions().size());
    
    Assert.assertEquals(1, codeParser.getErrorMessages().size());
    Assert.assertEquals(3, codeParser.getErrorMessages().get(0).line);
  }
  
  @Test
  public void parseCode_lValueIsHexadecimal_returnsFalseAndErrorMessageIsSet()
  {
    String code = """
            add x1, x2, x3
            fcvt.w.s x1, f3
            fadd.s 0x20, f2, f3
            """;
    codeParser.parseCode(code);
    
    Assert.assertFalse(codeParser.success());
    Assert.assertEquals(0, codeParser.getInstructions().size());
    
    Assert.assertEquals(1, codeParser.getErrorMessages().size());
  }
  
  @Test
  public void parseCode_invalidValue_returnsFalseAndErrorMessageIsSet()
  {
    String code = """
            add x1, value, x2
            fcvt.w.s x1, f3
            fadd.s f1, f2, f3
            """;
    codeParser.parseCode(code);
    
    Assert.assertFalse(codeParser.success());
    Assert.assertEquals(0, codeParser.getInstructions().size());
    
    Assert.assertEquals(1, codeParser.getErrorMessages().size());
  }
  
  @Test
  public void parseCode_immediateInsteadOfRegister_returnsFalseAndErrorMessageIsSet()
  {
    String code = """
            add x1, x2, 0x01
            fcvt.w.s x1, f3
            fadd.s f1, f2, f3
            """;
    codeParser.parseCode(code);
    
    Assert.assertFalse(codeParser.success());
    Assert.assertEquals(0, codeParser.getInstructions().size());
    
    Assert.assertEquals(1, codeParser.getErrorMessages().size());
  }
  
  @Test
  public void parseCode_registerInsteadOfImmediate_returnsFalseAndErrorMessageIsSet()
  {
    String code = """
            addi x1, x2, x3
            fcvt.w.s x1, f3
            fadd.s f1, f2, f3
            """;
    codeParser.parseCode(code);
    
    Assert.assertFalse(codeParser.success());
    Assert.assertEquals(0, codeParser.getInstructions().size());
    Assert.assertEquals(1, codeParser.getErrorMessages().size());
  }
  
  @Test
  public void parseCode_multipleErrors_returnsFalseAndErrorMessageIsSet()
  {
    String code = """
            addi x1, x2, x3
            fcvt.w.s x1, f3, x2
            fadd.s 0x01, f2, f3
            """;
    codeParser.parseCode(code);
    
    Assert.assertFalse(codeParser.success());
    Assert.assertEquals(0, codeParser.getInstructions().size());
    
    Assert.assertEquals(3, codeParser.getErrorMessages().size());
  }
  
  @Test
  public void parseCode_codeValid_parsesAliasedRegisters()
  {
    String code = """
            addi sp, x3, 5
            beq x3, zero, 0
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(2, codeParser.getInstructions().size());
    Assert.assertTrue(codeParser.getErrorMessages().isEmpty());
  }
  
  @Test
  public void parseCode_colonInTheMiddleOfAWord_correctlySplits()
  {
    String code = """
            addi sp, x3, 5
            anyt:hing  # Takes this as a label and a word 'hing'
            beq x3, zero, 0
            """;
    codeParser.parseCode(code);
    
    Assert.assertFalse(codeParser.success());
    Assert.assertEquals(1, codeParser.getErrorMessages().size());
    Assert.assertEquals(2, codeParser.getErrorMessages().get(0).line);
    Assert.assertEquals(6, codeParser.getErrorMessages().get(0).columnStart);
    Assert.assertEquals(9, codeParser.getErrorMessages().get(0).columnEnd);
  }
  
  @Test
  public void parseCode_labelAsArgument_returnsFalse()
  {
    String code = """
            addi sp L: addi sp, sp, 1
            """;
    codeParser.parseCode(code);
    
    // The parser may not recover well, so number of errors is not checked
    Assert.assertFalse(codeParser.success());
  }
  
  @Test
  public void parseCode_wrongTypeAlias_returnsFalse()
  {
    // sp is an int register alias, using it in a float instruction should fail
    String code = """
            fadd.s f1, sp ,f2
            """;
    codeParser.parseCode(code);
    
    Assert.assertFalse(codeParser.success());
    Assert.assertEquals(1, codeParser.getErrorMessages().size());
  }
  
  @Test
  public void parseCode_jarl_onlyOneOptionalArgument()
  {
    String code = """
            jalr x1, x2
            """;
    codeParser.parseCode(code);
    
    Assert.assertFalse(codeParser.success());
  }
  
  @Test
  public void parseCode_jarl_correctFullForm()
  {
    String code = """
            jalr x2, x3, 5
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
  }
  
  @Test
  public void parseCode_jarl_correctShortForm()
  {
    String code = """
            jalr x4
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
  }
  
  @Test
  public void parseCode_single_byte_parses()
  {
    String code = """
            N:
            .byte 25
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(1, codeParser.getMemoryLocations().size());
    MemoryLocation n = codeParser.getMemoryLocations().get(0);
    Assert.assertEquals("N", n.name);
    Assert.assertEquals(DataTypeEnum.kByte, n.dataType);
    Assert.assertEquals(1, n.getSize());
    Assert.assertEquals((byte) 25, (byte) n.value.get(0));
  }
  
  @Test
  public void parseCode_single_hword_parses()
  {
    String code = """
            N:
            .hword 0x1234
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(1, codeParser.getMemoryLocations().size());
    MemoryLocation n = codeParser.getMemoryLocations().get(0);
    Assert.assertEquals("N", n.name);
    Assert.assertEquals(DataTypeEnum.kShort, n.dataType);
    Assert.assertEquals(2, n.getSize());
    Assert.assertEquals((byte) 0x34, (byte) n.value.get(0));
    Assert.assertEquals((byte) 0x12, (byte) n.value.get(1));
  }
  
  @Test
  public void parseCode_single_word_parses()
  {
    String code = """
            N:
            .word 0xaabbccdd
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(1, codeParser.getMemoryLocations().size());
    MemoryLocation n = codeParser.getMemoryLocations().get(0);
    Assert.assertEquals("N", n.name);
    Assert.assertEquals(DataTypeEnum.kInt, n.dataType);
    
    Assert.assertEquals(4, n.getSize());
    Assert.assertEquals((byte) 0xdd, (byte) n.value.get(0));
    Assert.assertEquals((byte) 0xcc, (byte) n.value.get(1));
    Assert.assertEquals((byte) 0xbb, (byte) n.value.get(2));
    Assert.assertEquals((byte) 0xaa, (byte) n.value.get(3));
  }
  
  @Test
  public void parseCode_single_ascii()
  {
    String code = """
            hello:
            .ascii "abc"
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(1, codeParser.getMemoryLocations().size());
    MemoryLocation hello = codeParser.getMemoryLocations().get(0);
    Assert.assertEquals("hello", hello.name);
    Assert.assertEquals(DataTypeEnum.kByte, hello.dataType);
    
    Assert.assertEquals(3, hello.getSize());
    Assert.assertEquals((byte) 'a', (byte) hello.value.get(0));
    Assert.assertEquals((byte) 'b', (byte) hello.value.get(1));
    Assert.assertEquals((byte) 'c', (byte) hello.value.get(2));
  }
  
  @Test
  public void parseCode_wrong_ascii()
  {
    String code = """
            hello:
            .ascii 5
            """;
    codeParser.parseCode(code);
    
    Assert.assertFalse(codeParser.success());
  }
  
  @Test
  public void parseCode_wrong_word()
  {
    String code = """
            hello:
            .word
            """;
    codeParser.parseCode(code);
    
    Assert.assertFalse(codeParser.success());
  }
  
  @Test
  public void parseCode_multiple_ascii()
  {
    String code = """
            hello:
            .ascii "abc", "def"
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(1, codeParser.getMemoryLocations().size());
    MemoryLocation hello = codeParser.getMemoryLocations().get(0);
    Assert.assertEquals("hello", hello.name);
    Assert.assertEquals(DataTypeEnum.kByte, hello.dataType);
    
    Assert.assertEquals(6, hello.getSize());
    Assert.assertEquals((byte) 'a', (byte) hello.value.get(0));
    Assert.assertEquals((byte) 'b', (byte) hello.value.get(1));
    Assert.assertEquals((byte) 'c', (byte) hello.value.get(2));
    Assert.assertEquals((byte) 'd', (byte) hello.value.get(3));
    Assert.assertEquals((byte) 'e', (byte) hello.value.get(4));
    Assert.assertEquals((byte) 'f', (byte) hello.value.get(5));
  }
  
  @Test
  public void parseCode_single_asciiz()
  {
    String code = """
            hello:
            .asciiz "abc"
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(1, codeParser.getMemoryLocations().size());
    MemoryLocation hello = codeParser.getMemoryLocations().get(0);
    Assert.assertEquals("hello", hello.name);
    Assert.assertEquals(DataTypeEnum.kByte, hello.dataType);
    
    Assert.assertEquals(4, hello.getSize());
    Assert.assertEquals((byte) 'a', (byte) hello.value.get(0));
    Assert.assertEquals((byte) 'b', (byte) hello.value.get(1));
    Assert.assertEquals((byte) 'c', (byte) hello.value.get(2));
    Assert.assertEquals((byte) 0, (byte) hello.value.get(3));
  }
  
  @Test
  public void parseCode_multiple_asciiz()
  {
    String code = """
            hello:
            .asciiz "abc", "a"
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(1, codeParser.getMemoryLocations().size());
    MemoryLocation hello = codeParser.getMemoryLocations().get(0);
    Assert.assertEquals("hello", hello.name);
    Assert.assertEquals(DataTypeEnum.kByte, hello.dataType);
    
    Assert.assertEquals(6, hello.getSize());
    Assert.assertEquals((byte) 'a', (byte) hello.value.get(0));
    Assert.assertEquals((byte) 'b', (byte) hello.value.get(1));
    Assert.assertEquals((byte) 'c', (byte) hello.value.get(2));
    Assert.assertEquals((byte) 0, (byte) hello.value.get(3));
    Assert.assertEquals((byte) 'a', (byte) hello.value.get(4));
    Assert.assertEquals((byte) 0, (byte) hello.value.get(5));
  }
  
  @Test
  public void parseCode_single_negative_word_parses()
  {
    String code = """
            N:
            .word -1
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(1, codeParser.getMemoryLocations().size());
    MemoryLocation n = codeParser.getMemoryLocations().get(0);
    Assert.assertEquals("N", n.name);
    Assert.assertEquals(DataTypeEnum.kInt, n.dataType);
    
    Assert.assertEquals(4, n.getSize());
    Assert.assertEquals((byte) 0xff, (byte) n.value.get(0));
    Assert.assertEquals((byte) 0xff, (byte) n.value.get(1));
    Assert.assertEquals((byte) 0xff, (byte) n.value.get(2));
    Assert.assertEquals((byte) 0xff, (byte) n.value.get(3));
  }
  
  @Test
  public void parseCode_two_bytes_parses()
  {
    String code = """
            N:
            .byte 25,26
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(1, codeParser.getMemoryLocations().size());
    MemoryLocation n = codeParser.getMemoryLocations().get(0);
    Assert.assertEquals("N", n.name);
    Assert.assertEquals(DataTypeEnum.kByte, n.dataType);
    Assert.assertEquals(2, n.getSize());
    Assert.assertEquals((byte) 25, (byte) n.value.get(0));
    Assert.assertEquals((byte) 26, (byte) n.value.get(1));
  }
  
  @Test
  public void parseCode_align()
  {
    String code = """
            .align 3
            N:
            .byte 25,26
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(1, codeParser.getMemoryLocations().size());
    MemoryLocation n = codeParser.getMemoryLocations().get(0);
    Assert.assertEquals(3, n.alignment);
  }
  
  @Test
  public void parseCode_alignReset()
  {
    String code = """
            .align 3
            N:
            .byte 25,26
            M:
            .byte 32
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    MemoryLocation m = codeParser.getMemoryLocations().get(1);
    // Alignment should be reset after the label
    Assert.assertEquals(1, m.alignment);
  }
  
  @Test
  public void parseCode_skip()
  {
    String code = """
            N:
            .skip 8
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(1, codeParser.getMemoryLocations().size());
    MemoryLocation n = codeParser.getMemoryLocations().get(0);
    Assert.assertEquals("N", n.name);
    Assert.assertEquals(DataTypeEnum.kByte, n.dataType);
    Assert.assertEquals(8, n.getSize());
    for (int i = 0; i < 8; i++)
    {
      Assert.assertEquals((byte) 0, (byte) n.value.get(i));
    }
  }
  
  @Test
  public void parseCode_skip_fill()
  {
    String code = """
            N:
            .skip 5, 2
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(1, codeParser.getMemoryLocations().size());
    MemoryLocation n = codeParser.getMemoryLocations().get(0);
    Assert.assertEquals("N", n.name);
    Assert.assertEquals(DataTypeEnum.kByte, n.dataType);
    Assert.assertEquals(5, n.getSize());
    for (int i = 0; i < 5; i++)
    {
      Assert.assertEquals((byte) 2, (byte) n.value.get(i));
    }
  }
  
  @Test
  public void memoryInitializer_allocate_byte()
  {
    String code = """
            N:
            .byte 25
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(1, codeParser.getMemoryLocations().size());
    
    MemoryInitializer memoryInitializer = new MemoryInitializer(0, 0);
    SimulatedMemory   memory            = new SimulatedMemory();
    memoryInitializer.initializeMemory(memory, codeParser.getMemoryLocations(), codeParser.getLabels());
    
    Assert.assertEquals((byte) 25, memory.getFromMemory(0L));
  }
  
  @Test
  public void memoryInitializer_allocate_word()
  {
    String code = """
            N:
            .word 0x1234
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(1, codeParser.getMemoryLocations().size());
    
    MemoryInitializer memoryInitializer = new MemoryInitializer(0, 0);
    SimulatedMemory   memory            = new SimulatedMemory();
    memoryInitializer.initializeMemory(memory, codeParser.getMemoryLocations(), codeParser.getLabels());
    
    Assert.assertEquals((byte) 0x34, memory.getFromMemory(0L));
    Assert.assertEquals((byte) 0x12, memory.getFromMemory(1L));
    Assert.assertEquals((byte) 0, memory.getFromMemory(2L));
    Assert.assertEquals((byte) 0, memory.getFromMemory(3L));
  }
  
  @Test
  public void memoryInitializer_allocate_word_aligned()
  {
    // Align to 8 bytes!!
    String code = """
            .align 3
            N:
            .word 0x1234
            """;
    codeParser.parseCode(code);
    
    Assert.assertTrue(codeParser.success());
    Assert.assertEquals(1, codeParser.getMemoryLocations().size());
    
    MemoryInitializer memoryInitializer = new MemoryInitializer(1, 0);
    SimulatedMemory   memory            = new SimulatedMemory();
    memoryInitializer.initializeMemory(memory, codeParser.getMemoryLocations(), codeParser.getLabels());
    
    Assert.assertEquals((byte) 0x34, memory.getFromMemory(8L));
    Assert.assertEquals((byte) 0x12, memory.getFromMemory(9L));
    Assert.assertEquals((byte) 0, memory.getFromMemory(10L));
    Assert.assertEquals((byte) 0, memory.getFromMemory(11L));
  }
}
