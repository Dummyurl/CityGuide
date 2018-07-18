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

class CompletedProposalsAdapter(val activity: Activity, private val proposals: ArrayList<Proposal>) : RecyclerView.Adapter<CompletedProposalsAdapter.ViewHolder>(){


    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(proposals[position])

    override fun getItemCount(): Int = proposals.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(activity, parent.inflate(R.layout.completed_proposal_item))
    }

    fun update(newProposals: ArrayList<Proposal>){
        proposals.clear()
        proposals.addAll(newProposals)
        notifyDataSetChanged()
    }

    class ViewHolder(val activity: Activity, itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(item: Proposal) = with(itemView) {
            val db = DB(activity)
            proposal_info.text = "${item.user?.firstName} ${item.user?.secondName} ● Lasted ${item.payment!!.totalHours}h"
            val startDate = DateTime(item.start)
            date.text = "${startDate.dayOfMonth}. ${startDate.monthOfYear}."
            city_name.text = db.GetPlace(item.placeId).city
            total_amount.text = CurrencyConverter.convert(item.payment!!.totalAmount)
        }
    }

}