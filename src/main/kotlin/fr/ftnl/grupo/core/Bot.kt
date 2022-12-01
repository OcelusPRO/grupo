package fr.ftnl.grupo.core

import dev.minn.jda.ktx.jdabuilder.injectKTX
import fr.ftnl.grupo.CONFIG
import fr.ftnl.grupo.config.Configuration
import fr.ftnl.grupo.core.listeners.Dispatcher
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag

class Bot(cfg : Configuration = CONFIG) {
	lateinit var manager : ShardManager
	
	init {
		val builder = DefaultShardManagerBuilder.createDefault(cfg.botConfig.token)
		
		builder.setAutoReconnect(true)
		builder.setMemberCachePolicy(MemberCachePolicy.NONE)
		builder.disableCache(CacheFlag.values().toList())
		builder.injectKTX()
		
		builder.addEventListeners(Dispatcher())
		
		manager = builder.build()
	}
}