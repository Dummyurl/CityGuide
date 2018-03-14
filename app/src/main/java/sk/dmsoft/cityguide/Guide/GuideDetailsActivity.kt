package sk.dmsoft.cityguide.Guide

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import sk.dmsoft.cityguide.R

import kotlinx.android.synthetic.main.activity_guide_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Models.Guides.GuideDetails

class GuideDetailsActivity : AppCompatActivity() {

    lateinit var api: Api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide_details)
        setSupportActionBar(toolbar)

        api = Api(this)

        val guideId = intent.extras.getString("GUIDE_ID", "")

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
    }

}
