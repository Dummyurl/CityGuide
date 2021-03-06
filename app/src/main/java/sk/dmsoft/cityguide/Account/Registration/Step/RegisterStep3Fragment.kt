package sk.dmsoft.cityguide.Account.Registration.Step

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.robertlevonyan.views.chip.Chip
import kotlinx.android.synthetic.main.fragment_register_step3.*
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Models.Account.Registration3
import sk.dmsoft.cityguide.Models.Interest
import sk.dmsoft.cityguide.Models.Language
import sk.dmsoft.cityguide.Models.SelectedInterest
import sk.dmsoft.cityguide.Models.SelectedLanguage

import sk.dmsoft.cityguide.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [RegisterStep3Fragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 *
 */
class RegisterStep3Fragment : Fragment() {
    private var listener: Step3Listener? = null
    private var allInterests = ArrayList<Interest>()
    private var allLanguages = ArrayList<Language>()
    private var selectedInterests = ArrayList<Interest>()
    private var selectedLanguages = ArrayList<Language>()
    private lateinit var db: DB
    private var beforeSelectedInterests = ArrayList<SelectedInterest>()
    private var beforeSelectedLanguages = ArrayList<SelectedLanguage>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        db = DB(context!!)
        allInterests = db.GetInterests()
        allLanguages = db.GetLanguages()
        beforeSelectedInterests = db.GetSelectedInterests()
        beforeSelectedLanguages = db.GetSelectedLanguages()
        return inflater.inflate(R.layout.fragment_register_step3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for (item: Int in allInterests.map { it.id }) {
            val chip = Chip(context)
            chip.isSelectable = true
            chip.chipText = allInterests.find { it.id == item }?.name
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.marginEnd = 10
            params.marginStart = 10
            params.bottomMargin = 20
            chip.layoutParams = LinearLayout.LayoutParams(params)
            if (beforeSelectedInterests.count { it.id == item } > 0){
                chip.isSelected = true
                addInterest(item)
            }
            interests_holder.addView(chip)
            chip.setOnSelectClickListener { _, selected ->
                if (selected)
                    addInterest(item)
                else
                    removeInterest(item)
            }
        }

        for (item: Int in allLanguages.map { it.id }) {
            val chip = Chip(context)
            chip.isSelectable = true
            chip.chipText = allLanguages.find { it.id == item }?.lang
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.marginEnd = 10
            params.marginStart = 10
            params.bottomMargin = 20
            chip.layoutParams = LinearLayout.LayoutParams(params)
            if (beforeSelectedLanguages.count { it.id == item } > 0){
                chip.isSelected = true
                addLanguage(item)
            }
            languages_holder.addView(chip)
            chip.setOnSelectClickListener { _, selected ->
                if (selected)
                    addLanguage(item)
                else
                    removeLanguage(item)
            }
        }

        next.setOnClickListener { completeStep3() }
    }

    fun completeStep3(){
        val model = Registration3()
        model.interests = selectedInterests
        model.languages = selectedLanguages

        if (selectedInterests.count() < 3 )
            Snackbar.make(activity!!.findViewById<View>(android.R.id.content), "Select at least 3 interests", Snackbar.LENGTH_LONG).show()

        else if (selectedLanguages.count() < 1)
            Snackbar.make(activity!!.findViewById<View>(android.R.id.content), "Select at least 1 language", Snackbar.LENGTH_LONG).show()

        else {
            db.SaveSelectedInterests(selectedInterests.map { SelectedInterest(it.id) } as ArrayList<SelectedInterest>)
            db.SaveSelectedLanguages(selectedLanguages.map { SelectedLanguage(it.id) } as ArrayList<SelectedLanguage>)
            listener?.onStep3Completed(model)
            next.isEnabled = false
        }
    }

    fun addInterest(id: Int){
        selectedInterests.add(allInterests.find { it.id == id }!!)
    }

    fun addLanguage(id: Int){
        selectedLanguages.add(allLanguages.find { it.id == id }!!)
    }

    fun removeInterest(id: Int){
        val interest = allInterests.find { it.id == id }!!
        selectedInterests.remove(interest)
    }

    fun removeLanguage(id: Int){
        val language = allLanguages.find { it.id == id }!!
        selectedLanguages.remove(language)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Step3Listener) {
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
    interface Step3Listener {
        // TODO: Update argument type and name
        fun onStep3Completed(model: Registration3)
    }

}
