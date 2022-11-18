# EmCare

 **Description**: Em Care is a digital solution that initially aims to improve health outcomes for mothers and children in emergency settings. It will provide decision-support to frontline health workers in emergencies, preserving the fidelity of, and increasing access to WHO clinical guidelines.

 # Other things to include:

   - **Technology stack**: Java,Spring boot,Spring JPA, Hibernate, Angular 13 Framework, HTML/CSS TypeScript.
   - **Status**: This project is in pilot development phase.
   - **Links Staging instances**: https://emcare.argusoft.com

## Dependencies

    Em Care have dependencies on KeyCloak Tool, and Google's FHIR SDK.
## Configuration

    Em Care have different configuration based on each components which are describe below in [How to Run](/how-to-run) section. 

## Components in Project:

1. emcare-android-ui: An android native application built with the help of Google's FHIR SDK.
2. emcare-web: Backend application built using Spring Boot framework and PostgreSQL as its database.
3. emcare-ui: Web application built using Angular framework.
 
## How to Run

**emcare-web**

KeyCloak is an “Identity and Access Management” tool for the Em Care system.
To setup KeyCloak for Em Care follow the below steps

    1. Download KeyCloak-15.0.2 from https://www.keycloak.org/downloads
    2. Go to the KeyCloak-15.0.2/standalone/configuration/standalone.xml and change schema or DB credential for your use

    **Run KeyCloak**
    1. cd bin 
    2. ./standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 -Djboss.socket.binding.port-offset=100 &
    3. Create an admin user from the keycloak UI
    4. Set Client secret and admin user info in KeycloakConfig.java file

**Steps for running Em Care web**:

    1. Go to emcare-web/ directory
    2. Run command "mvn clean install"
    3. Go to "/target" folder
    4. Run "java -jar emcare-web.jar"

        java -jar emcare-web-0.0.1-SNAPSHOT.jar --keycloak.credentials.secret=********-******-****-****-********** --ibm.access-key=******************************** --spring.mail.password=************ --twilio.account.ssid=****************** --twilio.account.token=************** --twilio.phone.number=************ --twilio.messaging.service.id=********************** --spring.datasource.password=************ --root=/home/************

**Note For emcare-web**

    1. keycloak.credentials.secret --> Provide credentials of KeyCloak (You can see this key from the KeyCloak user interface) for user identity and access management.
    2. ibm.access-key --> Provide a key for dynamic language translation (You have to create an account in IBM and get the key from there https://www.ibm.com/cloud/watson-language-translator)
    3. spring.mail.password --> Provide mail account server password for communicating with Em Care users.
    4. twilio.account.ssid --> provider Twilio Account SSID for communicating with users via SMS. (You can get this from https://www.twilio.com/)
    5. twilio.account.token --> provider Twilio Account TOKEN for communicating with users via SMS. (You can get this from https://www.twilio.com/)
    6. twilio.phone.number --> provider Twilio Account Phone Number for communicate with user via SMS. (You can get this from https://www.twilio.com/)
    7. twilio.messaging.service.id --> provider Twilio Account SERVICE-ID for communicating with users via SMS. (You can get this from https://www.twilio.com/)
    8. spring.datasource.password --> Provide a Postgresql database password.
    9. root --> Provide root path for resource management.


**Steps for running Em Care UI**: 

    1. Install primary requirement Node(V16).
    2. Install Angular 13 CLI.
    3. Go to the directory emcare-ui/ .
    4. Run "npm install".
    5. Run "ng serve". (By default server start on 4200 port).

## Getting involved
    People should get involved and describe key areas you are currently focusing on; e.g., trying to get feedback on features, fixing certain bugs, building important pieces, etc.

## Additional Information or Links
1. **Em Care Staging application** : https://emcare.argusoft.com
2. **KeyCloak Documentation** : https://www.keycloak.org/documentation
3. **IBM Cloud Translation** : https://www.ibm.com/cloud/watson-language-translator

