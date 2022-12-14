package fr.ftnl.grupo.database.models.tbl

import fr.ftnl.grupo.database.abstract.BaseIntEntity
import fr.ftnl.grupo.database.abstract.BaseIntEntityClass
import fr.ftnl.grupo.database.abstract.BaseIntIdTable
import fr.ftnl.grupo.database.models.tbj.UserGametag
import fr.ftnl.grupo.database.models.tbj.UserGametags
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column

object Users : BaseIntIdTable("TBL_USERS_USR") {
    val discordId: Column<Long> = long("discord_id").uniqueIndex()
    val discordUsername: Column<String> = varchar("discord_username", 40)
}

class User(id: EntityID<Int>) : BaseIntEntity(id, Users) {
    companion object : BaseIntEntityClass<User>(Users)
    
    var discordId by Users.discordId
    var discordUsername by Users.discordUsername
    
    val gametags by UserGametag referrersOn UserGametags.user
}
