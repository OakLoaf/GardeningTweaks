package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.ConfigManager;
import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.dependencies.ProtocolLibHook;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RejuvenatedBushes implements Listener {
    private final ProtocolLibHook protocolLibHook;

    public RejuvenatedBushes(ProtocolLibHook protocolLibHook) {
        this.protocolLibHook = protocolLibHook;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ConfigManager.RejuvenatedBushes rejuvenatedBushes = GardeningTweaks.configManager.getRejuvenatedBushesConfig();
        if (!rejuvenatedBushes.enabled()) return;

        Block block = event.getClickedBlock();
        if (block == null) return;
        Material blockType = block.getType();

        if (blockType != Material.DEAD_BUSH) return;
        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        switch(mainHand.getType()) {
            case WHEAT_SEEDS -> bushToSapling(player, mainHand, block, Material.OAK_SAPLING);
            case BEETROOT_SEEDS -> bushToSapling(player, mainHand, block, Material.ACACIA_SAPLING);
            case MELON_SEEDS -> bushToSapling(player, mainHand, block, Material.SPRUCE_SAPLING);
            case PUMPKIN_SEEDS -> bushToSapling(player, mainHand, block, Material.BIRCH_SAPLING);
        }
    }

    public void bushToSapling(Player player, ItemStack mainHand, Block block, Material saplingType) {
        if (player.getGameMode() != GameMode.CREATIVE) mainHand.setAmount(mainHand.getAmount() - 1);
        block.setType(saplingType);
        protocolLibHook.armInteractAnimation(player);
        World world = block.getWorld();
        Location location = block.getLocation();
        world.spawnParticle(Particle.VILLAGER_HAPPY, location.clone().add(0.5, 0.5, 0.5), 50, 0.3, 0.3, 0.3);
        world.playSound(location, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 2f, 1f);
    }
}