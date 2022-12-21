package fr.ftnl.grupo.database.models.tbj

import fr.ftnl.grupo.database.abstract.BaseIntIdTable
import fr.ftnl.grupo.database.models.tbl.GamePlateforme
import fr.ftnl.grupo.database.models.tbl.GamePlateformes
import fr.ftnl.grupo.database.models.tbl.User
import fr.ftnl.grupo.database.models.tbl.Users
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column

object UserGametags : BaseIntIdTable("TBJ_USR_USERGAMETAGS_GMP_UGT") {
    val user: Column<EntityID<Int>> = reference("user", Users)
    val plateforme: Column<EntityID<Int>> = reference("plateforme", GamePlateformes)
    val gametag: Column<String> = varchar("gametag", 80)
}

class UserGametag(id: EntityID<Int>) : IntEntity(id) {
    
    companion object : IntEntityClass<UserGametag>(UserGametags)
    
    var user by User referencedOn UserGametags.user
    var plateforme by GamePlateforme referencedOn UserGametags.plateforme
    var gametag by UserGametags.gametag
    
    val createdAt by UserGametags.createdAt
    val updatedAt by UserGametags.updatedAt
}
