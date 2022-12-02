package fr.ftnl.grupo.lang

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.DiscordLocale
import java.io.File

private object LangManagers {
    val LANG_MANAGERS: MutableMap<String, LangManager> = mutableMapOf()
}

/**
 * Loads the lang files.
 */
class LangLoader {
    init {
        if (LangManagers.LANG_MANAGERS.isEmpty()) {
            val folder = File("lang")
            folder.mkdirs()
            folder.listFiles()?.forEach {
                val name = it.name.split(".").first()
                LangManagers.LANG_MANAGERS[name] = LangManager(name)
            }
        }
    }
	
    /**
     * Get langManager by name.
     * @param user [User] user.
     * @param guild [Guild] guild.
     * @return [LangManager] langManager.
     */
    fun getLangManager(locals: DiscordLocale?): LangManager {
        val code = getLangByLocals(locals)
            ?: "en"
        if (!LangManagers.LANG_MANAGERS.containsKey(code)) LangManagers.LANG_MANAGERS[code] = LangManager(code)
        return LangManagers.LANG_MANAGERS[code]!!
    }
	
    private fun getLangByLocals(locals: DiscordLocale?): String? {
        return locals?.locale?.split("-")?.firstOrNull()
    }
}
