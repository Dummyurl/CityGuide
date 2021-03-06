package sk.dmsoft.cityguide.Search.Fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_search_request.*
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Commons.GridSpacingItemDecoration
import sk.dmsoft.cityguide.Commons.Adapters.PlacesAdapter
import sk.dmsoft.cityguide.Models.Place
import sk.dmsoft.cityguide.Models.Search.SearchResults

import sk.dmsoft.cityguide.R
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SearchRequestFragment.OnSearchTextInserted] interface
 * to handle interaction events.
 */
class SearchRequestFragment : Fragment() {

    private var mListener: OnSearchTextInserted? = null
    lateinit var placesAdapter: PlacesAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var timer: Timer? = Timer()

        places_list.setHasFixedSize(true)
        places_list.layoutManager = GridLayoutManager(activity, 2)
        places_list.addItemDecoration(GridSpacingItemDecoration(2, 30, true))

        placesAdapter = PlacesAdapter(activity!!, DB(activity!!).GetPlaces(), { place: Place, position: Int, itemView: View ->
            mListener?.onCitySelected(place, itemView)
        })

        places_list.adapter = placesAdapter

        search_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                timer?.cancel()
            }

            override fun afterTextChanged(s: Editable) {
                timer = Timer()
                timer!!.schedule(object : TimerTask() {
                    override fun run() {
                        activity?.runOnUiThread {
                            mListener?.onSearch(search_text.text.toString())
                        }
                    }
                }, 500)
            }
        })
    }

    fun UpdateSearch(model: SearchResults){
        placesAdapter.updateList(model.countries.map {  it.places!!}.flatten() as ArrayList<Place>)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnSearchTextInserted) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnSearchTextInserted")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnSearchTextInserted {
        // TODO: Update argument type and name
        fun onSearch(searchText: String)
        fun onCitySelected(place: Place, itemView: View)
    }
}// Required empty public constructor
