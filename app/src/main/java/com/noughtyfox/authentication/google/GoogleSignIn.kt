package com.noughtyfox.authentication.google

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.noughtyfox.authentication.AuthStore
import com.noughtyfox.authentication.AuthType
import kotlinx.coroutines.runBlocking

class GoogleSignIn(
    private val context: Context?,
    private val registry: ActivityResultRegistry?,
    private val onSignIn: (GoogleSignInAccount) -> Unit,
    private val onFails: (ApiException) -> Unit
) : DefaultLifecycleObserver {

    private companion object {
        const val REGISTER_KEY = "key"
    }

    private var googleActivityResult: ActivityResultLauncher<Intent>? = null

    override fun onCreate(owner: LifecycleOwner) {
        googleActivityResult =
            registry?.register(
                REGISTER_KEY, owner,
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                getData(context, result.data, onSignIn, onFails)
            }
    }

    fun signInWithGoogle(clientId: String? = null) {
        val googleSignIn = context?.getGoogleSignInClient(clientId)
        if (context?.isUserSignedIn() == true) {
            googleSignIn?.signOut()
        }

        googleActivityResult?.launch(googleSignIn?.signInIntent)
    }

    private fun getData(
        context: Context?,
        data: Intent?,
        onSignIn: (GoogleSignInAccount) -> Unit,
        onFails: (ApiException) -> Unit
    ) = try {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val account = task.getResult(ApiException::class.java)
        runBlocking {
            context?.let { context ->
                AuthStore(context).saveAuthType(AuthType.Facebook)
            }
        }

        onSignIn.invoke(account)
    } catch (e: ApiException) {
        onFails.invoke(e)
    }
}

private fun Context.getGoogleSignInClient(clientId: String?): GoogleSignInClient {
    val builder = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    builder.apply {
        if (clientId != null) {
            requestIdToken(clientId)
        } else {
            requestId()
        }

        requestEmail()
        requestProfile()
    }


    return GoogleSignIn.getClient(this, builder.build())
}

fun Context.isUserSignedIn(): Boolean = GoogleSignIn.getLastSignedInAccount(this) != null

fun signOut(context: Context, clientId: String? = null, onSignOut: () -> Unit) {
    context.getGoogleSignInClient(clientId).signOut().addOnCompleteListener {
        runBlocking { AuthStore(context).saveAuthType(null) }
        onSignOut.invoke()
    }
}