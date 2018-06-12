package sk.dmsoft.cityguide.Account.Registration.Step

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_select_account_type.*
import sk.dmsoft.cityguide.Commons.EAccountType

import sk.dmsoft.cityguide.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SelectAccountTypeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 *
 */
class SelectAccountTypeFragment : Fragment() {
    private var listener: OnAccountTypeSelected? = null

    var selectedAccountType = EAccountType.tourist

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_account_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tourist_card.setOnClickListener { selectTourist() }
        guide_card.setOnClickListener { selectGuru() }
        next.setOnClickListener { next() }
    }

    fun selectTourist(){
        tourist_card.setCardBackgroundColor(0xff00C853.toInt())
        tourist_card.elevation = 10.0f
        guide_card.elevation = 2f
        textView17.setTextColor(0xff00C853.toInt())
        textView19.setTextColor(0xff000000.toInt())
        guide_card.setCardBackgroundColor(0x00000000.toInt())
        selectedAccountType = EAccountType.tourist
    }

    fun selectGuru(){
        guide_card.setCardBackgroundColor(0xff00C853.toInt())
        guide_card.elevation = 10.0f
        tourist_card.elevation = 2f
        textView19.setTextColor(0xff00C853.toInt())
        textView17.setTextColor(0xff000000.toInt())
        tourist_card.setCardBackgroundColor(0x00000000.toInt())
        selectedAccountType = EAccountType.guide
    }

    fun next(){
        listener?.AccountTypeSelected(selectedAccountType)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnAccountTypeSelected) {
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
    interface OnAccountTypeSelected {
        // TODO: Update argument type and name
        fun AccountTypeSelected(accountType: EAccountType)
    }

}
