package fr.ftnl.grupo.database.models.tbl

import fr.ftnl.grupo.database.models.tbj.GuildEventsChannel
import fr.ftnl.grupo.database.models.tbj.GuildEventsChannels
import fr.ftnl.grupo.database.models.tbj.GuildEventsRole
import fr.ftnl.grupo.database.models.tbj.GuildEventsRoles
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object GuildConfigurations : IntIdTable("TBL_GUILDCONFIGS_GLD") {
    val guildId: Column<Long> = long("guild_id").uniqueIndex()
    val defaultEventsChannel: Column<Long?> = long("default_events_channel").nullable().default(null)
    val defaultEventRoleID: Column<Long?> = long("default_event_role_id").nullable().default(null)
    val createdAt: Column<DateTime> = datetime("created_at").defaultExpression(CurrentDateTime)
}

class GuildConfiguration(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GuildConfiguration>(GuildConfigurations)
    var guildId by GuildConfigurations.guildId
    var defaultEventsChannel by GuildConfigurations.defaultEventsChannel
    var defaultEventRole by GuildConfigurations.defaultEventRoleID
    var createdAt by GuildConfigurations.createdAt
    val eventsChannels by GuildEventsChannel referrersOn GuildEventsChannels.guild
    val eventsRoles by GuildEventsRole referrersOn GuildEventsRoles.guild
}
