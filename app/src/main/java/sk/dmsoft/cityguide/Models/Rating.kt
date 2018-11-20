package sk.dmsoft.cityguide.Models

import org.joda.time.DateTime
import sk.dmsoft.cityguide.Models.Guides.GuideDetails

class Rating {
    var id = 0
    var userInfo : GuideDetails? = null
    var ratingStars = 3.0
    var comment: String? = null
    var date: String? = null
}