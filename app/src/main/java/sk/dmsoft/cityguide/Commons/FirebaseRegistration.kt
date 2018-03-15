package sk.dmsoft.cityguide.Commons

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Models.Account.RegisterFcm

/**
 * Created by Daniel on 14. 3. 2018.
 */
class FirebaseRegistration: FirebaseInstanceIdService() {

    private val TAG = "Firebase"
    private lateinit var api: Api

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        api = Api(this)
        Log.d(TAG, "Token refreshed!")
        val refreshedToken = FirebaseInstanceId.getInstance().token

        // we want to send messages to this application instance and manage this apps subscriptions on the server side
        // so now send the Instance ID token to the app server
        refreshedToken?.let {
            sendRegistrationToServer(it)
        }
    }

    private fun sendRegistrationToServer(refreshedToken: String) {
        val model = RegisterFcm()
        model.fcmId = refreshedToken
        AccountManager.fcmTokenId = refreshedToken
        api.registerFcm(model).enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                Log.e(TAG, t.toString())
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response?.code() == 200)
                    AccountManager.isFcmRegistered = true
            }

        })
    }
}