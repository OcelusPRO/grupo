package fr.ftnl.grupo.database.models.tbl

import fr.ftnl.grupo.database.abstract.BaseIntEntity
import fr.ftnl.grupo.database.abstract.BaseIntEntityClass
import fr.ftnl.grupo.database.abstract.BaseIntIdTable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column

object GamePlateformes : BaseIntIdTable("TBL_GAMEPLATEFORMES_GMP") {
    val name: Column<String> = varchar("name", 20)
    val showName: Column<String> = varchar("show_name", 50)
}

class GamePlateforme(id: EntityID<Int>) : BaseIntEntity(id, GamePlateformes) {
    
    companion object : BaseIntEntityClass<GamePlateforme>(GamePlateformes)
    
    var name by GamePlateformes.name
    var showName by GamePlateformes.showName
}
