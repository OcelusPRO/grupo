package fr.ftnl.grupo.core.commands

import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent

class CommandManager {
	private lateinit var event : GenericInteractionCreateEvent
	
	suspend fun handle(receivedEvent : GenericInteractionCreateEvent) {
		event = receivedEvent
		dispatch()
	}
	
	private suspend fun dispatch() {
		when (val e = event) {
			is SlashCommandInteractionEvent        -> ICmd.cmd.filterIsInstance<ISlashCmd>().find { e.name == it.name }.let { if (it != null) handleSlashCommand(it) }
			
			is UserContextInteractionEvent         -> ICmd.cmd.filterIsInstance<IUserCmd>().find { e.name == it.name }.let { if (it != null) handleUser(it) }
			
			is MessageContextInteractionEvent      -> ICmd.cmd.filterIsInstance<IMessageCmd>().find { e.name == it.name }.let { if (it != null) handleMessage(it) }
			
			is CommandAutoCompleteInteractionEvent -> ICmd.cmd.filterIsInstance<ISlashCmd>().find { e.name.startsWith(it.name) }.let { if (it != null) handleAutoComplete(it) }
			
			is ButtonInteractionEvent              -> ICmd.cmd.filterIsInstance<IButtonCmd>().find { e.componentId.startsWith(it.name) }.let { if (it != null) handleButtons(it) }
			
			is StringSelectInteractionEvent        -> ICmd.cmd.filterIsInstance<ISelectCmd>().find { e.componentId.startsWith(it.name) }.let { if (it != null) handleSelect(it) }
			
			is ModalInteractionEvent               -> ICmd.cmd.filterIsInstance<IModalCmd>().find { e.modalId.startsWith(it.name) }.let { if (it != null) handleModal(it) }
		}
	}
	
	private suspend fun handleSlashCommand(cmd : ISlashCmd) = (cmd).action(event as SlashCommandInteractionEvent)
	
	private suspend fun handleAutoComplete(cmd : ISlashCmd) = (cmd).action(event as CommandAutoCompleteInteractionEvent)
	
	private suspend fun handleMessage(cmd : IMessageCmd) = (cmd).action(event as MessageContextInteractionEvent)
	
	private suspend fun handleUser(cmd : IUserCmd) = (cmd).action(event as UserContextInteractionEvent)
	
	private suspend fun handleButtons(cmd : IButtonCmd) = (cmd).action(event as ButtonInteractionEvent)
	
	private suspend fun handleSelect(cmd : ISelectCmd) = (cmd).action(event as StringSelectInteractionEvent)
	
	private suspend fun handleModal(cmd : IModalCmd) = (cmd).action(event as ModalInteractionEvent)
	
}