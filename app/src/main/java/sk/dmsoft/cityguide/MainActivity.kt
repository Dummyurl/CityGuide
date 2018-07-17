package sk.dmsoft.cityguide

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.DragEvent
import android.view.Gravity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
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
import sk.dmsoft.cityguide.Proposal.Completed.StatisticsActivity
import sk.dmsoft.cityguide.Proposal.Fragments.EditProposalFragment
import sk.dmsoft.cityguide.Search.SearchActivity
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), EditProposalFragment.OnProposalUpdate {

    lateinit var api: Api
    lateinit var proposalsAdapter: ProposalsAdapter
    lateinit var unconfirmedProposalsAdapter: UnconfirmedProposalsAdapter
    val editProposalFragment = EditProposalFragment()
    var unreadNotifications = 0

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

        if (AccountManager.accountType == EAccountType.guide) {
            open_search.visibility = View.GONE
            open_statistics.visibility = View.VISIBLE
            open_statistics.setOnClickListener { startActivity(Intent(this, StatisticsActivity::class.java)) }
        }
        else
            open_search.setOnClickListener {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
            }

        addFragment(editProposalFragment, R.id.bottom_sheet_wrapper)
        if (AccountManager.accountType == EAccountType.tourist)
            checkPaymentMethod()

        open_profile.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        drawer_layout.addDrawerListener(object: DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerClosed(drawerView: View) {}

            override fun onDrawerOpened(drawerView: View) {
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                format.timeZone = TimeZone.getTimeZone("UTC")
                AccountManager.lastReadNotificationTime = DateTime(format.format(Date()))
                Log.e("Actual UTC time", AccountManager.lastReadNotificationTime.toString())
                unreadNotifications = 0
                setUnreadNotificationsText()
            }
        })
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
        proposalsAdapter = ProposalsAdapter(this, proposals) { proposal, _ ->
            api.deleteProposal(proposal.id).enqueue(object: Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                }

            })
        }
        proposals_recycler.setHasFixedSize(true)
        proposals_recycler.layoutManager = LinearLayoutManager(this)
        proposals_recycler.adapter = proposalsAdapter
        checkActiveProposal(proposals)

        if (proposals.size > 0) {
            proposals_recycler.visibility = View.VISIBLE
            empty_confirmed.visibility = View.GONE
        }
        else {
            proposals_recycler.visibility = View.GONE
            empty_confirmed.visibility = View.VISIBLE
        }
    }

    private fun initUnconfirmedProposals(proposals: ArrayList<Proposal>){
        unconfirmedProposalsAdapter = UnconfirmedProposalsAdapter(this, proposals) { proposal, position ->
            editProposalFragment.setProposal(proposal)
            editProposalFragment.show()
            drawer_layout.closeDrawer(Gravity.START)
        }
        unconfirmed_proposals_list.setHasFixedSize(true)
        unconfirmed_proposals_list.layoutManager = LinearLayoutManager(this)
        unconfirmed_proposals_list.adapter = unconfirmedProposalsAdapter
        swipe_refresh_confirmed.isRefreshing = false
        swipe_refresh_unconfirmed.isRefreshing = false

        if (proposals.size > 0) {
            unconfirmed_proposals_list.visibility = View.VISIBLE
            empty_unconfirmed.visibility = View.GONE
        }
        else {
            unconfirmed_proposals_list.visibility = View.GONE
            empty_unconfirmed.visibility = View.VISIBLE
        }
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
                if (response?.code() == 200) {
                    val proposals = response.body()!!
                    initUnconfirmedProposals(proposals)
                    unreadNotifications = proposals.count{ DateTime(it.lastChange) > AccountManager.lastReadNotificationTime}
                    Log.e("Unread notifications", unreadNotifications.toString())
                    setUnreadNotificationsText()
                }
            }

        })
    }

    fun checkActiveProposal(proposals: ArrayList<Proposal>){
        val active = proposals.find { it.state == ProposalState.InProgress.value  }
        if (active != null){
            val intent = Intent(this, ActiveProposalActivity::class.java)
            intent.putExtra("PROPOSAL_ID", active.id)
            //startActivity(intent)
        }
    }

    fun checkPaymentMethod(){
        if (!AccountManager.isPaymentMethodSaved){
            val intent = Intent(this, CreatePaymentMethodActivity::class.java)
            startActivity(intent)
        }
    }

    fun setUnreadNotificationsText(){
        if (unreadNotifications == 0)
            unread_notifications_count.visibility = View.GONE
        else {
            unread_notifications_count.visibility = View.VISIBLE
            unread_notifications_count.text = unreadNotifications.toString()
        }
    }
}
