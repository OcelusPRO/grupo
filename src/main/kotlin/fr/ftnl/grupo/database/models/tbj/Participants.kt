package fr.ftnl.grupo.database.models.tbj

import fr.ftnl.grupo.database.models.tbl.MatchmakingEvent
import fr.ftnl.grupo.database.models.tbl.MatchmakingEvents
import fr.ftnl.grupo.database.models.tbl.User
import fr.ftnl.grupo.database.models.tbl.Users
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object Participants : IntIdTable("TBJ_USR_PARTICIPANTS_MEV_PRT") {
    val matchmakingEvent: Column<EntityID<Int>> = reference("matchmaking_event", MatchmakingEvents)
    val user: Column<EntityID<Int>> = reference("user", Users)
    val type: Column<ParticipantType> = enumeration<ParticipantType>("type")
    val registerAt: Column<DateTime> = datetime("register_at").defaultExpression(CurrentDateTime)
}

enum class ParticipantType {
    PARTICIPANT,
    WAITING
}

class Participant(id: EntityID<Int>) : IntEntity(id) {
    
    companion object : IntEntityClass<Participant>(Participants)
    
    var matchmakingEvent by MatchmakingEvent referencedOn Participants.matchmakingEvent
    var user by User referencedOn Participants.user
    var type by Participants.type
    val registerAt by Participants.registerAt
}
