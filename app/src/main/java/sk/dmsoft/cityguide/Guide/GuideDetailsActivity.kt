package sk.dmsoft.cityguide.Guide

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import sk.dmsoft.cityguide.R

import kotlinx.android.synthetic.main.activity_guide_details.*
import kotlinx.android.synthetic.main.create_proposal_bottom_sheet.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Models.Guides.GuideDetails
import sk.dmsoft.cityguide.Models.Proposal.ProposalRequest
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Commons.Adapters.RatingAdapter
import sk.dmsoft.cityguide.Commons.AppSettings
import sk.dmsoft.cityguide.Commons.load
import sk.dmsoft.cityguide.Commons.loadCircle
import java.util.*
import java.text.SimpleDateFormat
import android.view.ViewAnimationUtils
import android.animation.Animator
import android.graphics.Rect
import android.support.design.widget.BottomSheetBehavior
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import org.joda.time.LocalDateTime
import android.view.Window.ID_ANDROID_CONTENT
import android.app.Activity
import android.util.DisplayMetrics
import sk.dmsoft.cityguide.Commons.CurrencyConverter
import java.math.RoundingMode
import java.text.DecimalFormat


class GuideDetailsActivity : AppCompatActivity() {

    lateinit var api: Api
    lateinit var guideId: String
    lateinit var db: DB
    lateinit var startDateTimeFragment: SwitchDateTimeDialogFragment
    lateinit var endDateTimeFragment: SwitchDateTimeDialogFragment
    var guide: GuideDetails = GuideDetails()
    var proposalRequest = ProposalRequest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide_details)
        setSupportActionBar(toolbar)

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
                    guide = response.body()!!
                    guide_name.text = "${guide.firstName} ${guide.secondName}"
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
                    if(response?.code() == 200){
                        Snackbar.make(findViewById(android.R.id.content), "Proposal sended", Snackbar.LENGTH_LONG).show()
                        BottomSheetBehavior.from(bottom_sheet).state = BottomSheetBehavior.STATE_COLLAPSED
                        send_proposal.isEnabled = false
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

        if (intent.getBooleanExtra("DENY_BOOKING", false)){
            bottom_sheet.visibility = View.GONE
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
                val locale = SimpleDateFormat("dd.MM.yyyy HH:mm")
                val locale2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                proposalRequest.start = locale2.format(date)
                start_date.setText(locale.format(date))
                checkDateValidity()
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

        start_date.setOnClickListener { startDateTimeFragment.show(supportFragmentManager, "dialog_start") }

        endDateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                "Select date",
                "OK",
                "Cancel"
        )
        endDateTimeFragment.startAtCalendarView()
        endDateTimeFragment.set24HoursMode(true)

        endDateTimeFragment.setOnButtonClickListener(object: SwitchDateTimeDialogFragment.OnButtonClickListener{
            override fun onPositiveButtonClick(date: Date) {
                val locale = SimpleDateFormat("dd.MM.yyyy HH:mm")
                val locale2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                end_date.setText(locale.format(date))
                proposalRequest.end = locale2.format(date)
                checkDateValidity()
            }

            override fun onNegativeButtonClick(p0: Date?) {
            }

        })

        end_date.setOnFocusChangeListener { view, b ->
            if (b)
                endDateTimeFragment.show(supportFragmentManager, "dialog_end")
        }

        end_date.setOnClickListener { endDateTimeFragment.show(supportFragmentManager, "dialog_end") }

    }

    fun checkDateValidity(){
        try {
            val locale2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val actualDate = LocalDateTime().toDate()
            val startDate = locale2.parse(proposalRequest.start)
            when {
                locale2.parse(proposalRequest.end) < locale2.parse(proposalRequest.start) -> {
                    Snackbar.make(findViewById(android.R.id.content), "Start date has to be sooner than end date", Snackbar.LENGTH_LONG).show()
                    send_proposal.isEnabled = false
                }
                startDate < actualDate -> {
                    Snackbar.make(findViewById(android.R.id.content), "You can only plan to the future!", Snackbar.LENGTH_LONG).show()
                    send_proposal.isEnabled = false
                }
                else -> {
                    send_proposal.isEnabled = true
                    calcPrice()
                }
            }
        }
        catch (e: Exception){}
    }

    fun calcPrice(){
        val locale2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val startDate = locale2.parse(proposalRequest.start)
        val endDate = locale2.parse(proposalRequest.end)
        val millis = endDate.time - startDate.time

        val df = DecimalFormat("#")
        df.roundingMode = RoundingMode.CEILING

        val hours = df.format(millis / 1000 / 60 / 60.0).toInt()
        total_amount.text = CurrencyConverter.convert(hours * guide.guideInfo.salary)
    }

    fun fillDetails(){
        city_image.load("${AppSettings.apiUrl}/places/photo/${guide.place?.id}") {
            supportStartPostponedEnterTransition()
        }
        guide_about.text = guide.about

        user_name.text = "${guide.firstName} ${guide.secondName}"
        place_name.text = guide.place?.city
        total_amount.text = "${guide.guideInfo.salary}€"
        hourly_rate.text = "${guide.guideInfo.salary}€/h"
        book_user.text = "Book ${guide.firstName}"

        total_hours.text = guide.totalHours.toString()
        total_proposals.text = guide.totalProposals.toString()

        ratings_recycler.setHasFixedSize(true)
        val ratingsAdapter = RatingAdapter(guide.guideInfo.ratings)
        val linearLayout = LinearLayoutManager(this)
        ratings_recycler.layoutManager = linearLayout
        ratings_recycler.adapter = ratingsAdapter
        var sum = 0.0
        guide.guideInfo.ratings.forEach {
            sum += it.ratingStars
        }

        user_interests.text = guide.interestsString

        customRatingBar.reload((sum/ guide.guideInfo.ratings.size))

        user_rating.reload(if (sum == 0.0) (sum/ guide.guideInfo.ratings.size) else 3.0)

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
                val displayMetrics = DisplayMetrics();
                windowManager.defaultDisplay.getMetrics(displayMetrics);
                val screenHeight = displayMetrics.heightPixels
                bottom_sheet_content.layoutParams.height = screenHeight - statusBarHeight()
                bottom_sheet_content.requestLayout()
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



    fun statusBarHeight(): Int {
        val rectangle = Rect()
        val window = window
        window.decorView.getWindowVisibleDisplayFrame(rectangle)

        var softNavBar = 0
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            softNavBar = resources.getDimensionPixelSize(resourceId)
        }
        return rectangle.top + softNavBar
    }

}
