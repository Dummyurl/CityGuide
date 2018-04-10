package sk.dmsoft.cityguide.Commons.Adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.unconfirmed_proposal_item.view.*
import sk.dmsoft.cityguide.Commons.*
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.Models.Proposal.ProposalState
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
            if (item.user != null) {
                user_photo.loadCircle("${AppSettings.apiUrl}users/photo/${item.user.id}")
                user_name.text = "${item.user.firstName} ${item.user.secondName}"
            }
            when (item.state){
                ProposalState.New.value -> title.text = "New proposal"
                ProposalState.WaitingForGuide.value -> {
                    title.text = if (AccountManager.accountType == EAccountType.guide) "New time proposal" else "Waiting for guru"
                }
                ProposalState.WaitingForTourist.value -> {
                    title.text = if (AccountManager.accountType == EAccountType.tourist) "Guide proposed new time" else "Waiting for tourist"
                }
                else -> {
                    title.text = "Code: ${item.state}"
                }
            }


            if (item.canChange) {
                setOnClickListener {
                    listener(item, adapterPosition)
                }

                setOnLongClickListener {
                    false
                }
            }
            else {
                itemView.alpha = 0.5f
            }

        }
    }

}