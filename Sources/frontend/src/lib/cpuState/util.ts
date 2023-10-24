/**
 * @file    util.ts
 *
 * @author  Michal Majer
 *          Faculty of Information Technology
 *          Brno University of Technology
 *          xmajer21@stud.fit.vutbr.cz
 *
 * @brief   Utility functions for CPU state
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

import { ArrayList, Reference, WithId } from '@/lib/types/cpuApi';
import { SimCodeModel } from '@/lib/types/cpuDeref';

/**
 * Type guard for Reference type.
 * Object is a reference if it has a '@ref' property
 */
export function isReference(obj: unknown): obj is Reference {
  if (typeof obj !== 'object' || obj === null) {
    return false;
  }
  return '@ref' in obj;
}

/**
 * Type guard for WithId interface.
 */
export function hasId(obj: unknown): obj is WithId {
  if (typeof obj !== 'object' || obj === null) {
    return false;
  }
  return '@id' in obj;
}

export type IdMap = { [id: number]: object };

/**
 * Given an object and a map of ids, creates a copy of the object with all references resolved
 * TODO: Can we do it without changing object identity?
 */
export function resolveRefs(obj: unknown, map: IdMap): unknown {
  const resolved: Record<string, unknown> = {};

  // Do nothing to primitives
  if (typeof obj !== 'object' || obj === null) {
    return obj;
  }

  // Handle arrays
  if (Array.isArray(obj)) {
    return obj.map((item) => resolveRefs(item, map));
  }

  // recursively visit all properties of the object
  for (const [key, value] of Object.entries(obj)) {
    if (isReference(value)) {
      // if the property is a reference, resolve it
      const mapValue = map[value['@ref']];
      if (!mapValue) {
        throw new Error(`Reference ${value['@ref']} not found in map`);
      }
      // The resolved object can have references inside, so we need to resolve them too
      const res = resolveRefs(mapValue, map);
      resolved[key] = res;
    } else if (typeof value === 'object' && value !== null) {
      // if the property is an object, recursively resolve all references in it
      resolved[key] = resolveRefs(value, map);
    } else {
      // otherwise just copy the property
      resolved[key] = value;
    }
  }

  return resolved;
}

/**
 * Type guard for SimCodeModel type.
 */
export function isSimCodeModel(obj: unknown): obj is SimCodeModel {
  if (typeof obj !== 'object' || obj === null) {
    return false;
  }
  return '@type' in obj && obj['@type'] === 'SimCodeModel';
}

export function getArrayItems<T>(arr: ArrayList<T>): Array<T> {
  return arr['@items'] ?? [];
}
