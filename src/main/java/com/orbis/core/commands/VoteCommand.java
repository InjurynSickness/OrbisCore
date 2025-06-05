package com.orbis.core.commands;

import com.orbis.core.OrbisCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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

        // Create voting header
        Component header = Component.text("=== ", NamedTextColor.GOLD, TextDecoration.BOLD)
            .append(Component.text("Vote for OrbisMC", NamedTextColor.YELLOW, TextDecoration.BOLD))
            .append(Component.text(" ===", NamedTextColor.GOLD, TextDecoration.BOLD));
        
        sender.sendMessage(header);

        // Voting Site One
        Component vote1 = createVoteLink(
            "Voting Site One",
            "https://www.minecraftiplist.com/server/OrbisMC-34599/vote",
            "Click to vote on MinecraftIPList"
        );

        // Voting Site Two (with username parameter)
        String vote2Url = "https://minecraftservers.org/vote/663746?username=" + playerName;
        Component vote2 = createVoteLink(
            "Voting Site Two",
            vote2Url,
            "Click to vote on MinecraftServers.org"
        );

        // Voting Site Three (with username parameter)
        String vote3Url = "https://minecraft-serverlist.com/server/1341/vote?username=" + playerName;
        Component vote3 = createVoteLink(
            "Voting Site Three",
            vote3Url,
            "Click to vote on Minecraft-ServerList"
        );

        // Voting Site Four (with username parameter)
        String vote4Url = "https://www.planetminecraft.com/server/orbismc-6338671/vote/?username=" + playerName;
        Component vote4 = createVoteLink(
            "Voting Site Four",
            vote4Url,
            "Click to vote on PlanetMinecraft"
        );

        // Send all voting links with spacing
        sender.sendMessage(Component.empty());
        sender.sendMessage(vote1);
        sender.sendMessage(vote2);
        sender.sendMessage(vote3);
        sender.sendMessage(vote4);
        
        // Thank you message
        Component thankYou = Component.text("Thank you for supporting OrbisMC!", NamedTextColor.GREEN, TextDecoration.ITALIC);
        sender.sendMessage(Component.empty());
        sender.sendMessage(thankYou);

        return true;
    }

    /**
     * Create a formatted vote link component
     */
    private Component createVoteLink(String siteName, String url, String hoverText) {
        return Component.text("» ", NamedTextColor.GOLD)
            .append(Component.text(siteName, NamedTextColor.YELLOW)
                .decorate(TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.openUrl(url))
                .hoverEvent(HoverEvent.showText(
                    Component.text(hoverText, NamedTextColor.GRAY)
                        .appendNewline()
                        .append(Component.text(url, NamedTextColor.BLUE))
                )));
    }
}