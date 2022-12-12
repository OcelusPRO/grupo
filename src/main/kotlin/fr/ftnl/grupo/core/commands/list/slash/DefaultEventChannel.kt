package fr.ftnl.grupo.core.commands.list.slash

import fr.ftnl.grupo.core.commands.ISlashCmd
import fr.ftnl.grupo.database.mediator.GuildConfigurationMediator
import fr.ftnl.grupo.extentions.toLang
import fr.ftnl.grupo.lang.LangKey
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class DefaultEventChannel : ISlashCmd {
    override val description: String
        get() = "Configurer les salons pour affiché les événements"
    override val localizedDescriptions: Map<DiscordLocale, String>
        get() = mapOf()
    override val options: List<OptionData>
        get() = listOf()
    
    override suspend fun action(event: SlashCommandInteractionEvent) {
        GuildConfigurationMediator.setDefaultEventChannel(event.guild!!.idLong, event.channel.idLong)
        event.reply(
            "Le salon par défaut pour les événements est maintenant ${event.channel.asMention}".toLang(
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