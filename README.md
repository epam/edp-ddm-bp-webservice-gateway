# bp-webservices-gateway

### Overview

* SOAP web service is designed for starting business processes.

### Usage

#### Prerequisites:

* business-process-management service is configured and running;
* Ceph-storage is configured and running;
* Keycloak is configured and running;
* digital-signature-ops service is configured and running.

#### Configuration

Available properties are following:

* `bpms.url` - business process management service base url;
* `dso.url` - digital signature ops service base url;
* `form-management-provider.url` - form management service base url;
* `ceph.http-endpoint` - ceph base url;
* `ceph.access-key` - ceph access key;
* `ceph.secret-key` - ceph secret key;
* `ceph.bucket` - ceph bucket name;
* `keycloak.url` - keycloak base url;
* `keycloak.realm` - keycloak realm name;
* `keycloak.client-id` - keycloak client identifier;
* `keycloak.client-secret` - keycloak client secret.

#### Run application:

* `java -jar <file-name>.jar`
* WSDL will be available on: `http://localhost:8080/ws/bpWebservice.wsdl`

### Local development

1. Run spring boot application using 'local' profile:
  * `mvn spring-boot:run -Drun.profiles=local` OR using appropriate functions of your IDE;
  * `application-local.yml` is configuration file for local development.

### Test execution

* Tests could be run via maven command:
  * `mvn verify` OR using appropriate functions of your IDE.
  
### License

The bp-webservices-gateway is released under version 2.0 of
the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).