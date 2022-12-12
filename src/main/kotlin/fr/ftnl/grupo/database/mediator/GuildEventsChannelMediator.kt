package fr.ftnl.grupo.database.mediator

import fr.ftnl.grupo.database.models.Game
import fr.ftnl.grupo.database.models.GuildConfiguration
import fr.ftnl.grupo.database.models.GuildEventsChannel

object GuildEventsChannelMediator {
    fun createChannelEvent(guild: GuildConfiguration, game: Game, channelId: Long) {
        GuildEventsChannel.new {
            this.guild = guild
            this.game = game
            this.channelId = channelId
        }
    }
}