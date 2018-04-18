package sk.dmsoft.cityguide.Models

/**
 * Created by Daniel on 15. 11. 2017.
 */
class Place {
    constructor(id: Int, name: String, zip: String, countryId: Int){
        this.id = id
        this.city = name
        this.zip = zip
        this.countryId = countryId
    }
    constructor()

    var id: Int = 0
    var city: String = ""
    var zip: String = ""
    var countryId: Int = 0
    var gurusCount: Int = 0
}