/**
 * @file    RadioInput.tsx
 *
 * @author  Michal Majer
 *          Faculty of Information Technology
 *          Brno University of Technology
 *          xmajer21@stud.fit.vutbr.cz
 *
 * @brief   Generic radio input component
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

// Custom radio input component
// Does not support multiple selections
//
// Works either with
// - react-hook-form (using `register` and `name` props), or
// - a simple `value` and `onNewValue` props

import React from 'react';
import { Control, FieldValues, Path, useController } from 'react-hook-form';

import { ReactClassName } from '@/lib/types/reactTypes';
import { cn } from '@/lib/utils';

import * as RadioGroup from '@radix-ui/react-radio-group';

/**
 * Uses radix ui for accessibility
 */

interface RadioInputBaseProps extends ReactClassName {
  choices: readonly string[];
  texts?: readonly string[];
}

type RadioInputProps = RadioInputBaseProps & {
  value: string;
  onNewValue: (newValue: string) => void;
};

/**
 * Uncontrolled radio input
 */
export function RadioInput({
  choices,
  texts,
  value,
  onNewValue,
}: RadioInputProps) {
  return (
    <RadioGroup.Root
      className={cn(
        'radio-field flex h-10 items-center justify-stretch rounded-md bg-muted p-1 text-muted-foreground',
      )}
      value={value}
      onValueChange={onNewValue}
    >
      {choices.map((choice, i) => {
        const inputId = `${choice}`;
        const text = texts ? texts[i] : choice;
        return (
          <React.Fragment key={choice}>
            <RadioGroup.Item value={choice} id={inputId}>
              {/* <RadioGroup.Indicator className="flex items-center justify-center w-full h-full relative after:content-[''] after:block after:w-[11px] after:h-[11px] after:rounded-[50%] after:bg-violet11" /> */}
            </RadioGroup.Item>
            <label
              className={cn(
                'hover:bg-white/60 hover:text-gray-700 flex-grow inline-flex items-center justify-center whitespace-nowrap rounded-sm px-3 py-1.5 text-sm font-medium ring-offset-background transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50',
              )}
              htmlFor={inputId}
            >
              {text}
            </label>
          </React.Fragment>
        );
      })}
    </RadioGroup.Root>
  );
}

type ControlledRadioInputProps<T extends FieldValues> = {
  name: Path<T>;
  control: Control<T>;
} & RadioInputBaseProps;

/**
 * Controlled version of RadioInput.
 * See react-hook-form docs for more info.
 */
export function ControlRadioInput<T extends FieldValues>({
  choices,
  texts,
  control,
  name,
  className = '',
}: ControlledRadioInputProps<T>) {
  const { field } = useController({
    name,
    control,
    rules: { required: true },
  });

  return (
    <RadioInput
      choices={choices}
      texts={texts}
      value={field.value}
      onNewValue={field.onChange}
      className={className}
    />
  );
}

// Added title and hint
export type RadioInputWithTitleProps<T extends FieldValues> = {
  title: string;
  hint?: string;
} & ControlledRadioInputProps<T>;

/**
 * Controlled version of RadioInput with title and hint. Works with control only.
 * See react-hook-form docs for more info.
 */
export function RadioInputWithTitle<T extends FieldValues>({
  choices,
  texts,
  control,
  name,
  className = '',
  title,
  hint,
}: RadioInputWithTitleProps<T>) {
  return (
    <div className={className}>
      <label className='mb-1 text-sm font-medium '>{title}</label>
      {hint ? (
        <span className='tooltip ml-1 text-xs'>
          &#9432;
          <div className='tooltiptext ml-2 rounded bg-gray-100 p-1'>{hint}</div>
        </span>
      ) : null}
      <div>
        <ControlRadioInput
          choices={choices}
          texts={texts}
          control={control}
          name={name}
        />
      </div>
    </div>
  );
}
