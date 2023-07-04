package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.data.ConfigManager;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class CustomGrassDrops implements Listener {
    private final Random random = new Random();

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        if (event.isCancelled()) return;
        ConfigManager.CustomGrassDrops customGrassDrops = GardeningTweaks.getConfigManager().getCustomGrassDropsConfig();
        if (!customGrassDrops.enabled() || customGrassDrops.items().size() == 0) return;

        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        BlockState blockState = event.getBlockState();
        List<ItemStack> drops = event.getItems().stream().map(Item::getItemStack).toList();
        if (blockState.getType() != Material.GRASS || drops.size() == 0) return;

        if (player.getGameMode() == GameMode.CREATIVE || mainHand.getType() == Material.SHEARS) return;
        event.setCancelled(true);
        List<Material> grassDrops = customGrassDrops.items();
        World world = blockState.getWorld();
        Location location = blockState.getLocation();

        for (int i = 0; i < drops.iterator().next().getAmount(); i++) {
            world.dropItemNaturally(location, new ItemStack(grassDrops.get(random.nextInt(grassDrops.size()))));
        }
    }
}
