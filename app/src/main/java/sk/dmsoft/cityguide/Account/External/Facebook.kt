package sk.dmsoft.cityguide.Account.External

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

class Facebook{
    var context: Activity
    var listener: FacebookInterface? = null

    constructor(context: Activity){
        this.context = context
        if (context is FacebookInterface)
            listener = context
    }

    fun register(callbackManager : CallbackManager){
            val loginManager = LoginManager.getInstance()
            loginManager.logInWithReadPermissions(context, arrayListOf("email"))
            loginManager.registerCallback(callbackManager,
                    object : FacebookCallback<LoginResult> {
                        override fun onSuccess(loginResult: LoginResult) {
                            Log.e("FB", loginResult.accessToken.userId)
                            val request = GraphRequest.newMeRequest(loginResult.accessToken) { responseObject, response ->
                                Log.v("LoginActivity", response.toString())

                                // Application code
                                val email = responseObject.getString("email")
                                Log.e("FB", email)
                                listener?.FacebookCompleted(email, loginResult.accessToken.token)
                            }
                            val parameters = Bundle()
                            parameters.putString("fields", "email")
                            request.parameters = parameters
                            request.executeAsync()
                        }

                        override fun onCancel() {
                            Log.e("FB", "Cancel")
                        }

                        override fun onError(exception: FacebookException) {
                            Log.e("FB", exception.localizedMessage)
                        }
                    })
        }


    interface FacebookInterface {
        fun FacebookCompleted(email: String, accessToken: String)
    }
}