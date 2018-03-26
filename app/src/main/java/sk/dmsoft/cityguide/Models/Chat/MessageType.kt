package sk.dmsoft.cityguide.Models.Chat

enum class MessageType(val value: Int) {
    Message(0), Map(1), Image(2);

    companion object {
        private val map = MessageType.values().associateBy(MessageType::value)
        fun fromInt(type: Int) = map[type]
    }
}