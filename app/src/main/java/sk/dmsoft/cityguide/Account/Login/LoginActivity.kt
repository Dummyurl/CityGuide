package sk.dmsoft.cityguide.Account.Login

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import sk.dmsoft.cityguide.R

import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_registration.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Account.Registration.RegistrationActivity
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.MainActivity
import sk.dmsoft.cityguide.Models.AccessToken
import sk.dmsoft.cityguide.Models.Account.Login

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)

        val api = Api(this)

        login.setOnClickListener({
            val model = Login()
            model.email = email.text.toString()
            model.password = password.text.toString()

            api.login(model).enqueue(object: Callback<AccessToken>{
                override fun onFailure(call: Call<AccessToken>?, t: Throwable?) {
                    Snackbar.make(findViewById(android.R.id.content), "Bad login", Snackbar.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<AccessToken>?, response: Response<AccessToken>) {
                    if (response.code() != 200) {
                        Snackbar.make(findViewById(android.R.id.content), "Bad login", Snackbar.LENGTH_LONG).show()
                        return
                    }
                    AccountManager.LogIn(response.body()!!)

                    if (AccountManager.isRegistrationCompleted)
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    else
                        startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
                }

            })

        })
    }

}
