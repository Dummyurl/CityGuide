package sk.dmsoft.cityguide.Models.Proposal

import org.joda.time.DateTime
import sk.dmsoft.cityguide.Models.Account.UserInfo
import sk.dmsoft.cityguide.Models.Place

/**
 * Created by Daniel on 27. 2. 2018.
 */
class Proposal {
    var id = 0
    var start = ""
    var realStart = ""
    var end = ""
    var realEnd = ""
    var place : Place? = null

    var touristStart = false
    var guideStart = false

    var touristEnd = false
    var guideEnd = false

    var perHourSalary = 0.0

    var meetingPoint: MeetingPoint? = null

    var payment: ProposalPayment? = null

    var canChange = false

    var state : Int = 0

    var user: UserInfo = UserInfo()

    var actualTime: Long = 0

    var realStartTimeSpan: Long = 0

    var lastChange = ""
}