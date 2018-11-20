package sk.dmsoft.cityguide

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_checkout.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Commons.AppSettings
import sk.dmsoft.cityguide.Commons.CurrencyConverter
import sk.dmsoft.cityguide.Commons.loadCircle
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.Models.Rating
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

        val proposalId = intent.getIntExtra("PROPOSAL_ID", 0)
        if (proposalId == 0){
            val proposalJson = intent.getStringExtra("PROPOSAL")
            proposal = Gson().fromJson(proposalJson, Proposal::class.java)
            initProposal(proposal!!.id)
        }
        else {
            proposal = Proposal()
            proposal?.id = proposalId
            initProposal(proposalId)
        }

        checkout_btn.setOnClickListener {
            //api.getCheckoutToken().enqueue(object : Callback<CheckoutToken> {
            //    override fun onFailure(call: Call<CheckoutToken>?, t: Throwable?) {
            //        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            //    }
//
            //    override fun onResponse(call: Call<CheckoutToken>?, response: Response<CheckoutToken>) {
            //        if (response.code() == 200) {
            //            token = response.body()!!.token
            //            val dropInRequest = DropInRequest().clientToken(token)
            //            startActivityForResult(dropInRequest.getIntent(this@CheckoutActivity), BRAINTREE_REQUEST_CODE)
            //        }
            //    }
            //})

            createTransaction("")
        }
    }

    fun initProposal(id: Int){
        api.getProposal(id).enqueue(object: Callback<Proposal>{
            override fun onFailure(call: Call<Proposal>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<Proposal>?, response: Response<Proposal>) {
                if (response.isSuccessful){
                    proposal = response.body()
                    total_amount.text = CurrencyConverter.convert(proposal!!.payment!!.totalAmount)
                    total_hours.text = "${proposal!!.payment!!.totalHours}h"
                    user_name.text = "${proposal?.user?.firstName} ${proposal?.user?.secondName}"
                    user_photo.loadCircle("${AppSettings.apiUrl}/users/photo/${proposal?.user?.id}")
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == BRAINTREE_REQUEST_CODE){
        }
    }

    private fun createTransaction(nonce: String){
        var rating: Rating? = null

        if (ratingbar.rating > 0){
            rating = Rating()
            rating.ratingStars = ratingbar.rating.toDouble()
            rating.comment = comment.text.toString()
        }

        val transaction = TransactionRequest(proposal!!.id, rating)
        api.createTransaction(transaction).enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                Log.e("BRAINTREE", response?.body().toString())
                val intent = Intent(this@CheckoutActivity, MainActivity::class.java)
                startActivity(intent)
            }
        })
    }
}
