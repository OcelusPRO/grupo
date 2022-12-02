package fr.ftnl.grupo.core.commands.list

import dev.minn.jda.ktx.messages.Embed
import fr.ftnl.grupo.core.commands.ISlashCmd
import fr.ftnl.grupo.database.models.User
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class MyProfile : ISlashCmd {
    override val description: String
        get() = "Afficher mon profile"
    override val localizedDescriptions: Map<DiscordLocale, String>
        get() = mapOf()
    override val options: List<OptionData>
        get() = listOf()
    
    override suspend fun action(event: SlashCommandInteractionEvent) {
        val user = User.getUserByDiscordId(event.user.idLong, event.user.asTag)
        val embed = Embed {
            title = "Profile de ${event.user.asTag}"
            description = "Voici votre profile"
            
            if (user.steamGameTag != null) {
                field {
                    name = "Steam"
                    value = user.steamGameTag!!
                }
            }
            if (user.originGameTag != null) {
                field {
                    name = "Origin"
                    value = user.originGameTag!!
                }
            }
            if (user.epicGameTag != null) {
                field {
                    name = "Epic Games"
                    value = user.epicGameTag!!
                }
            }
            if (user.ubisoftGameTag != null) {
                field {
                    name = "Ubisoft"
                    value = user.ubisoftGameTag!!
                }
            }
            if (user.battleNetGameTag != null) {
                field {
                    name = "Battle Net"
                    value = user.battleNetGameTag!!
                }
            }
            if (user.psnGameTag != null) {
                field {
                    name = "PSN"
                    value = user.psnGameTag!!
                }
            }
            if (user.xboxGameTag != null) {
                field {
                    name = "Xbox"
                    value = user.xboxGameTag!!
                }
            }
            if (user.switchGameTag != null) {
                field {
                    name = "Switch"
                    value = user.switchGameTag!!
                }
            }
        }
    }
    
    override val localizedNames: Map<DiscordLocale, String>
        get() = mapOf()
    override val name: String
        get() = "my-profile"
    override val userPermissions: List<Permission>
        get() = listOf()
    override val allowDM: Boolean
        get() = true
}
