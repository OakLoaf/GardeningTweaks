package me.dave.gardeningtweaks.data;

import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.gardeningtweaks.utils.RandomCollection;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class TreeData {
    private final List<Material> spreadBlocks = new ArrayList<>();
    private final List<Material> spreadBlocksOn = new ArrayList<>();
    private final RandomCollection<Material> flowerList = new RandomCollection<>();

    public TreeData(List<String> spreadBlocks, List<String> spreadBlocksOn, HashMap<String, Double> flowerList) {
        spreadBlocks.forEach(string -> {
            Material material;
            try {
                material = Material.valueOf(string);
            } catch (IllegalArgumentException err) {
                GardeningTweaks.getInstance().getLogger().warning("Ignoring " + string + ", that is not a valid material.");
                return;
            }
            this.spreadBlocks.add(material);
        });
        spreadBlocksOn.forEach(string -> {
            Material material;
            try {
                material = Material.valueOf(string);
            } catch (IllegalArgumentException err) {
                GardeningTweaks.getInstance().getLogger().warning("Ignoring " + string + ", that is not a valid material.");
                return;
            }
            this.spreadBlocksOn.add(material);
        });
        flowerList.forEach((string, weight) -> {
            Material material;
            try {
                material = Material.valueOf(string);
            } catch (IllegalArgumentException err) {
                GardeningTweaks.getInstance().getLogger().warning("Ignoring " + string + ", that is not a valid material.");
                return;
            }
            this.flowerList.add(material, weight);
        });
        flowerList.forEach((string, weight) -> this.flowerList.add(Material.valueOf(string), weight));
    }

    public boolean spreadsBlocks() {
        return spreadBlocks.size() > 0;
    }

    public List<Material> getSpreadMaterials() {
        return spreadBlocks;
    }

    public boolean isSpreadableMaterial(Block block) {
        if (spreadBlocksOn.size() == 0) {
            Collection<BoundingBox> boundingBoxes = block.getCollisionShape().getBoundingBoxes();
            if (boundingBoxes.size() == 1) {
                BoundingBox boundingBox = boundingBoxes.iterator().next();
                return boundingBox.getWidthX() == 1.0 && boundingBox.getWidthZ() == 1.0 && boundingBox.getHeight() == 1.0;
            }
        }
        return spreadBlocksOn.contains(block.getType());
    }

    public RandomCollection<Material> getFlowerCollection() {
        return flowerList;
    }
}
