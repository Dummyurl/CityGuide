package sk.dmsoft.cityguide.Models.Proposal

import org.joda.time.DateTime

/**
 * Created by Daniel on 27. 2. 2018.
 */
class Proposal {
    var id = 0
    var touristId = ""
    var guideId = ""
    var start = ""
    var realStart = ""
    var end = ""
    var realEnd = ""
    var placeId = 0

    var touristStart = false
    var guideStart = false

    var touristEnd = false
    var guideEnd = false

    var state : ProposalState = ProposalState.New
}