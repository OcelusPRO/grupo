package fr.ftnl.grupo.extentions

import fr.ftnl.grupo.lang.LangKey
import fr.ftnl.grupo.lang.LangLoader
import net.dv8tion.jda.api.interactions.DiscordLocale

fun String.toLang(locale: DiscordLocale, key: LangKey): String = LangLoader()
    .getLangManager(locale)
    .getString(key, this)
