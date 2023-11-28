/**
 * @file    IssueWindow.tsx
 *
 * @author  Michal Majer
 *          Faculty of Information Technology
 *          Brno University of Technology
 *          xmajer21@stud.fit.vutbr.cz
 *
 * @brief   A component for displaying the Issue Window
 *
 * @date    06 November 2023, 23:00 (created)
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

import clsx from 'clsx';

import {
  selectAluIssueWindowBlock,
  selectBranchIssueWindowBlock,
  selectFpIssueWindowBlock,
  selectLoadStoreIssueWindowBlock,
  selectRegisterById,
} from '@/lib/redux/cpustateSlice';
import { useAppSelector } from '@/lib/redux/hooks';
import { IssueItemModel, Reference } from '@/lib/types/cpuApi';

import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/components/base/ui/tooltip';
import Block from '@/components/simulation/Block';
import InstructionField, {
  InstructionBubble,
} from '@/components/simulation/InstructionField';
import { InstructionListDisplay } from '@/components/simulation/InstructionListDisplay';
import ValueInformation from '@/components/simulation/ValueTooltip';

type IssueType = 'alu' | 'fp' | 'branch' | 'ls';

export type IssueWindowProps = {
  type: IssueType;
};

function getSelector(type: IssueType) {
  if (type === 'alu') return selectAluIssueWindowBlock;
  if (type === 'fp') return selectFpIssueWindowBlock;
  if (type === 'branch') return selectBranchIssueWindowBlock;
  if (type === 'ls') return selectLoadStoreIssueWindowBlock;
  throw new Error(`Invalid type ${type}`);
}

function getTitle(type: IssueType) {
  if (type === 'alu') return 'ALU Issue Window';
  if (type === 'fp') return 'FP Issue Window';
  if (type === 'branch') return 'Branch Issue Window';
  if (type === 'ls') return 'L/S Issue Window';
  throw new Error(`Invalid type ${type}`);
}

function getGridClassName(type: IssueType) {
  if (type === 'alu') return 'aluIssue';
  if (type === 'fp') return 'fpIssue';
  if (type === 'branch') return 'branchIssue';
  if (type === 'ls') return 'lsIssue';
  throw new Error(`Invalid type ${type}`);
}

export default function IssueWindow({ type }: IssueWindowProps) {
  const issue = useAppSelector(getSelector(type));

  if (!issue) return null;

  const validity = issue.argumentValidityMap;
  const instructionIds: Reference[] = [];
  for (const key in validity) {
    // Cast to number
    const numericKey = Number(key);
    instructionIds.push(numericKey);
  }

  const title = getTitle(type);

  const instrCount = instructionIds.length;
  const stats = (
    <>
      <div>
        {instrCount} {instrCount === 1 ? 'instruction' : 'instructions'}
      </div>
    </>
  );

  const cls = clsx(getGridClassName(type), 'w-80');

  // TODO: Is this limit suitable?
  return (
    <Block title={title} stats={stats} className={cls}>
      <InstructionListDisplay
        limit={6}
        columns={3}
        instructions={instructionIds}
        legend={
          <>
            <div>Instruction</div>
            <div>Arg 1</div>
            <div>Arg 2</div>
          </>
        }
        instructionRenderer={(instruction) => (
          <IssueWindowItem
            simCodeId={instruction}
            items={
              instruction !== undefined ? validity[instruction] : undefined
            }
          />
        )}
      />
    </Block>
  );
}

type IssueWindowItemProps = {
  simCodeId?: Reference;
  items?: IssueItemModel[];
};

/**
 * Displays a single item in the Issue Window
 */
export function IssueWindowItem({ simCodeId, items }: IssueWindowItemProps) {
  const reg1 = useAppSelector((state) =>
    selectRegisterById(state, items?.[0]?.tag ?? 'INVALID'),
  );
  const reg2 = useAppSelector((state) =>
    selectRegisterById(state, items?.[1]?.tag ?? 'INVALID'),
  );
  if (!items) {
    return (
      <div className='col-span-3'>
        <InstructionField instructionId={simCodeId} />
      </div>
    );
  }

  const item1 = items[0];
  const item2 = items[1];

  const item1Style = clsx(
    'flex px-2',
    item1?.validityBit && 'text-green-500',
    item1 === undefined && 'invisible',
  );
  const item2Style = clsx(
    'flex px-2',
    item2?.validityBit && 'text-green-500',
    item2 === undefined && 'invisible',
  );

  // First try to get the value from the constant value, then from the register
  let item1Value = item1?.constantValue;
  if (item1Value === undefined) {
    item1Value = reg1?.value;
  }

  let item2Value = item2?.constantValue;
  if (item2Value === undefined) {
    item2Value = reg2?.value;
  }

  return (
    <>
      <InstructionField instructionId={simCodeId} />
      <TooltipProvider>
        <Tooltip>
          <TooltipTrigger>
            <InstructionBubble className={item1Style}>
              {item1Value?.stringRepresentation ?? '-'}
            </InstructionBubble>
          </TooltipTrigger>
          <TooltipContent>
            {item1Value ? (
              <ValueInformation value={item1Value} valid={item1?.validityBit ?? false} />
            ) : (
              <div className='text-gray-400'>No value</div>
            )}
          </TooltipContent>
        </Tooltip>
      </TooltipProvider>
      <TooltipProvider>
        <Tooltip>
          <TooltipTrigger>
            <InstructionBubble className={item2Style}>
              {item2Value?.stringRepresentation ?? '-'}
            </InstructionBubble>
          </TooltipTrigger>

          <TooltipContent>
            {item2Value ? (
              <ValueInformation value={item2Value} valid={item2?.validityBit ?? false} />
            ) : (
              <div className='text-gray-400'>No value</div>
            )}
          </TooltipContent>
        </Tooltip>
      </TooltipProvider>
    </>
  );
}
