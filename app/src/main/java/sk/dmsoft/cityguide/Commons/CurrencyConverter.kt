package sk.dmsoft.cityguide.Commons

import android.annotation.SuppressLint
import android.content.Context
import sk.dmsoft.cityguide.Api.DB
import sk.dmsoft.cityguide.Models.Currency

object CurrencyConverter {
    var currencies = ArrayList<Currency>()

    fun init(context: Context){
        try {
            currencies = DB(context).GetCurrencies()
        }
        catch (e: Exception){}
    }

    fun convert(value: Double): String{

        val currency = currencies.find { it.id == AccountManager.currency }
        if (currency != null) {
            if (currency.symbolBefore == null)
                currency.symbolBefore = ""
            if (currency.symbolAfter == null)
                currency.symbolAfter = ""
            if (currency.rate * value < 0.1)
                return "FREE"
            return "${currency.symbolBefore}${(currency.rate.toBigDecimal() * value.toBigDecimal()).setScale(2)}${currency.symbolAfter}"
        }
        return "${value}€"
    }

}