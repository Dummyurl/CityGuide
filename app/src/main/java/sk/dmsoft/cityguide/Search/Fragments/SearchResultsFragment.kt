package sk.dmsoft.cityguide.Search.Fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_search_results.*
import sk.dmsoft.cityguide.Commons.GuidesAdapter
import sk.dmsoft.cityguide.Commons.load
import sk.dmsoft.cityguide.Guide.GuideDetailsActivity
import sk.dmsoft.cityguide.Models.Guides.GuideListItem

import sk.dmsoft.cityguide.R

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



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater!!.inflate(R.layout.fragment_search_results, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        guidesAdapter = GuidesAdapter(activity, guides, { guide, position ->
            val intent = Intent(activity, GuideDetailsActivity::class.java)
            intent.putExtra("GUIDE_ID", guide.id)
            startActivity(intent)
        })

        guides_list.setHasFixedSize(true)
        guides_list.layoutManager = LinearLayoutManager(activity)
        guides_list.adapter = guidesAdapter

        city_image.load("https://zlavomat.sgcdn.cz/images/t/1280x640c/32/22/3222366-31a2f0.webp")
    }

    fun updateGuides(guides: ArrayList<GuideListItem>){
        this.guides = guides
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnSearchResultsInteraction) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
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
