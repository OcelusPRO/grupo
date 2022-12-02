package fr.ftnl.grupo.database

import fr.ftnl.grupo.CONFIG
import fr.ftnl.grupo.config.Configuration
import fr.ftnl.grupo.database.models.Games
import fr.ftnl.grupo.database.models.MatchmakingEvents
import fr.ftnl.grupo.database.models.Participants
import fr.ftnl.grupo.database.models.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Initializes the database.
 * @author Ocelus
 */
class DBManager(cfg: Configuration = CONFIG) {
    lateinit var connection: Database

    init {
        connection = Database.connect(
            url = "jdbc:mysql://${cfg.dbConfig.host}:${cfg.dbConfig.port}/${cfg.dbConfig.database}?useSSL=false",
            driver = "com.mysql.cj.jdbc.Driver",
            user = cfg.dbConfig.user,
            password = cfg.dbConfig.password,
        )
    
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                Games, MatchmakingEvents, Participants, Users
            )
        }
    }
}
