package me.dave.gardeningtweaks.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import me.dave.gardeningtweaks.GardeningMode;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

public class PlayerEvents implements Listener {
    private final GardeningTweaks plugin = GardeningTweaks.getInstance();
    private final ProtocolManager protocolManager;
    private final HashSet<UUID> cooldownList = new HashSet<>();
    private final Random random = new Random();

    public PlayerEvents(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (event.isSneaking() && GardeningTweaks.configManager.getGrowthDanceMode() != GardeningMode.DISABLED) {
            Player player = event.getPlayer();
            if (cooldownList.contains(player.getUniqueId())) return;
            cooldownList.add(player.getUniqueId());
            Bukkit.getScheduler().runTaskLater(plugin, () -> cooldownList.remove(player.getUniqueId()), 10);
            growCrops(player.getLocation());
        }
    }

    public void growCrops(Location location) {
        Location currLocation = location.clone().add(-2, 0, -2);

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < 5; k++) {
                    Block currBlock = currLocation.getBlock();

                    if (currBlock.getBlockData() instanceof Ageable crop) {
                        if (random.nextBoolean()) {
                            int newAge = crop.getAge() + random.nextInt(3);
                            int maxAge = crop.getMaximumAge();
                            if (newAge > maxAge) newAge = maxAge;
                            crop.setAge(newAge);
                            currBlock.setBlockData(crop);
                        }
                    }

                    currLocation.add(0, 0, 1);
                }
                currLocation.add(1, 0, -5);
            }
            currLocation.add(-5, 1, 0);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() == Material.DEAD_BUSH) {
            if (GardeningTweaks.configManager.getRejuvenatedBushes()) {
                Player player = event.getPlayer();
                ItemStack mainHand = player.getInventory().getItemInMainHand();
                switch(mainHand.getType()) {
                    case WHEAT_SEEDS -> bushToSapling(player, mainHand, block, Material.OAK_SAPLING);
                    case BEETROOT_SEEDS -> bushToSapling(player, mainHand, block, Material.ACACIA_SAPLING);
                    case MELON_SEEDS -> bushToSapling(player, mainHand, block, Material.SPRUCE_SAPLING);
                    case PUMPKIN_SEEDS -> bushToSapling(player, mainHand, block, Material.BIRCH_SAPLING);
                }
            }
        }
    }

    public void bushToSapling(Player player, ItemStack mainHand, Block block, Material saplingType) {
        if (player.getGameMode() != GameMode.CREATIVE) mainHand.setAmount(mainHand.getAmount() - 1);
        block.setType(saplingType);
        if (protocolManager != null) {
            PacketContainer armAnimation = new PacketContainer(PacketType.Play.Server.ANIMATION);
            armAnimation.getIntegers()
                .write(0, player.getEntityId())
                .write(1, 0);

            try {
                protocolManager.sendServerPacket(player, armAnimation);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        World world = block.getWorld();
        Location location = block.getLocation();
        world.spawnParticle(Particle.VILLAGER_HAPPY, location.clone().add(0.5, 0.5, 0.5), 50, 0.3, 0.3, 0.3);
        world.playSound(location, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 2f, 1f);
    }
}
