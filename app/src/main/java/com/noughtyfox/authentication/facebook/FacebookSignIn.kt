package com.noughtyfox.authentication.facebook

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.noughtyfox.authentication.AuthStore
import com.noughtyfox.authentication.AuthType
import kotlinx.coroutines.runBlocking

object FacebookSignIn {
    private const val TAG = "AuthFacebook"
    private val facebookCallbackManager: CallbackManager by lazy {
        CallbackManager.Factory.create()
    }

    fun signInWithFacebook(
        activity: AppCompatActivity,
        onSignIn: (FacebookSignInAccountData) -> Unit,
        onFails: (FacebookException) -> Unit
    ) {
        registerFacebookCallback(activity, onSignIn, onFails)

        try {
            LoginManager
                .getInstance()
                .logInWithReadPermissions(
                    activity,
                    facebookCallbackManager,
                    listOf("email", "public_profile")
                )
        } catch (e: FacebookException) {
            onFails.invoke(e)
            Log.e(TAG, e.toString())
        }
    }

    private fun registerFacebookCallback(
        activity: AppCompatActivity,
        onSignIn: (FacebookSignInAccountData) -> Unit,
        onFails: (FacebookException) -> Unit
    ) {
        LoginManager
            .getInstance()
            .registerCallback(facebookCallbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    getData(activity, result.accessToken, onSignIn)
                }

                override fun onCancel() {
                    onFails.invoke(FacebookException(message = "Login cancel"))
                    Log.e(TAG, "Login cancel")
                }

                override fun onError(error: FacebookException) {
                    onFails.invoke(error)
                    Log.e(TAG, "error -=- $error")
                }
            })
    }

    private fun getData(
        activity: AppCompatActivity,
        accessToken: AccessToken,
        onSignIn: (FacebookSignInAccountData) -> Unit
    ) {
        val graphRequest =
            GraphRequest.newMeRequest(accessToken) { obj, _ ->
                obj?.let {
                    val email = it.optString("email") ?: ""
                    val firstName = it.optString("first_name") ?: ""
                    val lastName = it.optString("last_name") ?: ""
                    val socialId = it.optString("id") ?: ""
                    val username = if (email.isNotEmpty() && email.contains("@")) {
                        email.split("@")[0]
                    } else {
                        ""
                    }

                    val facebookData = FacebookSignInAccountData(
                        email, firstName, lastName,
                        socialId, username, accessToken
                    )

                    runBlocking { AuthStore(activity).saveAuthType(AuthType.Facebook) }
                    onSignIn.invoke(facebookData)
                }
            }

        val bundle = Bundle()
        bundle.putString("fields", "id,first_name,last_name,email,picture,gender,location,link")
        graphRequest.parameters = bundle
        graphRequest.executeAsync()
    }
}

fun signOut(context: Context, onSignOut: () -> Unit) {
    if (AccessToken.getCurrentAccessToken() == null) {
        return  // already logged out
    }

    GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null,
        HttpMethod.DELETE, {
            LoginManager.getInstance().logOut()
            runBlocking { AuthStore(context).saveAuthType(null) }
            onSignOut.invoke()
        }).executeAsync()
}

data class FacebookSignInAccountData(
    val email: String,
    val firstName: String,
    val lastName: String,
    val socialId: String,
    val username: String,
    val accessToken: AccessToken
)