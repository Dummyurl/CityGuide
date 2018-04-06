package sk.dmsoft.cityguide.Models.Chat

enum class MessageType(val value: Int) {
    Message(0), ProposalStarted(1), Map(2), MeetingPoint(3), Image(4);

    companion object {
        private val map = MessageType.values().associateBy(MessageType::value)
        fun fromInt(type: Int) = map[type]
    }
}