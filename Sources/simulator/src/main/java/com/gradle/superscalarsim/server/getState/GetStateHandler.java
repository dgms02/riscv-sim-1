/**
 * @file GetStateHandler.java
 * @author Michal Majer
 * Faculty of Information Technology
 * Brno University of Technology
 * xmajer21@stud.fit.vutbr.cz
 * @brief Handler for the /getState endpoint
 * @date 26 Sep      2023 10:00 (created)
 * @section Licence
 * This file is part of the Superscalar simulator app
 * <p>
 * Copyright (C) 2023 Michal Majer
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

package com.gradle.superscalarsim.server.getState;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradle.superscalarsim.code.ParseError;
import com.gradle.superscalarsim.cpu.Cpu;
import com.gradle.superscalarsim.cpu.CpuConfiguration;
import com.gradle.superscalarsim.cpu.CpuState;
import com.gradle.superscalarsim.serialization.Serialization;
import com.gradle.superscalarsim.server.IRequestDeserializer;
import com.gradle.superscalarsim.server.IRequestResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @brief Handler for the /getState endpoint
 * Accepts a CpuConfiguration and returns corresponding state
 */
public class GetStateHandler implements IRequestResolver<GetStateRequest, GetStateResponse>, IRequestDeserializer<GetStateRequest>
{
  
  @Override
  public GetStateResponse resolve(GetStateRequest request)
  {
    GetStateResponse response;
    if (request == null)
    {
      // Send error
      response = new GetStateResponse(null);
    }
    else
    {
      // Get state
      response = getState(request);
    }
    return response;
  }
  
  /**
   * @param request Request containing configuration and program
   *
   * @return Response containing state of the CPU, with loaded program
   * @brief Get state of the CPU, with loaded program
   */
  private GetStateResponse getState(GetStateRequest request)
  {
    CpuConfiguration cfg;
    if (request.config == null)
    {
      System.out.println("Providing default configuration");
      cfg = CpuConfiguration.getDefaultConfiguration();
    }
    else
    {
      cfg = request.config;
    }
    // Validate configuration, including code
    CpuConfiguration.ValidationResult validationResult = cfg.validate();
    // Create response
    CpuState         state        = null;
    List<ParseError> codeErrors   = null;
    List<String>     configErrors = null;
    
    if (validationResult.valid)
    {
      // Initialize CPU, assumes valid configuration
      Cpu cpu = new Cpu(cfg);
      state = cpu.cpuState;
    }
    else
    {
      System.err.println("Provided configuration is invalid: " + validationResult.messages);
      configErrors = validationResult.messages;
      codeErrors   = validationResult.codeErrors;
    }
    return new GetStateResponse(state, configErrors, codeErrors);
  }
  
  @Override
  public GetStateRequest deserialize(InputStream json) throws IOException
  {
    ObjectMapper deserializer = Serialization.getDeserializer();
    return deserializer.readValue(json, GetStateRequest.class);
  }
}
