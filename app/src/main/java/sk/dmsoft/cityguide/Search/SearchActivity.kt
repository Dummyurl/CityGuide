package sk.dmsoft.cityguide.Search

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Commons.addFragment
import sk.dmsoft.cityguide.Models.Search.SearchRequest
import sk.dmsoft.cityguide.Models.Search.SearchResluts
import sk.dmsoft.cityguide.R
import sk.dmsoft.cityguide.Search.Fragments.SearchRequestFragment

class SearchActivity : AppCompatActivity(), SearchRequestFragment.OnSearchTextInserted {

    lateinit var api: Api


    override fun onSearch(model: SearchRequest) {
        api.search(model).enqueue(object: Callback<SearchResluts>{
            override fun onFailure(call: Call<SearchResluts>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<SearchResluts>?, response: Response<SearchResluts>?) {
                if (response?.code() == 200){
                    val results = response.body()
                    Log.e("Search", results.toString())
                }
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        api = Api(this)

        addFragment(SearchRequestFragment(), R.id.fragment_holder)
    }
}
