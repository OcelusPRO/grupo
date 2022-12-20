package fr.ftnl.grupo.core.commands.list.slash

import fr.ftnl.grupo.core.commands.ISlashCmd
import fr.ftnl.grupo.database.mediator.GuildConfigurationMediator
import fr.ftnl.grupo.extentions.toLang
import fr.ftnl.grupo.lang.LangKey
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class DefaultMatchmakingMention : ISlashCmd {
    override val description: String
        get() = "Configurer les salons pour affiché les événements"
    override val localizedDescriptions: Map<DiscordLocale, String>
        get() = mapOf()
    override val options: List<OptionData>
        get() = listOf(
            OptionData(OptionType.ROLE, "role", "Le rôle mentionné par défaut", true)
        )
    
    override suspend fun action(event: SlashCommandInteractionEvent) {
        val role = event.getOption("role")!!.asRole
        GuildConfigurationMediator.setDefaultMatchmakingMention(event.guild!!.idLong, role.idLong)
        event.reply( // TODO extract role mention from lang
            "Le rôle mentionné par defaut est maintenant ${role.asMention}".toLang(
                event.userLocale, LangKey.keyBuilder(this, "action", "defaultChannelSet")
            )
        ).setEphemeral(true).queue()
    }
    
    override val localizedNames: Map<DiscordLocale, String>
        get() = mapOf()
    override val name: String
        get() = "default-event-channel"
    override val userPermissions: List<Permission>
        get() = listOf(Permission.MANAGE_EVENTS)
    override val allowDM: Boolean
        get() = false
}