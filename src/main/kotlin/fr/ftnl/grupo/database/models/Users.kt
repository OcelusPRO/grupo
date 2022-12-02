package fr.ftnl.grupo.database.models

import fr.ftnl.grupo.CONFIG
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object Users : IntIdTable("${CONFIG.dbConfig.prefix}users") {
    val discordId : Column<Long> = long("discord_id")
    val discordUsername : Column<String> = varchar("discord_username", 255)
    
    val steamGameTag : Column<String?> = varchar("steam_game_tag", 255).nullable().default(null)
    val originGameTag : Column<String?> = varchar("origin_game_tag", 255).nullable().default(null)
    val epicGameTag : Column<String?> = varchar("epic_game_tag", 255).nullable().default(null)
    val battleNetGameTag : Column<String?> = varchar("battle_net_game_tag", 255).nullable().default(null)
    val ubisoftGameTag : Column<String?> = varchar("ubisoft_game_tag", 255).nullable().default(null)
    val psnGameTag : Column<String?> = varchar("psn_game_tag", 255).nullable().default(null)
    val xboxGameTag : Column<String?> = varchar("xbox_game_tag", 255).nullable().default(null)
    val switchGameTag : Column<String?> = varchar("switch_game_tag", 255).nullable().default(null)
    
    val karma : Column<Int> = integer("karma").default(0)
    
    val createdAt : Column<DateTime> = datetime("created_at").defaultExpression(CurrentDateTime)
}

class User(id : EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)
    
    val discordId by Users.discordId
    val discordUsername by Users.discordUsername
    
    val steamGameTag by Users.steamGameTag
    val originGameTag by Users.originGameTag
    val epicGameTag by Users.epicGameTag
    val battleNetGameTag by Users.battleNetGameTag
    val ubisoftGameTag by Users.ubisoftGameTag
    val psnGameTag by Users.psnGameTag
    val xboxGameTag by Users.xboxGameTag
    val switchGameTag by Users.switchGameTag
    
    val karma by Users.karma
    
    val createdAt by Users.createdAt
}

