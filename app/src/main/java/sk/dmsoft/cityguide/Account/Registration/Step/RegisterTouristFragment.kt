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
import sk.dmsoft.cityguide.Models.Account.Registration

import sk.dmsoft.cityguide.R

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RegisterTouristFragment.OnRegistration] interface
 * to handle interaction events.
 */
class RegisterTouristFragment : Fragment() {

    private var mListener: OnRegistration? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_tourist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login.setOnClickListener { startActivity(Intent(context, LoginActivity::class.java)) }
        guide_registration.setOnClickListener { mListener?.onSwitchToGuide() }
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
            mListener!!.onRegistrationComplete(model)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnRegistration) {
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
    interface OnRegistration {
        // TODO: Update argument type and name
        fun onRegistrationComplete(model: Registration)
        fun onSwitchToGuide()
    }
}// Required empty public constructor
