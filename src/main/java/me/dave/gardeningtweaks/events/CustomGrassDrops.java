package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.datamanager.ConfigManager;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class CustomGrassDrops implements Listener {
    private final Random random = new Random();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        ConfigManager.CustomGrassDrops customGrassDrops = GardeningTweaks.getConfigManager().getCustomGrassDropsConfig();
        if (!customGrassDrops.enabled() || customGrassDrops.items().size() == 0) return;

        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        Block block = event.getBlock();
        Collection<ItemStack> drops = block.getDrops(mainHand);
        if (block.getType() != Material.GRASS || drops.size() == 0) return;

        if (player.getGameMode() == GameMode.CREATIVE || mainHand.getType() == Material.SHEARS) return;
        event.setDropItems(false);
        List<Material> grassDrops = customGrassDrops.items();
        World world = block.getWorld();
        Location location = block.getLocation();

        for (int i = 0; i < drops.iterator().next().getAmount(); i++) {
            world.dropItemNaturally(location, new ItemStack(grassDrops.get(random.nextInt(grassDrops.size()))));
        }
    }
}
