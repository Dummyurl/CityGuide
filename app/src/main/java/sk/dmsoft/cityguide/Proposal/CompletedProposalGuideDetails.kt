package sk.dmsoft.cityguide.Proposal

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_completed_proposal_guide_details.*
import sk.dmsoft.cityguide.MainActivity
import sk.dmsoft.cityguide.R

class CompletedProposalGuideDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_proposal_guide_details)

        back_to_main.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
