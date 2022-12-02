package fr.ftnl.grupo.database.models

import dev.minn.jda.ktx.messages.MessageCreate
import fr.ftnl.grupo.CONFIG
import fr.ftnl.grupo.extentions.toLang
import fr.ftnl.grupo.lang.LangKey
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.utils.messages.MessageCreateData
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
    
    val message: Column<String> = text("message")
    
    val startDateTime: Column<DateTime> = datetime("start_date_time")
    val createdAt: Column<DateTime> = datetime("created_at").defaultExpression(CurrentDateTime)
    
    val guildInvite: Column<String> = text("guild_invite")
    val voiceChannelId: Column<String> = text("voice_channel_id")
    
    val localEvent: Column<Boolean> = bool("local_event")
}

class MatchmakingEvent(id: EntityID<Int>) : IntEntity(id) {
    
    companion object : IntEntityClass<MatchmakingEvent>(MatchmakingEvents)
    
    val game by Game referencedOn MatchmakingEvents.game
    
    val message by MatchmakingEvents.message
    
    val startDateTime by MatchmakingEvents.startDateTime
    val createdAt by MatchmakingEvents.createdAt
    val guildInvite by MatchmakingEvents.guildInvite
    val voiceChannelId by MatchmakingEvents.voiceChannelId
    
    val localEvent by MatchmakingEvents.localEvent
    
    val participants by Participant referrersOn Participants.matchmakingEvent
    
    private fun getParticipantsByType(type: ParticipantType) = participants.filter { it.type == type }
    private fun getParticipantsPlatformName(participant: Participant, game: Game): String {
        return when (game.platform) {
            GamePlatform.PC_EPIC       -> participant.user.epicGameTag
            GamePlatform.PC_STEAM      -> participant.user.steamGameTag
            GamePlatform.PS4           -> participant.user.psnGameTag
            GamePlatform.XBOX          -> participant.user.xboxGameTag
            GamePlatform.PC_BATTLE_NET -> participant.user.battleNetGameTag
            GamePlatform.PC_ORIGIN     -> participant.user.originGameTag
            GamePlatform.PC_UBISOFT    -> participant.user.ubisoftGameTag
            GamePlatform.SWITCH        -> participant.user.switchGameTag
            else                       -> null
        }
            ?: participant.user.discordUsername
    }
    
    fun makeEventMessage(locale: DiscordLocale): MessageCreateData {
        val active = getParticipantsByType(ParticipantType.PARTICIPANT)
        val waiting = getParticipantsByType(ParticipantType.WAITING)
        
        return MessageCreate {
            content = message
            embed {
                title = game.name
                description = game.description
                image = game.image
                timestamp = createdAt.toDate().toInstant()
                color = 0x000000
                
                field {
                    name = "✅ - Participants (%d)".toLang(
                            locale, LangKey.keyBuilder(this@MatchmakingEvent, "eventMessage", "participants")
                        ).format(active.size)
                    value = active.joinToString("\n") { p ->
                        "`-` [${getParticipantsPlatformName(p, game)}](https://discord.gg/ \"${p.user.discordUsername}\")"
                    }
                }
                
                field {
                    name = "❔ - En réserve (%d)".toLang(
                            locale, LangKey.keyBuilder(this@MatchmakingEvent, "eventMessage", "reserve")
                        ).format(waiting.size)
                    value = waiting.joinToString("\n") { p ->
                        "`-` [${getParticipantsPlatformName(p, game)}](https://discord.gg/ \"${p.user.discordUsername}\")"
                    }
                }
                
                field {
                    name = "📅 - On se retrouve :".toLang(
                        locale, LangKey.keyBuilder(this@MatchmakingEvent, "eventMessage", "date")
                    )
                    value = """
                        Sur le serveur : %s
                        Dans le salon vocal <#%s>
                        le <t:%s:f> (<t:%s:R>)
                    """.trimIndent().toLang(
                        locale, LangKey.keyBuilder(this@MatchmakingEvent, "eventMessage", "dateValue")
                    ).format(guildInvite, voiceChannelId, startDateTime.millis / 1000, startDateTime.millis / 1000)
                }
                
                footer {
                    name = "Cet évènement est %s".toLang(
                        locale, LangKey.keyBuilder(this@MatchmakingEvent, "eventMessage", "local")
                    ).format(if (localEvent) "local" else "global")
                }
            }
        }
    }
}
