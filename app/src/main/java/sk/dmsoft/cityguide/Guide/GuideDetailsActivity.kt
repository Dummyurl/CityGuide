package sk.dmsoft.cityguide.Guide

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import sk.dmsoft.cityguide.R

import kotlinx.android.synthetic.main.activity_guide_details.*
import kotlinx.android.synthetic.main.create_proposal_bottom_sheet.*
import okhttp3.ResponseBody
import org.joda.time.DateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Models.Guides.GuideDetails
import sk.dmsoft.cityguide.Models.Proposal.Proposal

class GuideDetailsActivity : AppCompatActivity() {

    lateinit var api: Api
    lateinit var guideId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide_details)
        setSupportActionBar(toolbar)

        api = Api(this)

        guideId = intent.extras.getString("GUIDE_ID", "")

        api.guideDetails(guideId).enqueue(object: Callback<GuideDetails>{
            override fun onFailure(call: Call<GuideDetails>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<GuideDetails>?, response: Response<GuideDetails>?) {
                if (response?.code() == 200){
                    val guideInfo = response.body()!!
                    guide_name.text = "${guideInfo.firstName} ${guideInfo.secondName}"
                }
            }

        })

        send_proposal.setOnClickListener {
            val model = Proposal()
            model.start = DateTime(2018, 3, 15, 12, 0).toString()
            model.end = DateTime(2018, 3, 15, 13, 0).toString()
            model.realStart = DateTime().toString()
            model.realEnd = DateTime().toString()
            model.guideId = guideId

            api.createProposal(model).enqueue(object: Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {

                }

                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    if(response?.code() == 201){
                        Snackbar.make(it, "Proposal sended", Snackbar.LENGTH_LONG).show()
                    }
                }

            })
        }
    }

}
