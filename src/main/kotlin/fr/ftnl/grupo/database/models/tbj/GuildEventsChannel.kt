package fr.ftnl.grupo.database.models.tbj

import fr.ftnl.grupo.database.models.tbl.Game
import fr.ftnl.grupo.database.models.tbl.Games
import fr.ftnl.grupo.database.models.tbl.GuildConfiguration
import fr.ftnl.grupo.database.models.tbl.GuildConfigurations
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.jodatime.CurrentDateTime
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.DateTime

object GuildEventsChannels : IntIdTable("TBJ_GLD_GUILDEVENTCHANNELS_GME_GEC") {
    val guild: Column<EntityID<Int>> = reference("guild", GuildConfigurations)
    val game: Column<EntityID<Int>> = reference("game", Games)
    
    val channelId: Column<Long> = long("channel_id")
    val createdAt: Column<DateTime> = datetime("created_at").defaultExpression(CurrentDateTime)
}


class GuildEventsChannel(id: EntityID<Int>) : IntEntity(id) {
    
    companion object : IntEntityClass<GuildEventsChannel>(GuildEventsChannels)
    
    var guild by GuildConfiguration referencedOn GuildEventsChannels.guild
    var game by Game referencedOn GuildEventsChannels.game
    
    var channelId by GuildEventsChannels.channelId
    var createdAt by GuildEventsChannels.createdAt
}
