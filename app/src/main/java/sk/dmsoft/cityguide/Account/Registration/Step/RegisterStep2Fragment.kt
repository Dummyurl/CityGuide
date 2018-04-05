package sk.dmsoft.cityguide.Account.Registration.Step

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_register_step2.*
import sk.dmsoft.cityguide.Commons.load
import sk.dmsoft.cityguide.Models.Account.Registration2

import sk.dmsoft.cityguide.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RegisterStep2Fragment.Step2Listener] interface
 * to handle interaction events.
 *
 */
class RegisterStep2Fragment : Fragment() {
    private var listener: Step2Listener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_step2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        select_photo.setOnClickListener { listener?.onPhotoSelect() }
        next.setOnClickListener { CompleteStep2() }
    }

    fun loadPhoto(uri: String){
        photo_image.load(uri)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun CompleteStep2() {
        val model = Registration2()
        model.aboutMe = about_me.text.toString()
        listener?.onStep2Completed(model)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Step2Listener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement Step2Listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface Step2Listener {
        // TODO: Update argument type and name
        fun onPhotoSelect()
        fun onStep2Completed(model: Registration2)
    }

}
