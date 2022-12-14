package fr.ftnl.grupo.database.models.tbl

import fr.ftnl.grupo.database.abstract.BaseIntEntity
import fr.ftnl.grupo.database.abstract.BaseIntEntityClass
import fr.ftnl.grupo.database.abstract.BaseIntIdTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column

object Games : BaseIntIdTable("TBL_GAMES_GME") {
    val name: Column<String> = varchar("name", 255)
    val description: Column<String> = text("description")
    val image: Column<String> = text("image")
    val url: Column<String> = text("url")
    val platform: Column<EntityID<Int>> = reference("platform", GamePlateformes)
    val players: Column<Int> = integer("players")
}

class Game(id: EntityID<Int>) : BaseIntEntity(id, Games) {
    
    companion object : BaseIntEntityClass<Game>(Games)
    
    var name by Games.name
    var description by Games.description
    var image by Games.image
    var url by Games.url
    var platform by GamePlateforme referencedOn Games.platform
    var players by Games.players
}
