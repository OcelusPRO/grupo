package fr.ftnl.grupo.database.models.tbj

import fr.ftnl.grupo.database.abstract.BaseIntEntity
import fr.ftnl.grupo.database.abstract.BaseIntEntityClass
import fr.ftnl.grupo.database.abstract.BaseIntIdTable
import fr.ftnl.grupo.database.models.tbl.MatchmakingEvent
import fr.ftnl.grupo.database.models.tbl.MatchmakingEvents
import fr.ftnl.grupo.database.models.tbl.User
import fr.ftnl.grupo.database.models.tbl.Users
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column

object Participants : BaseIntIdTable("TBJ_USR_PARTICIPANTS_MEV_PRT") {
    val matchmakingEvent: Column<EntityID<Int>> = reference("matchmaking_event", MatchmakingEvents)
    val user: Column<EntityID<Int>> = reference("user", Users)
    val type: Column<ParticipantType> = enumeration<ParticipantType>("type")
}

enum class ParticipantType {
    PARTICIPANT,
    WAITING
}

class Participant(id: EntityID<Int>) : BaseIntEntity(id, Participants) {
    
    companion object : BaseIntEntityClass<Participant>(Participants)
    
    var matchmakingEvent by MatchmakingEvent referencedOn Participants.matchmakingEvent
    var user by User referencedOn Participants.user
    var type by Participants.type
}
