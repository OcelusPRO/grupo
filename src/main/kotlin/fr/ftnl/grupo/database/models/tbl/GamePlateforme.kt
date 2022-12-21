package fr.ftnl.grupo.database.models.tbl

import fr.ftnl.grupo.database.abstract.BaseIntIdTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column

object GamePlateformes : BaseIntIdTable("TBL_GAMEPLATEFORMES_GMP") {
    val name: Column<String> = varchar("name", 20)
    val showName: Column<String> = varchar("show_name", 50)
}

class GamePlateforme(id: EntityID<Int>) : IntEntity(id) {
    
    companion object : IntEntityClass<GamePlateforme>(GamePlateformes)
    
    var name by GamePlateformes.name
    var showName by GamePlateformes.showName
    
    val createdAt by GamePlateformes.createdAt
    val updatedAt by GamePlateformes.updatedAt
}
