package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.ConfigManager;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class BlockEvents implements Listener {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();
    private final Random random = new Random();

    private final EnumSet<BlockFace> blockFaces = EnumSet.complementOf(EnumSet.of(
        BlockFace.EAST_NORTH_EAST, BlockFace.EAST_SOUTH_EAST,
        BlockFace.NORTH_NORTH_EAST, BlockFace.NORTH_NORTH_WEST,
        BlockFace.SOUTH_SOUTH_EAST, BlockFace.SOUTH_SOUTH_WEST,
        BlockFace.WEST_NORTH_WEST, BlockFace.WEST_SOUTH_WEST
    ));

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Decoarsify
        if (GardeningTweaks.configManager.isDecoarsifyEnabled()) {
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
        ConfigManager.CustomGrassDrops customGrassDrops = GardeningTweaks.configManager.getCustomGrassDropsConfig();
        if (customGrassDrops.enabled() && customGrassDrops.blocks().size() > 0) {
            Block block = event.getBlock();
            Player player = event.getPlayer();
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            Collection<ItemStack> drops = block.getDrops(mainHand);
            if (block.getType() == Material.GRASS && drops.size() > 0) {
                Location location = block.getLocation();
                World world = location.getWorld();
                if (world != null && player.getGameMode() != GameMode.CREATIVE && mainHand.getType() != Material.SHEARS) {
                    event.setDropItems(false);
                    List<Material> grassDrops = customGrassDrops.blocks();
                    for (int i = 0; i < drops.iterator().next().getAmount(); i++) {
                        world.dropItemNaturally(location, new ItemStack(grassDrops.get(random.nextInt(grassDrops.size()))));
                    }
                }
            }

            // Lumberjack
//            if ()
        }
    }

    private void breakVeinBlock(Block block) {
        Material currType = block.getType();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (BlockFace face : blockFaces) {
                Block blockToBreak = block.getRelative(face);
                if (blockToBreak.getType() == currType) breakVeinBlock(blockToBreak);
            }
            block.breakNaturally();
        }, 1);
    }
}
