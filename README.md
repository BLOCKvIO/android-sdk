BLOCKv SDK for Android (BETA)
======================

This is the official BLOCKv SDK. It allows you to easily integrate your own apps into the BLOCKv Platform. It handles a number of operations on your behalf, including:

- Wrapping API endpoints,
- Parsing JSON to native Java models,
- Managing OAuth2 tokens, and
- Interacting with the web socket.

### Requirements

- Android API 19+
- Kotlin

The BLOCKv SDK is dependant on Kotlin, if your version of Android Studio < 3.0 you will need to install it. Go to File | Settings | Plugins | Install JetBrains plugin… and then search for and install Kotlin. If you are looking at the "Welcome to Android Studio" screen, choose Configure | Plugins | Install JetBrains plugin… You'll need to restart the IDE after this completes.


### Install and configure the SDK

Add the BLOCKv maven repository and Kotlin plugin to the root-level `build.gradle` file:

```gradle
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

Next, add the Kotlin plugin and following dependencies to your module Gradle file (usually the `app/build.gradle`):

```gradle
apply plugin: 'kotlin-android' //This should be at the top of the file.
// ...
//
dependencies {
  // ...
  implementation 'io.blockv.sdk:core:1.0.0'
  // Make sure android Studio version is > 3.0 or include the Kotlin Plugin
  implementation 'org.jetbrains.kotlin:kotlin-stdlib-jre7:$kotlin_version'
  // (Optional) This is only required if you want to use the web socket.
  // Attempting to access the EventManager will throw MissingWebSocketException
  // if this is not included.
  implementation 'com.neovisionaries:nv-websocket-client:2.5'
}
```

Finally, add the following `uses-permission` element to the manifest:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

To access the BLOCKv SDK in your application code, import the class:

```java
import io.blockv.core.Blockv;
```

There is also an [RxJava2](https://github.com/ReactiveX/RxJava) wrapped version, to use this you require to add the following additional dependencies to your module Gradle file:

```gradle
dependencies {
  // ...
  implementation 'io.reactivex.rxjava2:rxandroid:2.0.1'
  implementation 'io.reactivex.rxjava2:rxjava:2.1.6'
  implementation 'io.blockv.sdk:rx:1.0.0'
}
```
To access the RxJava2 wrapped SDK in your application code, import the class:

```java
import io.blockv.rx.Blockv;
```

### Configure your BLOCKv integration

To configure your integration, create an instance of the BLOCKv SDK.

```java
public class ExampleActivity extends Activity
{ 
  //...
  protected void onCreate(Bundle savedInstanceState) {
    //...
    //BLOCKv instance should be maintained as a singleton
    Blockv blockv = new Blockv(this,"Your app id"); 
  }
}
```

> At this point you will need an App Id. See [FAQ](https://developer.blockv.io/docs/faq)

### Example

Please see the [BLOCKv Android Example](https://github.com/BLOCKvIO/android-sample) for an example on using the BLOCKv SDK.

### Dependencies
1. [nv-websocket-client](https://github.com/TakahikoKawasaki/nv-websocket-client) is used for the web socket.

### Recommendations

We recommend you use [Dagger 2](https://github.com/google/dagger), or a similar library, for singleton management for the BLOCKv SDK.

## Production Support

The BLOCKv SDK for Android is currently in public **beta**. Breaking changes may still be introduced in the coming months.  This is *important* to consider if you are planning on releasing the SDK as part of your application.

## Versioning

Once the release is official, the SDK will adhere to [semantic versioning](https://semver.org).

## Author

[BLOCKv](developer.blockv.io)

## License

BLOCKv is available under the BLOCKv AG license. See the [LICENSE](./LICENSE.md) file for more info.

