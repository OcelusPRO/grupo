package fr.ftnl.grupo

import fr.ftnl.grupo.config.Configuration
import fr.ftnl.grupo.core.Bot
import fr.ftnl.grupo.database.DBManager
import fr.ftnl.grupo.lang.LangKey
import fr.ftnl.grupo.lang.LangLoader
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.interactions.DiscordLocale
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class Tests {
	@Test
	fun `configuration file test`(){
		assertDoesNotThrow("No exceptions should be thrown") {
			val CONFIG = Configuration.loadConfiguration(File("./config.json"))
		}
	}
	
	@Test
	fun `languages tests`(): Unit {
		assertDoesNotThrow {
			val frLang = LangLoader().getLangManager(DiscordLocale.FRENCH)
			val enLang = LangLoader().getLangManager(DiscordLocale.ENGLISH_US)
			
			val frString = frLang.getString(LangKey.keyBuilder(this, "unite", "langTest"), "test en francais")
			val enString = enLang.getString(LangKey.keyBuilder(this, "unite", "langTest"), "test en francais")
			
			assertNotEquals(enString, frString)
			
			assertEquals("test en francais", frString)
			assertEquals("english test", enString)
		}
	}
	
	@Test
	fun `database connection test`(){
		assertDoesNotThrow {
			val bdd = DBManager(Configuration.loadConfiguration(File("./config.json")))
			val con = bdd.connection
			println(con.version)
			assertTrue(con.version.toFloat() >= 5.5f, "The database version should be at least 5.5")
		}
	}
	
	@Test
	fun `start discord bot`() : Unit {
		runBlocking {
			val bot = Bot(Configuration.loadConfiguration(File("./config.json")))
			val manager = bot.manager
			delay(5000) // Laissé le temps au bot de se connecté avant de vérifier le status
			assertEquals(manager.statuses.entries.firstOrNull()?.value, JDA.Status.CONNECTED)
			
			manager.shutdown()
			delay(5000) // Laissé le temps au bot de se déconnecté avant de vérifier le status
			assertEquals(manager.statuses.entries.firstOrNull()?.value, JDA.Status.SHUTDOWN)
		}
	}
}

