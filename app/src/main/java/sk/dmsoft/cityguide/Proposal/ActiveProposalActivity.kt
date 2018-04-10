package sk.dmsoft.cityguide.Proposal

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import sk.dmsoft.cityguide.R

import kotlinx.android.synthetic.main.activity_active_proposal.*
import okhttp3.ResponseBody
import org.joda.time.DateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.CheckoutActivity
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Commons.AppSettings
import sk.dmsoft.cityguide.Commons.EAccountType
import sk.dmsoft.cityguide.Commons.loadCircle
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import java.text.SimpleDateFormat
import java.util.*

class ActiveProposalActivity : AppCompatActivity() {

    lateinit var api: Api
    var proposalId: Int = 0
    var proposal = Proposal()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_proposal)
        api = Api(this)

        proposalId = intent.getIntExtra("PROPOSAL_ID",0)

        if (proposalId == 0)
            finish()
        loadProposal()

        end_proposal.setOnClickListener {
            api.endProposal(proposalId).enqueue(object: Callback<Proposal>{
                override fun onFailure(call: Call<Proposal>?, t: Throwable?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onResponse(call: Call<Proposal>?, response: Response<Proposal>) {
                    if (response.code() == 200){
                        proposal = response.body()!!
                        if (AccountManager.accountType == EAccountType.tourist) {
                            val intent = Intent(this@ActiveProposalActivity, CheckoutActivity::class.java)
                            intent.putExtra("PROPOSAL", Gson().toJson(proposal))
                            startActivity(intent)
                        }
                    }
                }
            })
        }

    }


    fun loadProposal(){
        api.getProposal(proposalId).enqueue(object: Callback<Proposal>{
            override fun onFailure(call: Call<Proposal>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<Proposal>?, response: Response<Proposal>) {
                if (response.code() == 200){
                    proposal = response.body()!!
                    setUpInfo()
                }
            }
        })
    }

    fun setUpInfo(){
        user_photo.loadCircle("${AppSettings.apiUrl}users/photo/${proposal.user.id}")
        user_name.text = "${proposal.user.firstName} ${proposal.user.secondName}"
        per_hour_salary.text = "5€"
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        format.timeZone = TimeZone.getTimeZone("UTC")
        val spendTime = Date().time - format.parse(proposal.realStart).time
        spend_time.text = "${proposal.realStart}"

        chronometer2.format = "%s"
        chronometer2.base = SystemClock.elapsedRealtime() - spendTime
        chronometer2.start()
    }

}
