export const defaultAsmCode =
  'add:\n    addi sp,sp,-48\n    sw s0,44(sp)\n    addi s0,sp,48\n    sw a0,-36(s0)\n    sw a1,-40(s0)\n    lw a5,-36(s0)\n    addi a5,a5,1\n    slli a5,a5,1\n    sw a5,-28(s0)\n    lw a4,-28(s0)\n    lw a5,-40(s0)\n    add a5,a4,a5\n    sw a5,-20(s0)\n    sw zero,-24(s0)\n    j .L2\n.L3:\n    lw a4,-20(s0)\n    lw a5,-40(s0)\n    add a5,a4,a5\n    sw a5,-20(s0)\n    lw a5,-24(s0)\n    addi a5,a5,1\n    sw a5,-24(s0)\n.L2:\n    lw a4,-24(s0)\n    lw a5,-36(s0)\n    blt a4,a5,.L3\n    lw a5,-20(s0)\n    mv a0,a5\n    lw s0,44(sp)\n    addi sp,sp,48\n    jr ra';
