package me.dave.gardeningtweaks.module.custom;

import me.dave.gardeningtweaks.api.events.BushRejuvenateEvent;
import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.module.Module;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RejuvenatedBushes extends Module implements Listener {

    public RejuvenatedBushes(String id) {
        super(id);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY || event.useItemInHand() == Event.Result.DENY || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        Material blockType = block.getType();
        if (blockType != Material.DEAD_BUSH) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        switch (mainHand.getType()) {
            case WHEAT_SEEDS -> bushToSapling(player, mainHand, block, Material.OAK_SAPLING);
            case BEETROOT_SEEDS -> bushToSapling(player, mainHand, block, Material.ACACIA_SAPLING);
            case MELON_SEEDS -> bushToSapling(player, mainHand, block, Material.SPRUCE_SAPLING);
            case PUMPKIN_SEEDS -> bushToSapling(player, mainHand, block, Material.BIRCH_SAPLING);
            case TORCHFLOWER_SEEDS -> bushToSapling(player, mainHand, block, Material.CHERRY_SAPLING);
        }
    }

    public void bushToSapling(Player player, ItemStack mainHand, Block block, Material saplingType) {
        if (!GardeningTweaks.callEvent(new BushRejuvenateEvent(block, player, mainHand, saplingType))) return;

        if (player.getGameMode() != GameMode.CREATIVE) mainHand.setAmount(mainHand.getAmount() - 1);
        block.setType(saplingType);

        if (GardeningTweaks.protocolLibHook != null) GardeningTweaks.protocolLibHook.armInteractAnimation(player);

        World world = block.getWorld();
        Location location = block.getLocation();
        world.spawnParticle(Particle.VILLAGER_HAPPY, location.clone().add(0.5, 0.5, 0.5), 50, 0.3, 0.3, 0.3);
        world.playSound(location, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 2f, 1f);
    }
}
