package org.lushplugins.gardeningtweaks.module;

import org.bukkit.event.Listener;
import org.lushplugins.gardeningtweaks.api.events.BushRejuvenateEvent;
import org.lushplugins.gardeningtweaks.GardeningTweaks;
import org.lushplugins.lushlib.module.Module;
import org.lushplugins.lushlib.registry.RegistryUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;

public class RejuvenatedBushes extends Module implements Listener {
    public static final String ID = "REJUVENATED_BUSHES";

    private HashMap<Material, Material> items;

    public RejuvenatedBushes() {
        super(ID);
    }

    @Override
    public void onEnable() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();
        plugin.saveDefaultResource("modules/rejuvenated-bushes.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "modules/rejuvenated-bushes.yml"));

        this.items = new HashMap<>();
        ConfigurationSection flowersSection = config.getConfigurationSection("items");
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

                this.items.put(from, to);
            });
        }

        if (this.items.isEmpty()) {
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
        if (!GardeningTweaks.getInstance().callEvent(new BushRejuvenateEvent(block, player, mainHand, saplingType))) return;

        if (player.getGameMode() != GameMode.CREATIVE) mainHand.setAmount(mainHand.getAmount() - 1);
        block.setType(saplingType);

        GardeningTweaks.getInstance().getPacketHook().ifPresent(packetHook -> packetHook.armInteractAnimation(player));

        World world = block.getWorld();
        Location location = block.getLocation();
        world.spawnParticle(Particle.VILLAGER_HAPPY, location.clone().add(0.5, 0.5, 0.5), 50, 0.3, 0.3, 0.3);
        world.playSound(location, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 2f, 1f);
    }
}
