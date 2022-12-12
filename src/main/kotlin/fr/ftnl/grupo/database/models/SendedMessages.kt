package fr.ftnl.grupo.database.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object SendedMessages : IntIdTable("TBL_SENDEDMESSAGES_SMS") {
    val matchmakingEvent: Column<EntityID<Int>> = reference("matchmaking_event", MatchmakingEvents)
    val messageId: Column<Long> = long("message_id")
    val channelId: Column<Long> = long("channel_id")
    val guild: Column<EntityID<Int>> = reference("guild", GuildConfigurations)
    val registerAt: Column<DateTime> = datetime("register_at").defaultExpression(CurrentDateTime)
}

class SendedMessage(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SendedMessage>(SendedMessages)
    
    var matchmakingEvent by MatchmakingEvent referencedOn SendedMessages.matchmakingEvent
    var messageId by SendedMessages.messageId
    var channelId by SendedMessages.channelId
    var guild by GuildConfiguration referencedOn SendedMessages.guild
    val registerAt by SendedMessages.registerAt
}
