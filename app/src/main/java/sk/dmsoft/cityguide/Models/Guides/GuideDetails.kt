package sk.dmsoft.cityguide.Models.Guides

import sk.dmsoft.cityguide.Models.Place
import sk.dmsoft.cityguide.Models.Rating

/**
 * Created by Daniel on 13. 3. 2018.
 */
class GuideDetails {
    var id: String = ""
    var firstName: String = ""
    var secondName: String = ""
    var place: Place? = null
    var ratings: ArrayList<Rating> = ArrayList()
    var salary: Double = 0.0
    var about = ""
    var totalHours = 0
    var totalProposals = 0
}