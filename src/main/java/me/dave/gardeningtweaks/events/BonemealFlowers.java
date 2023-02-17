package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.ConfigManager;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class BonemealFlowers implements Listener {
    private final Random random = new Random();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY || event.useItemInHand() == Event.Result.DENY) return;

        ConfigManager.BonemealFlowers bonemealFlowers = GardeningTweaks.configManager.getBonemealFlowersConfig();
        if (!bonemealFlowers.enabled()) return;

        Block block = event.getClickedBlock();
        if (block == null) return;
        Material blockType = block.getType();
        if (!Tag.FLOWERS.isTagged(blockType)) return;

        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() != Material.BONE_MEAL) return;


        switch(blockType) {
            case POPPY -> bonemealFlower(player, mainHand, block, Material.ROSE_BUSH);
            case PINK_TULIP -> bonemealFlower(player, mainHand, block, Material.LILAC);
            case ALLIUM -> bonemealFlower(player, mainHand, block, Material.PEONY);
            case DANDELION -> bonemealFlower(player, mainHand, block, Material.SUNFLOWER);
        }
    }

    public void bonemealFlower(Player player, ItemStack mainHand, Block block, Material flowerType) {
        World world = block.getWorld();
        Location location = block.getLocation();

        if (player.getGameMode() != GameMode.CREATIVE) mainHand.setAmount(mainHand.getAmount() - 1);
        if (GardeningTweaks.protocolLibHook != null) GardeningTweaks.protocolLibHook.armInteractAnimation(player);
        world.spawnParticle(Particle.VILLAGER_HAPPY, location.clone().add(0.5, 0.2, 0.5), 10, 0.2, 0.2, 0.2);
        world.playSound(location, Sound.ITEM_BONE_MEAL_USE, 0.4f, 1.4f);

        if (random.nextInt(30) == 0) {
            if (!(flowerType.createBlockData() instanceof Bisected)) return;
            Block blockAbove = block.getRelative(BlockFace.UP);
            if (blockAbove.getType() != Material.AIR) return;
            setFlower(block, flowerType, Bisected.Half.BOTTOM);
            setFlower(blockAbove, flowerType, Bisected.Half.TOP);
        }
    }

    private void setFlower(Block block, Material type, Bisected.Half half) {
        block.setType(type,false);
        Bisected data = (Bisected) block.getBlockData();
        data.setHalf(half);
        block.setBlockData(data);
    }
}
