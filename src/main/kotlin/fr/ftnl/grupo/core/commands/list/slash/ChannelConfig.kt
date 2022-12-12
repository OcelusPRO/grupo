package fr.ftnl.grupo.core.commands.list.slash

import fr.ftnl.grupo.core.commands.ISlashCmd
import fr.ftnl.grupo.database.mediator.GuildConfigurationMediator
import fr.ftnl.grupo.database.mediator.GuildEventsChannelMediator
import fr.ftnl.grupo.database.models.Game
import fr.ftnl.grupo.extentions.toLang
import fr.ftnl.grupo.lang.LangKey
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.jetbrains.exposed.sql.transactions.transaction

class ChannelConfig : ISlashCmd {
    override val description: String
        get() = "Configurer les salons pour affiché les événements"
    override val localizedDescriptions: Map<DiscordLocale, String>
        get() = mapOf()
    override val options: List<OptionData>
        get() = listOf(
            OptionData(OptionType.STRING, "game", "Quel jeu doit être envoyé dans ce salon ?", true, true)
        )
    
    override suspend fun action(event: SlashCommandInteractionEvent) {
        val guildConfig = GuildConfigurationMediator.findGuildConfiguration(event.guild!!.idLong)
        val gameValue = event.getOption("game")!!.asString
        val game = transaction {
            Game.findById(event.getOption("game")!!.asString.toInt())!!
        }
    
        GuildEventsChannelMediator.createChannelEvent(guildConfig, game, event.channel.idLong)
        event.reply(
            "Le salon %s est maintenant configuré pour afficher les événements de `%s`".toLang(
                event.userLocale, LangKey.keyBuilder(this, "action", "channelSet")
            ).format(
                event.channel.asMention, game.name
            )
        ).setEphemeral(true).queue()
    }
    
    override suspend fun action(event: CommandAutoCompleteInteractionEvent) {
        transaction {
            val games = Game.all()
            val filteredGames = games.filter {
                it.name.lowercase().contains(event.focusedOption.value.lowercase())
            }.chunked(25).first()
            val choices = filteredGames.map { Command.Choice(it.name, it.id.value.toString()) }
            println(choices)
            event.replyChoices(choices).queue()
        }
    }
    
    override val localizedNames: Map<DiscordLocale, String>
        get() = mapOf()
    override val name: String
        get() = "channel-config"
    override val userPermissions: List<Permission>
        get() = listOf(Permission.MANAGE_EVENTS)
    override val allowDM: Boolean
        get() = false
}