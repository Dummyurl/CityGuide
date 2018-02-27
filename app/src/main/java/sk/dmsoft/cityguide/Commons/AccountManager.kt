package sk.dmsoft.cityguide.Commons

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import sk.dmsoft.cityguide.Models.AccessToken
import java.util.*

@SuppressLint("StaticFieldLeak")
/**
 * Created by Daniel on 22. 2. 2018.
 */
object AccountManager {

    private lateinit var _context: Context
    private lateinit var _sharedPreferences: SharedPreferences

    fun init(context: Context){
        _context = context
        _sharedPreferences = context.getSharedPreferences("CityGuide_app", Context.MODE_PRIVATE)
    }

    // Getters
    val isLoggedIn : Boolean
        get() = _sharedPreferences.getString("ACCESS_TOKEN", "") != ""

    val accessToken: String
        get() = _sharedPreferences.getString("ACCESS_TOKEN", "")

    val accountType: EAccountType?
        get() = EAccountType.fromInt(_sharedPreferences.getInt("ACCOUNT_TYPE", -1))

    val isRegistrationCompleted: Boolean
        get() {
            if (accountType == EAccountType.tourist)
                return _sharedPreferences.getInt("REGISTRATION_STEP", 0) == 3
            return _sharedPreferences.getInt("REGISTRATION_STEP", 0) == 4
        }

    var registrationStep: Int
        get(){
            return _sharedPreferences.getInt("REGISTRATION_STEP", 0)
        }
        set(value) {
            _sharedPreferences.edit().putInt("REGISTRATION_STEP", value).apply()
        }

    // Methods
    fun LogIn(accessToken: AccessToken){
        val editor = _sharedPreferences.edit()
        editor.putString("ACCESS_TOKEN", accessToken.token)
        editor.putString("USER_EMAIL", accessToken.email)
        editor.putInt("ACCOUNT_TYPE", accessToken.accountType)
        editor.putInt("REGISTRATION_STEP", accessToken.registrationStep)
        editor.apply()
    }

    fun LogOut(){
        val editor = _sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}