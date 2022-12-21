package fr.ftnl.grupo.database.models.tbl

import fr.ftnl.grupo.database.abstract.BaseIntIdTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column

object SendedMessages : BaseIntIdTable("TBL_SENDEDMESSAGES_SMS") {
    val matchmakingEvent: Column<EntityID<Int>> = reference("matchmaking_event", MatchmakingEvents)
    val messageId: Column<Long> = long("message_id").uniqueIndex()
    val channelId: Column<Long> = long("channel_id")
    val guild: Column<EntityID<Int>> = reference("guild", GuildConfigurations)
}

class SendedMessage(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SendedMessage>(SendedMessages)
    
    var matchmakingEvent by MatchmakingEvent referencedOn SendedMessages.matchmakingEvent
    var messageId by SendedMessages.messageId
    var channelId by SendedMessages.channelId
    var guild by GuildConfiguration referencedOn SendedMessages.guild
    var createdAt by SendedMessages.createdAt
    var updatedAt by SendedMessages.updatedAt
}
