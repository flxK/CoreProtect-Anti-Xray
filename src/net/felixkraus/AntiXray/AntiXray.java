package net.felixkraus.AntiXray;

import static java.util.Arrays.asList;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.bstats.MetricsLite;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.coreprotect.CoreProtectAPI.ParseResult;

public class AntiXray extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (getConfig().getBoolean("useMetrics")) {
            try {
                new MetricsLite(this);
            }
            catch (Exception e) {
            }
        }

        Plugin CP = getServer().getPluginManager().getPlugin("CoreProtect");
        if (CP == null) {
            System.out.println("[CoreProtect-Anti-Xray] Failed to hook CoreProtect!");
            getServer().getPluginManager().disablePlugin(this);
        }
        else {
            System.out.println("[CoreProtect-Anti-Xray] CoreProtect hooked!");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("xlu")) {
            if (args.length != 2) {
                return false;
            }
            CoreProtectAPI CPApi = getCoreProtect();
            if (!isInteger(args[1])) {
                sender.sendMessage("Please give a valid integer for the time span.");
                return false;
            }
            else {
                List<Object> ids = asList(Material.STONE, Material.GOLD_ORE, Material.IRON_ORE, Material.COAL_ORE, Material.LAPIS_ORE, Material.DIAMOND_ORE, Material.REDSTONE_ORE, Material.EMERALD_ORE);
                int Diamonds = 0, Iron = 0, Gold = 0, Redstone = 0, Coal = 0, Emerald = 0, Stone = 0, Lapis = 0;

                int GesamtAbgebaut = CPApi.performLookup(Integer.parseInt(args[1]) * 86400, Arrays.asList(args[0]), null, null, null, null, 0, null).size();
                List<String[]> lookup = CPApi.performLookup(Integer.parseInt(args[1]) * 86400, Arrays.asList(args[0]), null, ids, null, Arrays.asList(0), 0, null);
                for (String[] value : lookup) {
                    ParseResult result = CPApi.parseResult(value);
                    switch (result.getType()) {
                        case STONE:
                            Stone++;
                            break;
                        case GOLD_ORE:
                            Gold++;
                            break;
                        case IRON_ORE:
                            Iron++;
                            break;
                        case COAL_ORE:
                            Coal++;
                            break;
                        case LAPIS_ORE:
                            Lapis++;
                            break;
                        case DIAMOND_ORE:
                            Diamonds++;
                            break;
                        case REDSTONE_ORE:
                            Redstone++;
                            break;
                        case EMERALD_ORE:
                            Emerald++;
                            break;
                        default:
                            break;
                    }
                }

                int summe = Stone + Diamonds + Gold + Iron + Coal + Lapis + Emerald + Redstone;
                sender.sendMessage(ChatColor.DARK_PURPLE + "--------------------------------");
                sender.sendMessage(ChatColor.YELLOW + "          CP Anti-Xray");
                sender.sendMessage(ChatColor.YELLOW + "--------------------------------");
                sender.sendMessage(ChatColor.BLUE + "Blocks destroyed by " + args[0] + ":");
                sender.sendMessage(ChatColor.YELLOW + "--------------------------------");
                sender.sendMessage("Total: " + GesamtAbgebaut);
                sender.sendMessage("Total (Below listed materials): " + summe);
                sender.sendMessage(messageString(ChatColor.GREEN, "Emeralds", Emerald, summe));
                sender.sendMessage(messageString(ChatColor.AQUA, "Diamonds", Diamonds, summe));
                sender.sendMessage(messageString(ChatColor.GOLD, "Gold", Gold, summe));
                sender.sendMessage(messageString(ChatColor.DARK_GRAY, "Iron", Iron, summe));
                sender.sendMessage(messageString(ChatColor.RED, "Redstone", Redstone, summe));
                sender.sendMessage(messageString(ChatColor.DARK_BLUE, "Lapis Lazuli", Lapis, summe));
                sender.sendMessage(messageString(ChatColor.BLACK, "Coal", Coal, summe));
                sender.sendMessage(messageString(ChatColor.GRAY, "Stone", Stone, summe));
                sender.sendMessage(ChatColor.DARK_PURPLE + "--------------------------------");
                return true;
            }
        }

        return false;
    }

    private static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c <= '/' || c >= ':') {
                return false;
            }
        }
        return true;
    }

    private String messageString(ChatColor color, String Name, int Anzahl, int summe) {
        return color + Name + ": " + Anzahl + " " + "  |  " + new DecimalFormat("#0.00").format((double) Anzahl / (double) summe * 100) + "%";
    }

    private CoreProtectAPI getCoreProtect() {
        Plugin pluginCP = getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (pluginCP == null || !(pluginCP instanceof CoreProtect)) {
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) pluginCP).getAPI();
        if (!CoreProtect.isEnabled()) {
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 6) {
            return null;
        }

        return CoreProtect;
    }

}
