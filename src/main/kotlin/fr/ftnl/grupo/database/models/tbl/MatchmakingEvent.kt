package fr.ftnl.grupo.database.models.tbl

import fr.ftnl.grupo.database.models.tbj.Participant
import fr.ftnl.grupo.database.models.tbj.Participants
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object MatchmakingEvents : IntIdTable("TBL_MATCHMAKINGEVENTS_MEV") {
    val game: Column<EntityID<Int>> = reference("game", Games)
    
    val message: Column<String> = text("message")
    
    val startDateTime: Column<DateTime> = datetime("start_date_time")
    val createdAt: Column<DateTime> = datetime("created_at").defaultExpression(CurrentDateTime)
    
    val guildInvite: Column<String> = text("guild_invite")
    val voiceChannelId: Column<Long> = long("voice_channel_id")
    val guildId: Column<Long> = long("guild_id")
    
    val localEvent: Column<Boolean> = bool("local_event")
    val repeatableDays: Column<Int?> = integer("repeatable_days").nullable().default(null)
}

class MatchmakingEvent(id: EntityID<Int>) : IntEntity(id) {
    
    companion object : IntEntityClass<MatchmakingEvent>(MatchmakingEvents)
    
    var game by Game referencedOn MatchmakingEvents.game
    
    var message by MatchmakingEvents.message
    
    var startDateTime by MatchmakingEvents.startDateTime
    var createdAt by MatchmakingEvents.createdAt
    var guildInvite by MatchmakingEvents.guildInvite
    var voiceChannelId by MatchmakingEvents.voiceChannelId
    var guildId by MatchmakingEvents.guildId
    
    var localEvent by MatchmakingEvents.localEvent
    var repeatableDays by MatchmakingEvents.repeatableDays
    
    val participants by Participant referrersOn Participants.matchmakingEvent
    val sendedMessages by SendedMessage referrersOn SendedMessages.matchmakingEvent
    
}
