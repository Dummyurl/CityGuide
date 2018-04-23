package sk.dmsoft.cityguide.Account.Registration

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import sk.dmsoft.cityguide.R

import kotlinx.android.synthetic.main.activity_registration.*
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.Log
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Account.Registration.Step.*
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Commons.EAccountType
import sk.dmsoft.cityguide.Models.AccessToken
import sk.dmsoft.cityguide.MainActivity
import sk.dmsoft.cityguide.Models.Account.*


class RegistrationActivity : AppCompatActivity(),
        RegisterTouristFragment.OnRegistration,
        RegisterGuideFragment.OnRegistrationGuide,
        RegisterStep1Fragment.Step1Listener,
        RegisterStep2Fragment.Step2Listener,
        RegisterStep3Fragment.Step3Listener,
        RegisterGuideInfoFragment.OnRegistrationGuideInfo{

    override fun onSwitchToGuide() {
        AccountManager.accountType = EAccountType.guide
        registrationSteps[0] = RegisterGuideFragment()
        registrationSteps.add(RegisterGuideInfoFragment())
        pager.adapter = PagerAdapter(supportFragmentManager)
    }

    val registrationSteps: ArrayList<Fragment> = ArrayList()
    val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    val step2Fragment: RegisterStep2Fragment = RegisterStep2Fragment()
    var profilePhotoUri: Uri = Uri.EMPTY

    lateinit var api: Api

    override fun onRegistrationComplete(model: Registration) {
        api.registration(model).enqueue(object: Callback<AccessToken>{
            override fun onFailure(call: Call<AccessToken>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<AccessToken>?, response: Response<AccessToken>) {
                if (response.code() == 200) {
                    AccountManager.LogIn(response.body()!!)
                    api = Api(this@RegistrationActivity)
                    AccountManager.registrationStep = 1
                    pager.setCurrentItem(1, true)
                }
            }

        })
    }

    override fun onRegistrationGuideComplete(model: Registration) {
        api.registration(model).enqueue(object: Callback<AccessToken>{
            override fun onFailure(call: Call<AccessToken>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<AccessToken>?, response: Response<AccessToken>) {
                if (response.code() == 200) {
                    AccountManager.LogIn(response.body()!!)
                    AccountManager.registrationStep = 1
                    pager.setCurrentItem(1, true)
                }
            }

        })
    }

    override fun onStep1Completed(model: Registration1) {
        api.registration1(model).enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.code() == 200){
                    AccountManager.registrationStep = 2
                    pager.setCurrentItem(2, true)
                }
            }
        })
    }

    override fun onPhotoSelect() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    override fun onStep2Completed(model: Registration2) {
        if (profilePhotoUri.toString().length > 5)
        api.registration2(model, profilePhotoUri).enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.code() == 200){
                    AccountManager.registrationStep = 3
                    pager.setCurrentItem(3, true)
                }
            }

        })
        else {
            AccountManager.registrationStep = 3

            if (AccountManager.accountType == EAccountType.guide)
                pager.setCurrentItem(3, true)

            else {
                startActivity(Intent(this@RegistrationActivity, MainActivity::class.java))
                finish()
            }
        }
    }


    override fun onStep3Completed(model: Registration3) {
        api.registration3(model).enqueue((object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.code() == 200){
                    AccountManager.registrationStep = 4
                    if (AccountManager.accountType == EAccountType.guide)
                        pager.setCurrentItem(4, true)

                    else {
                        startActivity(Intent(this@RegistrationActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }))
    }

    override fun onRegistrationGuideInfoCompleted(model: RegistrationGuideInfo) {
        api.registrationGuideInfo(model).enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response?.code() == 200) {
                    AccountManager.registrationStep = 4
                    startActivity(Intent(this@RegistrationActivity, MainActivity::class.java))
                    finish()
                }
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        profilePhotoUri = data.data
        step2Fragment.loadPhoto(data.data.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        api = Api(this)

        registrationSteps.add(RegisterTouristFragment())
        registrationSteps.add(RegisterStep1Fragment())
        registrationSteps.add(step2Fragment)
        registrationSteps.add(RegisterStep3Fragment())

        if (AccountManager.accountType == EAccountType.guide)
            registrationSteps.add(RegisterGuideInfoFragment())

        pager.adapter = PagerAdapter(supportFragmentManager)
        pager.setCurrentItem(AccountManager.registrationStep, true)
    }


    private inner class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return registrationSteps[position]
        }

        override fun getCount(): Int {
            return registrationSteps.size
        }
    }

}
