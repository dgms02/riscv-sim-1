/**
 * @file    transformErrors.ts
 *
 * @author  Michal Majer
 *          Faculty of Information Technology
 *          Brno University of Technology
 *          xmajer21@stud.fit.vutbr.cz
 *
 * @brief   Codemirror extension for line coloring based on mapping
 *
 * @date    30 September 2023, 12:00 (created)
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

import type { Diagnostic } from '@codemirror/lint';

import { ComplexErrorItem } from '@/lib/types/simulatorApi';

/**
 * Transforms errors from compiler API to codemirror diagnostics.
 * Most notably, it converts line and column to character index.
 *
 * @param errors  Array of errors from compiler API
 * @param code   Code to which the errors belong
 * @returns Array of codemirror diagnostics
 */
export function transformErrors(
  errors: Array<ComplexErrorItem>,
  code: string,
): Array<Diagnostic> {
  // Add one to each line length to account for the fact that caret can be at the end of the line
  const lineLengths = code.split('\n').map((line) => line.length + 1);
  const lineLengthsPrefixSum = [0];
  for (const lineLength of lineLengths) {
    // todo test this
    const prev = lineLengthsPrefixSum[lineLengthsPrefixSum.length - 1];
    if (prev === undefined) {
      throw new Error('Invalid line lengths');
    }
    lineLengthsPrefixSum.push(prev + lineLength);
  }

  return errors.map((error: ComplexErrorItem): Diagnostic => {
    // We have a line and column, but code mirror expects a 1D character index
    // We need to convert the line and column to a character index
    const span = error.locations[0];
    if (!span) {
      throw new Error('Invalid error span (0 locations)');
    }
    // 1-based line and column
    const line = span.caret.line;
    const lineStart = lineLengthsPrefixSum[line - 1];
    if (lineStart === undefined) {
      throw new Error(`Invalid line start for line ${line}`);
    }
    const charIndex = lineStart + span.caret['display-column'] - 1;

    let charIndexEnd;
    if (span.finish) {
      charIndexEnd =
        charIndex +
        1 +
        (span.finish['display-column'] - span.caret['display-column']);
    } else {
      charIndexEnd = charIndex;
    }

    return {
      from: charIndex,
      to: charIndexEnd,
      message: error.message,
      severity: error.kind,
    };
  });
}
