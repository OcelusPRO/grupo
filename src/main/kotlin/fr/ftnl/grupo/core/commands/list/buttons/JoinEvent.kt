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
import org.jetbrains.exposed.sql.transactions.transaction

class JoinEvent : IButtonCmd {
    override suspend fun action(event: ButtonInteractionEvent) {
        val eventId = event.componentId.split("::")[1].toInt()
        val mEvent = MatchmakingEventMediator.cache.get(eventId)
            ?: return event.reply("L'événement n'existe plus").setEphemeral(true).queue()
        val user = UsersMediator.getUserByDiscordId(event.user.idLong, event.user.asTag)
        val participants = MatchmakingEventMediator.getEventParticipants(mEvent)
        val participation = transaction { mEvent.participants.find { it.user.discordId == user.discordId } }
        when (participation?.type) {
            ParticipantType.WAITING     -> {
                ParticipantsMediator.editParticipantType(participation, ParticipantType.PARTICIPANT)
                event.reply(
                    "Vous êtes maintenant un participant de l'événement !".toLang(
                        event.userLocale, LangKey.keyBuilder(this, "action", "typeChange")
                    )
                ).setEphemeral(true).queue()
            }
            
            ParticipantType.PARTICIPANT -> {
                MatchmakingEventMediator.removeParticipant(user, mEvent)
                event.reply(
                    "Vous avez quitté l'événement !".toLang(
                        event.userLocale, LangKey.keyBuilder(this, "action", "leaveEvent")
                    )
                ).setEphemeral(true).queue()
            }
            
            null                        -> {
                val count = participants.size
                val gameMax = transaction { mEvent.game.players }
                if (count >= gameMax) {
                    event.reply(
                        "L'événement est complet !\n*Envisagez de vous inscrire en file d'attente !*".toLang(
                            event.userLocale, LangKey.keyBuilder(this, "action", "fullEvent")
                        )
                    ).setEphemeral(true).queue()
                } else {
                    MatchmakingEventMediator.addParticipant(user, ParticipantType.PARTICIPANT, mEvent)
                    event.reply(
                        "Vous êtes maintenant un participant de l'événement !".toLang(
                            event.userLocale, LangKey.keyBuilder(this, "action", "joinEvent")
                        )
                    ).setEphemeral(true).queue()
                }
            }
        }
    
        MatchmakingEventMediator.diffuseMessageUpdate(event.jda.shardManager!!, mEvent)
    }
    
    override val name: String
        get() = "MATCHMAKING_JOIN::"
    override val userPermissions: List<Permission>
        get() = listOf()
    override val allowDM: Boolean
        get() = false
}
