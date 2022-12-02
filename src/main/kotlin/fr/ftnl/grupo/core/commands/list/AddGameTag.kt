package fr.ftnl.grupo.core.commands.list

import fr.ftnl.grupo.core.commands.ISlashCmd
import fr.ftnl.grupo.database.models.GamePlatform
import fr.ftnl.grupo.database.models.User
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
        get() = listOf(OptionData(OptionType.STRING, "platform", "La plateforme du jeu", true).addChoices(GamePlatform.values().filterNot { it == GamePlatform.OTHER }.map {
                Command.Choice(
                    it.name, it.value
                )
            }), OptionData(OptionType.STRING, "tag", "Votre tag sur cette platform", true))
    
    override suspend fun action(event: SlashCommandInteractionEvent) {
        val platform = GamePlatform.getByValue(event.getOption("platform")?.asString!!)
        val tag = event.getOption("tag")?.asString!!
        
        val user = User.getUserByDiscordId(event.user.idLong, event.user.asTag)
        when (platform) {
            GamePlatform.PC_STEAM      -> user.steamGameTag = tag
            GamePlatform.PC_ORIGIN     -> user.originGameTag = tag
            GamePlatform.PC_EPIC       -> user.epicGameTag = tag
            GamePlatform.PC_UBISOFT    -> user.ubisoftGameTag = tag
            GamePlatform.PC_BATTLE_NET -> user.battleNetGameTag = tag
            GamePlatform.PS4           -> user.psnGameTag = tag
            GamePlatform.XBOX          -> user.xboxGameTag = tag
            GamePlatform.SWITCH        -> user.switchGameTag = tag
            else                       -> return event.reply("Cette plateforme n'est pas supporté").setEphemeral(event.isFromGuild).queue()
        }
        event.reply("Votre tag `${platform.showValue}` a bien été enregistré !").setEphemeral(event.isFromGuild).queue()
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
