package fr.ftnl.grupo.core.commands.list.buttons

import fr.ftnl.grupo.core.commands.IButtonCmd
import fr.ftnl.grupo.database.mediator.MatchmakingEventMediator
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class DeleteEvent : IButtonCmd {
    override suspend fun action(event: ButtonInteractionEvent) {
        val eventId = event.componentId.split("::")[1].toInt()
        val mEvent = MatchmakingEventMediator.cache.get(eventId)
            ?: return event.reply("L'événement n'existe plus").setEphemeral(true).queue()
        event.editComponents(event.message.components.map { it.asDisabled() }).queue()
        MatchmakingEventMediator.endEvent(mEvent)
    }
    
    override val name: String
        get() = "MATCHMAKING_CANCEL::"
    override val userPermissions: List<Permission>
        get() = listOf(
            Permission.MANAGE_EVENTS
        )
    override val allowDM: Boolean
        get() = false
}