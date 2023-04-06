# EM care APK Changelog


## Current Dev Changelog:

Download Link: [emcare_signed_march_23_dev.apk](https://drive.google.com/file/d/1Ok_sL4K9T_GoPw9pyXt9NaRYpjrGypOy/view?usp=share_link) 



* Fixed header showing questionnaireId.


## Version 1.0.9:

Download Link: [emcare_signed_21_march.apk](https://drive.google.com/file/d/1RJjxOA_uZ4nxVXlT9BWVLAqmlw3Z8hjz/view?usp=share_link) 

### Changelog:

- updated local build with updated error message on the dialog for required questions.
- added exception handling for erroneous initial Expression

- Navigation Changes: integrated not saving or deleting old data on going back to old questionnaire and submit

- integrated bug fix of hidden questions not being selected on opening a previously saved questionnaire.

- Support for itemMediaAnswer Extension | Media now supported in answer options

- Added support for auto migration

- Danger signs bug resolved.

- Child icon’s hold and copy in consultations now copy resources across questionnaires for the consultation session.

- Integrated consultation flow for patient under 2 months

- End consultation on danger signs should close.

- Review stage now enabled only from consultations stage.

- Refactored sync design changes & integrated sync percentage.

- UI Changes:

    - removed line  under header in questionnaire

    - increased the font size of the question in the questionnaire by one- .

    - updated help style and minor questionnaire styling- .

    - Multiple grouped checkbox with border and white - background.

    - reduced space between questions and answers in - questionnaire

    - text overflow issue in dropdown resolved by - implementing multiline.

    - yes/no options style change (horizontally - aligned)

    - Fixed input field in questionnaire hiding - due to keyboard.

    - Label of Date of Birth displaying correctly- .

    - Integrated dynamic font sizes for the - questionnaire.

    - Added blank space below questionnaire

    - Removed dialog prompt after registering patient to move to consultation flow

    - Help -> view more in blue color

    - Revert back to only stage name in header

    - Updated dd/mm/yyyy label


## Version 1.0.8:

Download Link: [emcare_signed_dec_19.apk](https://drive.google.com/file/d/1aa_nwH2EZAwR2mT9rikLE13YyJZS62Uh/view?usp=share_link) 

**Changelog:**

- On click & hold of child icon in questionnaire, json copy added

- Integrated About screen 

- Integrated android-fhir local build with read-only, validation & help icon

- Navigation changes, going back to the previous screen from Questionnaire.

- Increased Network timeout to 5 minutes instead of 2.

- Resolved bug related to highlighting validation

- Disabled initial Expression on opening previously saved consultation.

- updated local build with updated error message on the dialog for required questions.

- added exception handling for erroneous initial Expression

- Navigation Changes: integrated not saving or deleting old data on going back to old questionnaire and submit | Confirm if snackbar is required on such cases

- refactored sync after local build upgrade but this bug is affecting it. | [https://github.com/google/android-fhir/issues/1469](https://github.com/google/android-fhir/issues/1469)

- updated help style and minor questionnaire styling.

- integrated bug fix of hidden questions not being selected on opening a previously saved questionnaire.

- Support for itemMediaAnswer Extension

- resolved build issue.

- UI change: removed line from header in questionnaire

- Increased question's font size by one.


## Version 1.0.7:

Download Link: [emcare_signed_nov_24.apk](https://drive.google.com/file/d/15EAmC141S4fLM9U0RCw6ej72SRqtfrmP/view?usp=share_link) 

**Changelog:**

- integrated patientId & encounterId as reference to Questionnaire Response.

- integrate treatment to consultation flow

- implement delete on going previous stage.(with prompt notifying user that delete will be done)

- fixed dialog text to be without unnecessary camel casing.

- integrated previous consultation screen (without read only mode)

- integrated sync ui changes and blank name fix


## Version 1.0.6:

Download Link: [emcare_signed_nov_08.apk](https://drive.google.com/file/d/1J_rXUksHxjVYfvrq8ici7ZgNsCRYMJ8G/view?usp=share_link) 

**Changelog:**

- integrated initialExpression patch

- Started using forked repository build with fixes.

- resolved multi-line name & large name issue with single-line scrollable name

- made date format consistent in the UI.

- Integrated last consultation date in patient profile screen.

- Integrated caregiver to patient resource.


## Version 1.0.5:

Download Link: [emcare_signed_oct_31.apk](https://drive.google.com/file/d/1SfrsjAoRxk5xRV4o8D4uN8AwyERWEko8/view?usp=drivesdk) 

**Changelog:**

- Integrated review mode in the apk

- Integrated "Save As Draft" Button

- Integrated "Reset" Button for the Questionnaries

- Integrated Active Consultations List & Functionality in Patient Profile Screen

- Integrated Previous Consultations List in Patient Profile Screen.

- Updated Consultation flow after registration Questionnaire, added a dialog to confirm continuing consultation.

- Integrated required asterisks (*) to the questionnaire

- integrated progress bar to Questionnaire (Included along with latest local build)


## Version 1.0.4:

Download Link: [emcare_signed_oct_14.apk](https://drive.google.com/file/d/1gvMptjBu7C7NRbyktRREXzjVkpd6Zyxy/view?usp=sharing) 

**Changelog:**

- fixed navigation back on consultation flow

- integrated confirm on back press in consultation flow

- Integrated side panel outside click close.


## Version 1.0.3:

Download Link: [emcare_october_7_signed.apk](https://drive.google.com/file/d/1XA59r8uiQoIfiYVmLhwGtaCGnP6CvOGw/view?usp=sharing) 

**Changelog:**

- Integrated latest L3 bundle

- Integrated the Signup flow

- Integrated male/female icons for patients


## Version 1.0.2:

Download Link: [emcare-september-28.apk](https://drive.google.com/file/d/1lfdXSWBbYiQLE3i-T6db6yPYEGmjU7Kf/view?usp=sharing) 

**Changelog:**

- Integrated latest L3 bundle

- Integrated local builds for engine, SDC and workflow library.

- Code refactoring for the local builds.


## Version 1.0.1:

Download Link: [EmCare-September-23.apk](https://drive.google.com/file/d/1eZe12OE-D67Hda5VmQAMoTpKRle8Cgms/view?usp=sharing) 

**Changelog:**

- Saved patient added to consultation list

- integrated L3 bundle 

- added a test questionnaire, created using helsenorge.


## Alpha 2:

Download Link: [EmCare-September-21.apk](https://drive.google.com/file/d/1doQLo3P-nDfClzpB7QvfK4PHxl3g9gLy/view?usp=sharing)

**Changelog:**

- upgraded fhirEngine version to 0.1.0-beta02.

- upgraded SDC to 0.1.0-beta05 which resolved double dropdown bug.

- Resolved id bug by implementing workaround for extracted resource.

- implemented exception handler for StructureMap.

- removed the emergency button.

- set consultation list with working questionnaires only.

- added snack-bar on consultation list questionnaire save.

- Refactored validation flow for questionnaire.

- implemented Test SM which save patients for now.

- Synced data to the latest test bundle.


## Alpha 1:

Download Link: [emcare-september-14.apk](https://drive.google.com/file/d/1iABPeW4vm5B-KreNLkW7ofgl8Ooq7b2U/view?usp=sharing)

**Changelog:**

- Implemented design changes discussed in the weekly call.

- Removed QR Scan & "Reason for consultations".

- Implemented workarounds for UUID & population enocunterId & patientId for registration questionnaire.

- Replaced submit arrow in menu with submit button in Questionnaire.

- Synced data to the latest bundle.
