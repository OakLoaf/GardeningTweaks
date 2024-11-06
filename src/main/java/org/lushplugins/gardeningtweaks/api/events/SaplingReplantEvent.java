package org.lushplugins.gardeningtweaks.api.events;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SaplingReplantEvent extends BlockEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private final Player player;
    private final Item item;
    private final Material newType;

    public SaplingReplantEvent(@NotNull Block block, Player player, @NotNull Item item, @NotNull Material newType) {
        super(block);
        this.player = player;
        this.item = item;
        this.newType = newType;
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public Item getItem() {
        return item;
    }

    @NotNull
    public Material getNewType() {
        return newType;
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