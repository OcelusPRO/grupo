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

object Games : IntIdTable("${CONFIG.dbConfig.prefix}games") {
    val name : Column<String> = varchar("name", 255)
    val description : Column<String> = text("description")
    val image : Column<String> = text("image")
    val url : Column<String> = text("url")
    val platform : Column<GamePlatform> = enumeration<GamePlatform>("platform")
    val players : Column<Int> = integer("players")
    
    val createdAt : Column<DateTime> = datetime("created_at").defaultExpression(CurrentDateTime)
}

enum class GamePlatform(val value : String) {
    PC_STEAM("PC_STEAM"), PC_ORIGIN("PC_ORIGIN"), PC_EPIC("PC_EPIC"), PC_UBISOFT("PC_UBISOFT"), PC_BATTLE_NET("PC_BATTLE_NET"),
    
    PS4("PS4"), XBOX("XBOX"), SWITCH("SWITCH"),
    
    OTHER("OTHER")
}

class Game(id : EntityID<Int>) : IntEntity(id) {
    
    companion object : IntEntityClass<Game>(Games)
    
    val name by Games.name
    val description by Games.description
    val image by Games.image
    val url by Games.url
    val platform by Games.platform
    val players by Games.players
    val createdAt by Games.createdAt
}
