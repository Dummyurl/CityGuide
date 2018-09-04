package sk.dmsoft.cityguide.Chat

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Chronometer
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import sk.dmsoft.cityguide.R

import kotlinx.android.synthetic.main.activity_chat.*
import okhttp3.ResponseBody
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_17
import org.java_websocket.handshake.ServerHandshake
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Chat.Fragments.ChatFragment
import sk.dmsoft.cityguide.Chat.Fragments.MapFragment
import sk.dmsoft.cityguide.CheckoutActivity
import sk.dmsoft.cityguide.Commons.*
import sk.dmsoft.cityguide.Models.Chat.Message
import sk.dmsoft.cityguide.Models.Chat.MessageType
import sk.dmsoft.cityguide.Models.Proposal.MeetingPoint
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.Models.Proposal.ProposalState
import sk.dmsoft.cityguide.Proposal.Completed.CompletedProposalGuideDetails
import java.lang.Exception
import java.net.URI
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity(), ChatFragment.OnChatInteractionListener, MapFragment.OnFragmentInteractionListener {


    override fun setMeetingPoint(position: LatLng) {
        val model = MeetingPoint()
        model.latitude = position.latitude
        model.longitude = position.longitude
        api.setMeetingPoint(proposalId, model).enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                Log.e("Meeting point", response?.code().toString())
                hideMap()
                proposal?.meetingPoint = model
                switchStartSet()
            }
        })
    }


    override fun updateMyLocation(model: String) {
        if (wsClient.connection.isOpen)
            wsClient.send(model)
    }

    override fun onMessageSend(message: String) {
        if (wsClient.connection.isOpen)
            wsClient.send(message)
    }

    lateinit var api: Api

    val WS_TAG = "WS"
    var userId = ""
    var proposalId = 0
    var proposal: Proposal? = null

    var isMapVisible = false

    lateinit var wsClient: WebSocketClient
    val chatFragment: ChatFragment = ChatFragment()

    val mapFragment = MapFragment()

    var shareLocation = true

    var waitingForUser = false

    var unreadMessagesCount = 0

    lateinit var db: DB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)

        userId = intent.getStringExtra("USER_ID")
        proposalId = intent.getIntExtra("PROPOSAL_ID", 0)

        connectWebsocket()
        api = Api(this)
        db = DB(this)

        chatFragment.init(proposalId, userId)
        mapFragment.init(proposalId, userId)

        hideMap()

        addFragment(chatFragment, R.id.chat_fragment_wrapper)
        addFragment(mapFragment, R.id.map_fragment_wrapper)


        getProposal()

        //user_photo.loadCircle("${AppSettings.apiUrl}/users/photo/$userId")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        switchStartSet()

        set_meeting_point.setOnClickListener { changeMeetingPoint() }

        start_proposal.setOnClickListener{
            api.startProposal(proposalId).enqueue(object: Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    if (response?.code() == 200)
                        showWaitingtext()
                    else if (response?.code() == 401)
                        Snackbar.make(findViewById(android.R.id.content), "Bad time", Snackbar.LENGTH_LONG).show()
                }
            })
        }

        end_proposal.setOnClickListener { endProposal() }
        if (AccountManager.accountType == EAccountType.guide)
            end_proposal.visibility = View.GONE

        switchable_chat_map.setOnClickListener {
            if (isMapVisible)
                hideMap()
            else
                goToMeetingPoint()
        }

        waiting_text.text = "waiting for ${if (AccountManager.accountType == EAccountType.guide) "Tourist" else "Guide"}"
    }

    override fun onRestart() {
        super.onRestart()
        if (!wsClient.connection.isOpen)
            connectWebsocket()
    }

    override fun onStop() {
        super.onStop()
        wsClient.close()
    }

    fun getProposal(){
        api.getProposal(proposalId).enqueue(object: Callback<Proposal>{
            override fun onFailure(call: Call<Proposal>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<Proposal>?, response: Response<Proposal>?) {
                if (response?.code() == 200){
                    proposal = response.body()
                    place_name.text = db.GetPlace(proposal!!.placeId).city
                    user_name.text = "${proposal?.user?.firstName} ${proposal?.user?.secondName}"

                    if (proposal?.state == 5)
                        startProposal()

                    if (AccountManager.accountType == EAccountType.tourist && proposal?.touristStart!!)
                        showWaitingtext()

                    if (AccountManager.accountType == EAccountType.guide && proposal?.guideStart!!)
                        showWaitingtext()

                    if (proposal?.meetingPoint != null) {
                        val meetingPointPosition = LatLng(proposal?.meetingPoint?.latitude!!, proposal?.meetingPoint?.longitude!!)
                        mapFragment.updateMeetingPointPosition(meetingPointPosition)
                        set_meeting_point.setOnClickListener {
                            showMap()
                        }
                        switchStartSet()
                    }
                    else {
                        changeMeetingPoint()
                    }
                }
            }
        })
    }

    fun connectWebsocket(){
        wsClient = object: WebSocketClient(URI("ws://kaktus-app-service.azurewebsites.net/chat"), Draft_17(), mapOf("authorization" to "bearer ${AccountManager.accessToken}"), 1000){
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.e(WS_TAG, handshakedata.toString())
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.e(WS_TAG, reason)
            }

            override fun onMessage(messageJson: String?) {
                runOnUiThread {
                    Log.e("chat", messageJson)
                    val message: Message = Gson().fromJson(messageJson, Message::class.java)
                    when (message.MessageType) {
                        MessageType.Message.value -> {
                            chatFragment.addMessage(message)
                            addUnreadMessage()
                        }
                        MessageType.Map.value -> {
                            if (message.From != AccountManager.userId) {
                                val position = Gson().fromJson(URLDecoder.decode(message.Text, "UTF-8"), LatLng::class.java)
                                mapFragment.updateUserPosition(position)
                            }
                        }
                        MessageType.MeetingPoint.value -> {
                            val positionText = URLDecoder.decode(message.Text, "UTF-8")
                            getMeetingPoint(Gson().fromJson(positionText, MeetingPoint::class.java))
                            hideMap()
                            goToMeetingPoint()
                        }
                        MessageType.ProposalStart.value ->{ getProposal(); startProposal()}
                        MessageType.ProposalEnd.value -> goToCheckout()
                    }
                }
            }

            override fun onError(ex: Exception?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        wsClient.connect()
    }

    fun showMap(){
        mapFragment.updateMode()
        isMapVisible = true
        map_fragment_wrapper.visibility = View.VISIBLE
        switchable_chat_map.setImageResource(R.drawable.ic_message)
        unreadMessagesCount = 0
        updateUnreadMessages()
    }

    override fun hideMap(){
        isMapVisible = false
        map_fragment_wrapper.visibility = View.GONE
        switchable_chat_map.setImageResource(R.drawable.ic_map)
        updateUnreadMessages()
    }

    fun updateUnreadMessages(){
        if (unreadMessagesCount == 0 || !isMapVisible)
            unread_message_count.visibility = View.GONE
        if (unreadMessagesCount > 0 && isMapVisible)
            unread_message_count.visibility = View.VISIBLE

        unread_message_count.text = unreadMessagesCount.toString()
    }

    fun addUnreadMessage(){
        if (isMapVisible)
            unreadMessagesCount++
        updateUnreadMessages()
    }

    fun cancelProposal(){
        val builder = AlertDialog.Builder(this)
            builder.setTitle("Cancel proposal?")
                    .setMessage("Are you sure you want to cancel this proposal?")
                    .setNegativeButton("No", null)
                    .positiveButton ("Yes") {
                        api.deleteProposal(proposalId).enqueue(object: Callback<ResponseBody>{
                            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>) {
                                if(response.code() == 200)
                                    finish()
                            }
                        })
                    }
        builder.show()

    }

    fun changeMeetingPoint(){
        showMap()
        mapFragment.updateMode(MapMode.SetMeetingPoint)
    }

    fun goToMeetingPoint(){
        mapFragment.updateMode(MapMode.GoToMeetingPoint)
        showMap()
    }

    fun findUser(){
        mapFragment.updateMode(MapMode.GoToMeetingPoint)
        if (mapFragment.moveToUser())
            showMap()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        if (proposal?.state == ProposalState.InProgress.value)
            menuInflater.inflate(R.menu.chat_menu_active, menu)
        else
            menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId){
            R.id.cancel_proposal -> { cancelProposal() }
            //R.id.share_location -> { goToMeetingPoint() }
            R.id.find_user -> { findUser() }
            R.id.show_map -> {
                if (isMapVisible) {
                    hideMap()
                    p0.icon = getDrawable(R.drawable.ic_map)
                }
                else{
                    showMap()
                    p0.icon = getDrawable(R.drawable.ic_chat)
                }
            }
        }
        return true
    }

    fun switchStartSet(){
        if (!waitingForUser) {
            if (proposal?.meetingPoint != null) {
                start_proposal.visibility = View.VISIBLE
                set_meeting_point.visibility = View.GONE
            } else {
                start_proposal.visibility = View.GONE
                set_meeting_point.visibility = View.VISIBLE
            }
        }
    }

    fun getMeetingPoint(meetingPoint: MeetingPoint){
        proposal?.meetingPoint = meetingPoint
        mapFragment.updateMeetingPointPosition(LatLng(meetingPoint.latitude, meetingPoint.longitude))
        switchStartSet()
    }

    fun startProposal(){
        // 0x0000FF00
        invalidateOptionsMenu()
        mapFragment.hideChangeMeetingPoint()
        toolbar.setBackgroundColor(0xff00C853.toInt())
        window.statusBarColor = 0xff00C853.toInt()
        waiting_toolbar_layout.visibility = View.GONE
        active_proposal.visibility = View.VISIBLE
        mapFragment.hideChangeMeetingPoint()

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        format.timeZone = TimeZone.getTimeZone("UTC")
        val spendTime = proposal!!.actualTime - proposal!!.realStartTimeSpan

        chronometer2.format = "%s"
        chronometer2.base = (SystemClock.elapsedRealtime()) - (spendTime *1000)

        chronometer2.setOnChronometerTickListener {
            val time = SystemClock.elapsedRealtime() - it.getBase()
            val h = (time / 3600000).toInt()
            val m = (time - h * 3600000).toInt() / 60000
            var hh = if (h == 1) "$h hour " else h.toString() + " hours "
            val mm = if (m == 1) "$m minute" else m.toString() + " minutes"

            if (h < 1)
                hh = ""

            it.text = "$hh$mm"

            per_hour.text = "${CurrencyConverter.convert(proposal!!.perHourSalary * (h +1))}"
        }

        chronometer2.start()

        per_hour.text = "${CurrencyConverter.convert(proposal!!.perHourSalary)}"
    }

    fun endProposal(){
        api.endProposal(proposalId).enqueue(object: Callback<Proposal>{
            override fun onFailure(call: Call<Proposal>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<Proposal>?, response: Response<Proposal>) {
                if (response.code() == 200){
                    proposal = response.body()!!

                }
            }
        })
    }

    fun goToCheckout(){
        if (AccountManager.accountType == EAccountType.tourist) {
            val intent = Intent(this@ChatActivity, CheckoutActivity::class.java)
            intent.putExtra("PROPOSAL_ID", proposalId)
            startActivity(intent)
        }
        else {
            val intent = Intent(this, CompletedProposalGuideDetails::class.java)
            intent.putExtra("PROPOSAL_ID", proposalId)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        if (isMapVisible)
            hideMap()
        else
            super.onBackPressed()
    }

    fun showWaitingtext(){
        waiting_text.visibility = View.VISIBLE
        start_proposal.visibility = View.GONE
        set_meeting_point.visibility = View.GONE
        waitingForUser = true
    }
}
