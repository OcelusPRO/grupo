package fr.ftnl.grupo.database.models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object GamePlateformes : IntIdTable("TBL_GAMEPLATEFORME_GMP") {
    val name: Column<String> = varchar("name", 20)
    val showName: Column<String> = varchar("show_name", 50)
    
    val createdAt: Column<DateTime> = datetime("created_at").defaultExpression(CurrentDateTime)
}

class GamePlateforme(id: EntityID<Int>) : IntEntity(id) {
    
    companion object : IntEntityClass<GamePlateforme>(GamePlateformes)
    
    var name by GamePlateformes.name
    var showName by GamePlateformes.showName
    
    val createdAt by GamePlateformes.createdAt
}
