package sk.dmsoft.cityguide.Proposal.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment
import kotlinx.android.synthetic.main.fragment_edit_proposal.*
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import org.joda.time.format.DateTimeFormatter
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Commons.AppSettings
import sk.dmsoft.cityguide.Commons.CurrencyConverter
import sk.dmsoft.cityguide.Commons.loadCircle
import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.Models.Proposal.ProposalRequest

import sk.dmsoft.cityguide.R
import java.math.RoundingMode
import java.text.DecimalFormat
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

    var defaultStart = ""
    var defaultEnd = ""

    var dateChanged = false

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
        reject_proposal.setOnClickListener {
            hide()
            mListener?.onProposalrejected(proposal.id)
        }
        confirm_proposal.setOnClickListener {
            hide()
            if (dateChanged)
                mListener?.onProposalChange(proposal.id, proposalRequest)
            else
            mListener?.onProposalConfirm(proposal)
        }
    }

    fun setProposal(proposal: Proposal){
        val locale = SimpleDateFormat("dd.MM.yyyy HH:mm")
        this.proposal = proposal
        proposalRequest.start = proposal.start
        proposalRequest.end = proposal.end
        val startDate = DateTime(proposal.start)
        val endDate = DateTime(proposal.end)
        start_date.setText("${startDate.dayOfMonth}.${startDate.monthOfYear}.${startDate.year} ${startDate.hourOfDay}:${startDate.minuteOfHour}")
        end_date.setText("${endDate.dayOfMonth}.${endDate.monthOfYear}.${endDate.year} ${endDate.hourOfDay}:${endDate.minuteOfHour}")
        user_name.text = "${proposal.user.firstName} ${proposal.user.secondName}"
        user_interests.text = proposal.user.interestsString
        place_name.text = proposal.place?.city
        user_photo.loadCircle("${AppSettings.apiUrl}/users/photo/${proposal.user.id}")
        calcPrice()

        defaultStart = proposal.start
        defaultEnd = proposal.end
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
                val locale = SimpleDateFormat("dd.MM.yyyy HH:mm")
                val locale2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
                start_date.setText(locale.format(date))
                proposalRequest.start = locale2.format(date)+":00"
                checkDateValidity()
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
                val locale = SimpleDateFormat("dd.MM.yyyy HH:mm")
                val locale2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
                end_date.setText(locale.format(date))
                proposalRequest.end = locale2.format(date)+":00"
                checkDateValidity()
            }

            override fun onNegativeButtonClick(p0: Date?) {
            }

        })

        end_date.setOnFocusChangeListener { view, b ->
            if (b)
                endDateTimeFragment.show(activity!!.supportFragmentManager, "dialog_end")
        }
    }


    fun checkDateValidity(){
        try {
            val locale2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
            val actualDate = LocalDateTime().toDate()
            val startDate = locale2.parse(proposalRequest.start)
            when {
                locale2.parse(proposalRequest.end) < locale2.parse(proposalRequest.start) -> {
                    Snackbar.make(edit_proposal_sheet, "Start date has to be sooner than end date", Snackbar.LENGTH_LONG).show()
                    confirm_proposal.isEnabled = false
                }
                startDate < actualDate -> {
                    Snackbar.make(edit_proposal_sheet, "You can only plan to the future!", Snackbar.LENGTH_LONG).show()
                    confirm_proposal.isEnabled = false
                }
                else -> {
                    switchAcceptEdit()
                    calcPrice()
                    confirm_proposal.isEnabled = true
                }
            }
        }
        catch (e: Exception){}
    }

    fun calcPrice(){
        val locale2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val startDate = locale2.parse(proposalRequest.start)
        val endDate = locale2.parse(proposalRequest.end)
        val millis = endDate.time - startDate.time

        val df = DecimalFormat("#")
        df.roundingMode = RoundingMode.CEILING

        val hours = df.format(millis / 1000 / 60 / 60.0).toInt()
        total_amount.text = CurrencyConverter.convert(hours * proposal.perHourSalary)
    }

    fun switchAcceptEdit(){
        if (proposalRequest.start == defaultStart && proposalRequest.end == defaultEnd){
            confirm_proposal.text = resources.getText(R.string.confirm_proposal)
            dateChanged = false
        }
        else{
            confirm_proposal.text = resources.getText(R.string.change_proposal)
            dateChanged = true
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
        fun onProposalrejected(id: Int)
    }
}// Required empty public constructor
