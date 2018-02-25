package sk.dmsoft.cityguide.Commons


/**
 * Created by Daniel on 13. 11. 2017.
 */
enum class EAccountType (val value: Int){
    administrator(0), guide(1), tourist(2);

    companion object {
        private val map = EAccountType.values().associateBy(EAccountType::value)
        fun fromInt(type: Int) = map[type]
    }
}