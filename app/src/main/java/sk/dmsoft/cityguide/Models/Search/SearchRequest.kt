package sk.dmsoft.cityguide.Models.Search

/**
 * Created by Daniel on 8. 3. 2018.
 */
class SearchRequest {
    var search: String = ""
    var maxHourlyRate = 50
    var minRating: Float = 0.0f

    fun reset(){
        search = ""
        maxHourlyRate = 50
        minRating = 0.0f
    }
}