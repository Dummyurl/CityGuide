package sk.dmsoft.cityguide.Models.Account

import org.joda.time.DateTime
import sk.dmsoft.cityguide.Models.Interest

/**
 * Created by Daniel on 16. 3. 2018.
 */
class UserInfo {
    var id = ""
    var accountType: Int = 0
    var firstName = ""
    var secondName = ""
    var BirthDate: DateTime = DateTime()
    var about = ""
    var interests : ArrayList<Interest> = ArrayList()
    var interestsString: String = ""
        get() {
            return interests.map { it.name }.joinToString(separator = " • ")
        }
}