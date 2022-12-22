package fr.ftnl.grupo

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import fr.ftnl.grupo.config.Configuration
import fr.ftnl.grupo.core.Bot
import fr.ftnl.grupo.database.DBManager
import fr.ftnl.grupo.database.mediator.MatchmakingEventMediator
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.sharding.ShardManager
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

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
        executeEveryTime(1.minutes) { async { MatchmakingEventMediator.updateCache() }.start() } // update event cache
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
