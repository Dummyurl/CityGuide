package sk.dmsoft.cityguide.Models.Account

import org.joda.time.DateTime

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
    var interestsString = ""
}