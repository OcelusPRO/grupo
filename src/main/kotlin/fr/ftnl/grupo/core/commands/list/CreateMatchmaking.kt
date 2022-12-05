package fr.ftnl.grupo.core.commands.list

import dev.minn.jda.ktx.coroutines.await
import dev.minn.jda.ktx.events.await
import dev.minn.jda.ktx.messages.MessageCreateBuilder
import fr.ftnl.grupo.core.commands.ISlashCmd
import fr.ftnl.grupo.database.models.Game
import fr.ftnl.grupo.database.models.MatchmakingEvent
import fr.ftnl.grupo.extentions.toLang
import fr.ftnl.grupo.lang.LangKey
import kotlinx.coroutines.withTimeoutOrNull
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import kotlin.time.Duration.Companion.seconds

class CreateMatchmaking : ISlashCmd {
    override val description: String
        get() = "Lancé un nouveau matchmaking"
    override val localizedDescriptions: Map<DiscordLocale, String>
        get() = mapOf()
    override val options: List<OptionData>
        get() = listOf(
            OptionData(OptionType.STRING, "game", "Le jeu pour lequel vous voulez lancer un matchmaking", true, true),
            OptionData(OptionType.STRING, "message", "Le message à envoyer", true),
            OptionData(OptionType.STRING, "debut", "Heure du début de votre partie", true),
            OptionData(OptionType.CHANNEL, "channel", "Le channel vocal pour le matchmaking", true),
            OptionData(OptionType.BOOLEAN, "local", "Si le matchmaking est local ou non", true),
            OptionData(OptionType.STRING, "invite", "L'invitation du serveur (générer si vide)", false),
            OptionData(
                OptionType.INTEGER, "repeat", "Si le matchmaking est répétable, le nombre de jours entre chaque répétition", false
            )
        )
    
    override suspend fun action(event: SlashCommandInteractionEvent) {
        val channel = event.getOption("channel")!!.asChannel
        if (channel !is AudioChannel) {
            return event.reply(
                    "Le channel doit être un channel vocal".toLang(
                        event.userLocale, LangKey.keyBuilder(this, "action", "channel_not_voice")
                    )
                ).setEphemeral(false).queue()
        }
        val game = transaction {
            Game.findById(event.getOption("game")!!.asString.toInt())!!
        }
        val invite = event.getOption("invite")?.asString
            ?: generateInvite(event.guild!!, channel)
            ?: return event.reply(
                    "Impossible de générer une invitation".toLang(
                        event.userLocale, LangKey.keyBuilder(this, "action", "invite_generation_failed")
                    )
                ).setEphemeral(false).queue()
        val vc: AudioChannel = channel
        val date = fr.ftnl.grupo.core.utils.DateTimeUtils().parseFromString(event.getOption("debut")!!.asString)
            ?: return event.reply(
                    "Je n'ait pas réussi a lire la date de l'évenement, pouriez vous la formulé autrement (via https://r.3v.fi/discord-timestamps/ par exemple)".toLang(
                        event.userLocale, LangKey.keyBuilder(this, "action", "date_parsing_failed")
                    )
                ).setEphemeral(false).queue()
        val nonce = System.currentTimeMillis() // TODO add lang systeme on embed
        event.reply(
            MessageCreateBuilder {
                content = "**Est ce que cela vous conviens ?**"
                embed {
                    title = "Résumé de votre évènement :"
                    val repeatDays = event.getOption("repeat")
                    
                    description = """
                Sur le jeu : ${game.name} sur ${game.platform.name}
                Avec ${game.players} joueurs
                Se déroulera le <t:${date.time / 1000}:f>
                Sur le serveur : ${event.guild!!.name}
                Dans le channel vocal : ${vc.asMention}
                Avec l'invitation : $invite
                ${if (repeatDays != null) "Répétable tous les ${repeatDays.asLong} jours" else ""}
                ${if (event.getOption("local")!!.asBoolean) "Uniquement sur ce serveur" else "Sera diffusé aux autres serveurs"}
                Message : ${event.getOption("message")!!.asString}
                    """.trimIndent()
                }
            }.build()
        ).addActionRow(
            Button.success("confirm-$nonce", "Confirmer"), Button.danger("cancel-$nonce", "Annuler")
        ).setEphemeral(false).queue()
        
        withTimeoutOrNull(30.seconds) {
            val e = event.jda.await<ButtonInteractionEvent> {
                (it.user.id == event.user.id) && (it.componentId.split("-")[1] == nonce.toString())
            }
            when (e.componentId.split("-")[0]) {
                "confirm" -> {
                    MatchmakingEvent.createEvent(
                        game = game,
                        message = event.getOption("message")!!.asString,
                        startDateTime = DateTime.parse(date.toInstant().toString()),
                        guildInvite = invite,
                        voiceChannelId = vc.id,
                        localEvent = event.getOption("local")!!.asBoolean,
                        repeatableDays = event.getOption("repeat")?.asLong?.toInt()
                    )
                    e.editMessage("Votre évènement a été enregistré !").setSuppressEmbeds(true).setActionRow(null).queue()
                }
                
                "cancel"  -> {
                    e.editMessage("Votre évènement a été annulé !").setSuppressEmbeds(true).setActionRow(null).queue()
                }
            }
        }
    }
    
    private suspend fun generateInvite(guild: Guild, vc: AudioChannel): String? {
        val invite = guild.retrieveInvites().await().firstOrNull()
            ?: vc.createInvite().await()
            ?: return null
        return "https://discord.gg/${invite.code}"
    }
    
    override suspend fun action(event: CommandAutoCompleteInteractionEvent) {
        transaction {
            val games = Game.all()
            val filteredGames = games.filter { it.name.contains(event.focusedOption.value) }.chunked(25).first()
            val choices = filteredGames.map { Command.Choice(it.name, it.id.value.toString()) }
            println(choices)
            event.replyChoices(choices).queue()
        }
    }
    
    override val localizedNames: Map<DiscordLocale, String>
        get() = mapOf()
    override val name: String
        get() = "matchmaking"
    override val userPermissions: List<Permission>
        get() = listOf()
    override val allowDM: Boolean
        get() = false
}
