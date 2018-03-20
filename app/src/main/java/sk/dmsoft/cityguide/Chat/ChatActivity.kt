package sk.dmsoft.cityguide.Chat

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.gson.Gson
import sk.dmsoft.cityguide.R

import kotlinx.android.synthetic.main.activity_chat.*
import okhttp3.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_17
import org.java_websocket.handshake.ServerHandshake
import sk.dmsoft.cityguide.Commons.AccountManager
import java.lang.Exception
import java.net.URI

class ChatActivity : AppCompatActivity() {
    val WS_TAG = "WS"

    lateinit var wsClient: WebSocketClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)
        connectWebsocket()

        send_message.setOnClickListener {
            val message = Message()
            message.text = message_text.text.toString()
            message.conversationId = "1"
            val messageJson = Gson().toJson(message)
            wsClient.send(messageJson)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun connectWebsocket(){
        wsClient = object: WebSocketClient(URI("ws://cityguide.dmsoft.sk/chat"), Draft_17(), mapOf("authorization" to "bearer ${AccountManager.accessToken}"), 1000){
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.e(WS_TAG, handshakedata.toString())
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.e(WS_TAG, reason)
            }

            override fun onMessage(message: String?) {
                runOnUiThread({
                    received_msg.text = "${received_msg.text} \r\n ${message}"
                })
            }

            override fun onError(ex: Exception?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        wsClient.connect()
    }

}
