package fr.ftnl.grupo.database.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object Users : IntIdTable("TBL_USER_USR") {
    val discordId: Column<Long> = long("discord_id")
    val discordUsername: Column<String> = varchar("discord_username", 255)
    
    val steamGameTag: Column<String?> = varchar("steam_game_tag", 255).nullable().default(null)
    val originGameTag: Column<String?> = varchar("origin_game_tag", 255).nullable().default(null)
    val epicGameTag: Column<String?> = varchar("epic_game_tag", 255).nullable().default(null)
    val battleNetGameTag: Column<String?> = varchar("battle_net_game_tag", 255).nullable().default(null)
    val ubisoftGameTag: Column<String?> = varchar("ubisoft_game_tag", 255).nullable().default(null)
    val psnGameTag: Column<String?> = varchar("psn_game_tag", 255).nullable().default(null)
    val xboxGameTag: Column<String?> = varchar("xbox_game_tag", 255).nullable().default(null)
    val switchGameTag: Column<String?> = varchar("switch_game_tag", 255).nullable().default(null)
    
    val createdAt: Column<DateTime> = datetime("created_at").defaultExpression(CurrentDateTime)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)
    
    var discordId by Users.discordId
    var discordUsername by Users.discordUsername
    
    var steamGameTag by Users.steamGameTag
    var originGameTag by Users.originGameTag
    var epicGameTag by Users.epicGameTag
    var battleNetGameTag by Users.battleNetGameTag
    var ubisoftGameTag by Users.ubisoftGameTag
    var psnGameTag by Users.psnGameTag
    var xboxGameTag by Users.xboxGameTag
    var switchGameTag by Users.switchGameTag
    
    val createdAt by Users.createdAt

    fun getGameTagMap() = setOfNotNull(
        "Steam" to steamGameTag,
        "Origin" to originGameTag,
        "Epic game" to epicGameTag,
        "Battle.net" to battleNetGameTag,
        "Ubisoft connect" to ubisoftGameTag,
        "Play station network" to psnGameTag,
        "xbox" to xboxGameTag,
        "switch" to switchGameTag
    )
        .filter { it.second != null }
        .associate { it.first to it.second!! }
}
