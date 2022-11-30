package fr.ftnl.grupo.database

import fr.ftnl.grupo.CONFIG
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Initializes the database.
 * @author Ocelus
 */
class DBManager {
	
	init {
		Database.connect(
			url = "jdbc:mysql://${CONFIG.dbConfig.host}:${CONFIG.dbConfig.port}/${CONFIG.dbConfig.database}?useSSL=false",
			driver = "com.mysql.cj.jdbc.Driver",
			user = CONFIG.dbConfig.user,
			password = CONFIG.dbConfig.password,
		)
		
		transaction {
			SchemaUtils.createMissingTablesAndColumns(
			
			)
		}
	}
	
}