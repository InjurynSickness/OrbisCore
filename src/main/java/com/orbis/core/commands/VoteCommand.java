package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to show clickable links to voting sites
 */
public class VoteCommand implements CommandExecutor {

    private final OrbisCore plugin;

    public VoteCommand(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String playerName = "";

        // Get player name for voting URLs that require it
        if (sender instanceof Player) {
            playerName = sender.getName();
        }

        // Voting Site One
        Component vote1 = Component.text("» ", NamedTextColor.GOLD)
                .append(Component.text("Voting Site One", NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.openUrl("https://www.minecraftiplist.com/server/OrbisMC-34599/vote")));

        // Voting Site Two (with username parameter)
        String vote2Url = "https://minecraftservers.org/vote/663746?username=" + playerName;
        Component vote2 = Component.text("» ", NamedTextColor.GOLD)
                .append(Component.text("Voting Site Two", NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.openUrl(vote2Url)));

        // Voting Site Three (with username parameter)
        String vote3Url = "https://minecraft-serverlist.com/server/1341/vote?username=" + playerName;
        Component vote3 = Component.text("» ", NamedTextColor.GOLD)
                .append(Component.text("Voting Site Three", NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.openUrl(vote3Url)));

        // Voting Site Four (with username parameter)
        String vote4Url = "https://www.planetminecraft.com/server/orbismc-6338671/vote/?username=" + playerName;
        Component vote4 = Component.text("» ", NamedTextColor.GOLD)
                .append(Component.text("Voting Site Four", NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.openUrl(vote4Url)));

        // Send all voting links
        sender.sendMessage(vote1);
        sender.sendMessage(vote2);
        sender.sendMessage(vote3);
        sender.sendMessage(vote4);

        return true;
    }
}