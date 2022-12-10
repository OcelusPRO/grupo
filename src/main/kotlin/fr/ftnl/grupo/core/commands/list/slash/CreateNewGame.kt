package fr.ftnl.grupo.core.commands.list.slash

import fr.ftnl.grupo.core.commands.ISlashCmd
import fr.ftnl.grupo.core.commands.list.modal.NewGame
import fr.ftnl.grupo.database.models.GamePlatform
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class CreateNewGame : ISlashCmd {
    override val description: String
        get() = "Ajouter un nouveau jeu dans les possibilité de matchmaking"
    override val localizedDescriptions: Map<DiscordLocale, String>
        get() = mapOf()
    override val options: List<OptionData>
        get() = listOf(
            OptionData(OptionType.STRING, "plateforme", "Sur quel plateforme est le jeu", true).addChoices(GamePlatform.values().map { Command.Choice(it.showValue, it.value) }),
            OptionData(OptionType.INTEGER, "nombre-de-joueurs", "Combien de joueurs sont nécessaire pour jouer", true),
        )
    
    override suspend fun action(event: SlashCommandInteractionEvent) {
        val baseModal = NewGame().modal
        val platform = event.getOption("plateforme")!!.asString
        val nbPlayer = event.getOption("nombre-de-joueurs")!!.asInt
        val modal = baseModal.createCopy().setId(baseModal.id + platform + "::" + nbPlayer).build()
        event.replyModal(modal).queue()
    }
    
    override val localizedNames: Map<DiscordLocale, String>
        get() = mapOf()
    override val name: String
        get() = "add-game"
    override val userPermissions: List<Permission>
        get() = listOf(Permission.ADMINISTRATOR)
    override val allowDM: Boolean
        get() = false
    
    override val forGuild: String
        get() = "1047993030151516230"
}