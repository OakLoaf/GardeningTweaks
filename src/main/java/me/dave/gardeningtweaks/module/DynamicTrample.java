package me.dave.gardeningtweaks.module;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.platyutils.listener.EventListener;
import me.dave.platyutils.module.Module;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;

public class DynamicTrample extends Module implements EventListener {
    public static final String ID = "DYNAMIC_TRAMPLE";

    private Boolean featherFalling;
    private Boolean creativeMode;

    public DynamicTrample() {
        super(ID);
    }

    @Override
    public void onEnable() {
        GardeningTweaks plugin = GardeningTweaks.getInstance();
        plugin.saveDefaultResource("modules/dynamic-trample.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "modules/dynamic-trample.yml"));

        featherFalling = config.getBoolean("feather-falling", false);
        creativeMode = config.getBoolean("creative-mode", false);
    }

    @Override
    public void onDisable() {
        featherFalling = null;
        creativeMode = null;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.useInteractedBlock() == Event.Result.DENY || event.useItemInHand() == Event.Result.DENY) return;

        Action action = event.getAction();
        if (action != Action.PHYSICAL) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        Material material = block.getType();
        if (material != Material.FARMLAND) {
            return;
        }

        Player player = event.getPlayer();
        if (creativeMode && player.getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(true);
        }

        if (featherFalling) {
            ItemStack boots = player.getInventory().getBoots();
            if (boots != null) {
                ItemMeta bootsMeta = boots.getItemMeta();
                if (bootsMeta != null && bootsMeta.hasEnchant(Enchantment.PROTECTION_FALL)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
