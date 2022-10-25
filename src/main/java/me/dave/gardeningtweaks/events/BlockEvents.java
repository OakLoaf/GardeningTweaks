package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class BlockEvents implements Listener {
    private final Random random = new Random();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Decoarsify
        if (GardeningTweaks.configManager.hasDecoarsify()) {
            Block block = event.getBlock();
            if (block.getType() == Material.COARSE_DIRT) {
                Location location = block.getLocation();
                World world = location.getWorld();
                Player player = event.getPlayer();
                if (world != null && player.getGameMode() != GameMode.CREATIVE) {
                    ItemMeta mainHandMeta = player.getInventory().getItemInMainHand().getItemMeta();
                    if (mainHandMeta != null && !mainHandMeta.hasEnchant(Enchantment.SILK_TOUCH)) {
                        event.setDropItems(false);
                        world.dropItemNaturally(location, new ItemStack(Material.DIRT));
                    }
                }
            }
        }

        // Custom Grass Drops
        List<Material> grassDrops = GardeningTweaks.configManager.getGrassDrops();
        if (grassDrops.size() > 0) {
            Block block = event.getBlock();
            Player player = event.getPlayer();
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            Collection<ItemStack> drops = block.getDrops(mainHand);
            if (block.getType() == Material.GRASS && drops.size() > 0) {
                Location location = block.getLocation();
                World world = location.getWorld();
                if (world != null && player.getGameMode() != GameMode.CREATIVE && mainHand.getType() != Material.SHEARS) {
                    event.setDropItems(false);
                    for (int i = 0; i < drops.iterator().next().getAmount(); i++) {
                        world.dropItemNaturally(location, new ItemStack(grassDrops.get(random.nextInt(grassDrops.size()))));
                    }
                }
            }
        }
    }
}
