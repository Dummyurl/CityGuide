package sk.dmsoft.cityguide.Models.Account

import sk.dmsoft.cityguide.Commons.EAccountType

/**
 * Created by Daniel on 25. 2. 2018.
 */
class Registration {
    var email: String = ""
    var password: String = ""
    var confirmPassword: String = ""
    var accountType: EAccountType = EAccountType.tourist
}