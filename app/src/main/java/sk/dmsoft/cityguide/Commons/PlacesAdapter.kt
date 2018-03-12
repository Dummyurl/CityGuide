package sk.dmsoft.cityguide.Commons

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.place_item.view.*
import sk.dmsoft.cityguide.Models.Place
import sk.dmsoft.cityguide.R

class PlacesAdapter(val activity: Activity, var places: ArrayList<Place>, val clickListener: (Place, Int) -> Unit) : RecyclerView.Adapter<PlacesAdapter.ViewHolder>(){


    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(places[position], clickListener)

    override fun getItemCount(): Int = places.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(activity, parent.inflate(R.layout.place_item))
    }

    fun updateList(places: ArrayList<Place>){
        this.places.clear()
        this.places.addAll(places)
        notifyDataSetChanged()
    }


    class ViewHolder(val activity: Activity, itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(item: Place, listener: (Place, Int) -> Unit) = with(itemView) {

            city_name.text = item.city
            city_image.load("https://zlavomat.sgcdn.cz/images/t/1280x640c/32/22/3222366-31a2f0.webp")

            setOnClickListener {
                listener(item, adapterPosition)
            }

        }
    }

}