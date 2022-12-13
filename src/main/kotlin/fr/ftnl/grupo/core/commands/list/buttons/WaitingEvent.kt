package fr.ftnl.grupo.core.commands.list.buttons

import fr.ftnl.grupo.core.commands.IButtonCmd
import fr.ftnl.grupo.database.mediator.MatchmakingEventMediator
import fr.ftnl.grupo.database.mediator.ParticipantsMediator
import fr.ftnl.grupo.database.mediator.UsersMediator
import fr.ftnl.grupo.database.models.tbj.ParticipantType
import fr.ftnl.grupo.extentions.toLang
import fr.ftnl.grupo.lang.LangKey
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

class WaitingEvent : IButtonCmd {
    override suspend fun action(event: ButtonInteractionEvent) {
        val eventId = event.componentId.split("::")[1].toInt()
        val mEvent = MatchmakingEventMediator.cache.get(eventId)
            ?: return event.reply("L'événement n'existe plus").setEphemeral(true).queue()
        val user = UsersMediator.getUserByDiscordId(event.user.idLong, event.user.asTag)
        val participation = mEvent.participants.find { it.user == user }
        when (participation?.type) {
            ParticipantType.WAITING     -> {
                MatchmakingEventMediator.removeParticipant(user, mEvent)
                event.reply(
                    "Vous avez quitté l'événement !".toLang(
                        event.userLocale, LangKey.keyBuilder(this, "action", "leaveEvent")
                    )
                ).setEphemeral(true).queue()
            }
            
            ParticipantType.PARTICIPANT -> {
                ParticipantsMediator.editParticipantType(participation, ParticipantType.WAITING)
                event.reply(
                    "Vous êtes maintenant en attente d'une place !".toLang(
                        event.userLocale, LangKey.keyBuilder(this, "action", "typeChange")
                    )
                ).setEphemeral(true).queue()
            }
            
            null                        -> {
                MatchmakingEventMediator.addParticipant(user, ParticipantType.WAITING, mEvent)
                event.reply(
                    "Vous êtes maintenant en attente d'une place !".toLang(
                        event.userLocale, LangKey.keyBuilder(this, "action", "joinWaiting")
                    )
                ).setEphemeral(true).queue()
            }
        }
    }
    
    override val name: String
        get() = "MATCHMAKING_WAIT::"
    override val userPermissions: List<Permission>
        get() = listOf()
    override val allowDM: Boolean
        get() = false
}
