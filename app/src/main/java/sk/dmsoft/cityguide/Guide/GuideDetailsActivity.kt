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
import sk.dmsoft.cityguide.Models.Proposal.ProposalRequest
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Commons.AppSettings
import sk.dmsoft.cityguide.Commons.load
import sk.dmsoft.cityguide.Commons.loadCircle
import java.util.*


class GuideDetailsActivity : AppCompatActivity() {

    lateinit var api: Api
    lateinit var guideId: String
    lateinit var db: DB
    lateinit var startDateTimeFragment: SwitchDateTimeDialogFragment
    lateinit var endDateTimeFragment: SwitchDateTimeDialogFragment
    var guideInfo: GuideDetails = GuideDetails()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide_details)
        setSupportActionBar(toolbar)

        api = Api(this)
        db = DB(this)
        initDatePickers()

        guideId = intent.extras.getString("GUIDE_ID", "")


        api.guideDetails(guideId).enqueue(object: Callback<GuideDetails>{
            override fun onFailure(call: Call<GuideDetails>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<GuideDetails>?, response: Response<GuideDetails>?) {
                if (response?.code() == 200){
                    guideInfo = response.body()!!
                    guide_name.text = "${guideInfo.firstName} ${guideInfo.secondName}"
                    fillDetails()
                }
            }

        })

        send_proposal.setOnClickListener {
            val model = ProposalRequest()
            model.start = start_date.text.toString()
            model.end = end_date.text.toString()
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

    fun initDatePickers(){
        startDateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                "Select date",
                "OK",
                "Cancel"
        )
        startDateTimeFragment.startAtCalendarView()
        startDateTimeFragment.set24HoursMode(true)

        startDateTimeFragment.setOnButtonClickListener(object: SwitchDateTimeDialogFragment.OnButtonClickListener{
            override fun onPositiveButtonClick(date: Date) {
                start_date.setText(DateTime(date).toString())
            }

            override fun onNegativeButtonClick(p0: Date?) {
            }

        })

        start_date.setOnFocusChangeListener { view, b ->
            if (b)
                startDateTimeFragment.show(supportFragmentManager, "dialog_start")
        }

        endDateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                "Select date",
                "OK",
                "Cancel"
        )
        endDateTimeFragment.startAtCalendarView()
        endDateTimeFragment.set24HoursMode(true)

        endDateTimeFragment.setOnButtonClickListener(object: SwitchDateTimeDialogFragment.OnButtonClickListener{
            override fun onPositiveButtonClick(date: Date) {
                end_date.setText(DateTime(date).toString())
            }

            override fun onNegativeButtonClick(p0: Date?) {
            }

        })

        end_date.setOnFocusChangeListener { view, b ->
            if (b)
                endDateTimeFragment.show(supportFragmentManager, "dialog_end")
        }
    }

    fun fillDetails(){
        user_photo.loadCircle("${AppSettings.apiUrl}/users/photo/${guideInfo.id}")
        guide_photo.loadCircle("${AppSettings.apiUrl}/users/photo/${guideInfo.id}")
        city_image.load("${AppSettings.apiUrl}/places/photo/${guideInfo.place?.id}")

        user_rating.rating = 3f
        user_name.text = "${guideInfo.firstName} ${guideInfo.secondName}"
        place_name.text = guideInfo.place?.city
        total_amount.text = "${guideInfo.salary}€"
        hourly_rate.text = "${guideInfo.salary}€/h"
        book_user.text = "Book ${guideInfo.firstName}"
    }
}
