**Prerequisite**
=============
*  Android Studio 4.1.2
*  Android Version 6.0 to 10.0
*  Kotlin Language

**Setup Instruction**
* Rename package using project name refactor menu
* Rename App in string.xml file
 
**Feature**
=============
* MVVM Architecture
* AndroidX
* Material Design
* Dependency Ejection(Koin)
* Api Calling(Retrofit, Moshi)
* Local Database(Room)
* Shared Preference With Encryption(Tink)
* Image Loading(Glide) 
* Debugging(Timber) 
* RunTime Permission(EasyPermission) 
* Location
* Kotlin Coroutines
* ApiViewStateConstraintLayout(CustomView for API state handling)

**Git Ignore**(https://www.toptal.com/developers/gitignore)

 1. Enable runner: Git project setting -> CI/CD expand the Runner section -> Enable for available runner
 2. Environment variables: Git project setting -> CI/CD expand the environment variables section
    * ANDROID_SDK_PATH
    * DEBUG_BUILD_NAME
    * DEBUG_KEYSTORE_FILE
    * DEVELOPER_EMAILS
    * KEYSTORE_FILE
    * KEYSTORE_PASSWORD
    * PROJECT_NAME
    * KEY_ALIAS
    * KEY_PASSWORD
    * RELEASE_BUILD_NAME
    * SEND_OTA_TOKEN
    * URLOFMM
 3. Add script file into the project folder with file name ".gitlab-ci.yml" in Project Level

 **Utility**
 =============
 * AlertExt: This class has commonly used extension function for snackbar alerts
 * CalenderExt: This class has commonly used extension function for calender operations
 * ContextExt: This class extension function user for activity context operations like start activity and hide keyboard
 * CustomViewExt: This class used for ApiViewStateConstraintLayout operations
 * DeviceExt: This class used to get device information
 * DialogExt: This class used for the crating different dialogs
 * EditTextExt: This class has commonly used extension function for edittext operations
 * FileUtils: This class used for files operations
 * LifeCycleExt: This class has used for mutable live data observer extension function
 * StringExt: This class used for string operations
 * Utils: This class used for common utility operations
 * ViewExt: This class has commonly used extension function for view operations

 