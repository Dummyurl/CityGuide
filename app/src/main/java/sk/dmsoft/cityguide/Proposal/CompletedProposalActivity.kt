package sk.dmsoft.cityguide.Proposal

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
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.R

class CompletedProposalActivity : AppCompatActivity() {

    lateinit var api : Api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_proposal)
        api = Api(this)

        api.getCompletedProposals(0).enqueue(object: Callback<ArrayList<Proposal>>{
            override fun onFailure(call: Call<ArrayList<Proposal>>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ArrayList<Proposal>>?, response: Response<ArrayList<Proposal>>?) {
                if (response?.code() == 200){
                    initRecyclerView(response.body()!!)
                }
            }
        })
    }

    fun initRecyclerView(proposals: ArrayList<Proposal>){
        completed_proposals_recycler.setHasFixedSize(true)
        val adapter = CompletedProposalsAdapter(this, proposals)
        completed_proposals_recycler.adapter = adapter
        val layout = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        completed_proposals_recycler.layoutManager = layout
        imageView.visibility = View.GONE
    }
}
