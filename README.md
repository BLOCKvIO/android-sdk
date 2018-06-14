BLOCKv SDK for Android
======================

This is the official BLOCKv SDK. It allows you to easily integrate your own apps into the BLOCKv Platform. It handles a number of operations on your behalf, including:

- Wrapping API endpoints,
- Parsing JSON to native Java models, and
- Managing OAuth2 tokens.

### Requirements

- Android API 19+
- Kotlin

The BLOCKv SDK is dependant on Kotlin, if your version of Android Studio < 3.0 you will need to install it. Go to File | Settings | Plugins | Install JetBrains plugin… and then search for and install Kotlin. If you are looking at the "Welcome to Android Studio" screen, choose Configure | Plugins | Install JetBrains plugin… You'll need to restart the IDE after this completes.


### Install and configure the SDK

First, add the BLOCKv maven repository to the root-level `build.gradle` file:

```java
allprojects {
  //...
  repositories {
    //...
    maven {
      url "https://maven.blockv.io/artifactory/BLOCKv"
    }
  }
}
```

Next, add the following dependencies to your module Gradle file (usually the `app/build.gradle`):

```java
dependencies {
  // ...
  compile 'io.blockv.sdk:core:0.5.0'
  // Make sure android Studio version is > 3.0 or include the Kotlin Plugin
  compile "org.jetbrains.kotlin:kotlin-stdlib-jre7:$kotlin_version"
}
```

To access the BLOCKv SDK in your application code, import the class:

```java
import io.blockv.core.Blockv
```

### Configure your BLOCKv integration

To configure your integration, create an instance of the BLOCKv SDK.

```java
    protected void onCreate(Bundle savedInstanceState) {
       //Blockv instance should be maintained as a singleton
        Blockv blockv = new Blockv(this,"Your app id");

    }
```

> At this point you will need an App Id. See [FAQ](https://developer-dev.blockv.io/docs/faq)

### Recommendations

We recommend you use [Dagger 2](https://github.com/google/dagger), or a similar library, for singleton management for the BLOCKv SDK.