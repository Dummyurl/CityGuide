package sk.dmsoft.cityguide.Account.Registration.Step

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_register_guide_info.*
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Commons.CurrencyConverter
import sk.dmsoft.cityguide.Models.Account.RegistrationGuideInfo
import sk.dmsoft.cityguide.Models.Guides.GuideDetails

import sk.dmsoft.cityguide.R

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RegisterGuideInfoFragment.OnRegistrationGuideInfo] interface
 * to handle interaction events.
 */
class RegisterGuideInfoFragment : Fragment() {

    private var mListener: OnRegistrationGuideInfo? = null
    var currencyRate: Double = 0.0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_guide_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        estimated_picker.minValue = 1
        estimated_picker.maxValue = 24
        estimated_picker.value = 5
        estimated_picker.wrapSelectorWheel = false

        val db = DB(activity!!)

        val currenciesAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, db.GetCurrencies().map { it.id })
        currenciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        currencies_spinner.adapter = currenciesAdapter

        currencies_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) { }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                currencyRate = db.GetCurrencies()[p2].rate
            }

        }

        free_guiding_cb.setOnCheckedChangeListener { cb, isChecked ->
            if (isChecked){
                currencies_spinner.isEnabled = false
                salary.isEnabled = false
                salary.setText("0")
            }
            else {
                currencies_spinner.isEnabled = true
                salary.isEnabled = true
            }
        }

        finish.setOnClickListener { CompleteGuideInfoRegistration() }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun CompleteGuideInfoRegistration() {
        if ((salary.text.toString().toDouble() * currencyRate).toInt() > 50){
            Snackbar.make(activity!!.findViewById(android.R.id.content), "Maximum limit is ${CurrencyConverter.convert(50.0)}", Snackbar.LENGTH_LONG).show()
        }
        else
            if (mListener != null) {
                finish.isEnabled = false
                val model = RegistrationGuideInfo()
                model.salary = (salary.text.toString().toDouble() * currencyRate).toInt()
                model.paypalEmail = paypal_email.text.toString()
                model.estimatedTime = estimated_picker.value.toDouble()
                mListener!!.onRegistrationGuideInfoCompleted(model)
            }
    }

    fun init(info: GuideDetails){
        salary?.setText(info.salary.toString())
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnRegistrationGuideInfo) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnRegistrationGuideInfo")
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
    interface OnRegistrationGuideInfo {
        // TODO: Update argument type and name
        fun onRegistrationGuideInfoCompleted(model: RegistrationGuideInfo)
    }
}// Required empty public constructor
