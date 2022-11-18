# EmCare

 **Description**: Em Care is a digital solution that initially aims to improve health outcomes for mothers and children in emergency settings. It will provide decision-support to frontline health workers in emergencies, preserving the fidelity of, and increasing access to WHO clinical guidelines.

   - **Technology stack**: Java,Spring boot,Spring JPA, Hibernate, Angular 13 Framework, HTML/CSS TypeScript.
   - **Status**: This project is in pilot development phase.
   - **Links Staging instances**: https://emcare.argusoft.com
***
# Dependencies

Em Care have dependencies on KeyCloak Tool, IBM(Watson Language Translator), Twilio, and Google's FHIR SDK.
## **KeyCloak**
### What Is KeyCloak?
    KeyCloak is an “Identity and Access Management” tool for the Em Care system.
### Steps for KeyCloak configuration and run
    1. Download KeyCloak-15.0.2 from https://www.keycloak.org/downloads
    2. Go to the KeyCloak-15.0.2/standalone/configuration/standalone.xml and change schema or DB credential for your use
    3. cd bin 
    4. ./standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 -Djboss.socket.binding.port-offset=100 &
    5. Create an admin user from the keycloak UI (http://server-ip:port/auth/)
    6. Set Client secret and admin user info in KeycloakConfig.java file (emcare-web/src/main/java/com/argusoft/who/emcare/web/config/KeyCloakConfig.java)
***

## **IBM(Watson Language Translator)**
### What Is IBM(Watson Language Translator)?
    IBM Watson® Language Translator translates text from one language to another.
### Steps for IBM configuration
    1. Go to the https://cloud.ibm.com/login?state=/catalog/services/language-translator and make Sign-in or Sign-up.
    2. Make New Service for project
    3. Get access-key from the service console.
***

## **Twilio**
### What Is Twilio?
    Twilio is using for global communication. Currently we are using SMS service for Em Care system to communicate with user.
### Steps for Twilio configuration
    1. Go to the https://www.twilio.com/login and make Sign-in or Sign-Up.
    2. Make New SMS Service for project
    3. Get access-token, ssid, phone-number, and service-id  from the service console.
***
# Components in Project:

- **emcare-android-ui**: An android native application built with the help of Google's FHIR SDK.
- **emcare-web**: Backend application built using Spring Boot framework and PostgreSQL as its database.
- **emcare-ui**: Web application built using Angular framework.
*** 
# Configuration

    Em Care have different configuration based on each components which are describe below in "How to Run" section. 
## How to Run
#### Steps for running Em Care web


    1. Go to emcare-web/ directory
    2. Set KeyCloak Client secret and admin user info in KeycloakConfig.java file (emcare-web/src/main/java/com/argusoft/who/emcare/web/config/KeyCloakConfig.java)(Ignore if you done before)
    3. Run command "mvn clean install"
    4. Go to "/target" folder
    5. Run "java -jar emcare-web.jar"

        java -jar emcare-web-0.0.1-SNAPSHOT.jar --keycloak.credentials.secret=********-******-****-****-********** --ibm.access-key=******************************** --spring.mail.password=************ --twilio.account.ssid=****************** --twilio.account.token=************** --twilio.phone.number=************ --twilio.messaging.service.id=********************** --spring.datasource.password=************ --root=/home/************

#### Notes For emcare-web

- **keycloak.credentials.secret** -> Provide credentials of KeyCloak (You can see this key from the KeyCloak user interface) for user identity and access management.
- **ibm.access-key** -> Provide a key for dynamic language translation (You have to create an account in IBM and get the key from there https://www.ibm.com/cloud/watson-language-translator)
- **spring.mail.password** -> Provide mail account server password for communicating with Em Care users.
- **twilio.account.ssid** -> provider Twilio Account SSID for communicating with users via SMS. (You can get this from https://www.twilio.com/)
- **twilio.account.token** -> provider Twilio Account TOKEN for communicating with users via SMS. (You can get this from https://www.twilio.com/)
- **twilio.phone.number** -> provider Twilio Account Phone Number for communicate with user via SMS. (You can get this from https://www.twilio.com/)
- **twilio.messaging.service.id** -> provider Twilio Account SERVICE-ID for communicating with users via SMS. (You can get this from https://www.twilio.com/)
- **spring.datasource.password** -> Provide a Postgresql database password.
- **root** -> Provide root path for resource management.
***

### **Steps for running Em Care UI**: 

    1. Install primary requirement Node(V16).
    2. Install Angular 13 CLI.
    3. Go to the directory emcare-ui/ .
    4. Run "npm install".
    5. Run "ng serve". (By default server start on 4200 port).
***
# Getting involved
    People should get involved and describe key areas, we are currently focusing on; e.g., trying to get feedback on features, fixing certain bugs, building important pieces, etc.
***
# Additional Information or Links
1. **Em Care Staging application** : https://emcare.argusoft.com
2. **KeyCloak Documentation** : https://www.keycloak.org/documentation
3. **IBM Cloud Translation** : https://www.ibm.com/cloud/watson-language-translator

