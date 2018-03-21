package sk.dmsoft.cityguide.Chat

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.gson.Gson
import sk.dmsoft.cityguide.R

import kotlinx.android.synthetic.main.activity_chat.*
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_17
import org.java_websocket.handshake.ServerHandshake
import sk.dmsoft.cityguide.Chat.Fragments.ChatFragment
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Commons.addFragment
import sk.dmsoft.cityguide.Models.Chat.Message
import sk.dmsoft.cityguide.Models.Chat.ReceivedMessage
import java.lang.Exception
import java.net.URI

class ChatActivity : AppCompatActivity(), ChatFragment.OnChatInteractionListener {
    override fun onMessageSend(message: String) {
        wsClient.send(message)
    }

    val WS_TAG = "WS"

    lateinit var wsClient: WebSocketClient
    val chatFragment: ChatFragment = ChatFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)
        connectWebsocket()

        addFragment(chatFragment, android.R.id.content)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStop() {
        super.onStop()
        wsClient.close()
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
                    Log.e("chat", message)
                    val message: ReceivedMessage = Gson().fromJson(message, ReceivedMessage::class.java)
                    chatFragment.addMessage(message)
                })
            }

            override fun onError(ex: Exception?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        wsClient.connect()
    }

}
