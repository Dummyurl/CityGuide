package sk.dmsoft.cityguide.Chat

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import sk.dmsoft.cityguide.R

import kotlinx.android.synthetic.main.activity_chat.*
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_17
import org.java_websocket.handshake.ServerHandshake
import sk.dmsoft.cityguide.Chat.Fragments.ChatFragment
import sk.dmsoft.cityguide.Chat.Fragments.MapFragment
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Commons.Services.LocationService
import sk.dmsoft.cityguide.Commons.Services.LocationUpdateCallback
import sk.dmsoft.cityguide.Commons.addFragment
import sk.dmsoft.cityguide.Commons.replaceFragment
import sk.dmsoft.cityguide.Models.Chat.Message
import sk.dmsoft.cityguide.Models.Chat.MessageType
import java.lang.Exception
import java.net.URI

class ChatActivity : AppCompatActivity(), ChatFragment.OnChatInteractionListener, MapFragment.OnFragmentInteractionListener, LocationUpdateCallback {
    override fun onMessageSend(message: String) {
        wsClient.send(message)
    }

    lateinit var locationService: LocationService
    var serviceBounded = false

    val WS_TAG = "WS"
    var userId = ""
    var proposalId = 0

    lateinit var wsClient: WebSocketClient
    val chatFragment: ChatFragment = ChatFragment()

    val mapFragment = com.google.android.gms.maps.SupportMapFragment()
    var googleMap: GoogleMap? = null
    var myCircle: Circle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)
        connectWebsocket()

        mapFragment.getMapAsync {
            googleMap = it
            myCircle = googleMap?.addCircle(CircleOptions()
                    .center(LatLng(0.0, 0.0))
                    .fillColor(resources.getColor(R.color.colorPrimary))
                    .strokeWidth(0f)
                    .radius(10.0))
        }

        addFragment(chatFragment, R.id.chat_fragment_wrapper)

        userId = intent.getStringExtra("USER_ID")
        proposalId = intent.getIntExtra("PROPOSAL_ID", 0)

        chatFragment.init(proposalId, userId)

        bottom_menu.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.chat -> replaceFragment(chatFragment, R.id.chat_fragment_wrapper)
                R.id.map -> replaceFragment(mapFragment, R.id.chat_fragment_wrapper)
            }
            it.isChecked = true
            false
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // To-do("Logic when start location service")
        val serviceIntent = Intent(this, LocationService::class.java)
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

    }

    override fun onRestart() {
        super.onRestart()
        if (!wsClient.connection.isOpen)
            wsClient.connect()
    }

    override fun onStop() {
        super.onStop()
        //wsClient.close()
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
                    chatFragment.addMessage(message)
                })
            }

            override fun onError(ex: Exception?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        wsClient.connect()
    }




    val serviceConnection = object: ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // cast the IBinder and get MyService instance
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            serviceBounded = true
            locationService.setCallbacks(this@ChatActivity)
        }

        override fun onServiceDisconnected(arg0: ComponentName ) {
            serviceBounded = false
        }
    }

    override fun updateLocation(position: Location) {
        val myLocation = LatLng(position.latitude, position.longitude)
        val locationUpdateModel = Message()
        locationUpdateModel.ConversationId = proposalId
        locationUpdateModel.To = userId
        locationUpdateModel.Text = Gson().toJson(position)
        locationUpdateModel.MessageType = MessageType.Map.value
        if (wsClient.connection.isOpen)
            wsClient.send(Gson().toJson(locationUpdateModel))

        myCircle?.center = myLocation
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
        googleMap?.moveCamera(CameraUpdateFactory.zoomTo(15f))

        Log.e("Chat activity", Gson().toJson(locationUpdateModel))
    }

}
