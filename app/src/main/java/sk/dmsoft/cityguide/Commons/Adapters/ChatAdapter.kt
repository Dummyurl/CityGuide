package sk.dmsoft.cityguide.Commons.Adapters

import android.app.Activity
import android.view.View
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import kotlinx.android.synthetic.main.received_message_item.view.*
import kotlinx.android.synthetic.main.sended_message_item.view.*
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Commons.inflate
import sk.dmsoft.cityguide.Commons.loadCircle
import sk.dmsoft.cityguide.Models.Chat.ReceivedMessage
import sk.dmsoft.cityguide.R
import java.net.URLDecoder

/**
 * Created by Daniel on 20. 3. 2018.
 */
class ChatAdapter (val activity: Activity, val messages: ArrayList<ReceivedMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val ITEM_TYPE_RECEIVED = 1
    private val ITEM_TYPE_SENDED = 2

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == ITEM_TYPE_SENDED)
            (holder as SendedViewHolder).bind(messages[position])
        else
            (holder as ReceivedViewHolder).bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_TYPE_SENDED)
            SendedViewHolder(activity, parent.inflate(R.layout.sended_message_item))
        else
            ReceivedViewHolder(activity, parent.inflate(R.layout.received_message_item))
    }

    override fun getItemViewType(position: Int): Int {
        if (messages[position].message.from == AccountManager.userId)
            return ITEM_TYPE_SENDED
        return ITEM_TYPE_RECEIVED
    }

    fun addMessage(message: ReceivedMessage){
        messages.add(0, message)
        notifyDataSetChanged()
        notifyItemInserted(messages.size -1)
    }

    class ReceivedViewHolder(val activity: Activity, itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(item: ReceivedMessage) = with(itemView) {
            user_photo.loadCircle("http://cityguide.dmsoft.sk/users/photo/${item.message.from}")
            received_message_text.text = item.message.text
        }
    }

    class SendedViewHolder(val activity: Activity, itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(item: ReceivedMessage) = with(itemView) {
            message_text.text = URLDecoder.decode(item.message.text, "UTF-8")
        }
    }

}