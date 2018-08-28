package sk.dmsoft.cityguide

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_profile.*
import sk.dmsoft.cityguide.Account.ChangePasswordActivity
import sk.dmsoft.cityguide.Account.Registration.RegistrationActivity
import sk.dmsoft.cityguide.Api.Api
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Commons.AccountManager
import sk.dmsoft.cityguide.Commons.Adapters.SettingsAdapter
import sk.dmsoft.cityguide.Commons.EAccountType
import sk.dmsoft.cityguide.Commons.showAlertDialog
import android.support.v7.app.AlertDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.dmsoft.cityguide.Commons.positiveButton
import sk.dmsoft.cityguide.Guide.GuideDetailsActivity
import sk.dmsoft.cityguide.Proposal.Completed.CompletedProposalActivity


class ProfileActivity : AppCompatActivity() {

    val userFields: Array<String> = arrayOf(
            "Personal Info",
            "Profile info",
            "Interests"
    )

    val guideFields: Array<String> = arrayOf(
            "Payment settings",
            "View profile"
    )

    val touristFields: Array<String> = arrayOf(
            "Payment method"
    )

    val appSettingsFields: Array<String> = arrayOf(
            "Change currency", "Change password"
    )

    val allAccountFields: ArrayList<String> = ArrayList()
    val allAppSettingsFielda: ArrayList<String> = ArrayList()

    lateinit var db: DB
    lateinit var api: Api

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        db = DB(this)
        api = Api(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"

        logout.setOnClickListener {
            AccountManager.LogOut()
            startActivity(Intent(this, RegistrationActivity::class.java))
            db.DeleteSelectedInterests()
            api.logout().enqueue(object: Callback<ResponseBody>{
                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                }

                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                }
            })
            finish()
        }

        show_history.setOnClickListener { startActivity(Intent(this, CompletedProposalActivity::class.java)) }

        initFields()

        val settingsAdapter = SettingsAdapter(allAccountFields) { position ->
            if (AccountManager.accountType == EAccountType.guide && position == 4){
                val intent = Intent(this, GuideDetailsActivity::class.java)
                intent.putExtra("GUIDE_ID", AccountManager.userId)
                intent.putExtra("DENY_BOOKING", true)
                startActivity(intent)
            }

            else {
                val intent = Intent(this@ProfileActivity, RegistrationActivity::class.java)
                intent.putExtra("EDIT_MODE", true)
                intent.putExtra("REGISTRATION_STEP", position + 1)
                startActivity(intent)
            }
        }

        val appSettingsAdapter = SettingsAdapter(allAppSettingsFielda) {position ->
            if (AccountManager.accountType == EAccountType.tourist){
                when (position) {
                    0 -> showAlertDialog {
                        setTitle("Add payment method")
                        positiveButton("Ok") {  } }
                    1 -> showChangeCurrencyDialog()
                    2 -> changePassword()
                }
            }
            else {
                when (position) {
                    0 -> showChangeCurrencyDialog()
                    1 -> changePassword()
                }
            }
        }

        settings_recycler.setHasFixedSize(true)
        settings_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        settings_recycler.adapter = settingsAdapter
        settings_recycler.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

        app_settings_recycler.setHasFixedSize(true)
        app_settings_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        app_settings_recycler.adapter = appSettingsAdapter
        app_settings_recycler.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
    }

    fun initFields(){
        allAccountFields.addAll(userFields)
        if (AccountManager.accountType == EAccountType.guide)
            allAccountFields.addAll(guideFields)
        else
            allAppSettingsFielda.addAll(touristFields)

        allAppSettingsFielda.addAll(appSettingsFields)
    }

    fun showChangeCurrencyDialog(){
        // Creating and Building the Dialog
        val currencies= db.GetCurrencies()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select currency")
        val selectedCurrency = currencies.map { it.id }.indexOf(AccountManager.currency)
        builder.setSingleChoiceItems(currencies.map { it.id }.toTypedArray(), selectedCurrency) { dialog, item ->
            AccountManager.currency = currencies[item].id
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun changePassword(){
        startActivity(Intent(this, ChangePasswordActivity::class.java))
    }
}
