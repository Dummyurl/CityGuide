package sk.dmsoft.cityguide.Api

import android.content.Context
import ninja.sakib.pultusorm.core.PultusORM
import ninja.sakib.pultusorm.core.PultusORMCondition
import sk.dmsoft.cityguide.Models.*

/**
 * Created by Daniel on 23. 11. 2017.
 */

class DB (val context: Context) {
    val appPath = context.filesDir.absolutePath  // Output : /data/data/application_package_name/files/
    val pultusORM: PultusORM = PultusORM("cityGuide.db", appPath)

    fun SaveCountries(items: ArrayList<Country>){
        items.forEach {
            pultusORM.save(it)
        }
    }

    fun GetCountries(): ArrayList<Country> {
        val c = pultusORM.find(Country()) as ArrayList<Country>
        return c
    }

    fun SavePlaces(items: ArrayList<Place>){
        items.forEach {
            pultusORM.save(it)
        }
    }

    fun GetPlaces(): ArrayList<Place> {
        return pultusORM.find(Place()) as ArrayList<Place>
    }

    fun GetPlace(id: Int): Place{
        val query = PultusORMCondition.Builder()
                .eq("id", id).build()
        return pultusORM.find(Place(), query).first() as Place
    }

    fun SaveInterests(items: ArrayList<Interest>){
        items.forEach {
            pultusORM.save(it)
        }
    }

    fun SaveLanguages(items: ArrayList<Language>){
        items.forEach {
            pultusORM.save(it)
        }
    }

    fun GetLanguages(): ArrayList<Language>{
        return pultusORM.find(Language()) as ArrayList<Language>
    }

    fun GetInterests(): ArrayList<Interest>{
        return pultusORM.find(Interest()) as ArrayList<Interest>
    }

    fun SaveSelectedInterests(items: ArrayList<SelectedInterest>){
        Drop(SelectedInterest())
        items.forEach {
            pultusORM.save(it)
        }
    }

    fun SaveSelectedLanguages(items: ArrayList<SelectedLanguage>){
        Drop(SelectedLanguage())
        items.forEach {
            pultusORM.save(it)
        }
    }

    fun GetSelectedInterests(): ArrayList<SelectedInterest>{
        val i = pultusORM.find(SelectedInterest()) as ArrayList<SelectedInterest>
        return i
    }

    fun GetSelectedLanguages(): ArrayList<SelectedLanguage>{
        return pultusORM.find(SelectedLanguage()) as ArrayList<SelectedLanguage>
    }

    fun DeleteSelectedInterests(){
        Drop(SelectedInterest())
    }

    fun DeleteSelectedLanguages(){
        Drop(SelectedLanguage())
    }

    fun SaveCurrencies(items: ArrayList<Currency>){
        items.forEach {
            pultusORM.save(it)
        }
    }

    fun GetCurrencies(): ArrayList<Currency>{
        return pultusORM.find(Currency()) as ArrayList<Currency>
    }

    fun Drop(objectType: Any){
        pultusORM.drop(objectType)
    }


}