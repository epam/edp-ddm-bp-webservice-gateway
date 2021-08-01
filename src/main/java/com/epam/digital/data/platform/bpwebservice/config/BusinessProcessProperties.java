package com.epam.digital.data.platform.bpwebservice.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Class that represents business process properties that are defined in application properties with
 * header {@code trembita}
 *
 * @see TrembitaBusinessProcessProperties
 */
@Getter
@Setter
public class BusinessProcessProperties {

  private String processDefinitionId;
  private List<String> startVars;
  private List<String> returnVars;
  private boolean requiresSignature;
}
