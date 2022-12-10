package fr.ftnl.grupo.database.models

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.MessageCreate
import fr.ftnl.grupo.extentions.toLang
import fr.ftnl.grupo.lang.LangKey
import io.github.reactivecircus.cache4k.Cache
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import kotlin.time.Duration.Companion.minutes

object MatchmakingEvents : IntIdTable("matchmaking_events") {
    val game: Column<EntityID<Int>> = reference("game", Games)

    val message: Column<String> = text("message")

    val startDateTime: Column<DateTime> = datetime("start_date_time")
    val createdAt: Column<DateTime> = datetime("created_at").defaultExpression(CurrentDateTime)

    val guildInvite: Column<String> = text("guild_invite")
    val voiceChannelId: Column<String> = text("voice_channel_id")
    val guildId: Column<String> = text("guild_id")

    val localEvent: Column<Boolean> = bool("local_event")
    val repeatableDays: Column<Int?> = integer("repeatable_days").nullable().default(null)
}

class MatchmakingEvent(id: EntityID<Int>) : IntEntity(id) {

    companion object : IntEntityClass<MatchmakingEvent>(MatchmakingEvents) {
        val cache = Cache.Builder().expireAfterWrite(30.minutes).build<Int, MatchmakingEvent>()

        fun createEvent(
            game: Game, message: String, startDateTime: DateTime, guildInvite: String, voiceChannelId: String, guildId: String, localEvent: Boolean, repeatableDays: Int? = null
        ): MatchmakingEvent {
            val event = transaction {
                new {
                    this.game = game
                    this.message = message
                    this.startDateTime = startDateTime
                    this.guildInvite = guildInvite
                    this.voiceChannelId = voiceChannelId
                    this.guildId = guildId
                    this.localEvent = localEvent
                    this.repeatableDays = repeatableDays
                }
            }
            cache.put(event.id.value, event)
            return event
        }

        fun updateCache() {
            cache.invalidateAll()
            transaction {
                find { MatchmakingEvents.startDateTime greaterEq DateTime.now().minusHours(24) }.forEach {
                    cache.put(it.id.value, it)
                }
            }
        }
    }

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

    fun makeEventMessage(locale: DiscordLocale, channelLink: String): MessageCreateData {
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
                    name = "✅ - Participants (%d/%d)".toLang(
                        locale, LangKey.keyBuilder(this@MatchmakingEvent, "eventMessage", "participants")
                    ).format(active.size, game.players)
                    value = active.joinToString("\n") { p ->
                        "`-` [${getParticipantsPlatformName(p, game)}]($channelLink \"${p.user.discordUsername}\")"
                    }
                }
                field {
                    name = "❔ - En réserve (%d)".toLang(
                        locale, LangKey.keyBuilder(this@MatchmakingEvent, "eventMessage", "reserve")
                    ).format(waiting.size)
                    value = waiting.joinToString("\n") { p ->
                        "`-` [${getParticipantsPlatformName(p, game)}]($channelLink \"${p.user.discordUsername}\")"
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
                    ).format(
                        if (localEvent) {
                            "local".toLang(
                                locale, LangKey.keyBuilder(this@MatchmakingEvent, "eventMessage", "localValue")
                            )
                        } else {
                            "global".toLang(
                                locale, LangKey.keyBuilder(this@MatchmakingEvent, "eventMessage", "globalValue")
                            )
                        }
                    )
                }
            }
        }
    }

    fun endEvent() {
        if (repeatableDays != null) {
            createEvent(
                game, message, startDateTime.plusDays(repeatableDays!!), guildInvite, voiceChannelId, guildId, localEvent, repeatableDays
            )
        }
    }

    suspend fun diffuseEvent(guild: Guild): Boolean {
        var success = true
        if (!localEvent) {
            guild.jda.shardManager!!.guilds.forEach {
                val isLocalGuild = guild == it
                val result = sendEventMessage(it, isLocalGuild)
                if (!result && isLocalGuild) success = false
            }
        } else {
            success = sendEventMessage(guild, true)
        }
        return success
    }
    
    private suspend fun sendEventMessage(guild: Guild, localGuild: Boolean): Boolean {
        val config = GuildConfiguration.findGuildConfiguration(guild.idLong)
        
        var channelId = config.eventsChannels.firstOrNull { it.game == this.game }?.channelId
        
        if (localGuild) {
            channelId = channelId
                ?: config.defaultEventsChannel
                        ?: return false
        }
        
        val chanel = guild.channels.firstOrNull { it.idLong == channelId && it is MessageChannel }
            ?: return false
        chanel as MessageChannel
        chanel.sendMessage(
            makeEventMessage(
                guild.locale, "https://discord.com/channels/${guild.id}/$channelId"
            )
        ).addActionRow(
            Button.success(
                "MATCHMAKING_JOIN::${this.id.value}", Emoji.fromUnicode("✅")
            ).withLabel(
                "Participer".toLang(
                    guild.locale, LangKey.keyBuilder(this, "eventMessage", "joinButton")
                )
            ), Button.secondary(
                "MATCHMAKING_WAIT::${this.id.value}", Emoji.fromUnicode("❔")
            ).withLabel(
                "En réserve".toLang(
                    guild.locale, LangKey.keyBuilder(this, "eventMessage", "reserveButton")
                )
            ), Button.primary("MATCHMAKING_CONFIG::${this.id.value}", Emoji.fromUnicode("⚙️")), Button.danger("MATCHMAKING_CANCEL::${this.id.value}", Emoji.fromUnicode("🗑️"))
        ).queue {
            val event = this@MatchmakingEvent
            transaction {
                SendedMessage.new {
                    this.matchmakingEvent = event
                    this.messageId = it.idLong
                    this.channelId = it.channel.idLong
                    this.guild = config
                }
            }
        }
        return true
    }
    
    fun addParticipant(user: User, type: ParticipantType) {
        transaction {
            Participant.new {
                this.user = user
                this.type = type
                this.matchmakingEvent = this@MatchmakingEvent
            }
        }
    }
    
    fun removeParticipant(user: User) {
        transaction {
            this@MatchmakingEvent.participants.find { it.user.discordId == user.discordId }?.delete()
        }
    }
    
    suspend fun diffuseMessageUpdate(manager: ShardManager) {
        this.sendedMessages.forEach {
            val channel = manager.getTextChannelById(it.channelId)
            if (channel != null) {
                try {
                    val message = channel.retrieveMessageById(it.messageId).await()
                    message.editMessage(
                        MessageEditBuilder.fromCreateData(
                            makeEventMessage(
                                channel.guild.locale, "https://discord.com/channels/${channel.guild.id}/${channel.id}"
                            )
                        ).build()
                    ).queue()
                } catch (e: Exception) {
                }
            }
        }
    }
}
