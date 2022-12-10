package fr.ftnl.grupo.database.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object Games : IntIdTable("games") {
    val name: Column<String> = varchar("name", 255)
    val description: Column<String> = text("description")
    val image: Column<String> = text("image")
    val url: Column<String> = text("url")
    val platform: Column<GamePlatform> = enumeration<GamePlatform>("platform")
    val players: Column<Int> = integer("players")
    
    val createdAt: Column<DateTime> = datetime("created_at").defaultExpression(CurrentDateTime)
}

enum class GamePlatform(val value: String, val showValue: String) {
    PC_STEAM("PC_STEAM", "steam"),
    PC_ORIGIN("PC_ORIGIN", "origin"),
    PC_EPIC("PC_EPIC", "epic-game"),
    PC_UBISOFT("PC_UBISOFT", "ubisoft connect"),
    PC_BATTLE_NET("PC_BATTLE_NET", "battle.net"),
    
    PS4("PS4", "playstation network"),
    XBOX("XBOX", "xbox live"),
    SWITCH("SWITCH", "nintendo"),
    
    OTHER("OTHER", "autre");
    
    companion object {
        fun getByValue(value: String): GamePlatform {
            return values().first { it.value == value }
        }
    }
}

class Game(id: EntityID<Int>) : IntEntity(id) {
    
    companion object : IntEntityClass<Game>(Games)
    
    var name by Games.name
    var description by Games.description
    var image by Games.image
    var url by Games.url
    var platform by Games.platform
    var players by Games.players
    val createdAt by Games.createdAt
}
