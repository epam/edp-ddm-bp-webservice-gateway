/*
 * Copyright 2021 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.bpwebservice.controller;

import com.epam.digital.data.platform.bpwebservice.dto.StartBpRequest;
import com.epam.digital.data.platform.bpwebservice.dto.StartBpResponse;
import com.epam.digital.data.platform.bpwebservice.service.StartBpService;
import com.epam.digital.data.platform.starter.errorhandling.dto.SystemErrorDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint that is used for starting business-process
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StartBpController {

  private final StartBpService service;

  /**
   * Method that is used for starting business process instance
   * <p>
   * Delegates an invocation to {@link StartBpService#startBp(StartBpRequest)}
   *
   * @param startBpRequest received {@link StartBpRequest startBpRequest}
   * @return {@link StartBpResponse startBpResponse} from service
   */
  @PostMapping("/start-bp")
  @Operation(
      summary = "Start process instance",
      description = "Returns result variable of business process")
  @ApiResponse(
      description = "Returns result variable of business process",
      responseCode = "200",
      content = @Content(schema = @Schema(implementation = StartBpResponse.class)))
  @ApiResponse(
      description = "Business process definition cannot be started or missing required start "
          + "variable for the business process",
      responseCode = "422",
      content = @Content(schema = @Schema(implementation = SystemErrorDto.class)))
  @ApiResponse(
      description = "Internal server error",
      responseCode = "500",
      content = @Content(schema = @Schema(implementation = SystemErrorDto.class)))
  @ResponseBody
  public StartBpResponse startBp(@RequestBody StartBpRequest startBpRequest) {
    return service.startBp(startBpRequest);
  }
}
