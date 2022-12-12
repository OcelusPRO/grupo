package fr.ftnl.grupo.database.mediator

import fr.ftnl.grupo.database.models.Participant
import fr.ftnl.grupo.database.models.ParticipantType
import org.jetbrains.exposed.sql.transactions.transaction

object ParticipantsMediator {
    fun editParticipantType(participant: Participant, type: ParticipantType) {
        transaction { participant.type = type }
    }
}