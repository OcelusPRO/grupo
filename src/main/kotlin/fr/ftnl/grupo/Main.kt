package fr.ftnl.grupo

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import fr.ftnl.grupo.config.Configuration
import java.io.File

const val DEFAULT_CONFIG_FILE_PATH = "./config.json"

val GSON: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()

lateinit var CONFIG: Configuration

fun main(args: Array<String>) {
    CONFIG = Configuration.loadConfiguration(
        File(
            if (args.isNotEmpty()) {
                args[0].ifBlank { DEFAULT_CONFIG_FILE_PATH }
            } else {
                DEFAULT_CONFIG_FILE_PATH
            }
        )
    )
}
