package fr.ftnl.grupo.database.mediator

import fr.ftnl.grupo.database.models.tbj.GuildEventsChannel
import fr.ftnl.grupo.database.models.tbl.Game
import fr.ftnl.grupo.database.models.tbl.GuildConfiguration

object GuildEventsChannelMediator {
    fun createChannelEvent(guild: GuildConfiguration, game: Game, channelId: Long) {
        GuildEventsChannel.new {
            this.guild = guild
            this.game = game
            this.channelId = channelId
        }
    }
}