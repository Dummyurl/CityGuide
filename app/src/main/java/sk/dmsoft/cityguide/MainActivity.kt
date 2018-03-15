package sk.dmsoft.cityguide

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Button
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Commons.ProposalsAdapter
import sk.dmsoft.cityguide.Commons.UnconfirmedProposalsAdapter
import sk.dmsoft.cityguide.Commons.addFragment
import sk.dmsoft.cityguide.Models.Account.RegisterFcm
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.Proposal.Fragments.EditProposalFragment
import sk.dmsoft.cityguide.Search.SearchActivity

class MainActivity : AppCompatActivity(), EditProposalFragment.OnProposalUpdate {

    lateinit var api: Api
    lateinit var proposalsAdapter: ProposalsAdapter
    lateinit var unconfirmedProposalsAdapter: UnconfirmedProposalsAdapter
    val editProposalFragment = EditProposalFragment()

    override fun onProposalChange(proposal: Proposal) {
        api.editProposal(proposal).enqueue(object: Callback<ResponseBody>{
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

        api = Api(this)

        logout.setOnClickListener {
            AccountManager.LogOut()
            finish()
        }

        open_search.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        addFragment(editProposalFragment, R.id.main)
    }

    override fun onResume() {
        super.onResume()
        reloadProposals()
    }

    private fun initProposals(proposals: ArrayList<Proposal>) {
        proposalsAdapter = ProposalsAdapter(this, proposals, {_, _ -> })
        proposals_recycler.setHasFixedSize(true)
        proposals_recycler.layoutManager = LinearLayoutManager(this)
        proposals_recycler.adapter = proposalsAdapter
    }

    private fun initUnconfirmedProposals(proposals: ArrayList<Proposal>){
        unconfirmedProposalsAdapter = UnconfirmedProposalsAdapter(this, proposals, {proposal, _ ->
            editProposalFragment.setProposal(proposal)
        })
        unconfirmed_proposals_list.setHasFixedSize(true)
        unconfirmed_proposals_list.layoutManager = LinearLayoutManager(this)
        unconfirmed_proposals_list.adapter = unconfirmedProposalsAdapter
    }

    private fun reloadProposals(){

        api.getProposals().enqueue(object : Callback<ArrayList<Proposal>>{
            override fun onFailure(call: Call<ArrayList<Proposal>>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
}
