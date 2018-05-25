package sk.dmsoft.cityguide.Commons

import android.annotation.SuppressLint
import android.content.Context
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Models.Currency

object CurrencyConverter {
    var currencies = ArrayList<Currency>()

    fun init(context: Context){
        currencies = DB(context).GetCurrencies()
    }

    fun convert(value: Double): String{

        val currency = currencies.find { it.id == AccountManager.currency }
        if (currency != null)
            return "${currency.symbolBefore}${(currency.rate.toBigDecimal() * value.toBigDecimal()).setScale(2)}${currency.symbolAfter}"
        return "${value}€"
    }

}