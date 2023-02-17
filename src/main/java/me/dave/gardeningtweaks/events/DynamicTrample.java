package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.ConfigManager;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DynamicTrample implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY || event.useItemInHand() == Event.Result.DENY) return;

        ConfigManager.DynamicTrample dynamicTrample = GardeningTweaks.configManager.getDynamicTrampleConfig();
        if (!dynamicTrample.enabled()) return;
        Action action = event.getAction();
        if (action != Action.PHYSICAL) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        Material material = block.getType();
        if (material != Material.FARMLAND) return;

        Player player = event.getPlayer();
        if (dynamicTrample.creativeMode() && player.getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(true);
        }

        if (dynamicTrample.featherFalling()) {
            ItemStack boots = player.getInventory().getBoots();
            if (boots != null) {
                ItemMeta bootsMeta = boots.getItemMeta();
                if (bootsMeta != null && bootsMeta.hasEnchant(Enchantment.PROTECTION_FALL)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
