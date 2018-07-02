package sk.dmsoft.cityguide.Chat.Fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_map.*
import sk.dmsoft.cityguide.Commons.Adapters.ChatAdapter
import sk.dmsoft.cityguide.Models.Chat.Message
import sk.dmsoft.cityguide.Models.Chat.MessageType

import sk.dmsoft.cityguide.R

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ChatFragment.OnChatInteractionListener] interface
 * to handle interaction events.
 */
class ChatFragment : Fragment() {

    private var mListener: OnChatInteractionListener? = null
    private var proposalId = 0
    private var userId = ""
    lateinit var chatAdapter: ChatAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatAdapter = ChatAdapter(activity!!, ArrayList())
        messages_list.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        messages_list.layoutManager = layoutManager
        messages_list.adapter = chatAdapter



        send_message.setOnClickListener {
            val message = Message()
            message.Text = message_text.text.toString()
            message.ConversationId = proposalId
            message.To = userId
            message.MessageType = MessageType.Message.value
            val messageJson = Gson().toJson(message)
            mListener?.onMessageSend(messageJson)
            message_text.setText("")
        }
    }

    fun init(pId: Int, uId: String){
        proposalId = pId
        userId = uId
    }

    fun addMessage(message: Message){
        chatAdapter.addMessage(message)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnChatInteractionListener) {
            mListener = context
        } else {
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnChatInteractionListener {
        // TODO: Update argument type and name
        fun onMessageSend(message: String)
    }
}// Required empty public constructor
