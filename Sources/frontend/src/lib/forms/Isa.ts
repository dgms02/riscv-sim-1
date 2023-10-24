/**
 * @file    Isa.ts
 *
 * @author  Michal Majer
 *          Faculty of Information Technology
 *          Brno University of Technology
 *          xmajer21@stud.fit.vutbr.cz
 *
 * @brief   Type definition and validation for ISA configuration
 *
 * @date    19 September 2023, 22:00 (created)
 *
 * @section Licence
 * This file is part of the Superscalar simulator app
 *
 * Copyright (C) 2023  Michal Majer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import { z } from 'zod';

export const predictorTypes = ['1bit', '2bit'] as const;
export type PredictorType = (typeof predictorTypes)[number];

export const predictorDefaults = ['Taken', 'Not Taken'] as const;
export type PredictorDefault = (typeof predictorDefaults)[number];

export const cacheReplacementTypes = ['LRU', 'FIFO', 'Random'] as const;
export type CacheReplacementType = (typeof cacheReplacementTypes)[number];

export const storeBehaviorTypes = ['write-back'] as const;
export type StoreBehaviorType = (typeof storeBehaviorTypes)[number];

export const arithmeticUnits = ['FX', 'FP'] as const;
export const otherUnits = ['L/S', 'Branch', 'Memory'] as const;
export const fuTypes = [...arithmeticUnits, ...otherUnits] as const;
export type FuTypes = (typeof fuTypes)[number];

/**
 * <ul>
 *   <li>'+' - Addition</li>
 *   <li>'-' - Subtraction</li>
 *   <li>'*' - Multiplication</li>
 *   <li>'%' - Modulo</li>
 *   <li>'/' - Division</li>
 *   <li>'&' - Bitwise AND</li>
 *   <li>'|' - Bitwise OR</li>
 *   <li>'^' - Bitwise XOR</li>
 *   <li>'<<' - Bitwise left shift</li>
 *   <li>'>>' - Bitwise right shift</li>
 *   <li>'>>>' - Bitwise unsigned right shift</li>
 *   <li>'sqrt' - Square root</li>
 *   <li>'!' - Bitwise NOT</li>
 *   <li>'>' - Greater than signed</li>
 *   <li>'>=' - Greater than or equal</li>
 *   <li>'<' - Less than</li>
 *   <li>'<=' - Less than or equal</li>
 *   <li>'==' - Equal</li>
 *   <li>'!=' - Not equal</li>
 *   <li>'=' - Assign (left to the right)</li>
 *   <li>'pick' - Pick one of the two variables based on the value of the third variable (false picks the left one)</li>
 *   <li>'float' - Convert to float (does not change the bits, interpret cast)</li>
 *   <li>'bits' - Convert to bits (does not change the bits, interpret cast)</li>
 *   <li>'fclass' - Classify float (returns an int)</li>
 * </ul>
 */
export const fuOps = [
  '!',
  'bits',
  '+',
  '-',
  '*',
  '/',
  '%',
  '&',
  '|',
  '^',
  '>>>',
  '<<',
  '>>',
  '<=',
  '>=',
  '==',
  '<',
  '>',
  '=',
  'sqrt',
  'float',
  'fclass',
] as const;
export type FuOps = (typeof fuOps)[number];

export const lsUnitSchema = z.object({
  id: z.number(),
  fuType: z.enum(otherUnits),
  latency: z.number().min(1).max(16),
});
export type LsUnitConfig = z.infer<typeof lsUnitSchema>;

export const arithmeticUnitSchema = lsUnitSchema.extend({
  fuType: z.enum(arithmeticUnits), // Field overwrite
  operations: z.array(z.enum(fuOps)),
});
export type ArithmeticUnitConfig = z.infer<typeof arithmeticUnitSchema>;

export const fUnitSchema = z.union([lsUnitSchema, arithmeticUnitSchema]);
export type FUnitConfig = z.infer<typeof fUnitSchema>;

export function isArithmeticUnitConfig(
  fUnit: FUnitConfig,
): fUnit is ArithmeticUnitConfig {
  return fUnit.fuType === 'FX' || fUnit.fuType === 'FP';
}

// Schema for form validation
export const isaSchema = z.object({
  // Buffers
  robSize: z.number().min(1).max(1000),
  lbSize: z.number().min(1).max(1000),
  sbSize: z.number().min(1).max(1000),
  // Fetch
  fetchWidth: z.number().min(1).max(16),
  commitWidth: z.number().min(1).max(16),
  // Branch
  btbSize: z.number().min(1).max(2048),
  phtSize: z.number().min(1).max(16),
  predictorType: z.enum(predictorTypes),
  predictorDefault: z.enum(predictorDefaults),
  // Functional Units
  fUnits: z.array(fUnitSchema),
  // Cache
  cacheLines: z.number().min(1).max(1000),
  cacheLineSize: z.number().min(1).max(1000),
  cacheAssoc: z.number().min(1).max(1000),
  cacheReplacement: z.enum(cacheReplacementTypes),
  storeBehavior: z.enum(storeBehaviorTypes),
  storeLatency: z.number().min(0).max(1000),
  loadLatency: z.number().min(0).max(1000),
  laneReplacementDelay: z.number().min(1).max(1000),
  addRemainingDelay: z.boolean(), // todo
});

export type IsaConfig = z.infer<typeof isaSchema>;

export const isaNamed = isaSchema.extend({
  name: z.string(),
});

export type IsaNamedConfig = z.infer<typeof isaNamed>;

export const isaFormDefaultValues: IsaNamedConfig = {
  robSize: 256,
  lbSize: 64,
  sbSize: 64,
  fetchWidth: 3,
  commitWidth: 4,
  btbSize: 1024,
  phtSize: 10,
  predictorType: '1bit',
  predictorDefault: 'Not Taken',
  fUnits: [
    {
      id: 0,
      fuType: 'FX',
      latency: 2,
      operations: [
        '!',
        'bits',
        '+',
        '-',
        '*',
        '/',
        '%',
        '&',
        '|',
        '^',
        '>>>',
        '<<',
        '>>',
        '<=',
        '>=',
        '==',
        '<',
        '>',
        '=',
      ],
    },
    {
      id: 1,
      fuType: 'FP',
      latency: 2,
      operations: [
        '!',
        'bits',
        '+',
        '-',
        '*',
        '/',
        '%',
        '&',
        '|',
        '^',
        '>>>',
        '<<',
        '>>',
        '<=',
        '>=',
        '==',
        '<',
        '>',
        '=',
        'sqrt',
        'float',
        'fclass',
      ],
    },
    {
      id: 2,
      fuType: 'L/S',
      latency: 2,
    },
    {
      id: 3,
      fuType: 'Branch',
      latency: 2,
    },
    {
      id: 4,
      fuType: 'Memory',
      latency: 1,
    },
  ],
  cacheLines: 16,
  cacheLineSize: 32,
  cacheAssoc: 2,
  cacheReplacement: 'LRU',
  storeBehavior: 'write-back',
  storeLatency: 0,
  loadLatency: 1,
  laneReplacementDelay: 10,
  addRemainingDelay: false,
  name: 'Default',
};
