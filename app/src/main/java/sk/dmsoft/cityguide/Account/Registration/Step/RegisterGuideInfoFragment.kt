package sk.dmsoft.cityguide.Account.Registration.Step

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_register_guide_info.*
import sk.dmsoft.cityguide.Models.Account.RegistrationGuideInfo

import sk.dmsoft.cityguide.R

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RegisterGuideInfoFragment.OnRegistrationGuideInfo] interface
 * to handle interaction events.
 */
class RegisterGuideInfoFragment : Fragment() {

    private var mListener: OnRegistrationGuideInfo? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_guide_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        finish.setOnClickListener { CompleteGuideInfoRegistration() }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun CompleteGuideInfoRegistration() {
        if (mListener != null) {
            val model = RegistrationGuideInfo()
            model.salary = salary.text.toString().toInt()
            model.paypalEmail = paypal_email.text.toString()
            model.estimatedTime = estimated_time.text.toString().toDouble()
            mListener!!.onRegistrationGuideInfoCompleted(model)
        }
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
