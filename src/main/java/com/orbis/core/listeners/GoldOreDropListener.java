package com.orbis.core.listeners;

import com.orbis.core.OrbisCore;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Handles custom gold ore drop behavior
 */
public class GoldOreDropListener implements Listener {

    private final OrbisCore plugin;

    public GoldOreDropListener(OrbisCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material blockType = event.getBlock().getType();

        // Only handle gold ore blocks
        if (blockType != Material.GOLD_ORE && blockType != Material.DEEPSLATE_GOLD_ORE) {
            return;
        }

        // Only apply to survival mode players
        if (player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        ItemStack tool = player.getInventory().getItemInMainHand();

        // Check if tool is a pickaxe
        if (!isPickaxe(tool)) {
            return;
        }

        // Clear default drops
        event.setDropItems(false);

        // Check for silk touch enchantment
        boolean hasSilkTouch = tool.containsEnchantment(Enchantment.SILK_TOUCH);

        if (hasSilkTouch) {
            // Drop the ore block itself
            if (blockType == Material.GOLD_ORE) {
                event.getBlock().getWorld().dropItemNaturally(
                    event.getBlock().getLocation(), 
                    new ItemStack(Material.GOLD_ORE)
                );
            } else if (blockType == Material.DEEPSLATE_GOLD_ORE) {
                event.getBlock().getWorld().dropItemNaturally(
                    event.getBlock().getLocation(), 
                    new ItemStack(Material.DEEPSLATE_GOLD_ORE)
                );
            }
        } else {
            // Drop raw gold instead
            event.getBlock().getWorld().dropItemNaturally(
                event.getBlock().getLocation(), 
                new ItemStack(Material.RAW_GOLD)
            );
        }
    }

    /**
     * Check if the item is a pickaxe
     *
     * @param item The item to check
     * @return True if the item is a pickaxe
     */
    private boolean isPickaxe(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        Material type = item.getType();
        return type == Material.WOODEN_PICKAXE ||
               type == Material.STONE_PICKAXE ||
               type == Material.IRON_PICKAXE ||
               type == Material.GOLDEN_PICKAXE ||
               type == Material.DIAMOND_PICKAXE ||
               type == Material.NETHERITE_PICKAXE;
    }
}