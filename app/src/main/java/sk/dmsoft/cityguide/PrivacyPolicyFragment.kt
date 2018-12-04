package sk.dmsoft.cityguide

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import kotlinx.android.synthetic.main.fragment_privacy_policy.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PrivacyPolicyFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 *
 */
class PrivacyPolicyFragment : Fragment() {

    enum class RegisterMethodEnum{
        Email, Facebook, Google
    }

    private var listener: OnFragmentInteractionListener? = null
    var agreeAllowed = true
    var registerMethod: RegisterMethodEnum = RegisterMethodEnum.Email

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_privacy_policy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        privacy_web_view.loadUrl("file:///android_asset/privacy.html")

        agree_switch.setOnCheckedChangeListener { _, p1 ->
            if (p1)
                allowAgree()
            else
                denyAgree()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            initScrollView()
        }

        scroll_down_fab.setOnClickListener { scrollDown() }
        next.setOnClickListener { nextClicked() }
        cancel.setOnClickListener { cancelClicked() }
        next.isEnabled = false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun initScrollView(){
        agreeAllowed = false
        privacy_scroll_view.setOnScrollChangeListener { p0, p1, p2, p3, p4 ->
            val diff = privacy_web_view.bottom -( privacy_scroll_view.height + p2)
            if (diff < 5) {
                scroll_down_fab.visibility = View.GONE
            }
            else
                scroll_down_fab.visibility = View.VISIBLE
        }
    }

    fun setRegisterMethodEnum(method: RegisterMethodEnum){
        registerMethod = method
    }

    fun allowAgree(){
        agreeAllowed = true
        next.isEnabled = true
    }

    fun denyAgree(){
        agreeAllowed = false
        next.isEnabled = false
    }

    fun nextClicked(){
        if (agreeAllowed)
            listener?.OnPolicyAccepted(this, registerMethod)
    }

    fun cancelClicked(){
        listener?.onCancel(this)
    }

    fun scrollDown(){
        privacy_scroll_view.scrollBy(0, privacy_scroll_view.height)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
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
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun OnPolicyAccepted(fragment: PrivacyPolicyFragment, method: RegisterMethodEnum)
        fun onCancel(fragment: PrivacyPolicyFragment)
    }

}
