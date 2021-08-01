package com.epam.digital.data.platform.bpwebservice.config;

import com.epam.digital.data.platform.bpwebservice.constant.Constants;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

/**
 * Base configuration for SOAP web service that is needed for:
 * <ol>
 * <li>Adding bp-webservice-gateway xsd schema to application context</li>
 * <li>Adding bp-webservice-gateway wsdl definition to application context</li>
 * <li>Creating {@link MessageDispatcherServlet} that will process the SOAP messages</li>
 * <li>Registration {@link MessageDispatcherServlet} in application context</li>
 * </ol>
 */
@Configuration
public class BpWebserviceConfiguration {

  /**
   * @return created {@link MessageDispatcherServlet}
   */
  @Bean
  public MessageDispatcherServlet messageDispatcherServlet() {
    var servlet = new MessageDispatcherServlet();
    servlet.setTransformWsdlLocations(true);
    return servlet;
  }

  /**
   * Register previously created {@link MessageDispatcherServlet} in application context with url
   * mapping /ws/*
   *
   * @param servlet {@link MessageDispatcherServlet} to register
   * @return registration bean
   */
  @Bean
  public ServletRegistrationBean<MessageDispatcherServlet> servletRegistrationBean(
      MessageDispatcherServlet servlet) {
    return new ServletRegistrationBean<>(servlet, "/ws/*");
  }

  /**
   * BpWebservice {@link Wsdl11Definition WSDL Definition}
   *
   * @param schema bp-webservice-gateway {@link XsdSchema XSD schema bean}
   * @return created {@link Wsdl11Definition WSDL Definition}
   */
  @Bean(name = "bpWebservice")
  public Wsdl11Definition defaultWsdl11Definition(XsdSchema schema) {
    var wsdl11Definition = new DefaultWsdl11Definition();
    wsdl11Definition.setPortTypeName("BpWebservice");
    wsdl11Definition.setLocationUri("/ws");
    wsdl11Definition.setTargetNamespace(Constants.NAMESPACE);
    wsdl11Definition.setSchema(schema);
    return wsdl11Definition;
  }

  /**
   * @return bp-webservice-gateway {@link XsdSchema XSD schema bean}
   */
  @Bean
  public XsdSchema bpWebserviceSchema() {
    return new SimpleXsdSchema(new ClassPathResource("bp-webservice.xsd"));
  }
}
