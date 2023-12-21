/**
 * @file InputCodeArgument.java
 * @author Jan Vavra \n
 * Faculty of Information Technology \n
 * Brno University of Technology \n
 * xvavra20@fit.vutbr.cz
 * @author Michal Majer
 * Faculty of Information Technology
 * Brno University of Technology
 * xmajer21@stud.fit.vutbr.cz
 * @brief File contains container of argument of parsed instruction
 * @date 10 November  2020 17:45 (created) \n
 * 11 November  2020 14:05 (revised)
 * 26 Sep      2023 10:00 (revised)
 * @section Licence
 * This file is part of the Superscalar simulator app
 * <p>
 * Copyright (C) 2020  Jan Vavra
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gradle.superscalarsim.models;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.gradle.superscalarsim.models.register.RegisterDataContainer;
import com.gradle.superscalarsim.models.register.RegisterModel;

/**
 * @class InputCodeArgument
 * @brief Container of argument of parsed instruction
 */
public class InputCodeArgument
{
  /**
   * Parsed constant value of the argument. Uses type info from instruction definition.
   */
  RegisterDataContainer constantValue;
  /**
   * Name of the argument.
   * Example: rs1, imm, labelName.
   */
  private String name;
  
  /**
   * Register value of the argument.
   */
  @JsonIdentityReference(alwaysAsId = true)
  private RegisterModel registerValue;
  
  /**
   * Value of the argument.
   * Example: x5, 10, name of a label.
   */
  private String stringValue;
  
  /**
   * @param name  Name of the argument
   * @param value Value of the argument
   *
   * @brief Constructor for label argument
   */
  public InputCodeArgument(final String name, final String value)
  {
    this.name          = name;
    this.stringValue   = value;
    this.constantValue = null;
  }// end of Constructor
  
  /**
   * @brief Constructor for constant argument
   */
  public InputCodeArgument(final String name, final String value, final RegisterDataContainer constantValue)
  {
    this.name          = name;
    this.stringValue   = value;
    this.constantValue = constantValue;
  }// end of Constructor
  
  /**
   * @brief Constructor for register argument
   */
  public InputCodeArgument(final String name, final RegisterModel registerValue)
  {
    this.name          = name;
    this.stringValue   = registerValue.getName();
    this.registerValue = registerValue;
    this.constantValue = null;
  }// end of Constructor
  
  /**
   * @param argument Object to be copied
   *
   * @brief Copy constructor
   */
  public InputCodeArgument(final InputCodeArgument argument)
  {
    this.name          = argument.getName();
    this.stringValue   = argument.getValue();
    this.registerValue = argument.getRegisterValue();
    if (argument.getConstantValue() != null)
    {
      this.constantValue = new RegisterDataContainer(argument.getConstantValue());
    }
    else
    {
      this.constantValue = null;
    }
  }// end of Constructor
  
  /**
   * @return Argument name
   * @brief Get name of the argument
   */
  public String getName()
  {
    return name;
  }// end of getName
  //------------------------------------------------------
  
  /**
   * @return Argument value
   * @brief Get value of the argument
   */
  public String getValue()
  {
    return stringValue;
  }// end of getValue
  //------------------------------------------------------
  
  /**
   * @return Register value of the argument
   */
  public RegisterModel getRegisterValue()
  {
    return registerValue;
  }
  //------------------------------------------------------
  
  /**
   * @return Constant value of the argument, if it is a constant
   */
  public RegisterDataContainer getConstantValue()
  {
    return constantValue;
  }
  //------------------------------------------------------
  
  public void setConstantValue(RegisterDataContainer constantValue)
  {
    this.constantValue = constantValue;
  }
  //------------------------------------------------------
  
  /**
   * @param stringValue New value of argument
   *
   * @brief Sets new value of the argument
   */
  public void setValue(final String stringValue)
  {
    this.stringValue = stringValue;
  }// end of setValue
  //------------------------------------------------------
  
  /**
   * String representation of the object
   */
  @Override
  public String toString()
  {
    return name + " = " + stringValue;
  }
}
