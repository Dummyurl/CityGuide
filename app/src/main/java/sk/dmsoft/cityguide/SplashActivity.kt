package sk.dmsoft.cityguide

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Account.Login.LoginActivity
import sk.dmsoft.cityguide.Account.Registration.RegistrationActivity
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Models.Country
import sk.dmsoft.cityguide.Models.Place

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
            else {
                startActivity(Intent(this, RegistrationActivity::class.java))
                finish()
            }
        }
    }

}