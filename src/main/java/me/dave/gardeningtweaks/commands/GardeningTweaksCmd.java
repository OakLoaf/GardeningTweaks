package me.dave.gardeningtweaks.commands;

import me.dave.chatcolorhandler.ChatColorHandler;
import me.dave.gardeningtweaks.GardeningTweaks;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.ArrayList;
import java.util.List;

public class GardeningTweaksCmd implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("gardeningtweaks.admin.reload")) {
                    sender.sendMessage(ChatColorHandler.translateAlternateColorCodes("&cYou have insufficient permissions."));
                    return true;
                }
                GardeningTweaks.getConfigManager().reloadConfig();
                sender.sendMessage(ChatColorHandler.translateAlternateColorCodes("&#feb5ff✿ &#96D590GardeningTweaks has been reloaded."));
                return true;
            }
        }
        PluginDescriptionFile pdf = GardeningTweaks.getInstance().getDescription();
        sender.sendMessage("You are currently running " + pdf.getName() + " Version: " + pdf.getVersion());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {

        List<String> tabComplete = new ArrayList<>();
        List<String> wordCompletion = new ArrayList<>();
        boolean wordCompletionSuccess = false;

        if (args.length == 1) {
            if (commandSender.hasPermission("gardeningtweaks.admin.reload")) tabComplete.add("reload");
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
