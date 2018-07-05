package sk.dmsoft.cityguide.Commons.Adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.completed_proposal_item.view.*
import org.joda.time.DateTime
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Commons.*
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.R

class CompletedProposalsAdapter(val activity: Activity, val proposals: ArrayList<Proposal>) : RecyclerView.Adapter<CompletedProposalsAdapter.ViewHolder>(){


    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(proposals[position])

    override fun getItemCount(): Int = proposals.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(activity, parent.inflate(R.layout.completed_proposal_item))
    }


    class ViewHolder(val activity: Activity, itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(item: Proposal) = with(itemView) {
            city_image.load("${AppSettings.apiUrl}places/photo/${item.placeId}", {})
            val db = DB(activity)
            if (item.user != null) {
                guide_name.text = "${item.user.firstName} ${item.user.secondName}"
                guide_photo.loadCircle("${AppSettings.apiUrl}users/photo/${item.user.id}")
            }
            proposal_info.text = "Paid ${CurrencyConverter.convert(item.payment!!.totalAmount)} ● Lasted ${item.payment!!.totalHours}h"
            val startDate = DateTime(item.start)
            proposal_date.text = "${startDate.dayOfMonth}. ${startDate.monthOfYear}."
            proposal_time.text = "Start at ${startDate.hourOfDay}:${startDate.minuteOfHour}"
            place_name.text = db.GetPlace(item.placeId).city
        }
    }

}