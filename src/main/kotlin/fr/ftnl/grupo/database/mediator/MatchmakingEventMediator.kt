package fr.ftnl.grupo.database.mediator

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.messages.MessageCreate
import fr.ftnl.grupo.database.models.tbj.Participant
import fr.ftnl.grupo.database.models.tbj.ParticipantType
import fr.ftnl.grupo.database.models.tbl.*
import fr.ftnl.grupo.extentions.toLang
import fr.ftnl.grupo.lang.LangKey
import fr.ftnl.grupo.objects.EventGuildInfo
import fr.ftnl.grupo.objects.EventTimeInfo
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import kotlin.time.Duration.Companion.minutes

object MatchmakingEventMediator {
    val cache = Cache.Builder().expireAfterWrite(30.minutes).build<Int, MatchmakingEvent>()

    fun createEvent(
        game: Game, message: String, localEvent: Boolean, guildInfo: EventGuildInfo, timeInfo: EventTimeInfo
    ): MatchmakingEvent {
        val event = transaction {
            MatchmakingEvent.new {
                this.game = game
                this.message = message
                this.startDateTime = timeInfo.startDateTime
                this.guildInvite = guildInfo.guildInvite
                this.voiceChannelId = guildInfo.voiceChannelId
                this.guildId = guildInfo.guildId
                this.localEvent = localEvent
                this.repeatableDays = timeInfo.repeatableDays
            }
        }
        cache.put(event.id.value, event)
        return event
    }

    fun updateCache() {
        cache.invalidateAll()
        transaction {
            MatchmakingEvent.find { MatchmakingEvents.startDateTime greaterEq DateTime.now().minusHours(24) }.forEach {
                cache.put(it.id.value, it)
            }
        }
    }

    /*
    *
    *
    *
    * */

    fun getEventParticipants(event: MatchmakingEvent) = transaction { event.participants.toList() }

    private fun getMatchmakingEvent(id: Int) = cache.get(id)

    suspend fun diffuseEvent(guild: Guild, eventId: Int): Boolean {
        val event = getMatchmakingEvent(eventId)
            ?: return false
        var success = true
        if (transaction { !event.localEvent }) {
            guild.jda.shardManager!!.guilds.forEach {
                val isLocalGuild = guild == it
                val result = sendEventMessage(it, isLocalGuild, event)
                if (!result && isLocalGuild) success = false
            }
        } else {
            success = sendEventMessage(guild, true, event)
        }
        return success
    }
    
    private fun getParticipantsByType(type: ParticipantType, event: MatchmakingEvent): List<Participant> {
        return transaction { event.participants.filter { it.type == type } }
    }
    
    private fun getParticipantsPlatformName(participant: Participant, game: Game): String {
        return UsersMediator.getUserPlateformes(participant.user).find { it.plateforme == game.platform }?.gametag
            ?: participant.user.discordUsername
    }
    
    suspend fun makeEventMessage(guild: Guild, channelLink: String, event: MatchmakingEvent, forGuild: Guild? = null): MessageCreateData {
        val active = getParticipantsByType(ParticipantType.PARTICIPANT, event)
        val waiting = getParticipantsByType(ParticipantType.WAITING, event)
        val game = transaction { event.game }
        val config = GuildConfigurationMediator.getGuildConfiguration(guild.idLong)
        
        val mention = transaction {
            config.eventsRoles.find { it.game == event.game }
                ?: config.defaultEventRole
        }
        
        var message = event.message.replace("@", "@\u200B")
        if (mention != null) message = message.replace("<ROLE_MENTION>", "<@&${config.id}>")
        
        return MessageCreate {
            
            content = message
            
            embed {
                title = game.name
                description = game.description
                image = game.image
                timestamp = event.createdAt.toDate().toInstant()
                color = 0x000000
                field {
                    name = "✅ - Participants (%d/%d)".toLang(
                        guild.locale, LangKey.keyBuilder(this@MatchmakingEventMediator, "eventMessage", "participants")
                    ).format(active.size, game.players)
                    value = active.joinToString("\n") { p ->
                        "`-` [${getParticipantsPlatformName(p, game)}]($channelLink \"${p.user.discordUsername}\")"
                    }
                    inline = true
                }
                field {
                    name = "❔ - En réserve (%d)".toLang(
                        guild.locale, LangKey.keyBuilder(this@MatchmakingEventMediator, "eventMessage", "reserve")
                    ).format(waiting.size)
                    value = waiting.joinToString("\n") { p ->
                        "`-` [${getParticipantsPlatformName(p, game)}]($channelLink \"${p.user.discordUsername}\")"
                    }
                    inline = true
                }
                field {
                    name = "📅 - On se retrouve :".toLang(
                        guild.locale, LangKey.keyBuilder(this@MatchmakingEventMediator, "eventMessage", "date")
                    )
                    value = """
                        Sur le serveur : %s
                        Dans le salon vocal <#%s>
                        le <t:%s:f> (<t:%s:R>)
                    """.trimIndent().toLang(
                        guild.locale, LangKey.keyBuilder(this@MatchmakingEventMediator, "eventMessage", "dateValue")
                    ).format(
                        event.guildInvite, event.voiceChannelId, event.startDateTime.millis / 1000, event.startDateTime.millis / 1000
                    )
                    inline = false
                }
                footer {
                    name = "Cet évènement est %s".toLang(
                        guild.locale, LangKey.keyBuilder(this@MatchmakingEventMediator, "eventMessage", "local")
                    ).format(
                        if (event.localEvent) {
                            "local".toLang(
                                guild.locale, LangKey.keyBuilder(this@MatchmakingEventMediator, "eventMessage", "localValue")
                            )
                        } else {
                            "global".toLang(
                                guild.locale, LangKey.keyBuilder(this@MatchmakingEventMediator, "eventMessage", "globalValue")
                            )
                        }
                    )
                }
            }
        }
    }
    
