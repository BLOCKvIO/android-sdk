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

Add the BLOCKv maven repository and Kotlin plugin to the root-level `build.gradle` file:

```java
buildscript {
  ext.kotlin_version = '1.2.41'
  //...
  dependencies {
    //...
    classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
  }
}
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

Next, add the kotlin plugin and following dependencies to your module Gradle file (usually the `app/build.gradle`):

```java
apply plugin: 'kotlin-android' //This should be at the top of the file.
// ...
//
dependencies {
  // ...
  implementation 'io.blockv.sdk:core:1.0.0'
  // Make sure android Studio version is > 3.0 or include the Kotlin Plugin
  implementation "org.jetbrains.kotlin:kotlin-stdlib-jre7:$kotlin_version"
}
```

To access the BLOCKv SDK in your application code, import the class:

```java
import io.blockv.core.Blockv
```

There is also an RxJava2 wrapped version, to use this you require to add the following additional dependencies to your module Gradle file:

```java
dependencies {
  // ...
  implementation 'io.reactivex.rxjava2:rxandroid:2.0.1'
  implementation 'io.reactivex.rxjava2:rxjava:2.1.6'
  implementation 'io.blockv.sdk:rx:1.0.0'
]
```
To access the RxJava2 wrapped SDK in your application code, import the class:

```java
import io.blockv.rx.Blockv
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