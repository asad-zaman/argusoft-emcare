# Em Care Backend (emcare-web)

**Description:** Em Care is a digital solution that initially aims to improve health outcomes for mothers and children in emergency settings. It will provide decision-support to frontline health workers in emergencies, preserving the fidelity of, and increasing access to WHO clinical guidelines.

This project contains the web (backend) module of Em Care. 

 - **Technology stack**: Java, Spring boot, Spring JPA, Spring Hibernate, and PostgreSQL.

 

# Dependencies

###### Following are the dependencies,being used in the app:
- KeyCloak(for Identity and Access Management)
- IBM Watson (for Internationalisation)
- Twilio (for SMS services)
***

## **KeyCloak(Identity and Access Management)**
### Setting up KeyCloak
1. Download KeyCloak-15.0.2 from https://www.keycloak.org/downloads
2. Extract the downloaded zip file
3. Go to folder KeyCloak-15.0.2/standalone/configuration/standalone.xml for changing the database to use. If you want to use the default one, ignore this step.
4. Go to "bin" folder.
5. Run command "./standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 -Djboss.socket.binding.port-offset=100 &" - this shall start the KeyCloak server in a background process.

## **IBM Watson Language Translator**
### Setting up IBM Watson Language Translator
1. Go to the https://cloud.ibm.com/login?state=/catalog/services/language-translator and Sign-in/ Sign-up.
2. Create a new service for your project
3. Get access-key from the service console. (access-key to be used in steps below)

## **Twilio(SMS Service)**
### Setting up Twilio
1. Go to the https://www.twilio.com/login and make Sign-in or Sign-Up.
2. Make New SMS Service for project
3. Get access-token, ssid, phone-number, and service-id  from the service console. (to be used in the steps below)

***

## How to Run
#### Steps for running Em Care web

        1. Go to folder emcare-web/ 
        2. Create an admin user from the keycloak UI (http://server-ip:port/auth/)
        3. Get access-key from the IBM service console. (https://cloud.ibm.com/login?state=/catalog/services/language-translator)
        4. Get access-token, ssid, phone-number, and service-id  from the twilio service console. (https://www.twilio.com/login)
        5. Set KeyCloak Client secret and admin user info in KeycloakConfig.java file (emcare-web/src/main/java/com/argusoft/who/emcare/web/config/KeyCloakConfig.java) (Ignore if you have done it before)
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

# Additional Information or Links
1. **KeyCloak Documentation** : https://www.keycloak.org/documentation
2. **IBM Cloud Translation** : https://www.ibm.com/cloud/watson-language-translator
3. **Twilio** : https://www.twilio.com/docs/sms


