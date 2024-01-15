package me.dave.gardeningtweaks.commands;

import me.dave.chatcolorhandler.ChatColorHandler;
import me.dave.gardeningtweaks.GardeningTweaks;
import me.dave.platyutils.utils.Updater;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GardeningTweaksCmd implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1) {
            switch (args[0]) {
                case "reload" -> {
                    if (!sender.hasPermission("gardeningtweaks.admin.reload")) {
                        ChatColorHandler.sendMessage(sender, "&cYou have insufficient permissions.");
                        return true;
                    }
                    GardeningTweaks.getInstance().getConfigManager().reloadConfig();
                    ChatColorHandler.sendMessage(sender, "&#feb5ff✿ &#96D590GardeningTweaks has been reloaded.");
                    return true;
                }
                case "update" -> {
                    if (!sender.hasPermission("gardeningtweaks.update")) {
                        ChatColorHandler.sendMessage(sender, "&#ff6969Insufficient permissions");
                        return true;
                    }

                    Updater updater = GardeningTweaks.getInstance().getUpdater();

                    if (updater.isAlreadyDownloaded() || !updater.isUpdateAvailable()) {
                        ChatColorHandler.sendMessage(sender, "&#ff6969It looks like there is no new update available!");
                        return true;
                    }

                    updater.downloadUpdate().thenAccept(success -> {
                        if (success) {
                            ChatColorHandler.sendMessage(sender, "&#b7faa2Successfully updated ActivityRewarder, restart the server to apply changes!");
                        } else {
                            ChatColorHandler.sendMessage(sender, "&#ff6969Failed to update ActivityRewarder!");
                        }
                    });

                    return true;
                }
            }
        }
        PluginDescriptionFile pdf = GardeningTweaks.getInstance().getDescription();
        sender.sendMessage("You are currently running " + pdf.getName() + " Version: " + pdf.getVersion());
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        List<String> tabComplete = new ArrayList<>();
        List<String> wordCompletion = new ArrayList<>();
        boolean wordCompletionSuccess = false;

        if (args.length == 1) {
            if (sender.hasPermission("gardeningtweaks.admin.reload")) {
                tabComplete.add("reload");
            }
            if (sender.hasPermission("gardeningtweaks.update")) {
                tabComplete.add("update");
            }
        }

        for (String currTab : tabComplete) {
            int currArg = args.length - 1;
            if (currTab.startsWith(args[currArg])) {
                wordCompletion.add(currTab);
                wordCompletionSuccess = true;
            }
        }
        if (wordCompletionSuccess) return wordCompletion;
        return tabComplete;
    }
}
