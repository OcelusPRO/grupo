package fr.ftnl.grupo.core.listeners

import dev.minn.jda.ktx.events.CoroutineEventListener
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.session.ReadyEvent

class Dispatcher: CoroutineEventListener {
	override suspend fun onEvent(event : GenericEvent) {
		when(event){
			is ReadyEvent -> { println("Bot is ready!") }
			
			
		}
	}
}