package sk.dmsoft.cityguide

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Account.Login.LoginActivity
import sk.dmsoft.cityguide.Account.Registration.RegistrationActivity
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Models.Account.RegisterFcm
import sk.dmsoft.cityguide.Models.Country
import sk.dmsoft.cityguide.Models.Place
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import android.content.Context.NOTIFICATION_SERVICE
import android.support.annotation.RequiresApi


/**
 * Created by Daniel on 22. 2. 2018.
 */

class SplashActivity : AppCompatActivity() {

    private lateinit var api: Api
    private lateinit var db: DB

    private var placesDownloaded = false
    private var countriesDownloaded = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sk.dmsoft.cityguide.Commons.AccountManager.init(this)
        api  = Api(this)
        db = DB(this)

        SaveCountries()
        SavePlaces()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannel()


        if (!AccountManager.isFcmRegistered)
            RegisterFcm()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(){
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        /* Create or update. */
        val channel = NotificationChannel("sk.dmsoft.cityguide", "Channel human readable title",  NotificationManager.IMPORTANCE_DEFAULT)
        mNotificationManager.createNotificationChannel(channel)
    }

    private fun RegisterFcm(){
        val model = sk.dmsoft.cityguide.Models.Account.RegisterFcm()
        model.fcmId = AccountManager.fcmTokenId
        api.registerFcm(model).enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                Log.e("", t.toString())
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response?.code() == 200)
                    AccountManager.isFcmRegistered = true
            }

        })
    }

    private fun SaveCountries(){
        db.Drop(Country())
        api.getCountries().enqueue(object: Callback<ArrayList<Country>> {
            override fun onFailure(call: Call<ArrayList<Country>>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ArrayList<Country>>?, response: Response<ArrayList<Country>>?) {
                db.SaveCountries(response?.body()!!)
                countriesDownloaded = true
                CloseSplashScreen()
            }

        })
    }

    private fun SavePlaces(){
        db.Drop(Place())
        api.getPlaces().enqueue(object: Callback<ArrayList<Place>> {
            override fun onFailure(call: Call<ArrayList<Place>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<ArrayList<Place>>?, response: Response<ArrayList<Place>>?) {
                db.SavePlaces(response?.body()!!)
                placesDownloaded = true
                CloseSplashScreen()
            }

        })
    }

    private fun CloseSplashScreen(){
        if (placesDownloaded && countriesDownloaded) {
            if (AccountManager.isLoggedIn && AccountManager.isRegistrationCompleted)
                startActivity(Intent(this, MainActivity::class.java))

            else
                startActivity(Intent(this, RegistrationActivity::class.java))
            finish()

        }
    }

}