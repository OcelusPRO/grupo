package fr.ftnl.grupo.config

import com.google.gson.Gson
import fr.ftnl.grupo.GSON
import fr.ftnl.grupo.config.dependent.BotConfig
import fr.ftnl.grupo.config.dependent.DatabaseConfig
import java.io.File

data class Configuration(
	val botConfig : BotConfig = BotConfig(),
	val dbConfig : DatabaseConfig = DatabaseConfig(),
) {
	class ConfigurationException(message : String) : Exception(message)
	
	fun saveConfiguration() : Configuration {
		if (!configFile.exists()) configFile.createNewFile()
		configFile.writeText(GSON.toJson(this))
		return this
	}
	
	companion object {
		var configFile = File("./config.json")
		
		/**
		 * Loads the configuration from the given file.
		 * @param file [File] The file to load the configuration from.
		 * @return The loaded [Configuration].
		 * @throws ConfigurationException If the file is not a valid configuration file.
		 */
		fun loadConfiguration(file : File) : Configuration {
			if (file.createNewFile()) {
				val config = Configuration()
				file.writeText(GSON.toJson(config))
				throw ConfigurationException("Veuillez remplir le fichier de configuration")
			}
			configFile = file
			return try {
				val cfg = Gson().fromJson(file.readText(), Configuration::class.java)
				cfg.saveConfiguration()
			} catch (e : Exception) {
				e.printStackTrace()
				throw ConfigurationException("La configuration n'est pas valide")
			}
		}
	}
	
}