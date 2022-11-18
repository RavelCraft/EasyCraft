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
    COMMAND_HOME_NAN(ChatColor.RED + "Please enter a valid number!", ChatColor.RED + "Merci d'entrer un numéro valide!");

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
