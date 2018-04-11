package sk.dmsoft.cityguide.Proposal

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import sk.dmsoft.cityguide.R

import kotlinx.android.synthetic.main.activity_active_proposal.*
import okhttp3.ResponseBody
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_17
import org.java_websocket.handshake.ServerHandshake
import org.joda.time.DateTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.CheckoutActivity
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Commons.AppSettings
import sk.dmsoft.cityguide.Commons.EAccountType
import sk.dmsoft.cityguide.Commons.loadCircle
import sk.dmsoft.cityguide.Models.Chat.Message
import sk.dmsoft.cityguide.Models.Chat.MessageType
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import java.lang.Exception
import java.net.URI
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.*

class ActiveProposalActivity : AppCompatActivity() {

    lateinit var api: Api
    var proposalId: Int = 0
    var proposal = Proposal()

    lateinit var wsClient: WebSocketClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_proposal)
        api = Api(this)

        connectWebsocket()

        proposalId = intent.getIntExtra("PROPOSAL_ID",0)

        if (proposalId == 0)
            finish()
        loadProposal()

        end_proposal.setOnClickListener {
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

    }


    fun loadProposal(){
        api.getProposal(proposalId).enqueue(object: Callback<Proposal>{
            override fun onFailure(call: Call<Proposal>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<Proposal>?, response: Response<Proposal>) {
                if (response.code() == 200){
                    proposal = response.body()!!
                    setUpInfo()
                }
            }
        })
    }

    fun setUpInfo(){
        user_photo.loadCircle("${AppSettings.apiUrl}users/photo/${proposal.user.id}")
        user_name.text = "${proposal.user.firstName} ${proposal.user.secondName}"
        per_hour_salary.text = "5€"
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        format.timeZone = TimeZone.getTimeZone("UTC")
        val spendTime = Date().time - format.parse(proposal.realStart).time
        spend_time.text = "${proposal.realStart}"

        chronometer2.format = "%s"
        chronometer2.base = SystemClock.elapsedRealtime() - spendTime
        chronometer2.start()
    }


    fun connectWebsocket(){
        wsClient = object: WebSocketClient(URI("ws://cityguide.dmsoft.sk/chat"), Draft_17(), mapOf("authorization" to "bearer ${AccountManager.accessToken}"), 1000){
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.e("Active", handshakedata.toString())
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.e("Active", reason)
            }

            override fun onMessage(messageJson: String?) {
                runOnUiThread({
                    Log.e("chat", messageJson)
                    val message: Message = Gson().fromJson(messageJson, Message::class.java)
                    when (message.MessageType) {
                       MessageType.ProposalEnd.value -> endProposal()
                    }
                })
            }

            override fun onError(ex: Exception?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        wsClient.connect()
    }

    fun endProposal(){
        if (AccountManager.accountType == EAccountType.tourist) {
            val intent = Intent(this@ActiveProposalActivity, CheckoutActivity::class.java)
            intent.putExtra("PROPOSAL", Gson().toJson(proposal))
            startActivity(intent)
        }
    }

}
