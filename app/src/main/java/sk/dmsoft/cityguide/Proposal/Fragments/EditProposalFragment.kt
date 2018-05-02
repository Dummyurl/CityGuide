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
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment
import kotlinx.android.synthetic.main.fragment_edit_proposal.*
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Commons.AppSettings
import sk.dmsoft.cityguide.Commons.loadCircle
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.Models.Proposal.ProposalRequest

import sk.dmsoft.cityguide.R
import java.text.SimpleDateFormat
import java.util.*

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
    lateinit var startDateTimeFragment: SwitchDateTimeDialogFragment
    lateinit var endDateTimeFragment: SwitchDateTimeDialogFragment
    var proposalRequest: ProposalRequest = ProposalRequest()
    lateinit var db: DB

    val isSheetVisible: Boolean
        get() {
            return bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        db = DB(context!!)
        return inflater.inflate(R.layout.fragment_edit_proposal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDatePickers()

        bottomSheet = BottomSheetBehavior.from(edit_proposal_sheet)
        edit_proposal.setOnClickListener {
            hide()
            mListener?.onProposalChange(proposal.id, proposalRequest)
        }
        confirm_proposal.setOnClickListener {
            hide()
            mListener?.onProposalConfirm(proposal)
        }
    }

    fun setProposal(proposal: Proposal){
        this.proposal = proposal
        proposalRequest.start = proposal.start
        proposalRequest.end = proposal.end
        start_date.setText(DateTime(proposal.start).toLocalDateTime().toString())
        end_date.setText(DateTime(proposal.start).toLocalDateTime().toString())
        user_name.text = "${proposal.user.firstName} ${proposal.user.secondName}"
        place_name.text = db.GetPlace(proposal.placeId).city
        user_photo.loadCircle("${AppSettings.apiUrl}/users/photo/${proposal.user.id}")
        total_amount.text = "${proposal.perHourSalary}€"
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

    fun initDatePickers(){
        startDateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                "Select date",
                "OK",
                "Cancel"
        )
        startDateTimeFragment.startAtCalendarView()
        startDateTimeFragment.set24HoursMode(true)

        startDateTimeFragment.setOnButtonClickListener(object: SwitchDateTimeDialogFragment.OnButtonClickListener{
            override fun onPositiveButtonClick(date: Date) {
                val locale = SimpleDateFormat("dd.MM.YYYY HH:mm")
                start_date.setText(locale.format(date))
                proposalRequest.start = DateTime(date).toString()
            }

            override fun onNegativeButtonClick(p0: Date?) {
            }

        })

        start_date.setOnFocusChangeListener { view, b ->
            if (b)
                startDateTimeFragment.show(activity!!.supportFragmentManager, "dialog_start")
        }

        endDateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                "Select date",
                "OK",
                "Cancel"
        )
        endDateTimeFragment.startAtCalendarView()
        endDateTimeFragment.set24HoursMode(true)

        endDateTimeFragment.setOnButtonClickListener(object: SwitchDateTimeDialogFragment.OnButtonClickListener{
            override fun onPositiveButtonClick(date: Date) {
                val locale = SimpleDateFormat("dd.MM.YYYY HH:mm")
                end_date.setText(locale.format(date))
                proposalRequest.end = DateTime(date).toString()
            }

            override fun onNegativeButtonClick(p0: Date?) {
            }

        })

        end_date.setOnFocusChangeListener { view, b ->
            if (b)
                endDateTimeFragment.show(activity!!.supportFragmentManager, "dialog_end")
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
