package sk.dmsoft.cityguide.Proposal

import android.os.Bundle
import android.os.SystemClock
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import sk.dmsoft.cityguide.R

import kotlinx.android.synthetic.main.activity_active_proposal.*
import okhttp3.ResponseBody
import org.joda.time.DateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
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
            api.endProposal(proposalId).enqueue(object: Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    //finish()
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
        user_photo.loadCircle("http://cityguide.dmsoft.sk/users/photo/${proposal.user.id}")
        user_name.text = "${proposal.user.firstName} ${proposal.user.secondName}"
        per_hour_salary.text = "20€"
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        var a = Date()
        val spendTime = Date().time - format.parse(proposal.realStart).time
        spend_time.text = "${spendTime}"

        chronometer2.format = "%s"
        chronometer2.base = spendTime
        chronometer2.start()
    }

}
