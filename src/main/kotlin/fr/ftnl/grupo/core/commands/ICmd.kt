package fr.ftnl.grupo.core.commands

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.modals.Modal
import org.reflections.Reflections
import org.slf4j.Logger
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

interface ICmd {
	val name : String
	val userPermissions : List<Permission>
	val allowDM : Boolean
	val cooldown: Duration
		get() = 0.seconds
	
	companion object {
		val cmd = getCommands()
		private var posted = false
		
		private fun getCommands() : List<ICmd> {
			println("Loading commands...")
			val reflections : Set<Class<out ICmd>> = Reflections(ICmd::class.java.`package`.name + ".list").getSubTypesOf(
				ICmd::class.java
			)
			return reflections.filter { !it.isInterface }.map {
				it.getConstructor().newInstance()
			}
		}
		
		fun postDataCmd(jda : JDA, logger: Logger) {
			if (posted) return
			val commands = cmd.filterIsInstance(IDataCmd::class.java)
			val toGlobalPostCommand : MutableList<IDataCmd> = mutableListOf()
			val toGuildPostCommand : MutableMap<Guild, MutableList<IDataCmd>> = mutableMapOf()
			commands.forEach {
				val guild = it.forGuild?.let { it1 -> jda.getGuildById(it1) }
				if (guild != null) {
					val guildPost = toGuildPostCommand.getOrDefault(guild, mutableListOf())
					guildPost.add(it)
					toGuildPostCommand[guild] = guildPost
				} else toGlobalPostCommand.add(it)
			}
			jda.updateCommands().addCommands(toGlobalPostCommand.map { it.data }).queue()
			toGuildPostCommand.forEach { (guild, list) ->
				guild.updateCommands().addCommands(list.map { it.data }).queue()
			}
			posted = true
		}
	}
	
	
}

interface IDataCmd : ICmd {
	val localizedNames : Map<DiscordLocale, String>
	val forGuild : String?
		get() = null
	val data : CommandData
}

interface ISlashCmd : IDataCmd {
	val description : String
	val localizedDescriptions : Map<DiscordLocale, String>
	val options : List<OptionData>
	
	override val data : CommandData
		get() = Commands.slash(name, description).setDescriptionLocalizations(localizedDescriptions).setNameLocalizations(localizedNames).addOptions(options)
			.setDefaultPermissions(DefaultMemberPermissions.enabledFor(userPermissions))
	
	suspend fun action(event : SlashCommandInteractionEvent)
	suspend fun action(event : CommandAutoCompleteInteractionEvent) = Unit
}

interface IMessageCmd : IDataCmd {
	override val data : CommandData
		get() = Commands.message(name).setNameLocalizations(localizedNames).setDefaultPermissions(DefaultMemberPermissions.enabledFor(userPermissions))
	
	suspend fun action(event : MessageContextInteractionEvent)
}

interface IUserCmd : IDataCmd {
	override val data : CommandData
		get() = Commands.user(name).setNameLocalizations(localizedNames).setDefaultPermissions(DefaultMemberPermissions.enabledFor(userPermissions))
	
	suspend fun action(event : UserContextInteractionEvent)
}

interface IButtonCmd : ICmd {
	suspend fun action(event : ButtonInteractionEvent)
}

interface IModalCmd : ICmd {
	val modal : Modal
	suspend fun action(event : ModalInteractionEvent)
}

interface ISelectCmd : ICmd {
	suspend fun action(event : StringSelectInteractionEvent)
}
