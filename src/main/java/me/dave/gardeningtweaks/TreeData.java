package me.dave.gardeningtweaks;

import me.dave.gardeningtweaks.utilities.RandomCollection;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class TreeData {
    private final List<Material> spreadBlocks = new ArrayList<>();
    private final List<Material> spreadBlocksOn = new ArrayList<>();
    private final RandomCollection<Material> flowerList = new RandomCollection<>();

    public TreeData(List<String> spreadBlocks, List<String> spreadBlocksOn, HashMap<String, Double> flowerList) {
        spreadBlocks.forEach(string -> this.spreadBlocks.add(Material.valueOf(string)));
        spreadBlocksOn.forEach(string -> this.spreadBlocksOn.add(Material.valueOf(string)));
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
