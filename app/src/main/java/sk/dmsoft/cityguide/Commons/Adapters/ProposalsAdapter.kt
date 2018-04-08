package sk.dmsoft.cityguide.Commons.Adapters

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import kotlinx.android.synthetic.main.proposal_item.view.*
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.R
import android.view.View
import org.joda.time.DateTime
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Chat.ChatActivity
import sk.dmsoft.cityguide.Commons.inflate
import sk.dmsoft.cityguide.Commons.load
import sk.dmsoft.cityguide.Commons.loadCircle
import java.util.*

/**
 * Created by Daniel on 27. 2. 2018.
 */
class ProposalsAdapter(val activity: Activity, val proposals: ArrayList<Proposal>, val longClickListener: (Proposal, Int) -> Unit) : RecyclerView.Adapter<ProposalsAdapter.ViewHolder>(){


    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(proposals[position], longClickListener)

    override fun getItemCount(): Int = proposals.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(activity, parent.inflate(R.layout.proposal_item))
    }


    class ViewHolder(val activity: Activity, itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(item: Proposal, listener: (Proposal, Int) -> Unit) = with(itemView) {
            val db = DB(activity)
            city_image.load("http://cityguide.dmsoft.sk/places/photo/${item.placeId}")
            if (item.user != null) {
                guide_name.text = "${item.user.firstName} ${item.user.secondName}"
                val startDate = DateTime(item.start)
                proposal_date.text = "${startDate.dayOfMonth}. ${startDate.monthOfYear}."
                proposal_time.text = "Start at ${startDate.hourOfDay}:${startDate.minuteOfHour}"
                //place_name.text = db.GetPlace(item.placeId).city
                guide_photo.loadCircle("http://cityguide.dmsoft.sk/users/photo/${item.user.id}")
            }
            open_chat.setOnClickListener {
                val intent = Intent(activity, ChatActivity::class.java)
                intent.putExtra("PROPOSAL_ID", item.id)
                intent.putExtra("USER_ID", item.user.id)
                activity.startActivity(intent)
            }

            setOnClickListener {

            }

            setOnLongClickListener {
                false
            }

        }
    }

}