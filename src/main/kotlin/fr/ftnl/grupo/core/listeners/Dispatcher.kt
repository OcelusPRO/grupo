package fr.ftnl.grupo.core.listeners

import dev.minn.jda.ktx.events.CoroutineEventListener
import dev.minn.jda.ktx.util.SLF4J
import fr.ftnl.grupo.core.commands.CommandManager
import fr.ftnl.grupo.core.commands.ICmd
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent
import net.dv8tion.jda.api.events.session.ReadyEvent

class Dispatcher : CoroutineEventListener {
    private val logger by SLF4J
    
    override suspend fun onEvent(event: GenericEvent) {
        when (event) {
            is ReadyEvent -> {
                logger.info("${event.jda.selfUser.asTag} is ready!")
                ICmd.postDataCmd(event.jda, logger)
            }
    
            is GenericInteractionCreateEvent -> CommandManager().dispatch(event)
        }
    }
}
