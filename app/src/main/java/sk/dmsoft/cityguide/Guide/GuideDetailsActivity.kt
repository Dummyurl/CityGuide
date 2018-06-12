package sk.dmsoft.cityguide.Guide

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.transition.TransitionListenerAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.transition.Transition
import android.util.Log
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
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Commons.Adapters.RatingAdapter
import sk.dmsoft.cityguide.Commons.AppSettings
import sk.dmsoft.cityguide.Commons.load
import sk.dmsoft.cityguide.Commons.loadCircle
import sk.dmsoft.cityguide.Models.Rating
import java.util.*
import sk.dmsoft.cityguide.R.id.appBarLayout
import kotlin.collections.ArrayList
import com.paypal.android.sdk.onetouch.core.metadata.l
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ofLocalizedDate
import java.time.format.FormatStyle
import android.view.ViewAnimationUtils
import android.animation.Animator
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.support.design.widget.BottomSheetBehavior
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator


class GuideDetailsActivity : AppCompatActivity() {

    lateinit var api: Api
    lateinit var guideId: String
    lateinit var db: DB
    lateinit var startDateTimeFragment: SwitchDateTimeDialogFragment
    lateinit var endDateTimeFragment: SwitchDateTimeDialogFragment
    var guideInfo: GuideDetails = GuideDetails()
    var proposalRequest = ProposalRequest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide_details)
        setSupportActionBar(toolbar)

        BottomSheetBehavior.from(bottom_sheet).state = BottomSheetBehavior.STATE_EXPANDED

        api = Api(this)
        db = DB(this)
        initDatePickers()

        guideId = intent.extras.getString("GUIDE_ID", "")

        supportPostponeEnterTransition()

        user_photo.loadCircle("${AppSettings.apiUrl}/users/photo/${guideId}")
        guide_photo.loadCircle("${AppSettings.apiUrl}/users/photo/${guideId}")


        book_user.setOnClickListener {
            BottomSheetBehavior.from(bottom_sheet).state = BottomSheetBehavior.STATE_EXPANDED
        }

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
            proposalRequest.guideId = guideId

            api.createProposal(proposalRequest).enqueue(object: Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {

                }

                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    if(response?.code() == 201){
                        Snackbar.make(findViewById(android.R.id.content), "Proposal sended", Snackbar.LENGTH_LONG).show()
                        BottomSheetBehavior.from(bottom_sheet).state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }

            })
        }

        appbar_layout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val offsetFactor = -verticalOffset.toFloat()  / appBarLayout.totalScrollRange.toFloat()
            Log.e("appbar", "$offsetFactor")
            guide_photo.scaleX = 1 - offsetFactor * 0.85f
            guide_photo.scaleY =  1 - offsetFactor * 0.85f
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
                val locale = SimpleDateFormat("dd.MM.YYYY HH:mm")
                val locale2 = SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss")
                proposalRequest.start = locale2.format(date)
                start_date.setText(locale.format(date))
            }

            override fun onNegativeButtonClick(p0: Date?) {
            }

        })

        start_date.showSoftInputOnFocus = false
        end_date.showSoftInputOnFocus = false
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
                val locale = SimpleDateFormat("dd.MM.YYYY HH:mm")
                val locale2 = SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss")
                end_date.setText(locale.format(date))
                proposalRequest.end = locale2.format(date)
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
        city_image.load("${AppSettings.apiUrl}/places/photo/${guideInfo.place?.id}", {
            supportStartPostponedEnterTransition()
        })
        guide_about.text = guideInfo.about

        user_rating.rating = 3f
        user_name.text = "${guideInfo.firstName} ${guideInfo.secondName}"
        place_name.text = guideInfo.place?.city
        total_amount.text = "${guideInfo.salary}€"
        hourly_rate.text = "${guideInfo.salary}€/h"
        book_user.text = "Book ${guideInfo.firstName}"

        total_hours.text = guideInfo.totalHours.toString()
        total_proposals.text = guideInfo.totalProposals.toString()

        ratings_recycler.setHasFixedSize(true)
        val ratingsAdapter = RatingAdapter(guideInfo.ratings)
        val linearLayout = LinearLayoutManager(this)
        ratings_recycler.layoutManager = linearLayout
        ratings_recycler.adapter = ratingsAdapter
        var sum = 0f
        guideInfo.ratings.forEach {
            sum += it.ratingStars
        }
        ratingBar.rating = sum/guideInfo.ratings.size
        user_rating.rating = if (sum == 0f) sum/guideInfo.ratings.size else 3f

        showRevealAnim()
    }


    fun showRevealAnim(){
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = 1000
        toolbar.animation = fadeIn
        toolbar.visibility = View.VISIBLE
        val x = nested_view.width / 2
        val y = (nested_view.top / 2) * -1

        val startRadius = 0
        val endRadius = Math.hypot(nested_view.width.toDouble(), nested_view.height.toDouble()).toInt()

        val anim = ViewAnimationUtils.createCircularReveal(nested_view, x, y, startRadius.toFloat(), endRadius.toFloat())

        nested_view.visibility = View.VISIBLE
        anim.duration = 800
        anim.start()
        anim.addListener(object: Animator.AnimatorListener{
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                BottomSheetBehavior.from(bottom_sheet).state = BottomSheetBehavior.STATE_COLLAPSED

            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }

        })
    }

    override fun onBackPressed() {
        val x = nested_view.width / 2
        val y = (nested_view.top / 2) * -1

        val startRadius = 0
        val endRadius = Math.hypot(nested_view.width.toDouble(), nested_view.height.toDouble()).toInt()

        val anim = ViewAnimationUtils.createCircularReveal(nested_view, x, y, endRadius.toFloat(), startRadius.toFloat())

        nested_view.visibility = View.VISIBLE
        anim.duration = 600
        anim.start()
        anim.addListener(object: Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}

            override fun onAnimationEnd(p0: Animator?) {
                nested_view.visibility = View.GONE
            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationStart(p0: Animator?) {}

        })

        toolbar.animate().alpha(0f).setDuration(300).withEndAction {
            super.onBackPressed()
        }
    }

}
