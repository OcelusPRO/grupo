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

object GuildConfigurations : IntIdTable("guild_configurations") {
    val guildId: Column<Long> = long("guild_id")
    val defaultEventsChannel: Column<Long?> = long("default_events_channel").nullable().default(null)
    
    val createdAt: Column<DateTime> = datetime("created_at").defaultExpression(CurrentDateTime)
}


class GuildConfiguration(id: EntityID<Int>) : IntEntity(id) {
    
    companion object : IntEntityClass<GuildConfiguration>(GuildConfigurations) {
        val cache = Cache.Builder().expireAfterWrite(1.hours).build<Long, NullableObject<GuildConfiguration>>()
        
        suspend fun findGuildConfiguration(guildId: Long): GuildConfiguration {
            return cache.get(guildId) {
                NullableObject(transaction { find { GuildConfigurations.guildId eq guildId }.firstOrNull() })
            }.value
                ?: transaction {
                    GuildConfiguration.new {
                        this.guildId = guildId
                    }
                }
        }
    }
    
    var guildId by GuildConfigurations.guildId
    var defaultEventsChannel by GuildConfigurations.defaultEventsChannel
    
    var createdAt by GuildConfigurations.createdAt
    
    val eventsChannels by GuildEventsChannel referrersOn GuildEventsChannels.guild
    
    
    fun setDefaultEventChannel(channel: Long) {
        transaction {
            defaultEventsChannel = channel
        }
    }
}
