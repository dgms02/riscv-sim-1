/**
 * @file    FunctionUnitGroup.tsx
 *
 * @author  Michal Majer
 *          Faculty of Information Technology
 *          Brno University of Technology
 *          xmajer21@stud.fit.vutbr.cz
 *
 * @brief   A component for displaying a group of functional units (e.g. ALU)
 *
 * @date    11 November 2023, 17:00 (created)
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

import {
  selectArithmeticFunctionUnitBlocks,
  selectBranchFunctionUnitBlocks,
  selectFpFunctionUnitBlocks,
} from '@/lib/redux/cpustateSlice';
import { useAppSelector } from '@/lib/redux/hooks';

import Block from '@/components/simulation/Block';
import InstructionField from '@/components/simulation/InstructionField';

type FUType = 'alu' | 'fp' | 'branch';

export type FunctionUnitGroupProps = {
  type: FUType;
};

function getSelector(type: FUType) {
  if (type == 'alu') return selectArithmeticFunctionUnitBlocks;
  if (type == 'fp') return selectFpFunctionUnitBlocks;
  if (type == 'branch') return selectBranchFunctionUnitBlocks;
  throw new Error(`Invalid type ${type}`);
}

export default function FunctionUnitGroup({ type }: FunctionUnitGroupProps) {
  const fus = useAppSelector(getSelector(type));

  if (!fus) return null;

  // TODO: has no limit
  return (
    <>
      {fus.map((fu) => (
        <Block
          title={fu.name}
          key={fu.name}
          stats={`${fu.counter}/${fu.delay}`}
        >
          <InstructionField instructionId={fu.simCodeModel} />
        </Block>
      ))}
    </>
  );
}
