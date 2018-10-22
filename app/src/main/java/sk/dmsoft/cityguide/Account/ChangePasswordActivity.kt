package sk.dmsoft.cityguide.Account

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_change_password.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Models.Account.UpdatePassword
import sk.dmsoft.cityguide.Models.CustomErrors
import sk.dmsoft.cityguide.R

class ChangePasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        confirm_password_change.setOnClickListener { changePassword() }
    }

    fun changePassword(){
        val model = UpdatePassword()
        model.oldPassword = old_password.text.toString()
        model.newPassword = new_password.text.toString()
        model.newPasswordConfirm = new_password_confirm.text.toString()

        Api(this).changePassword(model).enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response?.code() == 200){
                    confirm_password_layout.error = ""
                    new_password_layout.error = ""
                    old_password_layout.error = ""
                    Snackbar.make(findViewById(android.R.id.content), "Password successfully changed", Snackbar.LENGTH_LONG).show()
                }
                else if (response?.code() == 400){
                    val errors = Gson().fromJson(response?.errorBody()!!.charStream() , CustomErrors::class.java).errors

                    if (errors.any{it.code == "PasswordMismatch"})
                        old_password_layout.error = errors.first { it.field == "Password" }.description
                    else
                        old_password_layout.error = ""

                    if (errors.any{it.field == "Password" && it.code != "PasswordMismatch"})
                        new_password_layout.error = errors.first { it.field == "Password" }.description
                    else
                        new_password_layout.error = ""

                    if (errors.any{it.field == "NewPasswordConfirm"})
                        confirm_password_layout.error = errors.first { it.field == "NewPasswordConfirm" }.description
                    else
                        confirm_password_layout.error = ""
                }
            }
        })
    }
}
