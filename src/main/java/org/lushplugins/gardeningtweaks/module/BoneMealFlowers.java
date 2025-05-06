package org.lushplugins.gardeningtweaks.module;

import org.bukkit.event.Listener;
import org.lushplugins.gardeningtweaks.api.events.FlowerBoneMealEvent;
import org.lushplugins.gardeningtweaks.GardeningTweaks;
import org.lushplugins.lushlib.module.Module;
import org.lushplugins.lushlib.registry.RegistryUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;

public class BoneMealFlowers extends Module implements Listener {
    public static final String ID = "BONE_MEAL_FLOWERS";

    private HashMap<Material, Material> flowers;

    public BoneMealFlowers() {
        super(ID);
    }

    @Override
    public void onEnable() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();
        plugin.saveDefaultResource("modules/bone-meal-flowers.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "modules/bone-meal-flowers.yml"));

        this.flowers = new HashMap<>();
        ConfigurationSection flowersSection = config.getConfigurationSection("flowers");
        if (flowersSection != null) {
            flowersSection.getValues(false).forEach((fromRaw, toRaw) -> {
                Material from = RegistryUtils.parseString(fromRaw, Registry.MATERIAL);
                Material to = RegistryUtils.parseString(String.valueOf(toRaw), Registry.MATERIAL);
                if (from == null) {
                    GardeningTweaks.getInstance().getLogger().warning("'" + fromRaw + "' is not a valid material");
                    return;
                } else if (to == null) {
                    GardeningTweaks.getInstance().getLogger().warning("'" + toRaw + "' is not a valid material");
                    return;
                }

                if (!Tag.FLOWERS.isTagged(from)) {
                    GardeningTweaks.getInstance().getLogger().warning("'" + fromRaw + "' is not a flower");
                } else if (!Tag.FLOWERS.isTagged(to)) {
                    GardeningTweaks.getInstance().getLogger().warning("'" + toRaw + "' is not a flower");
                } else {
                    this.flowers.put(from, to);
                }
            });
        }

        if (this.flowers.isEmpty()) {
            GardeningTweaks.getInstance().getLogger().warning("There are no valid materials configured, automatically disabling the '" + ID  + "' module");
            disable();
        }
    }

    @Override
    public void onDisable() {
        if (flowers != null) {
            flowers.clear();
            flowers = null;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY || event.useItemInHand() == Event.Result.DENY) return;

        Block block = event.getClickedBlock();
        if (block == null) return;
        Material blockType = block.getType();
        if (!Tag.FLOWERS.isTagged(blockType)) return;

        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.getType() != Material.BONE_MEAL) return;

        if (flowers.containsKey(blockType)) {
            boneMealFlower(player, mainHand, block, flowers.get(blockType), 50);
        }
    }

    public static void boneMealFlower(@Nullable Player player, @Nullable ItemStack mainHand, Block block, Material flowerType, int chance) {
        World world = block.getWorld();
        Location location = block.getLocation();

        if (!GardeningTweaks.getInstance().callEvent(new FlowerBoneMealEvent(block))) return;

        if (player != null) {
            if (!GardeningTweaks.getInstance().callEvent(new BlockPlaceEvent(block, block.getState(), block.getRelative(BlockFace.DOWN), new ItemStack(Material.BONE_MEAL), player, true, EquipmentSlot.HAND))) return;
            if (flowerType.createBlockData() instanceof Bisected && !GardeningTweaks.getInstance().callEvent(new BlockPlaceEvent(block.getRelative(BlockFace.UP), block.getState(), block.getRelative(BlockFace.DOWN), new ItemStack(Material.BONE_MEAL), player, true, EquipmentSlot.HAND))) return;

            if (player.getGameMode() != GameMode.CREATIVE && mainHand != null) mainHand.setAmount(mainHand.getAmount() - 1);
            GardeningTweaks.getInstance().getPacketHook().ifPresent(packetHook -> packetHook.armInteractAnimation(player));
        }

        world.spawnParticle(Particle.VILLAGER_HAPPY, location.clone().add(0.5, 0.2, 0.5), 10, 0.2, 0.2, 0.2);
        world.playSound(location, Sound.ITEM_BONE_MEAL_USE, 0.4f, 1.4f);

        if (GardeningTweaks.getRandom().nextInt(100) < chance) {
            if (flowerType.createBlockData() instanceof Bisected) {
                Block blockAbove = block.getRelative(BlockFace.UP);
                if (blockAbove.getType() != Material.AIR) return;
                setFlower(block, flowerType, Bisected.Half.BOTTOM);
                setFlower(blockAbove, flowerType, Bisected.Half.TOP);
            } else {
                block.setType(flowerType, false);
            }
        }
    }

    private static void setFlower(Block block, Material type, Bisected.Half half) {
        block.setType(type,false);
        Bisected data = (Bisected) block.getBlockData();
        data.setHalf(half);
        block.setBlockData(data);
    }
}
