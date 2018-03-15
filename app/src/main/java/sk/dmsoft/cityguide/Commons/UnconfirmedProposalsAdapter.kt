package sk.dmsoft.cityguide.Commons

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.unconfirmed_proposal_item.view.*
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.R

/**
 * Created by Daniel on 15. 3. 2018.
 */
class UnconfirmedProposalsAdapter (val activity: Activity, val proposals: ArrayList<Proposal>, val clickListener: (Proposal, Int) -> Unit) : RecyclerView.Adapter<UnconfirmedProposalsAdapter.ViewHolder>(){


    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(proposals[position], clickListener)

    override fun getItemCount(): Int = proposals.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(activity, parent.inflate(R.layout.unconfirmed_proposal_item))
    }


    class ViewHolder(val activity: Activity, itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(item: Proposal, listener: (Proposal, Int) -> Unit) = with(itemView) {
            user_photo.loadCircle("http://cityguide.dmsoft.sk/users/photo/${item.guideId}")

            setOnClickListener {
                listener(item, adapterPosition)
            }

            setOnLongClickListener {
                false
            }

        }
    }

}