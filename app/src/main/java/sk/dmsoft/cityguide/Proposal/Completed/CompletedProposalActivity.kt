package sk.dmsoft.cityguide.Proposal.Completed

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_completed_proposal.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Commons.Adapters.CompletedProposalsAdapter
import sk.dmsoft.cityguide.Commons.addFragment
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.R

class CompletedProposalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_proposal)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "History"

        addFragment(ProposalHistoryFragment(), R.id.completed_fragment_holder)
    }


}
