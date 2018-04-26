package sk.dmsoft.cityguide.Models

import sk.dmsoft.cityguide.Models.Proposal.Proposal
import sk.dmsoft.cityguide.Models.Proposal.ProposalPayment

class InitResponse {
    var activeProposal: Proposal? = null
    var proposalToPay: ProposalPayment? = null
    var places: ArrayList<Place> = ArrayList()
    var countries: ArrayList<Country> = ArrayList()
    var braintreeConnected: Boolean = false
    var paypalConnected: Boolean = false

}