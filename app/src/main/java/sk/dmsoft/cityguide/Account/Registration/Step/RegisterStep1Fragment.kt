package sk.dmsoft.cityguide.Account.Registration.Step

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_register_step1.*
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Models.Account.Registration1

import sk.dmsoft.cityguide.R
import android.R.attr.startYear
import android.os.Build
import android.support.annotation.RequiresApi
import sk.dmsoft.cityguide.MainActivity
import android.widget.DatePicker
import org.joda.time.DateTime


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RegisterStep1Fragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class RegisterStep1Fragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private var mListener: Step1Listener? = null
    lateinit var db: DB
    val model = Registration1()
    lateinit var datePickerDialog: DatePickerDialog

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        db = DB(context)

        return inflater!!.inflate(R.layout.fragment_register_step1, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        datePickerDialog = DatePickerDialog(context, this, 2018, 1, 1)

        val countriesAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, db.GetCountries().filter { it.name != null }.map { it.name })
        countriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        countries_spinner.adapter = countriesAdapter
        val placesAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, db.GetPlaces().filter { (it.countryId == 0)}.map { it.city } )
        placesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        places_spinner.adapter = placesAdapter

        countries_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedCountry = db.GetCountries()[p2].id
                places_spinner.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, db.GetPlaces().filter { (it.countryId == selectedCountry)}.map { it.city } )
            }
        }

        places_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) { }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                model.placeId = db.GetPlaces()[p2].id
            }

        }

        next.setOnClickListener { completeStep1() }
        birth_date.onFocusChangeListener = View.OnFocusChangeListener { _, p1 -> if(p1) datePickerDialog.show() }
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        model.birthDate = DateTime(p1, p2, p3, 0, 0).toString()
        birth_date.setText(DateTime(p1, p2, p3, 0, 0).toLocalDate().toString())
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun completeStep1() {
        if (mListener != null) {
            model.firstName = first_name.text.toString()
            model.secondName = second_name.text.toString()
            mListener!!.onStep1Completed(model)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Step1Listener) {
            mListener = context
        } else {
//            throw RuntimeException(context!!.toString() + " must implement Step2Listener")
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
    interface Step1Listener {
        // TODO: Update argument type and name
        fun onStep1Completed(model: Registration1)
    }
}// Required empty public constructor
