package fr.ftnl.grupo.database.models.tbj

import fr.ftnl.grupo.database.models.tbl.Game
import fr.ftnl.grupo.database.models.tbl.Games
import fr.ftnl.grupo.database.models.tbl.GuildConfiguration
import fr.ftnl.grupo.database.models.tbl.GuildConfigurations
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object GuildEventsRoles : IntIdTable("TBJ_GLD_GUILDEVENTROLE_GME_GER") {
    val guild: Column<EntityID<Int>> = reference("guild", GuildConfigurations)
    val game: Column<EntityID<Int>> = reference("game", Games)
    
    val roleId: Column<Long> = long("role_id")
    val createdAt: Column<DateTime> = datetime("created_at").defaultExpression(CurrentDateTime)
}


class GuildEventsRole(id: EntityID<Int>) : IntEntity(id) {
    
    companion object : IntEntityClass<GuildEventsRole>(GuildEventsRoles)
    
    var guild by GuildConfiguration referencedOn GuildEventsRoles.guild
    var game by Game referencedOn GuildEventsRoles.game
    
    var roleId by GuildEventsRoles.roleId
    var createdAt by GuildEventsRoles.createdAt
}
