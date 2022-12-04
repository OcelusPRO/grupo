package fr.ftnl.grupo.database.models

import fr.ftnl.grupo.objects.NullableObject
import io.github.reactivecircus.cache4k.Cache
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import kotlin.time.Duration.Companion.hours

object Users : IntIdTable("users") {
    val discordId: Column<Long> = long("discord_id")
    val discordUsername: Column<String> = varchar("discord_username", 255)
    
    val steamGameTag: Column<String?> = varchar("steam_game_tag", 255).nullable().default(null)
    val originGameTag: Column<String?> = varchar("origin_game_tag", 255).nullable().default(null)
    val epicGameTag: Column<String?> = varchar("epic_game_tag", 255).nullable().default(null)
    val battleNetGameTag: Column<String?> = varchar("battle_net_game_tag", 255).nullable().default(null)
    val ubisoftGameTag: Column<String?> = varchar("ubisoft_game_tag", 255).nullable().default(null)
    val psnGameTag: Column<String?> = varchar("psn_game_tag", 255).nullable().default(null)
    val xboxGameTag: Column<String?> = varchar("xbox_game_tag", 255).nullable().default(null)
    val switchGameTag: Column<String?> = varchar("switch_game_tag", 255).nullable().default(null)
    
    val karma: Column<Int> = integer("karma").default(0)
    
    val createdAt: Column<DateTime> = datetime("created_at").defaultExpression(CurrentDateTime)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users) {
        private val cache = Cache.Builder().expireAfterAccess(6.hours).build<Long, NullableObject<User?>>()
        
        suspend fun getUserByDiscordId(id: Long, username: String): User {
            val result = cache.get(id) { NullableObject(transaction { find { Users.discordId eq id }.firstOrNull() }) }
            val final = result.value
                ?: transaction {
                    User.new {
                        discordId = id
                        discordUsername = username
                    }
                }
            if (final.discordUsername != username) {
                transaction { final.discordUsername = username }
            }
            cache.put(id, NullableObject(final))
            return final
        }
    }
    
    var discordId by Users.discordId
    var discordUsername by Users.discordUsername
    
    var steamGameTag by Users.steamGameTag
    var originGameTag by Users.originGameTag
    var epicGameTag by Users.epicGameTag
    var battleNetGameTag by Users.battleNetGameTag
    var ubisoftGameTag by Users.ubisoftGameTag
    var psnGameTag by Users.psnGameTag
    var xboxGameTag by Users.xboxGameTag
    var switchGameTag by Users.switchGameTag
    
    var karma by Users.karma
    
    val createdAt by Users.createdAt
}
