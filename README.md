# noughty-authentication-lib-android

How to use:

Step 1. Add the JitPack repository to your build file
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency
```groovy
dependencies {
    implementation 'com.github.The-Noughty-Fox:noughty-authentication-lib-android:latest.release'
    }
```

## Google
```java
    private val googleSignIn by lazy {
        GoogleSignIn(context, activityResultRegistry,
            onSignIn = { account ->
                //On success, get data from google account.
            },
            onFails = { exception ->
                //On fails.
            })
    }
```

Override method onCreate() and put this code:

```java
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(googleSignIn)
    }
```

Next sign in:</br>
```java
googleSignIn.signInWithGoogle(/* your clientId or can be null */)
```

## Facebook
In your manifest write this:</br>
```xml
      <meta-data
            android:name="com.facebook.sdk.ApplicationId"
            android:value="@string/facebook_app_id" />
        <meta-data
            android:name="com.facebook.sdk.ClientToken"
            android:value="@string/facebook_client_token" />
        <activity
            android:name="com.facebook.FacebookActivity"
            android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation" />
```

In your resString:</br>
```xml
    <string name="facebook_app_id">//your facebook app id</string>
    <string name="fb_login_protocol_scheme">//your facebook schema</string>
    <string name="facebook_client_token">//your facebook client token</string>
```
Next sign in:
```java
       FacebookSignIn.signInWithFacebook(activity, 
       onSignIn = { account->
           //On success, get data from facebook account.
        },
       onFails = { exception ->
           //On fails.
            })
```
</li>
