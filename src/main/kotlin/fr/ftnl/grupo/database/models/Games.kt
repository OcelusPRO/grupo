package fr.ftnl.grupo.database.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object Games : IntIdTable("TBL_GAME_GME") {
    val name: Column<String> = varchar("name", 255)
    val description: Column<String> = text("description")
    val image: Column<String> = text("image")
    val url: Column<String> = text("url")
    val platform: Column<EntityID<Int>> = reference("platform", GamePlateformes)
    val players: Column<Int> = integer("players")
    
    val createdAt: Column<DateTime> = datetime("created_at").defaultExpression(CurrentDateTime)
}


class Game(id: EntityID<Int>) : IntEntity(id) {
    
    companion object : IntEntityClass<Game>(Games)
    
    var name by Games.name
    var description by Games.description
    var image by Games.image
    var url by Games.url
    var platform by GamePlateforme referencedOn Games.platform
    var players by Games.players
    val createdAt by Games.createdAt
}
