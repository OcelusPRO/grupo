package fr.ftnl.grupo.core.listeners

import dev.minn.jda.ktx.events.CoroutineEventListener
import fr.ftnl.grupo.core.commands.CommandManager
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent
import net.dv8tion.jda.api.events.session.ReadyEvent

class Dispatcher : CoroutineEventListener {
	override suspend fun onEvent(event : GenericEvent) {
		when (event) {
			is ReadyEvent -> println("Bot is ready!")
			is GenericInteractionCreateEvent -> CommandManager().handle(event)
		}
	}
}