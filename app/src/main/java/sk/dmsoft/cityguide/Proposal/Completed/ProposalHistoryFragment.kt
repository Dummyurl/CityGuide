package sk.dmsoft.cityguide.Proposal.Completed


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_completed_proposal.*
import kotlinx.android.synthetic.main.fragment_proposal_history.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Commons.Adapters.CompletedProposalsAdapter
import sk.dmsoft.cityguide.Models.Proposal.Proposal

import sk.dmsoft.cityguide.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ProposalHistoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_proposal_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Api(activity).getCompletedProposals(0).enqueue(object: Callback<ArrayList<Proposal>> {
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
        val adapter = CompletedProposalsAdapter(activity!!, proposals)
        completed_proposals_recycler.adapter = adapter
        val layout = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
        completed_proposals_recycler.layoutManager = layout
        completed_proposals_recycler.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

    }

}
