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

/**
 * THe difference between byte and char is in display and memory definition.
 */
export const dataTypes = [
  'kByte',
  'kShort',
  'kInt',
  'kUInt',
  'kLong',
  'kULong',
  'kFloat',
  'kDouble',
  'kBool',
  'kChar',
] as const;
export const dataTypesText = [
  'Byte',
  'Short',
  'Integer',
  'Unsigned Integer',
  'Long',
  'Unsigned Long',
  'Float',
  'Double',
  'Boolean',
  'Char',
] as const;

/**
 * Definition of memory location, as the API expects it
 */

export const dataChunk = z.object({
  dataType: z.enum(dataTypes),
  values: z.array(z.string()),
});
export type DataChunk = z.infer<typeof dataChunk>;

export const memoryLocation = z.object({
  name: z.string().min(1),
  alignment: z.number().min(1).max(16),
  dataChunks: z.array(dataChunk),
});
export type MemoryLocationApi = z.infer<typeof memoryLocation>;

export const memoryLocationDefaultValue: MemoryLocationApi = {
  name: 'Array',
  alignment: 4,
  dataChunks: [],
};

/**
 * This is the memory location with additional fields for the form.
 * These extra fields are kept in the app, but not sent to the backend.
 */
export const memoryLocationIsa = memoryLocation.extend({
  dataType: z.enum(dataTypes),
  dataSource: z.enum(['constant', 'random', 'file']),
});
export type MemoryLocationIsa = z.infer<typeof memoryLocationIsa>;

export const arithmeticUnits = ['FX', 'FP'] as const;
export const otherUnits = ['L_S', 'Branch', 'Memory'] as const;
export const fuTypes = [...arithmeticUnits, ...otherUnits] as const;
export type FuTypes = (typeof fuTypes)[number];

export const operations = [
  'bitwise',
  'addition',
  'multiplication',
  'division',
  'special',
] as const;
export type Operations = (typeof operations)[number];

export const lsUnitSchema = z.object({
  id: z.number(),
  name: z.optional(z.string()),
  fuType: z.enum(otherUnits),
  latency: z.number().min(1).max(16),
});
export type LsUnitConfig = z.infer<typeof lsUnitSchema>;

export const arithmeticUnitSchema = lsUnitSchema.extend({
  fuType: z.enum(arithmeticUnits), // Field overwrite
  operations: z.array(z.enum(operations)),
});
export type ArithmeticUnitConfig = z.infer<typeof arithmeticUnitSchema>;

export const fUnitSchema = z.union([lsUnitSchema, arithmeticUnitSchema]);
export type FUnitConfig = z.infer<typeof fUnitSchema>;

export function isArithmeticUnitConfig(
  fUnit: FUnitConfig,
): fUnit is ArithmeticUnitConfig {
  return fUnit.fuType === 'FX' || fUnit.fuType === 'FP';
}

/**
 * Schema for form validation.
 * Contains all the fields that are used in the form, including name.
 */
export const isaFormSchema = z.object({
  // Name. Used for saving different configurations.
  name: z.string(),
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
export type CpuConfig = z.infer<typeof isaFormSchema>;

/**
 * The configuration that is sent to the backend.
 */
export const simulationConfig = z.object({
  /**
   * CPU configuration. Buffer sizes, functional units, etc.
   */
  cpuConfig: isaFormSchema,
  /**
   * Assembly code to be simulated.
   */
  code: z.string(),
  /**
   * Memory locations to be allocated.
   */
  memoryLocations: z.array(memoryLocationIsa),
});
export type SimulationConfig = z.infer<typeof simulationConfig>;

/**
 * Default configuration
 */
export const defaultCpuConfig: CpuConfig = {
  name: 'Default',
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
      name: 'FX Universal',
      fuType: 'FX',
      latency: 2,
      operations: [
        'bitwise',
        'addition',
        'multiplication',
        'division',
        'special',
      ],
    },
    {
      id: 1,
      name: 'FP',
      fuType: 'FP',
      latency: 2,
      operations: [
        'bitwise',
        'addition',
        'multiplication',
        'division',
        'special',
      ],
    },
    {
      id: 2,
      fuType: 'L_S',
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
};

export const defaultSimulationConfig: SimulationConfig = {
  cpuConfig: defaultCpuConfig,
  code: '',
  memoryLocations: [],
};
