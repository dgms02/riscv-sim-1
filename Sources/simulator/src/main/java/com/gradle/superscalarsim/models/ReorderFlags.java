/**
 * @file    ReorderFlags.java
 *
 * @author  Jan Vavra \n
 *          Faculty of Information Technology \n
 *          Brno University of Technology \n
 *          xvavra20@fit.vutbr.cz
 *
 * @brief File contains container of flags for processed instructions in ROB
 *
 * @date  3 February   2020 16:00 (created) \n
 *        16 February  2020 16:00 (revised)
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

/**
 * @class ReorderFlags
 * @brief Flag container for instructions inside of ROB marking their state
 */
public class ReorderFlags
{
  /// Is instruction valid
  private boolean isValid;
  /// Is instruction busy
  private boolean isBusy;
  /// Is instruction speculative
  private boolean isSpeculative;

  /**
   * @brief Constructor
   * @param [in] isSpeculative - Is instruction speculative
   */
  public ReorderFlags(boolean isSpeculative)
  {
    this.isValid       = true;
    this.isBusy        = true;
    this.isSpeculative = isSpeculative;
  }// end of Constructor
  //------------------------------------------------------

  /**
   * @brief Gets busy bit
   * @return Boolean value of busy bit
   */
  public boolean isBusy()
  {
    return this.isBusy;
  }// end of isBusy
  //------------------------------------------------------

  /**
   * @brief Gets valid bit
   * @return Boolean value of valid bit
   */
  public boolean isValid()
  {
    return this.isValid;
  }// end of isValid
  //------------------------------------------------------

  /**
   * @brief Gets speculative bit
   * @return Boolean value of speculative bit
   */
  public boolean isSpeculative()
  {
    return this.isSpeculative;
  }// end of isSpeculative
  //------------------------------------------------------

  /**
   * @brief Sets busy bit
   * @param [in] busy - New value of the busy bit
   */
  public void setBusy(boolean busy)
  {
    this.isBusy = busy;
  }// end of setBusy
  //------------------------------------------------------

  /**
   * @brief Sets valid bit
   * @param [in] valid - New value of the valid bit
   */
  public void setValid(boolean valid)
  {
    this.isValid = valid;
  }// end of setValid
  //------------------------------------------------------

  /**
   * @brief Sets speculative bit
   * @param [in] speculative - New value of the speculative bit
   */
  public void setSpeculative(boolean speculative)
  {
    this.isSpeculative = speculative;
  }// end of setSpeculative
  //------------------------------------------------------

  /**
   * @brief Checks if the instruction is ready for commit based on flags
   * @return TRue if instruction is ready, false otherwise
   */
  public boolean isReadyToBeCommitted()
  {
    return !this.isBusy && !this.isSpeculative && this.isValid;
  }// end of isReadyToBeCommitted
  //------------------------------------------------------

  /**
   * @brief Checks if instruction has failed and can be removed
   * @return True if instruction can be removed, false otherwise
   */
  public boolean isReadyToBeRemoved()
  {
    return !this.isSpeculative && !this.isValid;
  }// end of isReadyToBeRemoved
  //------------------------------------------------------

}
