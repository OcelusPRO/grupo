package fr.ftnl.grupo.database.mediator

import fr.ftnl.grupo.database.models.tbj.UserGametag
import fr.ftnl.grupo.database.models.tbl.GamePlateforme
import fr.ftnl.grupo.database.models.tbl.User
import org.jetbrains.exposed.sql.transactions.transaction

object UserGametagsMediator {
    
    fun setUserGametag(user: User, plateforme: GamePlateforme, gametag: String) {
        val userPlateforme = UsersMediator.getUserPlateformes(user)
        if (userPlateforme.any { it.plateforme == plateforme }) {
            transaction { userPlateforme.first { it.plateforme == plateforme }.gametag = gametag }
        } else {
            transaction {
                UserGametag.new {
                    this.user = user
                    this.plateforme = plateforme
                    this.gametag = gametag
                }
            }
        }
    }
}