package com.epam.digital.data.platform.bpwebservice;

import com.epam.digital.data.platform.bpms.client.config.FeignConfig;
import com.epam.digital.data.platform.dso.client.DigitalSealRestClient;
import com.epam.digital.data.platform.integration.ceph.config.CephConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.ws.config.annotation.EnableWs;

/**
 * Application class that contains main method for Spring Boot Application
 */
@EnableWs
@SpringBootApplication
@EnableFeignClients(clients = DigitalSealRestClient.class)
@Import({CephConfig.class, FeignConfig.class})
public class BpWebserviceGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(BpWebserviceGatewayApplication.class, args);
  }
}
