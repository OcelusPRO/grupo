package fr.ftnl.grupo.database.models.tbl

import fr.ftnl.grupo.database.models.tbj.UserGametag
import fr.ftnl.grupo.database.models.tbj.UserGametags
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object Users : IntIdTable("TBL_USERS_USR") {
    val discordId: Column<Long> = long("discord_id")
    val discordUsername: Column<String> = varchar("discord_username", 40)
    
    val createdAt: Column<DateTime> = datetime("created_at").defaultExpression(CurrentDateTime)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)
    
    var discordId by Users.discordId
    var discordUsername by Users.discordUsername
    
    val gametags by UserGametag referrersOn UserGametags.user
    
    val createdAt by Users.createdAt
}