    fun endEvent(event: MatchmakingEvent) {
        if (event.repeatableDays != null) {
            createEvent(
                game = event.game, message = event.message, localEvent = event.localEvent, guildInfo = EventGuildInfo(
                    guildId = event.guildId, guildInvite = event.guildInvite, voiceChannelId = event.voiceChannelId
                ), timeInfo = EventTimeInfo(
                    startDateTime = event.startDateTime.plusDays(event.repeatableDays!!), repeatableDays = event.repeatableDays
                )
            )
        }
    }
    
    fun addParticipant(user: User, type: ParticipantType, event: MatchmakingEvent) {
        transaction {
            Participant.new {
                this.user = user
                this.type = type
                this.matchmakingEvent = event
            }
        }
    }
    
    fun removeParticipant(user: User, event: MatchmakingEvent) {
        transaction {
            event.participants.find { it.user.discordId == user.discordId }?.delete()
        }
    }
    
    suspend fun diffuseMessageUpdate(manager: ShardManager, event: MatchmakingEvent) {
        val msg = transaction { event.sendedMessages }
        transaction {
            msg.forEach {
                runBlocking { messageEditor(manager, it, event) }
            }
        }
    }
    
    private suspend fun messageEditor(manager: ShardManager, it: SendedMessage, event: MatchmakingEvent) {
        val channel = manager.getTextChannelById(transaction { it.channelId })
        if (channel != null) {
            try {
                val message = runBlocking { channel.retrieveMessageById(transaction { it.messageId }).await() }
                message.editMessage(
                    MessageEditBuilder.fromCreateData(
                        makeEventMessage(
                            channel.guild, "https://discord.com/channels/${channel.guild.id}/${channel.id}", event
                        )
                    ).build()
                ).setActionRow(
                        buttons(event, channel.guild.locale)
                    ).queue()
            } catch (ignored: Exception) { /*ignored*/
            }
        }
    }
    
    private fun buttons(event: MatchmakingEvent, locale: DiscordLocale) = listOf(
        Button.success(
            "MATCHMAKING_JOIN::${event.id.value}", Emoji.fromUnicode("✅")
        ).withLabel(
            "Participer".toLang(
                locale, LangKey.keyBuilder(this, "eventMessage", "joinButton")
            )
        ), Button.secondary(
            "MATCHMAKING_WAIT::${event.id.value}", Emoji.fromUnicode("❔")
        ).withLabel(
            "En réserve".toLang(
                locale, LangKey.keyBuilder(this, "eventMessage", "reserveButton")
            )
        ), Button.primary(
            "MATCHMAKING_CONFIG::${event.id.value}", Emoji.fromUnicode("⚙️")
        ).asDisabled(),
        
        Button.danger(
            "MATCHMAKING_CANCEL::${event.id.value}", Emoji.fromUnicode("🗑️")
        )
    )
    
    private suspend fun sendEventMessage(guild: Guild, localGuild: Boolean, event: MatchmakingEvent): Boolean {
        val config = GuildConfigurationMediator.getGuildConfiguration(guild.idLong)
        
        var channelId = transaction { config.eventsChannels.firstOrNull { it.game == event.game }?.channelId }
        
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
                guild, "https://discord.com/channels/${guild.id}/$channelId", event
            )
        ).addActionRow(
            buttons(event, chanel.guild.locale)
        ).queue {
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
}
