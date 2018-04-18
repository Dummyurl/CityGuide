package sk.dmsoft.cityguide

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Commons.*
import sk.dmsoft.cityguide.Commons.Adapters.ProposalsAdapter
import sk.dmsoft.cityguide.Commons.Adapters.UnconfirmedProposalsAdapter
import sk.dmsoft.cityguide.Guide.GuideDetailsActivity
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.Models.Proposal.ProposalRequest
import sk.dmsoft.cityguide.Models.Proposal.ProposalState
import sk.dmsoft.cityguide.Proposal.ActiveProposalActivity
import sk.dmsoft.cityguide.Proposal.Fragments.EditProposalFragment
import sk.dmsoft.cityguide.Search.SearchActivity

class MainActivity : AppCompatActivity(), EditProposalFragment.OnProposalUpdate {

    lateinit var api: Api
    lateinit var proposalsAdapter: ProposalsAdapter
    lateinit var unconfirmedProposalsAdapter: UnconfirmedProposalsAdapter
    val editProposalFragment = EditProposalFragment()

    override fun onProposalChange(id: Int, proposal: ProposalRequest) {
        api.editProposal(id, proposal).enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if(response?.code() == 200){

                }
            }

        })
    }

    override fun onProposalConfirm(proposal: Proposal) {
        api.confirmProposal(proposal).enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if(response?.code() == 200){

                }
            }

        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val intent = Intent(this, GuideDetailsActivity::class.java)
        intent.putExtra("GUIDE_ID", "b2cce26e-c9e5-48ed-8b0a-ee43f360def3")
        startActivity(intent)

        api = Api(this)

        logout.setOnClickListener {
            AccountManager.LogOut()
            finish()
        }

        open_drawer.setOnClickListener {
            drawer_layout.openDrawer(Gravity.START)
        }

        swipe_refresh_confirmed.setColorSchemeResources(R.color.colorPrimary)
        swipe_refresh_unconfirmed.setColorSchemeResources(R.color.colorPrimary)

        swipe_refresh_confirmed.setOnRefreshListener {
            reloadProposals()
        }

        swipe_refresh_unconfirmed.setOnRefreshListener {
            reloadProposals()
        }

        if (AccountManager.accountType == EAccountType.guide)
            open_search.visibility = View.GONE
        else
            open_search.setOnClickListener {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
            }

        addFragment(editProposalFragment, R.id.bottom_sheet_wrapper)
    }

    override fun onBackPressed() {
        if (editProposalFragment.isSheetVisible)
            editProposalFragment.hide()
        else
            super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        reloadProposals()
    }

    private fun initProposals(proposals: ArrayList<Proposal>) {
        proposalsAdapter = ProposalsAdapter(this, proposals, { proposal, _ ->
            api.deleteProposal(proposal.id).enqueue(object: Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                }

            })
        })
        proposals_recycler.setHasFixedSize(true)
        proposals_recycler.layoutManager = LinearLayoutManager(this)
        proposals_recycler.adapter = proposalsAdapter
        checkActiveProposal(proposals)
    }

    private fun initUnconfirmedProposals(proposals: ArrayList<Proposal>){
        unconfirmedProposalsAdapter = UnconfirmedProposalsAdapter(this, proposals, { proposal, position ->
            editProposalFragment.setProposal(proposal)
            editProposalFragment.show()
            drawer_layout.closeDrawer(Gravity.START)
        })
        unconfirmed_proposals_list.setHasFixedSize(true)
        unconfirmed_proposals_list.layoutManager = LinearLayoutManager(this)
        unconfirmed_proposals_list.adapter = unconfirmedProposalsAdapter
        swipe_refresh_confirmed.isRefreshing = false
        swipe_refresh_unconfirmed.isRefreshing = false
    }

    private fun reloadProposals(){

        api.getProposals().enqueue(object : Callback<ArrayList<Proposal>>{
            override fun onFailure(call: Call<ArrayList<Proposal>>?, t: Throwable?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ArrayList<Proposal>>?, response: Response<ArrayList<Proposal>>?) {
                if(response?.code() == 200)
                    initProposals(response.body()!!)
            }

        })

        api.getUnconfirmedProposals().enqueue(object: Callback<ArrayList<Proposal>> {
            override fun onFailure(call: Call<ArrayList<Proposal>>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ArrayList<Proposal>>?, response: Response<ArrayList<Proposal>>?) {
                if (response?.code() == 200)
                    initUnconfirmedProposals(response.body()!!)
            }

        })
    }

    fun checkActiveProposal(proposals: ArrayList<Proposal>){
        val active = proposals.find { it.state == ProposalState.InProgress.value  }
        if (active != null){
            val intent = Intent(this, ActiveProposalActivity::class.java)
            intent.putExtra("PROPOSAL_ID", active.id)
            startActivity(intent)
        }
    }
}
