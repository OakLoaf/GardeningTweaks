package me.dave.gardeningtweaks.events;

import me.dave.gardeningtweaks.api.events.FlowerBonemealEvent;
import me.dave.gardeningtweaks.data.ConfigManager;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class BonemealFlowers implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY || event.useItemInHand() == Event.Result.DENY) return;

        ConfigManager.BonemealFlowers bonemealFlowers = GardeningTweaks.getConfigManager().getBonemealFlowersConfig();
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

    public static void bonemealFlower(@Nullable Player player, @Nullable ItemStack mainHand, Block block, Material flowerType) {
        World world = block.getWorld();
        Location location = block.getLocation();

        if (!GardeningTweaks.callEvent(new FlowerBonemealEvent(block))) return;

        if (player != null) {
            if (!GardeningTweaks.callEvent(new BlockPlaceEvent(block, block.getState(), block.getRelative(BlockFace.DOWN), new ItemStack(Material.BONE_MEAL), player, true, EquipmentSlot.HAND))) return;
            if (!GardeningTweaks.callEvent(new BlockPlaceEvent(block.getRelative(BlockFace.UP), block.getState(), block.getRelative(BlockFace.DOWN), new ItemStack(Material.BONE_MEAL), player, true, EquipmentSlot.HAND))) return;

            if (player.getGameMode() != GameMode.CREATIVE && mainHand != null) mainHand.setAmount(mainHand.getAmount() - 1);
            if (GardeningTweaks.protocolLibHook != null) GardeningTweaks.protocolLibHook.armInteractAnimation(player);
        }

        world.spawnParticle(Particle.VILLAGER_HAPPY, location.clone().add(0.5, 0.2, 0.5), 10, 0.2, 0.2, 0.2);
        world.playSound(location, Sound.ITEM_BONE_MEAL_USE, 0.4f, 1.4f);

        if (GardeningTweaks.getRandom().nextInt(30) == 0) {
            if (!(flowerType.createBlockData() instanceof Bisected)) return;
            Block blockAbove = block.getRelative(BlockFace.UP);
            if (blockAbove.getType() != Material.AIR) return;
            setFlower(block, flowerType, Bisected.Half.BOTTOM);
            setFlower(blockAbove, flowerType, Bisected.Half.TOP);
        }
    }

    private static void setFlower(Block block, Material type, Bisected.Half half) {
        block.setType(type,false);
        Bisected data = (Bisected) block.getBlockData();
        data.setHalf(half);
        block.setBlockData(data);
    }
}
