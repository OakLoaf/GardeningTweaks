package me.dave.gardeningtweaks.module;

import me.dave.gardeningtweaks.api.events.BushRejuvenateEvent;
import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.hooks.HookId;
import me.dave.gardeningtweaks.hooks.ProtocolLibHook;
import me.dave.platyutils.listener.EventListener;
import me.dave.platyutils.module.Module;
import me.dave.platyutils.utils.StringUtils;
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

public class RejuvenatedBushes extends Module implements EventListener {
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

        items = new HashMap<>();
        ConfigurationSection flowersSection = config.getConfigurationSection("items");
        if (flowersSection != null) {
            flowersSection.getValues(false).forEach((fromRaw, toRaw) -> {
                StringUtils.getEnum(String.valueOf(fromRaw), Material.class).ifPresentOrElse(
                    from -> StringUtils.getEnum(String.valueOf(fromRaw), Material.class).ifPresentOrElse(
                        to -> items.put(from, to),
                        () -> GardeningTweaks.getInstance().getLogger().warning("'" + toRaw + "' is not a valid material")
                    ),
                    () -> GardeningTweaks.getInstance().getLogger().warning("'" + fromRaw + "' is not a valid material")
                );
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

        GardeningTweaks.getInstance().getHook(HookId.PROTOCOL_LIB.toString()).ifPresent(hook -> ((ProtocolLibHook) hook).armInteractAnimation(player));

        World world = block.getWorld();
        Location location = block.getLocation();
        world.spawnParticle(Particle.VILLAGER_HAPPY, location.clone().add(0.5, 0.5, 0.5), 50, 0.3, 0.3, 0.3);
        world.playSound(location, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 2f, 1f);
    }
}
