package me.dave.gardeningtweaks.module.custom;

import me.dave.gardeningtweaks.api.events.BushRejuvenateEvent;
import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.module.Module;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;

public class RejuvenatedBushes extends Module implements Listener {
    private HashMap<Material, Material> items;

    public RejuvenatedBushes(String id) {
        super(id);
    }

    @Override
    public void onEnable() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();

        File configFile = new File(plugin.getDataFolder(), "modules/rejuvenated-bushes.yml");
        if (!configFile.exists()) {
            plugin.saveResource("modules/rejuvenated-bushes.yml", false);
            plugin.getLogger().info("File Created: rejuvenated-bushes.yml");
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        items = new HashMap<>();
        ConfigurationSection flowersSection = config.getConfigurationSection("items");
        if (flowersSection != null) {
            flowersSection.getValues(false).forEach((fromRaw, toRaw) -> {
                Material from;
                try {
                    from = Material.valueOf(String.valueOf(fromRaw));
                } catch (IllegalArgumentException e) {
                    GardeningTweaks.getInstance().getLogger().warning("'" + fromRaw + "' is not a valid material");
                    return;
                }

                Material to;
                try {
                    to = Material.valueOf(String.valueOf(toRaw));
                } catch (IllegalArgumentException e) {
                    GardeningTweaks.getInstance().getLogger().warning("'" + toRaw + "' is not a valid material");
                    return;
                }

                items.put(from, to);
            });
        }

        if (items.isEmpty()) {
            GardeningTweaks.getInstance().getLogger().warning("There are no valid materials configured, automatically disabling the 'rejuvenated-bushes' module");
            disable();
        }
    }

    @Override
    public void onDisable() {
        if (items != null) {
            items.clear();
            items = null;
        }
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
        if (items.containsKey(mainHand.getType())) {
            bushToSapling(player, mainHand, block, items.get(mainHand.getType()));
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
