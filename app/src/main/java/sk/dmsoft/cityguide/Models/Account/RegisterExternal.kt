package sk.dmsoft.cityguide.Models.Account

import sk.dmsoft.cityguide.Commons.EAccountType

class RegisterExternal {
    var email: String = ""
    var accountType: EAccountType = EAccountType.tourist
    var userId: String = ""
    var fcmId: String = ""
}