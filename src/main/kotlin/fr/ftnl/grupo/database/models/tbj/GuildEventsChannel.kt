package fr.ftnl.grupo.database.models.tbj

import fr.ftnl.grupo.database.abstract.BaseIntEntity
import fr.ftnl.grupo.database.abstract.BaseIntEntityClass
import fr.ftnl.grupo.database.abstract.BaseIntIdTable
import fr.ftnl.grupo.database.models.tbl.Game
import fr.ftnl.grupo.database.models.tbl.Games
import fr.ftnl.grupo.database.models.tbl.GuildConfiguration
import fr.ftnl.grupo.database.models.tbl.GuildConfigurations
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column

object GuildEventsChannels : BaseIntIdTable("TBJ_GLD_GUILDEVENTCHANNELS_GME_GEC") {
    val guild: Column<EntityID<Int>> = reference("guild", GuildConfigurations)
    val game: Column<EntityID<Int>> = reference("game", Games)
    
    val channelId: Column<Long> = long("channel_id")
}

class GuildEventsChannel(id: EntityID<Int>) : BaseIntEntity(id, GuildEventsChannels) {
    
    companion object : BaseIntEntityClass<GuildEventsChannel>(GuildEventsChannels)
    
    var guild by GuildConfiguration referencedOn GuildEventsChannels.guild
    var game by Game referencedOn GuildEventsChannels.game
    
    var channelId by GuildEventsChannels.channelId
}
