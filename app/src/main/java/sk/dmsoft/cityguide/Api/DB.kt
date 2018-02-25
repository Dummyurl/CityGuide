package sk.dmsoft.cityguide.Api

import android.content.Context
import ninja.sakib.pultusorm.core.PultusORM
import sk.dmsoft.cityguide.Models.Country
import sk.dmsoft.cityguide.Models.Place

/**
 * Created by Daniel on 23. 11. 2017.
 */

public class DB (val context: Context) {
    val appPath = context.filesDir.absolutePath  // Output : /data/data/application_package_name/files/
    val pultusORM: PultusORM = PultusORM("cityGuide.db", appPath)

    fun SaveCountries(items: ArrayList<Country>){
        items.forEach {
            pultusORM.save(it)
        }
    }

    fun GetCountries(): ArrayList<Country> {
        return pultusORM.find(Country()) as ArrayList<Country>
    }

    fun SavePlaces(items: ArrayList<Place>){
        items.forEach {
            pultusORM.save(it)
        }
    }

    fun GetPlaces(): ArrayList<Place> {
        return pultusORM.find(Place()) as ArrayList<Place>
    }

    fun Drop(objectType: Any){
        pultusORM.drop(objectType)
    }


}