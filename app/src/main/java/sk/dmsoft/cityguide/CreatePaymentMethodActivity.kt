package sk.dmsoft.cityguide

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.dropin.DropInResult
import kotlinx.android.synthetic.main.activity_create_payment_method.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Models.CheckoutToken
import sk.dmsoft.cityguide.Models.CreatePaymentMethodRequest

class CreatePaymentMethodActivity : AppCompatActivity() {

    val BRAINTREE_REQUEST_CODE = 1
    lateinit var api: Api
    var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_payment_method)

        api = Api(this)

        add_payment_method.setOnClickListener { addPaymentMethod() }
        skip.setOnClickListener { finish() }
    }

    fun addPaymentMethod(){
        api.getCheckoutToken().enqueue(object : Callback<CheckoutToken> {
            override fun onFailure(call: Call<CheckoutToken>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<CheckoutToken>?, response: Response<CheckoutToken>) {
                if (response.code() == 200) {
                    token = response.body()!!.token
                    val dropInRequest = DropInRequest().clientToken(token)
                    startActivityForResult(dropInRequest.getIntent(this@CreatePaymentMethodActivity), BRAINTREE_REQUEST_CODE)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BRAINTREE_REQUEST_CODE){
            val results: DropInResult = data!!.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT)
            Log.e("BRAINTREE", results.toString())
            savePaymentMethod(results.paymentMethodNonce!!.nonce)
        }
    }

    fun savePaymentMethod(nonce: String){
        val model = CreatePaymentMethodRequest(nonce)
        api.savePaymentMethod(model).enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                AccountManager.isPaymentMethodSaved = true
                finish()
            }
        })
    }
}
