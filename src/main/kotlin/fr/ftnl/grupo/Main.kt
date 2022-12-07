package fr.ftnl.grupo

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.minn.jda.ktx.coroutines.await
import fr.ftnl.grupo.config.Configuration
import fr.ftnl.grupo.core.Bot
import fr.ftnl.grupo.database.DBManager
import fr.ftnl.grupo.database.models.MatchmakingEvent
import fr.ftnl.grupo.database.models.ParticipantType
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

const val DEFAULT_CONFIG_FILE_PATH = "./config.json"

val GSON: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()

lateinit var CONFIG: Configuration
lateinit var BOT: ShardManager
fun main(args: Array<String>) {
    CONFIG = Configuration.loadConfiguration(File(if (args.isNotEmpty()) {
        args[0].ifBlank { DEFAULT_CONFIG_FILE_PATH }
    } else {
        DEFAULT_CONFIG_FILE_PATH
    }))
    DBManager(CONFIG)
    BOT = Bot(CONFIG).manager
    
    runBlocking {
        while (BOT.statuses.entries.all { it.value != JDA.Status.CONNECTED }) {
            delay(1000)
        }
        executeEveryTime(10.seconds) { async { updateEvents(BOT) }.start() } // check cache for update events
        executeEveryTime(1.minutes) { async { MatchmakingEvent.updateCache() }.start() } // update event cache
    }
}

suspend fun executeEveryTime(time: Duration, block: suspend () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    delay(time)
    executeEveryTime(time, block)
}

suspend fun updateEvents(manager: ShardManager) {
    val events = MatchmakingEvent.cache.asMap()
    events.entries.forEach { event ->
        val messages = event.value.sendedMessages
        messages.forEach { msg ->
            try {
                val discordMessage = manager.getTextChannelById(msg.channelId)?.retrieveMessageById(msg.messageId)?.await()
                    ?: throw NullPointerException("Message not found")
                val participants = discordMessage.embeds.firstOrNull()?.fields?.firstOrNull()?.value?.split("\n")?.size
                    ?: 0
                val waitedUsers = discordMessage.embeds.firstOrNull()?.fields?.getOrNull(1)?.value?.split("\n")?.size
                    ?: 0
    
                val eventParticipants = event.value.participants.filter { it.type == ParticipantType.PARTICIPANT }
                val eventWaitedUsers = event.value.participants.filter { it.type == ParticipantType.WAITING }
                if ((eventParticipants.size != participants) || (eventWaitedUsers.size != waitedUsers)) {
                    val newMessage = event.value.makeEventMessage(
                        locale = discordMessage.guild.locale,
                        channelLink = "https://discord.com/channels/${msg.guildId}/${msg.channelId}",
                    )
                    discordMessage.editMessage(MessageEditBuilder.fromCreateData(newMessage).build()).queue()
                }
            } catch (ignored: NullPointerException) { /* ignored */
            }
        }
    }
}
