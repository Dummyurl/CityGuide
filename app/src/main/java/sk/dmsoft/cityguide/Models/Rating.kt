package sk.dmsoft.cityguide.Models

import org.joda.time.DateTime

class Rating {
    var id = 0
    var guideId = ""
    var userId = ""
    var userName = ""
    var ratingStars = 3.0
    var comment: String? = null
    var date: String = DateTime().toString()
}