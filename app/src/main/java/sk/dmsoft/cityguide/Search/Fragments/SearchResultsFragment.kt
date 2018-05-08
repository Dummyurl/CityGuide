package sk.dmsoft.cityguide.Search.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_search_results.*
import sk.dmsoft.cityguide.Commons.Adapters.GuidesAdapter
import sk.dmsoft.cityguide.Commons.AppSettings
import sk.dmsoft.cityguide.Commons.load
import sk.dmsoft.cityguide.Guide.GuideDetailsActivity
import sk.dmsoft.cityguide.Models.Guides.GuideListItem

import sk.dmsoft.cityguide.R
import android.R.attr.transitionName
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.util.Pair


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SearchResultsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class SearchResultsFragment : Fragment() {

    private var mListener: OnSearchResultsInteraction? = null
    lateinit var guidesAdapter: GuidesAdapter
    var guides: ArrayList<GuideListItem> = ArrayList()
    var placeId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_search_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        guidesAdapter = GuidesAdapter(activity!!, guides, { guide, position, itemView ->
            val intent = Intent(activity, GuideDetailsActivity::class.java)
            intent.putExtra("GUIDE_ID", guide.id)

            val userPhotoPair: Pair<View, String> = Pair.create(itemView, itemView.transitionName)
            val cityPhotoPair: Pair<View, String> = Pair.create(city_image, city_image.transitionName)

            val options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(activity as FragmentActivity, userPhotoPair, cityPhotoPair )

            ActivityCompat.startActivity(context!!, intent, options.toBundle())
        })

        guides_list.setHasFixedSize(true)
        guides_list.layoutManager = LinearLayoutManager(activity)
        guides_list.adapter = guidesAdapter
        guides_list.setItemViewCacheSize(20)
        guides_list.isDrawingCacheEnabled = true

        activity?.supportPostponeEnterTransition()
        city_image?.load("${AppSettings.apiUrl}/places/photo/$placeId", {
            activity?.supportStartPostponedEnterTransition()
        })
    }

    fun updateGuides(guides: ArrayList<GuideListItem>){
        this.guides = guides
    }

    fun updateImage(placeId: Int){
        this.placeId = placeId
        city_image?.load("${AppSettings.apiUrl}/places/photo/$placeId", {
            activity?.supportStartPostponedEnterTransition()
        })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnSearchResultsInteraction) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnChatInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnSearchResultsInteraction {
        // TODO: Update argument type and name
        fun onGuideSelected()
        fun onSearchUpdate()
    }
}// Required empty public constructor
