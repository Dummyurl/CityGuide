package sk.dmsoft.cityguide

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.dropin.DropInResult
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Models.CheckoutToken
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.Models.TransactionRequest

class CheckoutActivity : AppCompatActivity() {

    var proposal: Proposal? = null
    var token = ""
    lateinit var api: Api
    val BRAINTREE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        api = Api(this)

        val proposalJson = intent.getStringExtra("PROPOSAL")
        proposal = Gson().fromJson(proposalJson, Proposal::class.java)

        api.getCheckoutToken().enqueue(object: Callback<CheckoutToken>{
            override fun onFailure(call: Call<CheckoutToken>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<CheckoutToken>?, response: Response<CheckoutToken>) {
                if (response.code() == 200){
                    token = response.body()!!.token
                    val dropInRequest = DropInRequest().clientToken(token)
                    startActivityForResult(dropInRequest.getIntent(this@CheckoutActivity), BRAINTREE_REQUEST_CODE)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BRAINTREE_REQUEST_CODE){
            val results: DropInResult = data!!.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT)
            Log.e("BRAINTREE", results.toString())
            createTransaction(results.paymentMethodNonce!!.nonce)
        }
    }

    private fun createTransaction(nonce: String){
        val transaction = TransactionRequest(nonce, proposal!!.id)
        api.createTransaction(transaction).enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                Log.e("BRAINTREE", response?.body().toString())
            }
        })
    }
}
