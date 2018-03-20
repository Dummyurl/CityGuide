package sk.dmsoft.cityguide.Chat.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import sk.dmsoft.cityguide.Chat.Message

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


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_chat, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun sendMessage() {
        if (mListener != null) {
            val message = Message()
            mListener!!.onMessageSend(message)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnChatInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnChatInteractionListener")
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
        fun onMessageSend(message: Message)
    }
}// Required empty public constructor
