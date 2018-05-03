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
        AccountManager.currency = "EUR"
        if (AccountManager.currency == "EUR")
            return "$value€"
        val currency = currencies.find { it.id == AccountManager.currency }
        if (currency != null)
            return "${(currency.rate.toBigDecimal() * value.toBigDecimal()).setScale(2)}${currency.symbol}"
        return "${value}€"
    }

}