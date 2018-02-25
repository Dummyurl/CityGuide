package sk.dmsoft.cityguide.Account.Registration

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import sk.dmsoft.cityguide.R

import kotlinx.android.synthetic.main.activity_registration.*
import android.support.v4.app.FragmentStatePagerAdapter
import kotlinx.android.synthetic.main.fragment_register_tourist.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Account.Login.LoginActivity
import sk.dmsoft.cityguide.Account.Registration.Step.RegisterTouristFragment
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Commons.EAccountType
import sk.dmsoft.cityguide.Models.AccessToken
import sk.dmsoft.cityguide.Models.Account.Registration


class RegistrationActivity : AppCompatActivity(), RegisterTouristFragment.OnRegistration {


    val touristSteps: ArrayList<Fragment> = ArrayList()
    val guideSteps: ArrayList<Fragment> = ArrayList()

    var registrationMode = EAccountType.tourist
    lateinit var api: Api

    override fun onRegistrationComplete(model: Registration) {
        api.registration(model).enqueue(object: Callback<AccessToken>{
            override fun onFailure(call: Call<AccessToken>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<AccessToken>?, response: Response<AccessToken>) {
                if (response.code() == 200) {
                    AccountManager.LogIn(response.body()!!.token)
                    AccountManager.registrationStep = 1
                    pager.setCurrentItem(1, true)
                }
            }

        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        setSupportActionBar(toolbar)

        api = Api(this)

        touristSteps.add(RegisterTouristFragment())
        pager.adapter = PagerAdapter(supportFragmentManager)
    }


    private inner class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return if (registrationMode == EAccountType.tourist) touristSteps[position] else guideSteps[position]
        }

        override fun getCount(): Int {
            return if (registrationMode == EAccountType.tourist) touristSteps.size else guideSteps.size
        }
    }

}
