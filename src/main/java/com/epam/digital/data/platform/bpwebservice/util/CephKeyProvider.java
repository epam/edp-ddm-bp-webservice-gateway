package com.epam.digital.data.platform.bpwebservice.util;


import org.springframework.stereotype.Component;

/**
 * The class represents a provider that is used to generate the key to get the ceph document
 */
@Component
public class CephKeyProvider {

  private static final String START_FORM_DATA_STRING_FORMAT = "start_form_%s";
  private static final String START_FORM_DATA_VALUE_FORMAT = "lowcode_%s_%s";

  /**
   * Method for generating the ceph key to save start form data, uses process definition key and
   * generated unique identifier to construct the key
   *
   * @param processDefinitionKey process definition key
   * @param uuid                 unique identifier
   * @return generated ceph key
   */
  public String generateStartFormKey(String processDefinitionKey, String uuid) {
    var startFormDataVariableName = String.format(START_FORM_DATA_STRING_FORMAT, uuid);
    return String.format(START_FORM_DATA_VALUE_FORMAT, processDefinitionKey,
        startFormDataVariableName);
  }
}
