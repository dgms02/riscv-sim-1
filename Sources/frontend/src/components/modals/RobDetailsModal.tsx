/**
 * @file    RobDetailsModal.tsx
 *
 * @author  Michal Majer
 *          Faculty of Information Technology
 *          Brno University of Technology
 *          xmajer21@stud.fit.vutbr.cz
 *
 * @brief   Modal for displaying details about the Reorder Buffer
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

import { selectROB } from '@/lib/redux/cpustateSlice';
import { useAppSelector } from '@/lib/redux/hooks';

import {
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/base/ui/card';

export const RobDetailsModal = () => {
  const rob = useAppSelector(selectROB);

  if (!rob) throw new Error('ROB not found');

  return (
    <>
      <CardHeader>
        <CardTitle>Reorder Buffer</CardTitle>
        <CardDescription>Detailed view</CardDescription>
      </CardHeader>
      <CardContent>
        <h2>Buffer</h2>
        <div>
          Capacity: {rob.reorderQueue.length}/{rob.bufferSize}
        </div>
      </CardContent>
    </>
  );
};
