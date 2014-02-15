package net.felixkraus.AntiXray;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.coreprotect.CoreProtectAPI.*;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by felix on 14.02.14.
 */
public class AntiXray extends JavaPlugin {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){

        if(label.equalsIgnoreCase("xlu")){
            if(args.length!=2) return false;
            CoreProtectAPI CPApi = getCoreProtect();
            if(!isInteger(args[1])){
                sender.sendMessage("Please give a valid integer for the time span.");
                return false;
            }
            else {
                List<Integer> ids = new LinkedList<Integer>();
                ids.add(56);
                ids.add(14);
                ids.add(15);
                ids.add(16);
                ids.add(129);
                ids.add(73);
                ids.add(74);
                ids.add(21);
                ids.add(1);
                List<String[]> lookup = CPApi.performLookup(args[0], Integer.parseInt(args[1])*86400, 0, null, ids, null);
                int Diamonds = 0;
                int Iron = 0;
                int Gold = 0;
                int Redstone = 0;
                int Coal = 0;
                int Emerald = 0;
                int Stone = 0;
                int Lapis = 0;
                for (String[] value : lookup){
                    ParseResult result = CPApi.parseResult(value);
                    if(result.getActionId()==0)
                    switch(result.getTypeId()){
                        case 1 : Stone++; break;
                        case 56 : Diamonds++; break;
                        case 14 : Gold++; break;
                        case 15 : Iron ++; break;
                        case 16 : Coal++; break;
                        case 21 : Lapis++; break;
                        case 129 : Emerald++; break;
                        case 73 : Redstone++; break;
                        case 74 : Redstone++; break;
                    }

                }
                int summe = Stone+Diamonds+Gold+Iron+Coal+Lapis+Emerald+Redstone;
                sender.sendMessage(ChatColor.YELLOW+"----------CP Anti-Xray----------");
                sender.sendMessage("Total: "+summe);
                sender.sendMessage(ChatColor.GREEN+"Emeralds: "+Emerald+ percentage(Emerald, summe));
                sender.sendMessage(ChatColor.AQUA+"Diamonds: "+Diamonds+ percentage(Diamonds, summe));
                sender.sendMessage(ChatColor.YELLOW+"Gold: "+Gold+ percentage(Gold, summe));
                sender.sendMessage(ChatColor.DARK_GRAY+"Iron: "+Iron+ percentage(Iron, summe));
                sender.sendMessage(ChatColor.RED+"Redstone: "+Redstone+ percentage(Redstone, summe));
                sender.sendMessage(ChatColor.DARK_BLUE+"Lapis Lazuli: "+Lapis+ percentage(Lapis, summe));
                sender.sendMessage(ChatColor.BLACK+"Coal: "+Coal+ percentage(Coal, summe));
                sender.sendMessage("Stone: "+Stone+ percentage(Stone, summe));
                return true;
            }
        }


        return false;
    }



    public static boolean isInteger(String str) {
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


    public String percentage(int Anzahl, int Summe){
        double Anz = (double) Anzahl;
        double Sum = (double) Summe;
        double zw = Anz/Sum;
        double perc = zw*100;
        DecimalFormat f = new DecimalFormat("#0.00");
        return "  |  "+f.format(perc)+"%";
    }


  private CoreProtectAPI getCoreProtect() {
        Plugin pluginCP = getServer().getPluginManager().getPlugin("CoreProtect");

// Check that CoreProtect is loaded
        if (pluginCP == null || !(pluginCP instanceof CoreProtect)) {
            return null;
        }

// Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect)pluginCP).getAPI();
        if (CoreProtect.isEnabled()==false){
            return null;
        }

// Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 2){
            return null;
        }

        return CoreProtect;
    }

}
