package sk.dmsoft.cityguide.Commons

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import kotlinx.android.synthetic.main.proposal_item.view.*
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.R
import android.view.View

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
            city_image.load("https://zlavomat.sgcdn.cz/images/t/1280x640c/32/22/3222366-31a2f0.webp")
            guide_name.text = item.guideId
            start_date.text = item.start
            guide_photo.loadCircle("http://cityguide.dmsoft.sk/users/photo/${item.guideId}")

            setOnClickListener {

            }

            setOnLongClickListener {
                false
            }

        }
    }

}