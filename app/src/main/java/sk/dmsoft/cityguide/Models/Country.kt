package sk.dmsoft.cityguide.Models

import ninja.sakib.pultusorm.annotations.Ignore

/**
 * Created by Daniel on 15. 11. 2017.
 */
class Country {
    constructor(id: Int, name: String){
        this.id = id
        this.name = name
    }
    constructor()

    var id: Int = 0
    var name: String = ""
    @Ignore
    var places: ArrayList<Place>? = ArrayList()
}