/**
 * @file    PreCommitModel.java
 *
 * @author  Jan Vavra \n
 *          Faculty of Information Technology \n
 *          Brno University of Technology \n
 *          xvavra20@fit.vutbr.cz
 *
 * @brief File contains container for holding register values before instruction commit
 *
 * @date  20 February  2021 15:00 (created) \n
 *        28 April     2021 19:30 (revised)
 *
 * @section Licence
 * This file is part of the Superscalar simulator app
 *
 * Copyright (C) 2020  Jan Vavra
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
 *
 */
package com.gradle.superscalarsim.models;

import com.gradle.superscalarsim.enums.RegisterReadinessEnum;

/**
 * @class PreCommitModel
 * @brief Container for holding register values before instruction commit
 */
public class PreCommitModel
{
  /// Id when was model recorded
  private final int id;
  /// Architectural register to save
  private final String archRegister;
  /// Speculative register associated to architectural
  private final String speculRegister;
  /// Value of architectural before commit
  private final double value;
  /// Order id, which was used during mapping
  private final int registerOrder;
  /// State of the speculative register
  private final RegisterReadinessEnum speculState;

  /**
   * @brief Constructor
   * @param [in] id             - Id when was model recorded
   * @param [in] archRegister   - Architectural register to save
   * @param [in] speculRegister - Speculative register associated to architectural
   * @param [in] value          - Value of architectural before commit
   * @param [in] registerOrder  - Id specifying order between mappings to same register
   */
  public PreCommitModel(int id,
                        String archRegister,
                        String speculRegister,
                        double value,
                        int registerOrder,
                        RegisterReadinessEnum speculState)
  {
    this.id             = id;
    this.archRegister   = archRegister;
    this.speculRegister = speculRegister;
    this.value          = value;
    this.registerOrder  = registerOrder;
    this.speculState    = speculState;
  }// end of Constructor
  //-------------------------------------------------------------------------------------------

  /**
   * @brief Gets id when was model recorded
   * @return Recorded id
   */
  public int getId()
  {
    return id;
  }// end of getId
  //-------------------------------------------------------------------------------------------

  /**
   * @brief Gets name of saved architectural register
   * @return Name of saved architectural register
   */
  public String getArchRegister()
  {
    return archRegister;
  }// end of getArchRegister
  //-------------------------------------------------------------------------------------------

  /**
   * @brief Gets speculative register name mapped to saved architectural one
   * @return Speculative register name
   */
  public String getSpeculRegister()
  {
    return speculRegister;
  }// end of getSpeculRegister
  //-------------------------------------------------------------------------------------------

  /**
   * @brief Gets architectural register value before commit
   * @return Architectural register value before commit
   */
  public double getValue()
  {
    return value;
  }// end of getValue
  //-------------------------------------------------------------------------------------------

  /**
   * @brief Get order id
   * @return Order id
   */
  public int getRegisterOrder()
  {
    return registerOrder;
  }// end of getRegisterOrder
  //-------------------------------------------------------------------------------------------

  /**
   * @brief Get Speculative register state
   * @return Speculative register state
   */
  public RegisterReadinessEnum getSpeculState()
  {
    return speculState;
  }// end of getSpeculState
  //-------------------------------------------------------------------------------------------
}
