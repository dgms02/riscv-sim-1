/**
 * @file ManagerRegistry.java
 * @author Michal Majer
 * Faculty of Information Technology
 * Brno University of Technology
 * xmajer21@stud.fit.vutbr.cz
 * @brief File contains container of all managers
 * @date 07 November  2023 18:00 (created)
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

package com.gradle.superscalarsim.managers;

/**
 * The class keeps track (through managers) of all instances of certain classes.
 * This is useful for serialization - the JSON can be normalized and the references
 * easily resolved.
 *
 * @brief Container of all managers.
 */
public class ManagerRegistry
{
  /**
   * Input code model manager
   */
  public InputCodeModelManager inputCodeManager;
  
  /**
   * Sim code model manager
   */
  public SimCodeModelManager simCodeManager;
  
  /**
   * Register model manager
   */
  public RegisterModelManager registerModelManager;
  
  public ManagerRegistry()
  {
    inputCodeManager     = new InputCodeModelManager();
    simCodeManager       = new SimCodeModelManager();
    registerModelManager = new RegisterModelManager();
  }
}
