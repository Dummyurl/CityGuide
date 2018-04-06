package sk.dmsoft.cityguide.Commons

enum class MapMode(val value: Int) {
    SetMeetingPoint(1), GoToMeetingPoint(2), Inactive(3);

    companion object {
        private val map = MapMode.values().associateBy(MapMode::value)
        fun fromInt(type: Int) = map[type]
    }
}