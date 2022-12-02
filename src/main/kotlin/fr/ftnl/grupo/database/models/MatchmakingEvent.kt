package fr.ftnl.grupo.database.models

import fr.ftnl.grupo.CONFIG
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object MatchmakingEvents : IntIdTable("${CONFIG.dbConfig.prefix}matchmaking_events") {
    val game: Column<EntityID<Int>> = reference("game", Games)
    
    val startDateTime: Column<DateTime> = datetime("start_date_time")
    val endDateTime: Column<DateTime> = datetime("end_date_time")
    val createdAt: Column<DateTime> = datetime("created_at").defaultExpression(CurrentDateTime)
    
    val guildInvite: Column<String> = text("guild_invite")
    val voiceChannelId: Column<String> = text("voice_channel_id")
}

class MatchmakingEvent(id: EntityID<Int>) : IntEntity(id) {
    
    companion object : IntEntityClass<MatchmakingEvent>(MatchmakingEvents)
    
    val game by Game referencedOn MatchmakingEvents.game
    val startDateTime by MatchmakingEvents.startDateTime
    val endDateTime by MatchmakingEvents.endDateTime
    val createdAt by MatchmakingEvents.createdAt
    val guildInvite by MatchmakingEvents.guildInvite
    val voiceChannelId by MatchmakingEvents.voiceChannelId

    val participants by Participant referrersOn Participants.matchmakingEvent
}
