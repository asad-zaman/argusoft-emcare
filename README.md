# EmCare

 **Description**: Em Care is a digital solution that initially aims to improve health outcomes for mothers and children in emergency settings. It will provide decision-support to frontline health workers in emergencies, preserving the fidelity of, and increasing access to WHO clinical guidelines.

   - **Technology stack**: Java,Spring boot,Spring JPA, Hibernate, Angular 13 Framework, HTML/CSS, and TypeScript.
   - **Status**: This project is in pilot development phase.
   - **Links Staging instances**: https://emcare.argusoft.com
***
# Components in Project:

- **emcare-android**: An android native application built with the help of Google's FHIR SDK.  [(README.md)](/emcare-android/README.md)
- **emcare-web**: Backend application built using Spring Boot framework and PostgreSQL as its database. [(README.md)](/emcare-web/backend.md)
- **emcare-ui**: Web application built using Angular framework. [(README.md)](/emcare-ui/front-end.md)
<!-- *** 
# Dependencies
###### Following dependencies are being used in the app
- KeyCloak(Identity and Access Management)
- IBM(Watson Language Translator)
- Twilio
- Google's FHIR SDK
## **KeyCloak(Identity and Access Management)**
### Setting up KeyCloak
1. Download KeyCloak-15.0.2 from https://www.keycloak.org/downloads
2. Go to the KeyCloak-15.0.2/standalone/configuration/standalone.xml and change schema or DB credential for your use
3. cd bin 
4. ./standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 -Djboss.socket.binding.port-offset=100 &
***

## **IBM Watson Language Translator**
### Setting up IBM
1. Go to the https://cloud.ibm.com/login?state=/catalog/services/language-translator and make Sign-in or Sign-up.
2. Make new service for project
3. Get access-key from the service console.
***

## **Twilio(SMS Service)**
### Setting up Twilio
1. Go to the https://www.twilio.com/login and make Sign-in or Sign-Up.
2. Make New SMS Service for project
3. Get access-token, ssid, phone-number, and service-id  from the service console.
***
## **Google FHIR SDK**
 - **Engine**: To manage the FHIR resource locally in the application as well as to handle the sync & management of resources between   application & server
 - **SDC**: It is used to render the questionnaire using its form filler, fetch questionnaire Response & extract resources from the questionnaire using structure map based extraction.
 - **Workflow Library**: Currently using to evaluate cql for initialExpression & wip to use it for the plan definition

# Configuration
Em Care have different configuration based on each components which are describe below in "How to Run" section. 
## How to Run
#### Steps for running Em Care web

1. Go to emcare-web/ directory
2. Create an admin user from the keycloak UI (http://server-ip:port/auth/)
3. Get access-key from the IBM service console. (https://cloud.ibm.com/login?state=/catalog/services/language-translator)
4. Get access-token, ssid, phone-number, and service-id  from the twilio service console. (https://www.twilio.com/login)
5. Set KeyCloak Client secret and admin user info in KeycloakConfig.java file (emcare-web/src/main/java/com/argusoft/who/emcare/web/config/KeyCloakConfig.java)(Ignore if you done before)
6. Run command "mvn clean install"
7. Go to "/target" folder
8. Run "java -jar emcare-web.jar"

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
*** -->
# Getting involved
People should get involved and describe key areas, we are currently focusing on; e.g., trying to get feedback on features, fixing certain bugs, building important pieces, etc.
***
<!-- # Additional Information or Links
1. **Em Care Staging application** : https://emcare.argusoft.com
2. **KeyCloak Documentation** : https://www.keycloak.org/documentation
3. **IBM Cloud Translation** : https://www.ibm.com/cloud/watson-language-translator
3. **Twilio** : https://www.twilio.com/docs/sms -->

