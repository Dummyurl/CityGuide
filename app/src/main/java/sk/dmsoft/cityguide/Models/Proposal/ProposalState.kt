package sk.dmsoft.cityguide.Models.Proposal

/**
 * Created by Daniel on 15. 3. 2018.
 */
enum class ProposalState (val value: Int) {
    New(0), WaitingForGuide(1), WaitingForTourist(2), Changed(3), Confirmed(4), InProgress(5), Completed(6);

    companion object {
        private val map = ProposalState.values().associateBy(ProposalState::value)
        fun fromInt(type: Int) = map[type]
    }
}