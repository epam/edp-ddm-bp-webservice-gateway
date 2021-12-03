package com.epam.digital.data.platform.bpwebservice.config;

import com.epam.digital.data.platform.storage.base.config.CephStorageConfiguration;
import com.epam.digital.data.platform.storage.base.factory.StorageServiceFactory;
import com.epam.digital.data.platform.storage.form.service.FormDataStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The configurations that contains and configures beans for form data storage service.
 */
@Configuration
public class FormDataStorageConfig {

  @Bean
  public StorageServiceFactory storageServiceFactory(ObjectMapper objectMapper) {
    return new StorageServiceFactory(objectMapper);
  }

  @Bean
  @ConfigurationProperties(prefix = "ceph")
  public CephStorageConfiguration cephStorageConfiguration() {
    return new CephStorageConfiguration();
  }

  @Bean
  public FormDataStorageService formDataStorageService(StorageServiceFactory factory,
      CephStorageConfiguration config) {
    return factory.formDataStorageService(config);
  }
}
