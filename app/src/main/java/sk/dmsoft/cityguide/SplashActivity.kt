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
import com.google.gson.Gson
import sk.dmsoft.cityguide.Commons.PicassoCache
import sk.dmsoft.cityguide.Models.InitResponse
import sk.dmsoft.cityguide.Models.Interest
import sk.dmsoft.cityguide.Proposal.ActiveProposalActivity


/**
 * Created by Daniel on 22. 2. 2018.
 */

class SplashActivity : AppCompatActivity() {

    private lateinit var api: Api
    private lateinit var db: DB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sk.dmsoft.cityguide.Commons.AccountManager.init(this)
        api  = Api(this)
        db = DB(this)

        PicassoCache.CreatePicassoCache(this)

        init()
        getInterests()

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

    private fun init(){
        api.init().enqueue(object: Callback<InitResponse>{
            override fun onFailure(call: Call<InitResponse>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<InitResponse>?, response: Response<InitResponse>) {
                if (response.code() == 200){
                    val initResponse = response.body()!!
                    var continueToMainScreen = true
                    if (initResponse.activeProposal != null){
                        val intent = Intent(this@SplashActivity, ActiveProposalActivity::class.java)
                        intent.putExtra("PROPOSAL_ID", initResponse.activeProposal?.id)
                        startActivity(intent)
                        finish()
                        continueToMainScreen = false
                    }
                    if (initResponse.proposalToPay != null){
                        val intent = Intent(this@SplashActivity, CheckoutActivity::class.java)
                        val proposalJson = Gson().toJson(initResponse.proposalToPay!!.proposal)
                        intent.putExtra("PROPOSAL", proposalJson)
                        startActivity(intent)
                        finish()
                        continueToMainScreen = false
                    }
                    if (initResponse.places.size > 0)
                        savePlaces(initResponse.places)

                    if (initResponse.countries.size > 0)
                        saveCountries(initResponse.countries)

                    if (continueToMainScreen)
                        closeSplashScreen()
                }
            }
        })
    }

    fun getInterests(){
        api.getInterests().enqueue(object: Callback<ArrayList<Interest>>{
            override fun onFailure(call: Call<ArrayList<Interest>>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ArrayList<Interest>>?, response: Response<ArrayList<Interest>>) {
                if (response.isSuccessful){
                    db.Drop(Interest())
                    db.SaveInterests(response.body()!!)
                }
            }
        })
    }

    private fun saveCountries(countries: ArrayList<Country>){
        db.Drop(Country())
        db.SaveCountries(countries)

    }

    private fun savePlaces(places: ArrayList<Place>){
        db.Drop(Place())
        db.SavePlaces(places)
    }

    private fun closeSplashScreen(){
        if (AccountManager.isLoggedIn && AccountManager.isRegistrationCompleted)
            startActivity(Intent(this, MainActivity::class.java))

        else
            startActivity(Intent(this, RegistrationActivity::class.java))
        finish()
    }

}