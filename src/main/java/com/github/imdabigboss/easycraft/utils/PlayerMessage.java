package com.github.imdabigboss.easycraft.utils;

import com.github.imdabigboss.easycraft.EasyCraft;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public enum PlayerMessage {
    COMMAND_MUST_BE_PLAYER(ChatColor.RED + "You must be a player to use this command!", ChatColor.RED + "Vous devez être un joueur pour utiliser cette commande!"),

    COMMAND_HOME_ENTER_HOME_NUMBER(ChatColor.RED + "Please enter a home number. Your max home count is: %s", ChatColor.RED + "Merci d'entrer un numéro de maison. Votre nombre maximum de maisons est: %s"),
    COMMAND_HOME_NO_HOME_NUMBER(ChatColor.RED + "You have no home with that number!", ChatColor.RED + "Vous n'avez pas de maison avec ce numéro!"),
    COMMAND_HOME_DELETED(ChatColor.AQUA + "You have deleted your home!", ChatColor.AQUA + "Vous avez supprimé votre maison!"),
    COMMAND_HOME_TELEPORTED(ChatColor.AQUA + "You have teleported to your home!", ChatColor.AQUA + "Vous avez été téléporté à votre maison!"),
    COMMAND_HOME_TELEPORT_ERROR(ChatColor.RED + "An error occurred while teleporting you to your home!", ChatColor.RED + "Une erreur s'est produite lors de la téléportation à votre maison!"),
    COMMAND_HOME_SET(ChatColor.AQUA + "You have set your home!", ChatColor.AQUA + "Vous avez défini votre maison!"),
    COMMAND_HOME_TOO_LARGE(ChatColor.RED + "Your max home count is %s!", ChatColor.RED + "Votre nombre maximum de maisons est %s!"),
    COMMAND_HOME_NAN(ChatColor.RED + "Please enter a valid number!", ChatColor.RED + "Merci d'entrer un numéro valide!"),

    COMMAND_TPA_TP_SELF(ChatColor.RED + "You can not teleport to yourself!", ChatColor.RED + "Vous ne pouvez pas vous téléporter vous-même!"),
    COMMAND_TPA_OFFLINE(ChatColor.RED + "%s is not online!", ChatColor.RED + "%s n'est pas en ligne!"),
    COMMAND_TPA_REQUEST_TPA(ChatColor.AQUA + "You have sent a teleport request to %s so you can teleport to them!", ChatColor.AQUA + "Vous avez envoyé une demande de téléportation à %s afin que vous puissiez vous téléporter à eux!"),
    COMMAND_TPA_REQUEST_TPAHERE(ChatColor.AQUA + "You have sent a teleport request to %s so they can teleport to you!", ChatColor.AQUA + "Vous avez envoyé une demande de téléportation à %s afin qu'ils puissent se téléporter à vous!"),
    COMMAND_TPA_ALREADY_HAS_REQ(ChatColor.RED + "%s already has a teleport request pending... Please wait!", ChatColor.RED + "%s already has a teleport request pending... Please wait!"),
    COMMAND_TPA_TPA_HELP("Usage: /tpa <player>", "Utilisation: /tpa <joueur>"),
    COMMAND_TPA_TPAHERE_HELP("Usage: /tpahere <player>", "Utilisation: /tpahere <joueur>"),
    COMMAND_TPA_ACCEPT_HELP("Usage: /tpaccept", "Utilisation: /tpaccept"),
    COMMAND_TPA_DENY_HELP("Usage: /tpdeny", "Utilisation: /tpdeny"),
    COMMAND_TPA_REQ_ACCEPTED_SENDER(ChatColor.AQUA + "Tpa request accepted!", ChatColor.AQUA + "Demande de téléportation acceptée!"),
    COMMAND_TPA_REQ_ACCEPTED_RECEIVER(ChatColor.AQUA + "%s accepted your tpa request!", ChatColor.AQUA + "%s a accepté votre demande de téléportation!"),
    COMMAND_TPA_REQ_DENIED_SENDER(ChatColor.AQUA + "Tpa request denied!", ChatColor.AQUA + "Demande de téléportation refusée!"),
    COMMAND_TPA_REQ_DENIED_RECEIVER(ChatColor.AQUA + "%s denied your tpa request!", ChatColor.AQUA + "%s a refusé votre demande de téléportation!"),
    COMMAND_TPA_NO_REQ(ChatColor.RED + "You have no tpa request pending!", ChatColor.RED + "Vous n'avez pas de demande de téléportation en attente!"),
    COMMAND_TPA_PLAYER_NO_LONGER_ONLINE(ChatColor.RED + "That player is no longer online...", ChatColor.RED + "Ce joueur n'est plus en ligne..."),
    COMMAND_TPA_TIMED_OUT(ChatColor.RED + "Your tpa request for %s timed out!", ChatColor.RED + "Votre demande de téléportation pour %s a expiré!"),

    COMMAND_ENDERCHEST_OPEN(ChatColor.AQUA + "You have opened your enderchest!", ChatColor.AQUA + "Vous avez ouvert votre coffre de l'end!"),
    COMMAND_ENDERCHEST_NO_PERK(ChatColor.RED + "You do not have the enderchest perk!", ChatColor.RED + "Vous n'avez pas le perk du coffre de l'end!"),

    COMMAND_MAINTENANCE_ENABLED(ChatColor.AQUA + "Maintenance mode has been enabled!", ChatColor.AQUA + "Le mode maintenance a été activé!"),
    COMMAND_MAINTENANCE_ENABLED_BROADCAST(ChatColor.AQUA + "%s has enabled maintenance mode!", ChatColor.AQUA + "%s a activé le mode maintenance!"),
    COMMAND_MAINTENANCE_ENABLED_KICK(ChatColor.RED + "The server is currently in maintenance mode! Please check back later.", ChatColor.RED + "Le serveur est actuellement en mode maintenance! Veuillez revenir plus tard."),
    COMMAND_MAINTENANCE_DISABLED(ChatColor.AQUA + "Maintenance mode has been disabled!", ChatColor.AQUA + "Le mode maintenance a été désactivé!"),
    COMMAND_MAINTENANCE_DISABLED_BROADCAST(ChatColor.AQUA + "%s has disabled maintenance mode!", ChatColor.AQUA + "%s a désactivé le mode maintenance!"),
    COMMAND_MAINTENANCE_HELP("Maintenance mode is %s.\nYou may set maintenance mode 'on' or 'off'.", "Le mode maintenance est %s.\nVous pouvez le changer avec 'on' ou 'off'."),

    COMMAND_MINIBLOCK_GIVEN(ChatColor.AQUA + "You have been given the mini block!", ChatColor.AQUA + "Vous avez reçu le mini block"),
    COMMAND_MINIBLOCK_ERROR(ChatColor.RED + "Something went wrong.", ChatColor.RED + "Une erreur c'est produite.");

    private final String english;
    private final String french;

    PlayerMessage(String english, String french) {
        this.english = english;
        this.french = french;
    }

    public String getEnglish() {
        return english;
    }

    public String getFrench() {
        return french;
    }

    public static Component formatMessage(PlayerMessage message, CommandSender commandSender, String... args) {
        if (commandSender instanceof Player player) {
            return formatMessage(message, player, args);
        } else {
            return formatMessage(message, MessageLanguage.ENGLISH, args);
        }
    }

    public static Component formatMessage(PlayerMessage message, Player player, String... args) {
        Locale locale = player.locale();
        MessageLanguage language = EasyCraft.getLanguageManager().getPlayerLanguage(player);

        if (language == null) {
            if (locale.getLanguage().equals(MessageLanguage.FRENCH.getCode())) {
                language = MessageLanguage.FRENCH;
            } else {
                language = MessageLanguage.ENGLISH;
            }
        }

        return formatMessage(message, language, args);
    }

    private static Component formatMessage(PlayerMessage message, MessageLanguage language, String... args) {
        String tmp;
        if (language == MessageLanguage.FRENCH) {
            tmp = message.getFrench();
        } else {
            tmp = message.getEnglish();
        }

        if (args != null) {
            tmp = String.format(tmp, args);
        }

        return Component.text(tmp);
    }

    public enum MessageLanguage {
        ENGLISH("en"),
        FRENCH("fr");

        private final String code;

        MessageLanguage(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public static MessageLanguage getLanguage(String code) {
            for (MessageLanguage language : values()) {
                if (language.getCode().equals(code)) {
                    return language;
                }
            }
            return null;
        }
    }
}
