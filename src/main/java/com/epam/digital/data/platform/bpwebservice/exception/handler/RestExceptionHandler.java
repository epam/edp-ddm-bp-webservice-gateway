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

package com.epam.digital.data.platform.bpwebservice.exception.handler;

import com.epam.digital.data.platform.bpwebservice.exception.BpmsConnectionException;
import com.epam.digital.data.platform.bpwebservice.exception.DsoSignatureException;
import com.epam.digital.data.platform.bpwebservice.exception.KeycloakAuthException;
import com.epam.digital.data.platform.bpwebservice.exception.MissedRequiredBusinessProcessInputParameterException;
import com.epam.digital.data.platform.bpwebservice.exception.NoSuchBusinessProcessDefinedException;
import com.epam.digital.data.platform.bpwebservice.exception.StorageConnectionException;
import com.epam.digital.data.platform.dso.client.exception.BaseException;
import com.epam.digital.data.platform.starter.errorhandling.BaseRestExceptionHandler;
import com.epam.digital.data.platform.starter.errorhandling.dto.SystemErrorDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * The class represents a handler for exception.
 * <p>
 * Contains methods for handling start business processes exceptions:
 * <li>{@link NoSuchBusinessProcessDefinedException}</li>
 * <li>{@link MissedRequiredBusinessProcessInputParameterException}</li>
 * <li>{@link DsoSignatureException}</li>
 * <li>{@link KeycloakAuthException}</li>
 * <li>{@link StorageConnectionException}</li>
 * <li>{@link BpmsConnectionException}</li>
 */
@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class RestExceptionHandler {

  /**
   * Map {@link NoSuchBusinessProcessDefinedException} to a response entity with code 422
   *
   * @param ex exception to handle
   * @return responseEntity to return to client
   */
  @ExceptionHandler(NoSuchBusinessProcessDefinedException.class)
  public ResponseEntity<SystemErrorDto> handleException(NoSuchBusinessProcessDefinedException ex) {
    return mapToResponseEntity(ex.getMessage(), null, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  /**
   * Map {@link MissedRequiredBusinessProcessInputParameterException} to a response entity with code 422
   *
   * @param ex exception to handle
   * @return responseEntity to return to client
   */
  @ExceptionHandler(MissedRequiredBusinessProcessInputParameterException.class)
  public ResponseEntity<SystemErrorDto> handleException(
      MissedRequiredBusinessProcessInputParameterException ex) {
    return mapToResponseEntity(ex.getMessage(), null, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  /**
   * Map {@link DsoSignatureException} to a response entity with code 500
   *
   * @param ex exception to handle
   * @return responseEntity to return to client
   */
  @ExceptionHandler(DsoSignatureException.class)
  public ResponseEntity<SystemErrorDto> handleException(DsoSignatureException ex) {
    var cause = ex.getCause();
    if (cause instanceof BaseException) {
      var baseException = (BaseException) cause;
      var errorDto = baseException.getErrorDto();
      return mapToResponseEntity(errorDto.getMessage(), errorDto.getLocalizedMessage(),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return mapToResponseEntity(cause.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Map {@link KeycloakAuthException} to a response entity with code 500
   *
   * @param ex exception to handle
   * @return responseEntity to return to client
   */
  @ExceptionHandler(KeycloakAuthException.class)
  public ResponseEntity<SystemErrorDto> handleException(KeycloakAuthException ex) {
    return mapToResponseEntity(ex.getCause().getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Map {@link StorageConnectionException} to a response entity with code 500
   *
   * @param ex exception to handle
   * @return responseEntity to return to client
   */
  @ExceptionHandler(StorageConnectionException.class)
  public ResponseEntity<SystemErrorDto> handleException(StorageConnectionException ex) {
    return mapToResponseEntity(ex.getCause().getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Map {@link BpmsConnectionException} to a response entity with code 500
   *
   * @param ex exception to handle
   * @return responseEntity to return to client
   */
  @ExceptionHandler(BpmsConnectionException.class)
  public ResponseEntity<SystemErrorDto> handleException(BpmsConnectionException ex) {
    return mapToResponseEntity(ex.getCause().getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<SystemErrorDto> mapToResponseEntity(String message,
      String localizedMessage, HttpStatus status) {
    var systemErrorDto = SystemErrorDto.builder()
        .traceId(MDC.get(BaseRestExceptionHandler.TRACE_ID_KEY))
        .message(message)
        .code(String.valueOf(status.value()))
        .localizedMessage(localizedMessage)
        .build();
    return new ResponseEntity<>(systemErrorDto, status);
  }
}
