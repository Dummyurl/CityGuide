package sk.dmsoft.cityguide

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_profile.*
import sk.dmsoft.cityguide.Account.Registration.RegistrationActivity
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Commons.AppSettings
import sk.dmsoft.cityguide.Commons.loadCircle

class ProfileActivity : AppCompatActivity() {

    lateinit var db: DB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        db = DB(this)

        val placesAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, db.GetCurrencies().map { it.id } )
        placesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currency_spinner.adapter = placesAdapter
        currency_spinner.setSelection(db.GetCurrencies().indexOf(db.GetCurrencies().find { it.id == AccountManager.currency }))

        currency_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                AccountManager.currency = db.GetCurrencies()[p2].id
            }
        }

        profile_photo.loadCircle("${AppSettings.apiUrl}/users/photo/${AccountManager.userId}")

        logout.setOnClickListener {
            AccountManager.LogOut()
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        }
    }
}
