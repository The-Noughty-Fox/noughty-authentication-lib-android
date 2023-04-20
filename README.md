# noughty-authentication-lib-android

How to use:</br>
<li>Google
<code>
    private val googleSignIn by lazy {
        GoogleSignIn(context, activityResultRegistry,
            onSignIn = { account ->
                //On success, get data from google account.
            },
            onFails = { exception ->
                //On fails.
            })
    }
</code>
</li>
</br>

Override method onCreate() and put this code:
<code>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(googleSignIn)
    }
</code>

Next sign in:</br>
<code>googleSignIn.signInWithGoogle(/* your clientId */)</code>

<li>Facebook
In your manifest write this:</br>
<code>
      <meta-data
            android:name="com.facebook.sdk.ApplicationId"
            android:value="@string/facebook_app_id" />
        <meta-data
            android:name="com.facebook.sdk.ClientToken"
            android:value="@string/facebook_client_token" />
        <activity
            android:name="com.facebook.FacebookActivity"
            android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation" />
</code>

In your resString:</br>
<code>
    string name="facebook_app_id">//your facebook app id</string
    string name="fb_login_protocol_scheme">//your facebook schema</string
    string name="facebook_client_token">//your facebook client token</string
</code>
<code>
       FacebookSignIn.signInWithFacebook(activity, 
       onSignIn = { account->
           //On success, get data from facebook account.
        },
       onFails = { exception ->
           //On fails.
            })
</code>
</li>
