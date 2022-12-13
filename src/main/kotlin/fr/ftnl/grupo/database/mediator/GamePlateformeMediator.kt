package fr.ftnl.grupo.database.mediator

import fr.ftnl.grupo.database.models.GamePlateforme
import fr.ftnl.grupo.database.models.GamePlateformes
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction

object GamePlateformeMediator {
    fun findByName(name: String) = transaction {
        GamePlateforme.find {
            GamePlateformes.name eq name or (GamePlateformes.showName eq name)
        }.firstOrNull()
    }
    
    fun allPlateformes() = transaction {
        GamePlateforme.all().toList()
    }
    
}