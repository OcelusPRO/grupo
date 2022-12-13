package fr.ftnl.grupo.database.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object UserGametags : IntIdTable("TBJ_USERGAMETAG_USR_GMP") {
    val user: Column<EntityID<Int>> = reference("user", Users)
    val plateforme: Column<EntityID<Int>> = reference("plateforme", GamePlateformes)
    val gametag: Column<String> = varchar("gametag", 80)
    
    val createdAt: Column<DateTime> = datetime("created_at").defaultExpression(CurrentDateTime)
}

class UserGametag(id: EntityID<Int>) : IntEntity(id) {
    
    companion object : IntEntityClass<UserGametag>(UserGametags)
    
    var user by User referencedOn UserGametags.user
    var plateforme by GamePlateforme referencedOn UserGametags.plateforme
    var gametag by UserGametags.gametag
    
    val createdAt by UserGametags.createdAt
}
