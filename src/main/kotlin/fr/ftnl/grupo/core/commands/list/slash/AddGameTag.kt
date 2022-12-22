package fr.ftnl.grupo.core.commands.list.slash

import fr.ftnl.grupo.core.commands.ISlashCmd
import fr.ftnl.grupo.database.mediator.GamePlateformeMediator
import fr.ftnl.grupo.database.mediator.UserGametagsMediator
import fr.ftnl.grupo.database.mediator.UsersMediator
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class AddGameTag : ISlashCmd {
    override val description: String
        get() = "Ajouté un tag de jeu a votre profile"
    override val localizedDescriptions: Map<DiscordLocale, String>
        get() = mapOf()
    override val options: List<OptionData>
        get() = listOf(OptionData(OptionType.STRING, "platform", "La plateforme du jeu", true).addChoices(GamePlateformeMediator.allPlateformes().map { Command.Choice(it.showName, it.name) }),
                       OptionData(OptionType.STRING, "tag", "Votre tag sur cette platform", true))
    
    override suspend fun action(event: SlashCommandInteractionEvent) {
        val platform = GamePlateformeMediator.findByName(event.getOption("platform")?.asString!!)
            ?: return event.reply("Cette plateforme n'existe pas").setEphemeral(true).queue()
        val tag = event.getOption("tag")?.asString!!
        val user = UsersMediator.getUserByDiscordId(event.user.idLong, event.user.asTag)
    
        UserGametagsMediator.setUserGametag(user, platform, tag)
    
        event.reply("Votre tag `${platform.showName}` a bien été enregistré !").setEphemeral(event.isFromGuild).queue()
    }
    
    override val localizedNames: Map<DiscordLocale, String>
        get() = mapOf()
    override val name: String
        get() = "add-game-tag"
    override val userPermissions: List<Permission>
        get() = listOf()
    override val allowDM: Boolean
        get() = true
}
