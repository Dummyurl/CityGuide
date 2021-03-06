package sk.dmsoft.cityguide.Models.Guides

import sk.dmsoft.cityguide.Models.Interest

/**
 * Created by Daniel on 27. 2. 2018.
 */
class GuideListItem {
    var id: String = ""
    var firstName: String = ""
    var secondName: String = ""
    var interests : ArrayList<Interest> = ArrayList()
    var guideInfo: GuideInfo? = null
    var ratingAvg: Double = 0.0
    var interestsString: String = ""
    get() {
        return interests.map { it.name }.joinToString(separator = " • ")
    }
}