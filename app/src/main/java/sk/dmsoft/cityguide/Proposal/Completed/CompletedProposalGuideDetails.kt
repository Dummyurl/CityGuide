package sk.dmsoft.cityguide.Proposal.Completed

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_completed_proposal_guide_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Commons.CurrencyConverter
import sk.dmsoft.cityguide.MainActivity
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.R

class CompletedProposalGuideDetails : AppCompatActivity() {

    lateinit var api: Api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_proposal_guide_details)

        api = Api(this)

        val proposalId = intent.getIntExtra("PROPOSAL_ID", 0)
        initProposal(proposalId)

        back_to_main.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    fun initProposal(id: Int){
        api.getProposal(id).enqueue(object: Callback<Proposal> {
            override fun onFailure(call: Call<Proposal>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<Proposal>?, response: Response<Proposal>) {
                if (response.isSuccessful){
                    if (response.body()!!.perHourSalary < 0.1){
                        congratulations_text.text = "Thanks for your free tour!"
                        total_amount.visibility = View.GONE
                    }
                    else
                        total_amount.text = CurrencyConverter.convert(response.body()!!.payment!!.totalAmount)
                }
            }
        })
    }
}
