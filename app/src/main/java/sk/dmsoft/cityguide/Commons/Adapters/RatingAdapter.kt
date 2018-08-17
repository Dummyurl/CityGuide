package sk.dmsoft.cityguide.Commons.Adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.rating_view.view.*
import sk.dmsoft.cityguide.Commons.AppSettings
import sk.dmsoft.cityguide.Commons.inflate
import sk.dmsoft.cityguide.Commons.loadCircle
import sk.dmsoft.cityguide.Models.Rating
import sk.dmsoft.cityguide.R

class RatingAdapter(val ratings: ArrayList<Rating>) : RecyclerView.Adapter<RatingAdapter.ViewHolder>(){


    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(ratings[position])

    override fun getItemCount(): Int = ratings.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.rating_view))
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(item: Rating) = with(itemView) {
            user_photo.loadCircle("${AppSettings.apiUrl}/users/photo/${item.userId}")
            user_name.text = "Daniel"
            rating.reload(item.ratingStars.toInt())
            comment.text = item.comment
        }
    }

}