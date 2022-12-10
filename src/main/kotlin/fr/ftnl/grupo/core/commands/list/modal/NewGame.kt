package fr.ftnl.grupo.core.commands.list.modal

import dev.minn.jda.ktx.interactions.components.TextInputBuilder
import fr.ftnl.grupo.core.commands.IModalCmd
import fr.ftnl.grupo.database.models.Game
import fr.ftnl.grupo.database.models.GamePlatform
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import org.jetbrains.exposed.sql.transactions.transaction

class NewGame : IModalCmd {
    override val modal: Modal
        get() = Modal.create("NEW_GAME_MODAL::", "Ajouter un nouveau jeu").addActionRow(
            TextInputBuilder(
                id = "name", label = "Nom du jeu", style = TextInputStyle.SHORT, required = true
            ).build(),
        ).addActionRow(
            TextInputBuilder(
                id = "description", label = "description du jeu", style = TextInputStyle.PARAGRAPH, required = true
            ).build(),
        ).addActionRow(
            TextInputBuilder(
                id = "image", label = "Image représentant le jeu", style = TextInputStyle.SHORT, required = true
            ).build(),
        ).addActionRow(
            TextInputBuilder(
                id = "url", label = "Page d'achat du jeu", style = TextInputStyle.SHORT, required = true
            ).build(),
        ).build()
    
    override suspend fun action(event: ModalInteractionEvent) {
        val gameName = event.getValue("name")!!.asString
        val gameDescription = event.getValue("description")!!.asString
        val gameImage = event.getValue("image")!!.asString
        val gameUrl = event.getValue("url")!!.asString
        
        val platformeName = event.modalId.split("::")[1]
        val gamePlayers = event.modalId.split("::")[2].toInt()
        
        val gamePlatform = GamePlatform.getByValue(event.getValue("platform")!!.asString)
        
        transaction {
            
            Game.new {
                name = gameName
                description = gameDescription
                image = gameImage
                url = gameUrl
                players = gamePlayers
                platform = gamePlatform
            }
        }
    }
    
    override val name: String
        get() = modal.id
    override val userPermissions: List<Permission>
        get() = listOf(Permission.ADMINISTRATOR)
    override val allowDM: Boolean
        get() = false
}