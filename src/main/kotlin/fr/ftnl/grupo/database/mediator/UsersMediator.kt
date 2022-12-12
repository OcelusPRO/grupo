package fr.ftnl.grupo.database.mediator

import fr.ftnl.grupo.database.models.User
import fr.ftnl.grupo.database.models.Users
import fr.ftnl.grupo.objects.NullableObject
import io.github.reactivecircus.cache4k.Cache
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration.Companion.hours

object UsersMediator {
    private val cache = Cache.Builder().expireAfterAccess(6.hours).build<Long, NullableObject<User?>>()
    suspend fun getUserByDiscordId(id: Long, username: String): User {
        val result = cache.get(id) { NullableObject(transaction { User.find { Users.discordId eq id }.firstOrNull() }) }
        val final = transaction { result.value }
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