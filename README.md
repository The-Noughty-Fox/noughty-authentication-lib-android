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
