package sk.dmsoft.cityguide.Account.Registration

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import sk.dmsoft.cityguide.R

import kotlinx.android.synthetic.main.activity_registration.*
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import com.facebook.*
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
import sk.dmsoft.cityguide.Models.Guides.GuideDetails
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.fragment_register_tourist.*
import sk.dmsoft.cityguide.Account.External.Facebook
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.R.attr.data
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.paypal.android.sdk.onetouch.core.metadata.w
import com.google.android.gms.common.api.ApiException
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import sk.dmsoft.cityguide.Commons.addFragment
import sk.dmsoft.cityguide.Commons.removeFragment
import sk.dmsoft.cityguide.Models.CustomError


class RegistrationActivity : AppCompatActivity(),
        RegisterTouristFragment.OnRegistration,
        RegisterStep1Fragment.Step1Listener,
        RegisterStep2Fragment.Step2Listener,
        RegisterStep3Fragment.Step3Listener,
        Facebook.FacebookInterface,
        RegistrationMethodFragment.OnFragmentInteractionListener,
        SelectAccountTypeFragment.OnAccountTypeSelected,
        RegisterGuideInfoFragment.OnRegistrationGuideInfo{

    var guideMode = false
    var editMode = false

    val registerMethodFragment: RegistrationMethodFragment = RegistrationMethodFragment()
    val registerTouristFragment = RegisterTouristFragment()
    val step1Fragment = RegisterStep1Fragment()
    val step2Fragment = RegisterStep2Fragment()
    val step3Fragment = RegisterStep3Fragment()
    val accountTypeFragment = SelectAccountTypeFragment()
    val registerGuideInfoFragment = RegisterGuideInfoFragment()

    lateinit var callbackManager : CallbackManager

    override fun onSwitchToGuide() {
        AccountManager.accountType = EAccountType.guide
        registrationSteps.add(registerGuideInfoFragment)
        pager.adapter = PagerAdapter(supportFragmentManager)
        guideMode = true
        pager.setCurrentItem(2, true)

    }

    fun onSwitchToTourist(){
        AccountManager.accountType = EAccountType.tourist
        registrationSteps.remove(registrationSteps.last())
        pager.adapter = PagerAdapter(supportFragmentManager)
        guideMode = false
        pager.setCurrentItem(2, true)
    }

    val registrationSteps: ArrayList<Fragment> = ArrayList()
    val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    val REQUEST_FACEBOOK_LOGIN = 64206
    val GOOGLE_SIGN_IN = 2
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
                    AccountManager.registrationStep = 1

                    if (editMode) {
                        finish()
                        return
                    }

                    pager.setCurrentItem(1, true)
                }
                else if (response.code() == 400){
                    val errors = Gson().fromJson(response.errorBody()!!.charStream() , Array<CustomError>::class.java)
                    if (errors.any{it.field == "Email"})
                        email_layout.error = errors.first { it.field == "Email" }.description
                    else
                        email_layout.error = ""

                    if (errors.any{it.field == "Password"})
                        password_layout.error = errors.first { it.field == "Password" }.description
                    else
                        password_layout.error = ""

                    if (errors.any{it.field == "ConfirmPassword"})
                        confirm_password_layout.error = errors.first { it.field == "ConfirmPassword" }.description
                    else
                        confirm_password_layout.error = ""
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

                    if (editMode) {
                        finish()
                        return
                    }

                    pager.setCurrentItem(3, true)
                }
            }
        })
    }

    override fun onPhotoSelect() {
        //val intent = Intent(Intent.ACTION_GET_CONTENT)
        //intent.type = "image/*"
        //if (intent.resolveActivity(packageManager) != null) {
        //    startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        //}
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(1, 1)
                .setGuidelines(CropImageView.Guidelines.OFF)
                .start(this)
    }

    override fun onStep2Completed(model: Registration2) {
        api.registration2(model, profilePhotoUri).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.code() == 200) {
                    AccountManager.registrationStep = 3
                    if (editMode) {
                        finish()
                        return
                    }
                    pager.setCurrentItem(4, true)
                }
            }
        })
        step2Fragment.showProgressBar()
    }


    override fun onStep3Completed(model: Registration3) {
        api.registration3(model).enqueue((object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.code() == 200){
                    AccountManager.registrationStep = 4

                    if (editMode) {
                        finish()
                        return
                    }

                    if (AccountManager.accountType == EAccountType.guide)
                        pager.setCurrentItem(5, true)

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
                    AccountManager.registrationStep = 5

                    if (editMode) {
                        finish()
                        return
                    }

                    startActivity(Intent(this@RegistrationActivity, MainActivity::class.java))
                    finish()
                }
            }

        })
    }


    override fun AccountTypeSelected(accountType: EAccountType) {
        val model = SetAccountTypeModel()
        model.accountType = accountType
        api.setAccountType(model).enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    pager.setCurrentItem(2, true)
                    AccountManager.accountType = accountType

                    if (accountType == EAccountType.guide){
                        guideMode = true
                        onSwitchToGuide()
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_SELECT_IMAGE_IN_ALBUM -> {
                profilePhotoUri = data!!.data
                step2Fragment.loadPhoto(data.data.toString())
            }
            REQUEST_FACEBOOK_LOGIN -> callbackManager.onActivityResult(requestCode, resultCode, data)
            GOOGLE_SIGN_IN -> {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    profilePhotoUri = result.uri
                  step2Fragment.loadPhoto(result.uri.toString())
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                  val error = result.error
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        checkPermissions()
        api = Api(this)
        FacebookSdk.sdkInitialize(this)


        editMode = intent.getBooleanExtra("EDIT_MODE", false)

        if (editMode)
            fillFragments()

        addFragment(registerMethodFragment, android.R.id.content)

        registrationSteps.add(registerTouristFragment)
        registrationSteps.add(accountTypeFragment)
        registrationSteps.add(step1Fragment)
        registrationSteps.add(step2Fragment)
        registrationSteps.add(step3Fragment)

        if (AccountManager.accountType == EAccountType.guide)
            onSwitchToGuide()

        pager.adapter = PagerAdapter(supportFragmentManager)

        if (editMode)
            pager.setCurrentItem(intent.getIntExtra("REGISTRATION_STEP", 0) +1, false)
        else
            pager.setCurrentItem(if (AccountManager.registrationStep == 0) AccountManager.registrationStep else AccountManager.registrationStep +1, true)

        if (AccountManager.registrationStep > 0)
            removeFragment(registerMethodFragment, false)

        pager.setOnTouchListener { view, dragEvent ->
            true
        }
    }


    private inner class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return registrationSteps[position]
        }

        override fun getCount(): Int {
            return registrationSteps.size
        }
    }

    //override fun onBackPressed() {
    //    if (guideMode)
    //        onSwitchToTourist()
    //    else
    //        super.onBackPressed()
    //}


    fun checkPermissions(){
        val permissionArrayList = ArrayList<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissionArrayList.add(Manifest.permission.ACCESS_COARSE_LOCATION)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissionArrayList.add(Manifest.permission.READ_EXTERNAL_STORAGE)

        if(!permissionArrayList.isEmpty())
            ActivityCompat.requestPermissions(this,permissionArrayList.toTypedArray(),1)
    }

    fun fillFragments(){
        api.guideDetails(AccountManager.userId).enqueue(object: Callback<GuideDetails>{
            override fun onFailure(call: Call<GuideDetails>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<GuideDetails>?, response: Response<GuideDetails>) {
                if (response.code() == 200){
                    val model = response.body()!!
                    step1Fragment.init(model)
                    step2Fragment.init(model)
                    if (guideMode)
                        registerGuideInfoFragment.init(model)
                }
            }
        })
    }

    override fun registerGoogleCallback(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            FacebookCompleted(account.email!!, "")
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GOOGLE SIGNIN", "signInResult:failed code=" + e.statusCode)
        }

    }


    override fun registerEmailCallback() {
        removeFragment(registerMethodFragment, true)
    }

    override fun registerFacebookCallback(){
        callbackManager = CallbackManager.Factory.create()
            val fbManager = Facebook(this)
            fbManager.register(callbackManager)

    }

    override fun FacebookCompleted(email: String, accessToken: String){
        val model = RegisterExternal()
        model.email = email
        model.accountType = EAccountType.tourist
        model.fcmId = AccountManager.fcmTokenId

        api.facebookRegister(model).enqueue(object: Callback<AccessToken>{
            override fun onFailure(call: Call<AccessToken>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<AccessToken>?, response: Response<AccessToken>) {
                if (response.code() == 200){
                    AccountManager.LogIn(response.body()!!)
                    removeFragment(registerMethodFragment, true)
                    if (AccountManager.isRegistrationCompleted){
                        startActivity(Intent(this@RegistrationActivity, MainActivity::class.java))
                        finish()
                    }
                    else{
                        AccountManager.registrationStep = response.body()!!.registrationStep
                        pager.setCurrentItem(response.body()!!.registrationStep, true)
                    }

                }
            }
        })
    }

}
