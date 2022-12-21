package fr.ftnl.grupo.database.models.tbl

import fr.ftnl.grupo.database.abstract.BaseIntIdTable
import fr.ftnl.grupo.database.models.tbj.GuildEventsChannel
import fr.ftnl.grupo.database.models.tbj.GuildEventsChannels
import fr.ftnl.grupo.database.models.tbj.GuildEventsRole
import fr.ftnl.grupo.database.models.tbj.GuildEventsRoles
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column

object GuildConfigurations : BaseIntIdTable("TBL_GUILDCONFIGS_GLD") {
    val guildId: Column<Long> = long("guild_id").uniqueIndex()
    val defaultEventsChannel: Column<Long?> = long("default_events_channel").nullable().default(null)
    val defaultEventRoleID: Column<Long?> = long("default_event_role_id").nullable().default(null)
}

class GuildConfiguration(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GuildConfiguration>(GuildConfigurations)
    
    var guildId by GuildConfigurations.guildId
    var defaultEventsChannel by GuildConfigurations.defaultEventsChannel
    var defaultEventRole by GuildConfigurations.defaultEventRoleID
    
    val eventsChannels by GuildEventsChannel referrersOn GuildEventsChannels.guild
    val eventsRoles by GuildEventsRole referrersOn GuildEventsRoles.guild
    
    var createdAt by GuildConfigurations.createdAt
    var updatedAt by GuildConfigurations.updatedAt
}
