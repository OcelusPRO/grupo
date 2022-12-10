package fr.ftnl.grupo.core.commands.list.buttons

import fr.ftnl.grupo.core.commands.IButtonCmd
import fr.ftnl.grupo.database.models.MatchmakingEvent
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class ConfigEvent : IButtonCmd {
    override suspend fun action(event: ButtonInteractionEvent) {
        val eventId = event.componentId.split("::")[1].toInt()
        val mEvent = MatchmakingEvent.cache.get(eventId)
            ?: return event.reply("L'événement n'existe pas").setEphemeral(true).queue() // TODO : add config event pannel
        event.reply("Cette action n'est pas encore implémentée").setEphemeral(true).queue()
    }
    
    override val name: String
        get() = "MATCHMAKING_CONFIG::"
    override val userPermissions: List<Permission>
        get() = listOf(Permission.MANAGE_EVENTS)
    override val allowDM: Boolean
        get() = false
}