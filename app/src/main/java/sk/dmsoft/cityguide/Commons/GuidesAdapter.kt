package sk.dmsoft.cityguide.Commons

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.guide_item.view.*
import sk.dmsoft.cityguide.Models.Guides.GuideListItem
import sk.dmsoft.cityguide.R

/**
 * Created by Daniel on 13. 3. 2018.
 */
class GuidesAdapter(val activity: Activity, val guides: ArrayList<GuideListItem>, val longClickListener: (GuideListItem, Int) -> Unit) : RecyclerView.Adapter<GuidesAdapter.ViewHolder>(){


    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(guides[position], longClickListener)

    override fun getItemCount(): Int = guides.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(activity, parent.inflate(R.layout.guide_item))
    }

    fun updateList(guides: ArrayList<GuideListItem>){
        this.guides.clear()
        this.guides.addAll(guides)
        notifyDataSetChanged()
    }


    class ViewHolder(val activity: Activity, itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(item: GuideListItem, listener: (GuideListItem, Int) -> Unit) = with(itemView) {
            guide_name.text = "${item.firstName} ${item.secondName}"
            guide_photo.loadCircle("http://cityguide.dmsoft.sk/users/photo/${item.id}")

            setOnClickListener {
                listener(item, adapterPosition)
            }

            setOnLongClickListener {
                false
            }

        }
    }

}