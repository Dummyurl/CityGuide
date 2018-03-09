package sk.dmsoft.cityguide.Account.Registration.Step

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_register_tourist.*
import sk.dmsoft.cityguide.Account.Login.LoginActivity
import sk.dmsoft.cityguide.Commons.EAccountType
import sk.dmsoft.cityguide.Models.Account.Registration

import sk.dmsoft.cityguide.R

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RegisterGuideFragment.OnRegistrationGuide] interface
 * to handle interaction events.
 */
class RegisterGuideFragment : Fragment() {

    private var mListener: OnRegistrationGuide? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_register_guide, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        register.setOnClickListener {
            CompleteRegistration()
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun CompleteRegistration() {
        if (mListener != null) {
            val model = Registration()
            model.email = email.text.toString()
            model.password = password.text.toString()
            model.confirmPassword = confirm_password.text.toString()
            model.accountType = EAccountType.guide
            mListener!!.onRegistrationGuideComplete(model)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnRegistrationGuide) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnRegistrationGuide")
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
    interface OnRegistrationGuide {
        // TODO: Update argument type and name
        fun onRegistrationGuideComplete(model: Registration)
    }
}// Required empty public constructor
