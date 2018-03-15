package sk.dmsoft.cityguide.Proposal.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_edit_proposal.*
import sk.dmsoft.cityguide.Models.Proposal.Proposal

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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_edit_proposal, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        edit_proposal.setOnClickListener {
            getNewProposalData()
            mListener?.onProposalChange(proposal)
        }
        confirm_proposal.setOnClickListener { mListener?.onProposalConfirm(proposal) }
    }

    fun getNewProposalData(){
        TODO("Update proposal from inputs")
    }

    fun setProposal(proposal: Proposal){
        this.proposal = proposal
        TODO("Update UI")
    }

    fun hide(){
        TODO("Hide bottom sheet")
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnProposalUpdate) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
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
        fun onProposalChange(proposal: Proposal)
        fun onProposalConfirm(proposal: Proposal)
    }
}// Required empty public constructor
