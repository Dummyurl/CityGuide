package sk.dmsoft.cityguide.Proposal.Completed

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment
import kotlinx.android.synthetic.main.activity_statistics.*
import org.joda.time.DateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Commons.Adapters.CompletedProposalsAdapter
import sk.dmsoft.cityguide.Commons.CurrencyConverter
import sk.dmsoft.cityguide.Commons.addFragment
import sk.dmsoft.cityguide.Models.Proposal.CompletedProposal
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.Models.Stats
import sk.dmsoft.cityguide.Models.StatsRequest
import sk.dmsoft.cityguide.R
import java.text.SimpleDateFormat
import java.util.*

class StatisticsActivity : AppCompatActivity() {

    lateinit var api: Api
    val statsRequest = StatsRequest()
    lateinit var adapter: CompletedProposalsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        api = Api(this)

        api.getStats().enqueue(object: Callback<Stats>{
            override fun onFailure(call: Call<Stats>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<Stats>?, response: Response<Stats>?) {
                if (response?.code() == 200)
                    showStats(response.body()!!)
            }
        })

        api.getCompletedProposals(0).enqueue(object: Callback<ArrayList<CompletedProposal>> {
            override fun onFailure(call: Call<ArrayList<CompletedProposal>>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ArrayList<CompletedProposal>>?, response: Response<ArrayList<CompletedProposal>>?) {
                if (response?.code() == 200){
                    initRecyclerView(response.body()!!)
                }
            }
        })

        initDatePickers()
    }

    fun showStats(stats: Stats){
        total_earned.text = CurrencyConverter.convert(stats.totalEarned)
        total_proposals.text = stats.totalProposals.toString()
    }


    fun initRecyclerView(proposals: ArrayList<CompletedProposal>){
        completed_proposals_recycler.setHasFixedSize(true)
        adapter = CompletedProposalsAdapter(this, proposals)
        completed_proposals_recycler.adapter = adapter
        val layout = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        completed_proposals_recycler.layoutManager = layout
        completed_proposals_recycler.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val locale = SimpleDateFormat("dd.MM.YYYY HH:mm")
        val mappedDates = proposals.map { dateFormat.parse(it.proposal.realStart) }
        date_from.setText(locale.format(mappedDates.min()))
        date_to.setText(locale.format(Date()))
        statsRequest.from = dateFormat.format(mappedDates.min())
        statsRequest.to = dateFormat.format(Date())
    }

    fun updateRecyclerView(proposals: ArrayList<CompletedProposal>){
        adapter.update(proposals)
    }

    fun initDatePickers(){
        val startDateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
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
                date_from.setText(locale.format(date))
                statsRequest.from = locale2.format(date)
                updateStats()
            }

            override fun onNegativeButtonClick(p0: Date?) {
            }

        })
        date_from.setOnFocusChangeListener { view, b ->
            if (b)
                startDateTimeFragment.show(this.supportFragmentManager, "dialog_start")
        }

        date_from.setOnClickListener {
            startDateTimeFragment.show(this.supportFragmentManager, "dialog_start")
        }

        val endDateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
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
                date_to.setText(locale.format(date))
                statsRequest.to = locale2.format(date)
                updateStats()
            }

            override fun onNegativeButtonClick(p0: Date?) {
            }

        })
        date_to.setOnFocusChangeListener { view, b ->
            if (b)
                endDateTimeFragment.show(this.supportFragmentManager, "dialog_start")
        }

        date_to.setOnClickListener {
            endDateTimeFragment.show(this.supportFragmentManager, "dialog_start")
        }
    }

    fun updateStats(){
        api.getSpecifiedStats(statsRequest).enqueue(object: Callback<Stats>{
            override fun onFailure(call: Call<Stats>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<Stats>?, response: Response<Stats>?) {
                if (response?.code() == 200)
                    showStats(response.body()!!)
            }
        })

        api.getSpecifiedCompletedProposals(0, statsRequest).enqueue(object: Callback<ArrayList<CompletedProposal>> {
            override fun onFailure(call: Call<ArrayList<CompletedProposal>>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ArrayList<CompletedProposal>>?, response: Response<ArrayList<CompletedProposal>>?) {
                if (response?.code() == 200){
                    updateRecyclerView(response.body()!!)
                }
            }
        })
    }
}
