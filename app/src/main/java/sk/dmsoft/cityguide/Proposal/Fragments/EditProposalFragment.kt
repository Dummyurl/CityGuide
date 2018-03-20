package sk.dmsoft.cityguide.Proposal.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_edit_proposal.*
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.Models.Proposal.ProposalRequest

import sk.dmsoft.cityguide.R

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [EditProposalFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class EditProposalFragment : Fragment() {

    private var mListener: OnProposalUpdate? = null
    private var proposal: Proposal = Proposal()
    private lateinit var bottomSheet: BottomSheetBehavior<ConstraintLayout>

    val isSheetVisible: Boolean
        get() {
            return bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED
        }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_edit_proposal, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheet = BottomSheetBehavior.from(edit_proposal_sheet)
        edit_proposal.setOnClickListener {
            hide()
            mListener?.onProposalChange(proposal.id, getNewProposalData())
        }
        confirm_proposal.setOnClickListener {
            hide()
            mListener?.onProposalConfirm(proposal)
        }
    }

    fun getNewProposalData(): ProposalRequest{
        val request = ProposalRequest()
        //TODO("Update proposal from inputs")
        return request
    }

    fun setProposal(proposal: Proposal){
        this.proposal = proposal
        //TODO("Update UI")
    }

    fun hide(){
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun show(){
        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnProposalUpdate) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnChatInteractionListener")
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
    interface OnProposalUpdate {
        // TODO: Update argument type and name
        fun onProposalChange(id: Int, proposal: ProposalRequest)
        fun onProposalConfirm(proposal: Proposal)
    }
}// Required empty public constructor
