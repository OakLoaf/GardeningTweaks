package me.dave.gardeningtweaks.api.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

public class TreeSpreadBlockEvent extends BlockEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private final Material newType;
    private final Block sapling;

    public TreeSpreadBlockEvent(@NotNull Block block, @NotNull Material newType, @NotNull Block sapling) {
        super(block);
        this.newType = newType;
        this.sapling = sapling;
    }

    @NotNull
    public Material getNewType() {
        return newType;
    }

    @NotNull
    public Block getSapling() {
        return sapling;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}