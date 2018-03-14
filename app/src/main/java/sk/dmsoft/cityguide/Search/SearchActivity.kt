package sk.dmsoft.cityguide.Search

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Commons.addFragment
import sk.dmsoft.cityguide.Commons.replaceFragment
import sk.dmsoft.cityguide.Models.Guides.GuideListItem
import sk.dmsoft.cityguide.Models.Place
import sk.dmsoft.cityguide.Models.Search.SearchInCity
import sk.dmsoft.cityguide.Models.Search.SearchRequest
import sk.dmsoft.cityguide.Models.Search.SearchResluts
import sk.dmsoft.cityguide.R
import sk.dmsoft.cityguide.Search.Fragments.SearchRequestFragment
import sk.dmsoft.cityguide.Search.Fragments.SearchResultsFragment

class SearchActivity : AppCompatActivity(), SearchRequestFragment.OnSearchTextInserted, SearchResultsFragment.OnSearchResultsInteraction {

    lateinit var api: Api
    val searchRequestFragment = SearchRequestFragment()
    val searchResultsFragment = SearchResultsFragment()
    lateinit var db: DB

    override fun onGuideSelected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSearchUpdate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCitySelected(place: Place) {
        val model = SearchInCity()
        model.placeId = place.id
        api.searchInCity(model).enqueue(object: Callback<ArrayList<GuideListItem>>{
            override fun onFailure(call: Call<ArrayList<GuideListItem>>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ArrayList<GuideListItem>>?, response: Response<ArrayList<GuideListItem>>?) {
                if (response?.code() == 200) {
                    replaceFragment(searchResultsFragment, R.id.fragment_holder, true)
                    searchResultsFragment.updateGuides(response.body()!!)
                }
            }

        })

    }

    override fun onSearch(model: SearchRequest) {
        if (model.search == ""){
            val results = SearchResluts()
            results.places = db.GetPlaces()
            searchRequestFragment.UpdateSearch(results)
        }
        else {
            api.search(model).enqueue(object : Callback<SearchResluts> {
                override fun onFailure(call: Call<SearchResluts>?, t: Throwable?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onResponse(call: Call<SearchResluts>?, response: Response<SearchResluts>) {
                    if (response.code() == 200) {
                        val results = response.body()!!
                        Log.e("Search", results.toString())
                        searchRequestFragment.UpdateSearch(results)
                    }
                }

            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        api = Api(this)
        db = DB(this)
        
        addFragment(searchRequestFragment, R.id.fragment_holder)
    }
}
