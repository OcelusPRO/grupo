package fr.ftnl.grupo.database

import fr.ftnl.grupo.CONFIG
import fr.ftnl.grupo.config.Configuration
import fr.ftnl.grupo.database.models.tbj.GuildEventsChannels
import fr.ftnl.grupo.database.models.tbj.Participants
import fr.ftnl.grupo.database.models.tbj.UserGametags
import fr.ftnl.grupo.database.models.tbl.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Initializes the database.
 * @author Ocelus
 */
class DBManager(cfg: Configuration = CONFIG) {
    val connection: Database

    init {
        connection = Database.connect(
            url = "jdbc:mysql://${cfg.dbConfig.host}:${cfg.dbConfig.port}/${cfg.dbConfig.database}?useSSL=false",
            driver = "com.mysql.cj.jdbc.Driver",
            user = cfg.dbConfig.user,
            password = cfg.dbConfig.password,
        )

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                Games,
                GamePlateformes,
                MatchmakingEvents,
                Participants,
                Users,
                GuildConfigurations,
                GuildEventsChannels,
                SendedMessages,
                UserGametags,
            )
        }
    }
}
