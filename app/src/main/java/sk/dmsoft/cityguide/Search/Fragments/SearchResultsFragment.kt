package sk.dmsoft.cityguide.Search.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_search_results, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun updateGuides(guides: ArrayList<GuideListItem>){

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
