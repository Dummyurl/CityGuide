package sk.dmsoft.cityguide.Chat

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
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
import retrofit2.http.Url
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Chat.Fragments.ChatFragment
import sk.dmsoft.cityguide.Chat.Fragments.MapFragment
import sk.dmsoft.cityguide.Commons.*
import sk.dmsoft.cityguide.Commons.Services.LocationService
import sk.dmsoft.cityguide.Commons.Services.LocationUpdateCallback
import sk.dmsoft.cityguide.Models.Chat.Message
import sk.dmsoft.cityguide.Models.Chat.MessageType
import sk.dmsoft.cityguide.Models.Proposal.MeetingPoint
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.Proposal.ActiveProposalActivity
import java.lang.Exception
import java.net.URI
import java.net.URLDecoder

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)

        userId = intent.getStringExtra("USER_ID")
        proposalId = intent.getIntExtra("PROPOSAL_ID", 0)

        connectWebsocket()
        api = Api(this)

        chatFragment.init(proposalId, userId)
        mapFragment.init(proposalId, userId)

        hideMap()

        addFragment(chatFragment, R.id.chat_fragment_wrapper)
        addFragment(mapFragment, R.id.map_fragment_wrapper)


        getProposal()

        user_photo.loadCircle("${AppSettings.apiUrl}/users/photo/$userId")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        switchStartSet()

        set_meeting_point.setOnClickListener { changeMeetingPoint() }

        start_proposal.setOnClickListener{
            api.startProposal(proposalId).enqueue(object: Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    Snackbar.make(chat_wrapper, "Wait for other side", Snackbar.LENGTH_LONG).show()
                }
            })
        }
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
                    if (proposal?.meetingPoint != null) {
                        val meetingPointPosition = LatLng(proposal?.meetingPoint?.latitude!!, proposal?.meetingPoint?.longitude!!)
                        mapFragment.updateMeetingPointPosition(meetingPointPosition)
                        set_meeting_point.setOnClickListener {
                            showMap()
                        }
                        switchStartSet()
                    }
                }
            }
        })
    }

    fun connectWebsocket(){
        wsClient = object: WebSocketClient(URI("ws://cityguide.dmsoft.sk/chat"), Draft_17(), mapOf("authorization" to "bearer ${AccountManager.accessToken}"), 1000){
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.e(WS_TAG, handshakedata.toString())
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.e(WS_TAG, reason)
            }

            override fun onMessage(messageJson: String?) {
                runOnUiThread({
                    Log.e("chat", messageJson)
                    val message: Message = Gson().fromJson(messageJson, Message::class.java)
                    when (message.MessageType) {
                        MessageType.Message.value -> chatFragment.addMessage(message)
                        MessageType.Map.value -> {
                            if (message.From != AccountManager.userId) {
                                val position = Gson().fromJson(URLDecoder.decode(message.Text, "UTF-8"), LatLng::class.java)
                                mapFragment.updateUserPosition(position)
                            }
                        }
                        MessageType.MeetingPoint.value -> getMeetingPoint(Gson().fromJson(URLDecoder.decode(message.Text, "UTF-8"), MeetingPoint::class.java))
                        MessageType.ProposalStart.value -> startProposal()
                    }
                })
            }

            override fun onError(ex: Exception?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        wsClient.connect()
    }

    fun showMap(){
        isMapVisible = true
        map_fragment_wrapper.visibility = View.VISIBLE
    }

    override fun hideMap(){
        isMapVisible = false
        map_fragment_wrapper.visibility = View.GONE
    }

    fun cancelProposal(){
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

    fun changeMeetingPoint(){
        showMap()
        mapFragment.updateMode(MapMode.SetMeetingPoint)
    }

    fun goToMeetingPoint(){
        mapFragment.updateMode(MapMode.GoToMeetingPoint)
        showMap()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.chat_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId){
            R.id.cancel_proposal -> { cancelProposal() }
            R.id.change_meeting_point -> { changeMeetingPoint() }
            R.id.share_location -> { goToMeetingPoint() }
        }
        return true
    }

    fun switchStartSet(){
        if (proposal?.meetingPoint != null){
            start_proposal.visibility = View.VISIBLE
            set_meeting_point.visibility = View.GONE
        }
        else{
            start_proposal.visibility = View.GONE
            set_meeting_point.visibility = View.VISIBLE
        }
    }

    fun getMeetingPoint(meetingPoint: MeetingPoint){
        proposal?.meetingPoint = meetingPoint
        switchStartSet()
    }

    fun startProposal(){
        if (wsClient.connection.isOpen)
            wsClient.close()
        val intent = Intent(this@ChatActivity, ActiveProposalActivity::class.java)
        intent.putExtra("PROPOSAL_ID", proposalId)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (isMapVisible)
            hideMap()
        else
            super.onBackPressed()
    }
}
