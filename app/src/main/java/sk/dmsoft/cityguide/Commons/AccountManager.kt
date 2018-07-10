package sk.dmsoft.cityguide.Commons

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.facebook.login.LoginManager
import sk.dmsoft.cityguide.Models.AccessToken
import java.util.*

@SuppressLint("StaticFieldLeak")
/**
 * Created by Daniel on 22. 2. 2018.
 */
object AccountManager {

    private lateinit var _context: Context
    private lateinit var _sharedPreferences: SharedPreferences
    private var paymentMethodSaved: Boolean = false

    fun init(context: Context){
        _context = context
        _sharedPreferences = context.getSharedPreferences("CityGuide_app", Context.MODE_PRIVATE)
    }

    // Getters
    val isLoggedIn : Boolean
        get() = _sharedPreferences.getString("ACCESS_TOKEN", "") != ""

    val accessToken: String
        get() = _sharedPreferences.getString("ACCESS_TOKEN", "")

    var accountType: EAccountType?
        get() = EAccountType.fromInt(_sharedPreferences.getInt("ACCOUNT_TYPE", -1))
        set(value) { _sharedPreferences.edit().putInt("ACCOUNT_TYPE", value!!.value).apply() }

    val isRegistrationCompleted: Boolean
        get() {
            if (accountType == EAccountType.tourist)
                return _sharedPreferences.getInt("REGISTRATION_STEP", 0) == 4
            return _sharedPreferences.getInt("REGISTRATION_STEP", 0) == 5
        }

    var registrationStep: Int
        get(){
            return _sharedPreferences.getInt("REGISTRATION_STEP", 0)
        }
        set(value) {
            _sharedPreferences.edit().putInt("REGISTRATION_STEP", value).commit()
        }

    var fcmTokenId: String
        get(){
            return _sharedPreferences.getString("FCM_TOKEN_ID", "")
        }
        set(value) {
            _sharedPreferences.edit().putString("FCM_TOKEN_ID", value).commit()
        }

    var isFcmRegistered: Boolean
        get(){
            return _sharedPreferences.getBoolean("FC_REGISTERED", false)
        }
        set(value) {
            _sharedPreferences.edit().putBoolean("FC_REGISTERED", value).commit()
        }

    val userId: String
        get() {
            return  _sharedPreferences.getString("ACCOUNT_ID", "")
        }

    var isPaymentMethodSaved: Boolean
        get() {
            return paymentMethodSaved
        }
        set(value){
            paymentMethodSaved = value
        }

    var currency: String
        get() {
            return _sharedPreferences.getString("CURRENCY", "EUR")
        }
        set(value){
            _sharedPreferences.edit().putString("CURRENCY", value).apply()
        }

    // Methods
    fun LogIn(accessToken: AccessToken){
        val editor = _sharedPreferences.edit()
        editor.putString("ACCESS_TOKEN", accessToken.token)
        editor.putString("USER_EMAIL", accessToken.email)
        editor.putInt("ACCOUNT_TYPE", accessToken.accountType)
        editor.putInt("REGISTRATION_STEP", accessToken.registrationStep)
        editor.putString("ACCOUNT_ID", accessToken.userId)
        editor.apply()
    }

    fun LogOut(){
        val editor = _sharedPreferences.edit()
        editor.clear()
        editor.apply()
        val loginManager = LoginManager.getInstance()
        loginManager.logOut()
    }
}