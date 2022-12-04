package fr.ftnl.grupo.config.dependent

data class DatabaseConfig(
    val host: String = "",
    val port: Int = 3306,
    val user: String = "",
    val password: String = "",
    val database: String = "",
)
