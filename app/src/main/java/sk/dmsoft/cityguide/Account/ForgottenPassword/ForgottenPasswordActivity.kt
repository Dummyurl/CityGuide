package sk.dmsoft.cityguide.Account.ForgottenPassword

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import kotlinx.android.synthetic.main.activity_forgotten_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Commons.positiveButton
import sk.dmsoft.cityguide.Commons.showAlertDialog
import sk.dmsoft.cityguide.Models.Account.ForgottenPassword
import sk.dmsoft.cityguide.Models.Account.PasswordResetCode
import sk.dmsoft.cityguide.R

class ForgottenPasswordActivity : AppCompatActivity() {

    lateinit var api: Api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotten_password)
        api = Api(this)

        step1_next.setOnClickListener { goToStep2() }
    }

    fun goToStep2(){
        val model = ForgottenPassword(step1_email.text.toString())
        api.forgottenPassword(model).enqueue(object: Callback<PasswordResetCode>{
            override fun onFailure(call: Call<PasswordResetCode>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<PasswordResetCode>?, response: Response<PasswordResetCode>) {
                if (response.isSuccessful){
                    showAlertDialog {
                        setTitle("Reset code")
                        setMessage(response.body()!!.code)
                        positiveButton("Ok") {  }
                    }
                }
            }

        })
    }

    fun resetPassword(){

    }



}
