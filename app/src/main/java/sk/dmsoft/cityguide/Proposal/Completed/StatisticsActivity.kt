package sk.dmsoft.cityguide.Proposal.Completed

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_statistics.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Commons.CurrencyConverter
import sk.dmsoft.cityguide.Commons.addFragment
import sk.dmsoft.cityguide.Models.Stats
import sk.dmsoft.cityguide.R

class StatisticsActivity : AppCompatActivity() {

    lateinit var api: Api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        api = Api(this)

        addFragment(ProposalHistoryFragment(), R.id.history_fragment_holder)

        api.getStats().enqueue(object: Callback<Stats>{
            override fun onFailure(call: Call<Stats>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<Stats>?, response: Response<Stats>?) {
                if (response?.code() == 200)
                    showStats(response.body()!!)
            }
        })
    }

    fun showStats(stats: Stats){
        total_earned.text = CurrencyConverter.convert(stats.totalEarned)
        total_proposals.text = stats.totalProposals.toString()
    }
}
