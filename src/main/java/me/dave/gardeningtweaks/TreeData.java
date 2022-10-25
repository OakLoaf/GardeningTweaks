package me.dave.gardeningtweaks;

import me.dave.gardeningtweaks.utilities.RandomCollection;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TreeData {
    private final String treeTypeName;
    private final List<Material> spreadBlocks = new ArrayList<>();
    private final List<Material> spreadBlocksOn = new ArrayList<>();
    private final RandomCollection<Material> flowerList = new RandomCollection<>();

    public TreeData(String treeTypeName, List<String> spreadBlocks, List<String> spreadBlocksOn, HashMap<String, Double> flowerList) {
        this.treeTypeName = treeTypeName;
        spreadBlocks.forEach(string -> this.spreadBlocks.add(Material.valueOf(string)));
        spreadBlocksOn.forEach(string -> this.spreadBlocksOn.add(Material.valueOf(string)));
        flowerList.forEach((string, weight) -> this.flowerList.add(Material.valueOf(string), weight));
    }

    public String getTreeTypeName() {
        return treeTypeName;
    }

    public List<Material> getSpreadBlocks() {
        return spreadBlocks;
    }

    public List<Material> getSpreadBlocksOn() {
        return spreadBlocksOn;
    }

    public RandomCollection<Material> getFlowerCollection() {
        return flowerList;
    }
}
