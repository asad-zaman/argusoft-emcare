# Em Care Android

**Description:** Em Care is a digital solution that initially aims to improve health outcomes for mothers and children in emergency settings. It will provide decision-support to frontline health workers in emergencies, preserving the fidelity of, and increasing access to WHO clinical guidelines.

 - **Technology stack**: Kotlin, Android Studio, Room Database, Retrofit.

 # Dependencies
###### Following dependencies are being used in the app
- Android FHIR : Engine, Data Capture & Workflow
***

## **Android FHIR**
### Setting up Android FHIR Local Build
1. Download latest local build by going to the [(Actions)](https://github.com/parthfloyd/android-fhir/actions) page & clicking on the latest successful build.
2. In the build page, go to the Artifacts section and download the *maven-repository* and extract it in any folder.

***

## How to Run
#### Steps for running Em Care Android:

        1. Open the "emcare-android" folder in Android Studio.
        2. Go to the "./build.gradle" file and on line 30
        3. Update the url string in url "file:///home/parth/Downloads/maven-repository" with the path to the unzipped maven-repository.
        4. Run Gradle Sync.
        5. Build the project.
        6. Run the project.

# Additional Information or Links
1. **Android-Fhir Project** : https://github.com/google/android-fhir
2. **Android-Fhir Forked Repository (EmCare Specific tweaks)** : https://github.com/parthfloyd/android-fhir
3. **SMART- EmCare** : https://github.com/WorldHealthOrganization/smart-emcare
