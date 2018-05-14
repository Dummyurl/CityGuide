package sk.dmsoft.cityguide

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_profile.*
import sk.dmsoft.cityguide.Account.ChangePasswordActivity
import sk.dmsoft.cityguide.Account.Registration.RegistrationActivity
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Commons.Adapters.SettingsAdapter
import sk.dmsoft.cityguide.Commons.AppSettings
import sk.dmsoft.cityguide.Commons.EAccountType
import sk.dmsoft.cityguide.Commons.loadCircle

class ProfileActivity : AppCompatActivity() {

    val userFields: Array<String> = arrayOf(
            "Personal Info",
            "About you",
            "Interests"
    )

    val guideFields: Array<String> = arrayOf(
            "Guide info"
    )

    val touristFields: Array<String> = arrayOf(
            "Payment method"
    )

    val accountFields: Array<String> = arrayOf(
            "Change password"
    )

    val settingsFields: ArrayList<String> = ArrayList()

    lateinit var db: DB
    lateinit var api: Api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        db = DB(this)
        api = Api(this)

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

        initFields()

        val settingsAdapter = SettingsAdapter(settingsFields, { position ->
            if (position < settingsFields.size - 2) {
                val intent = Intent(this@ProfileActivity, RegistrationActivity::class.java)
                intent.putExtra("EDIT_MODE", true)
                intent.putExtra("REGISTRATION_STEP", position+1)
                startActivity(intent)
            }

            if (position == settingsFields.size - 1)
                startActivity(Intent(this@ProfileActivity, ChangePasswordActivity::class.java))
        })

        settings_recycler.setHasFixedSize(true)
        settings_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        settings_recycler.adapter = settingsAdapter
        settings_recycler.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
    }

    fun initFields(){
        settingsFields.addAll(userFields)
        if (AccountManager.accountType == EAccountType.tourist)
            settingsFields.addAll(touristFields)
        else
            settingsFields.addAll(guideFields)
        settingsFields.addAll(accountFields)
    }
}
