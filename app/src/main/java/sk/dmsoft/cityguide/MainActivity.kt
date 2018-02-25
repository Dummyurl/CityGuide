package sk.dmsoft.cityguide

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import sk.dmsoft.cityguide.Commons.AccountManager

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logout.setOnClickListener({
            AccountManager.LogOut()
            finish()
        })

        at.text = AccountManager.accessToken +"\n" +AccountManager.accountType
    }


}
