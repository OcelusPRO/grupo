package fr.ftnl.grupo.database.mediator

import fr.ftnl.grupo.database.models.tbl.GuildConfiguration
import fr.ftnl.grupo.database.models.tbl.GuildConfigurations
import fr.ftnl.grupo.objects.NullableObject
import io.github.reactivecircus.cache4k.Cache
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration.Companion.hours

object GuildConfigurationMediator {
    
    val cache = Cache.Builder().expireAfterWrite(1.hours).build<Long, NullableObject<GuildConfiguration>>()
    
    suspend fun findGuildConfiguration(guildId: Long): GuildConfiguration {
        return cache.get(guildId) {
            NullableObject(transaction { GuildConfiguration.find { GuildConfigurations.guildId eq guildId }.firstOrNull() })
        }.value
            ?: transaction {
                GuildConfiguration.new {
                    this.guildId = guildId
                }
            }
    }
    
    /*
    *
    *
    *
    *
    * */
    
    
    suspend fun getGuildConfiguration(id: Long) = findGuildConfiguration(id)
    
    suspend fun setDefaultEventChannel(guildId: Long, channel: Long) {
        val config = getGuildConfiguration(guildId)
        transaction {
            config.defaultEventsChannel = channel
        }
    }
    
}