/**
 * @file SimulatedMemory.java
 * @author Jan Vavra \n
 * Faculty of Information Technology \n
 * Brno University of Technology \n
 * xvavra20@fit.vutbr.cz
 * @author Michal Majer
 * Faculty of Information Technology
 * Brno University of Technology
 * xmajer21@stud.fit.vutbr.cz
 * @brief File contains class simulating memory
 * @date 17 December  2020 10:00 (created) \n
 * 17 May       2021 17:55 (revised)
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
package com.gradle.superscalarsim.code;

import java.util.HashMap;
import java.util.Map;

/**
 * @class SimulatedMemory
 * @brief Class simulating memory with read/write capabilities
 */
public class SimulatedMemory
{
  /// Hash map with stored values, serves as memory
  private final Map<Long, Byte> memoryMap;
  
  /**
   * @brief Constructor
   */
  public SimulatedMemory()
  {
    this.memoryMap = new HashMap<>();
  }// end of Constructor
  //-------------------------------------------------------------------------------------------
  
  /**
   * @param [in] address - Hashmap key, pointing into specific place in memory
   * @param [in] value - Value to be saved into memory (hashmap)
   *
   * @brief Insert byte value into memory
   */
  public void insertIntoMemory(Long address, byte value, int id)
  {
    this.memoryMap.put(address, value);
  }// end of insertIntoMemory
  //-------------------------------------------------------------------------------------------
  
  /**
   * @param [in] address - Hashmap key, pointing into specific place in memory
   *
   * @return Value from hashmap pointed by key
   * @brief Get value from memory
   */
  public byte getFromMemory(Long address)
  {
    if (this.isInMemory(address))
    {
      return this.memoryMap.get(address);
    }
    else
    {
      return 0;
    }
  }// end of getFromMemory
  //-------------------------------------------------------------------------------------------
  
  /**
   * @param [in] address - Hashmap key, pointing into specific place in memory
   *
   * @return True if key has been set in past, false otherwise
   * @brief Check if some memory space is filled with data
   */
  public boolean isInMemory(Long address)
  {
    return this.memoryMap.containsKey(address);
  }// end of isInMemory
  //-------------------------------------------------------------------------------------------
  
  /**
   * @brief Resets memory to its initial state
   */
  public void reset()
  {
    this.memoryMap.clear();
  }// end of reset
  //-------------------------------------------------------------------------------------------
  
  /**
   * @return Map<Long, Byte> - Hasmap holding memory data
   * @brief Gets memory content
   */
  public Map<Long, Byte> getMemoryMap()
  {
    return memoryMap;
  }// end of deleteFromMemory
  //-------------------------------------------------------------------------------------------
}
