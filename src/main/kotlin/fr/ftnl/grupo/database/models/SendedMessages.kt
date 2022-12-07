package fr.ftnl.grupo.database.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object SendedMessages : IntIdTable("sent_messages") {
    val matchmakingEvent: Column<EntityID<Int>> = reference("matchmaking_event", MatchmakingEvents)
    val messageId: Column<Long> = long("message_id")
    val channelId: Column<Long> = long("channel_id")
    val guildId: Column<Long> = long("guild_id")
    val registerAt: Column<DateTime> = datetime("register_at").defaultExpression(CurrentDateTime)
}

class SendedMessage(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SendedMessage>(SendedMessages)
    
    val matchmakingEvent by MatchmakingEvent referencedOn SendedMessages.matchmakingEvent
    val messageId by SendedMessages.messageId
    val channelId by SendedMessages.channelId
    val guildId by SendedMessages.guildId
    val registerAt by SendedMessages.registerAt
}
