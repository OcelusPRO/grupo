package fr.ftnl.grupo.core.commands

import dev.minn.jda.ktx.util.SLF4J
import fr.ftnl.grupo.extentions.toLang
import fr.ftnl.grupo.lang.LangKey
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import org.joda.time.DateTime
import kotlin.reflect.jvm.jvmName
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import kotlin.time.toDuration

class CommandManager {
    private val logger by SLF4J
    private val lastCommandUsageMap: MutableMap<Pair<ICmd, String>, DateTime> = mutableMapOf()

    private fun reply(e: GenericInteractionCreateEvent, message: MessageCreateData, ephemeral: Boolean = true) {
        if (e is IReplyCallback) e.reply(message).setEphemeral(ephemeral).queue()
    }
	
    @OptIn(ExperimentalTime::class)
    private inline fun <reified T : ICmd, U : GenericInteractionCreateEvent> execCmd(
        e: U,
        filter: (U) -> String,
        action: (T) -> Unit
    ) {
        val cmd = ICmd.cmd.filterIsInstance<T>().find { it.name.startsWith(filter.invoke(e)) }
            ?: return
		
        if (!cmd.allowDM && !e.isFromGuild) {
            return reply(
                e,
                MessageCreateBuilder().setContent(
                    "Cette commande n'est pas disponible en messages privées".toLang(
                        e.userLocale,
                        LangKey.keyBuilder(this, "handle.interaction", "not_available_in_dm")
                    )
                ).build()
            )
        }
		
        val key = Pair(cmd, e.user.id)
        val entry = lastCommandUsageMap.getOrDefault(key, DateTime(0))
		
        val canDate = entry.plus(cmd.cooldown.toLong(DurationUnit.MILLISECONDS)).toDateTime().millis
        val canInDuration = DateTime.now().minus(canDate).millis.toDuration(DurationUnit.MILLISECONDS)
        val cooldownMessage = MessageCreateBuilder().setContent(
            ":timer: Vous devez encore attendre %ss avant de pouvoir utiliser cette commande.".toLang(
                e.userLocale,
                LangKey.keyBuilder(this, "handle.interaction", "cooldownMessage")
            )
                .format(canInDuration.toLong(DurationUnit.SECONDS))
        ).build()
		
        if (entry.plusSeconds(cmd.cooldown.toInt(DurationUnit.SECONDS)).isAfterNow) return reply(e, cooldownMessage)
		
        val time = measureTime {
            try {
                action.invoke(cmd)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        lastCommandUsageMap[key] = DateTime.now()
		
        logger.info(
            """
            	${T::class.jvmName.uppercase()} :
            	-	${cmd.name}
            	TEMPS :
            	-	${time.toDouble(DurationUnit.MILLISECONDS)}ms
            	USER :
            	-	[${e.user.id}] ${e.user.asTag}
            	RAW :
            	-	${e.rawData}
            }---------------------------------------------------------------------------
            """.trimIndent()
        )
    }
	
    suspend fun dispatch(event: GenericInteractionCreateEvent) {
        when (event) {
            is SlashCommandInteractionEvent -> execCmd<ISlashCmd, SlashCommandInteractionEvent>(event, { it.name }) {
                it.action(
                    event
                )
            }
            is MessageContextInteractionEvent -> execCmd<IMessageCmd, MessageContextInteractionEvent>(
                event,
                { it.name }
            ) {
                it.action(
                    event
                )
            }
            is UserContextInteractionEvent -> execCmd<IUserCmd, UserContextInteractionEvent>(event, { it.name }) {
                it.action(
                    event
                )
            }
            is CommandAutoCompleteInteractionEvent -> execCmd<ISlashCmd, CommandAutoCompleteInteractionEvent>(
                event,
                { it.name }
            ) {
                it.action(
                    event
                )
            }
            is ButtonInteractionEvent -> execCmd<IButtonCmd, ButtonInteractionEvent>(event, { it.componentId }) {
                it.action(
                    event
                )
            }
            is StringSelectInteractionEvent -> execCmd<ISelectCmd, StringSelectInteractionEvent>(
                event,
                { it.componentId }
            ) {
                it.action(
                    event
                )
            }
            is ModalInteractionEvent -> execCmd<IModalCmd, ModalInteractionEvent>(event, { it.id }) { it.action(event) }
        }
    }
}
