package sk.dmsoft.cityguide.Commons.Adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.settings_item.view.*
import sk.dmsoft.cityguide.Commons.inflate
import sk.dmsoft.cityguide.R

class SettingsAdapter(val ratings: ArrayList<String>, val listener: (position: Int) -> Unit) : RecyclerView.Adapter<SettingsAdapter.ViewHolder>(){


    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(ratings[position], listener)

    override fun getItemCount(): Int = ratings.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.settings_item))
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(item: String, listener: (position: Int) -> Unit) = with(itemView) {
            setting_name.text = item
            setOnClickListener {
                listener(adapterPosition)
            }
        }
    }

}