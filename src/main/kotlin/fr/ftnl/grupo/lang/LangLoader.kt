package fr.ftnl.grupo.lang

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.DiscordLocale
import java.io.File

private val LANG_MANAGERS : MutableMap<String, LangManager> = mutableMapOf()

/**
 * Loads the lang files.
 */
class LangLoader {
	init {
		if (LANG_MANAGERS.isEmpty()) {
			val folder = File("lang")
			folder.mkdirs()
			folder.listFiles()?.forEach {
				val name = it.name.split(".").first()
				LANG_MANAGERS[name] = LangManager(name)
			}
		}
	}
	
	/**
	 * Get langManager by name.
	 * @param user [User] user.
	 * @param guild [Guild] guild.
	 * @return [LangManager] langManager.
	 */
	fun getLangManager(locals : DiscordLocale?) : LangManager {
		val code = getLangByLocals(locals)
			?: "en"
		if (!LANG_MANAGERS.containsKey(code)) LANG_MANAGERS[code] = LangManager(code)
		return LANG_MANAGERS[code]!!
	}
	
	private fun getLangByLocals(locals : DiscordLocale?) : String? {
		return locals?.locale?.split("-")?.firstOrNull()
	}
}
