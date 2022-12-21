package fr.ftnl.grupo.database.abstract

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime

abstract class BaseIntIdTable(name: String) : IntIdTable(name) {
    val createdAt = datetime("createdAt").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updatedAt").nullable()
}