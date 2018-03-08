package sk.dmsoft.cityguide

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Search.SearchActivity

class MainActivity : AppCompatActivity() {

    lateinit var api: Api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        api = Api(this)

        logout.setOnClickListener {
            AccountManager.LogOut()
            finish()
        }

        open_search.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
    }


}
