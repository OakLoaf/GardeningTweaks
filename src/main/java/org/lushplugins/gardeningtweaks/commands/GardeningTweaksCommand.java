package org.lushplugins.gardeningtweaks.commands;

import org.bukkit.plugin.PluginDescriptionFile;
import org.lushplugins.gardeningtweaks.GardeningTweaks;
import org.lushplugins.pluginupdater.api.updater.Updater;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
@Command("gardeningtweaks")
public class GardeningTweaksCommand {

    @Command("gardeningtweaks")
    public String info() {
        PluginDescriptionFile pluginDescription = GardeningTweaks.getInstance().getDescription();
        return "You are currently running %s version %s"
            .formatted(pluginDescription.getName(), pluginDescription.getVersion());
    }

    @Subcommand("reload")
    @CommandPermission("gardeningtweaks.reload")
    public String reload() {
        GardeningTweaks.getInstance().getConfigManager().reloadConfig();
        return "&#feb5ff✿ &#96D590GardeningTweaks has been reloaded.";
    }

    @Subcommand("update")
    @CommandPermission("gardeningtweaks.update")
    public CompletableFuture<String> update() {
        Updater updater = GardeningTweaks.getInstance().getUpdater();
        if (updater == null || !GardeningTweaks.getInstance().getConfigManager().shouldCheckUpdates()) {
            return CompletableFuture.completedFuture("&#ff6969It looks like the updater is disabled!");
        }

        if (updater.isAlreadyDownloaded() || !updater.isUpdateAvailable()) {
            return CompletableFuture.completedFuture("&#ff6969It looks like there is no new update available!");
        }

        return updater.attemptDownload().thenApply(success -> {
            if (success) {
                return "&#b7faa2Successfully updated plugin, restart the server to apply changes!";
            } else {
                return "&#ff6969Failed to update plugin, check console for errors.";
            }
        });
    }
}
