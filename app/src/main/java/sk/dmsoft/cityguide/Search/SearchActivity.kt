package sk.dmsoft.cityguide.Search

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.transition.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import com.microsoft.appcenter.analytics.Analytics
import kotlinx.android.synthetic.main.activity_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Commons.CurrencyConverter
import sk.dmsoft.cityguide.Commons.addFragment
import sk.dmsoft.cityguide.Commons.replaceFragment
import sk.dmsoft.cityguide.Models.Guides.GuideListItem
import sk.dmsoft.cityguide.Models.Place
import sk.dmsoft.cityguide.Models.Search.SearchInCity
import sk.dmsoft.cityguide.Models.Search.SearchRequest
import sk.dmsoft.cityguide.Models.Search.SearchResults
import sk.dmsoft.cityguide.R
import sk.dmsoft.cityguide.Search.Fragments.SearchRequestFragment
import sk.dmsoft.cityguide.Search.Fragments.SearchResultsFragment
import java.util.HashMap

class SearchActivity : AppCompatActivity(), SearchRequestFragment.OnSearchTextInserted, SearchResultsFragment.OnSearchResultsInteraction {

    lateinit var api: Api
    val searchRequestFragment = SearchRequestFragment()
    val searchResultsFragment = SearchResultsFragment()
    lateinit var db: DB
    var placeId = 0

    val searchRequest = SearchRequest()

    override fun onGuideSelected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSearchUpdate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCitySelected(place: Place, itemView: View) {
        val model = SearchInCity()
        model.placeId = place.id
        placeId = place.id
        api.searchInCity(model).enqueue(object: Callback<ArrayList<GuideListItem>>{
            override fun onFailure(call: Call<ArrayList<GuideListItem>>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ArrayList<GuideListItem>>?, response: Response<ArrayList<GuideListItem>>?) {
                if (response?.code() == 200) {
                    //searchResultsFragment.enterTransition = Fade()
                    //searchResultsFragment.exitTransition = Fade()
                    //searchResultsFragment.sharedElementEnterTransition = FragmentTransition()
                    //searchResultsFragment.sharedElementReturnTransition = FragmentTransition()
                    //searchRequestFragment.exitTransition = Fade()
                    searchResultsFragment.updateImage(place.id)

                    val sharedView = itemView.findViewById<View>(R.id.city_image)
                    replaceFragment(searchResultsFragment, R.id.fragment_holder, true, sharedView)
                    val guides = response.body()!!
                    searchResultsFragment.updateGuides(guides)
                }
            }

        })

    }

    override fun onSearch(searchText: String) {
        searchRequest.search = searchText

        val properties = HashMap<String, String>()
        properties["searchQuery"] = searchText
        Analytics.trackEvent("Search places", properties)

        api.search(searchRequest).enqueue(object : Callback<SearchResults> {
            override fun onFailure(call: Call<SearchResults>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onResponse(call: Call<SearchResults>?, response: Response<SearchResults>) {
                if (response.code() == 200) {
                    val results = response.body()!!
                    Log.e("Search", results.toString())
                    searchRequestFragment.UpdateSearch(results)
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        api = Api(this)
        db = DB(this)

        apply_filter_btn.setOnClickListener {
            applyFilters()
        }

        addFragment(searchRequestFragment, R.id.fragment_holder)

        max_hourly_rate.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                max_price_text.text = CurrencyConverter.convert(p0?.progress!!.toDouble())
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
    }

    fun applyFilters(){
        searchRequest.maxHourlyRate = max_hourly_rate.progress
        searchRequest.minRating = min_rating.rating
        val model = SearchInCity()
        model.placeId = placeId
        model.maxPrice = max_hourly_rate.progress
        model.minRating = min_rating.rating.toInt()
        model.byMyInterests = by_interests.isChecked
        model.freeGuiding = free_guiding.isChecked

        api.searchInCity(model).enqueue(object: Callback<ArrayList<GuideListItem>>{
            override fun onFailure(call: Call<ArrayList<GuideListItem>>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ArrayList<GuideListItem>>?, response: Response<ArrayList<GuideListItem>>?) {
                if (response?.code() == 200) {
                    val guides = response.body()!!
                    searchResultsFragment.updateGuides(guides)
                }
            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.search_menu, menu)
        return true
    }

    fun openFilter(){
        drawer_layout.openDrawer(nav_view)
    }

    override fun onOptionsItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId){
            R.id.show_filter -> openFilter()
        }
        return true
    }
}

class FragmentTransition() : TransitionSet() {init {
    ordering = ORDERING_TOGETHER
    addTransition(ChangeBounds()).
            addTransition(ChangeTransform()).
            addTransition(ChangeImageTransform())
}

}