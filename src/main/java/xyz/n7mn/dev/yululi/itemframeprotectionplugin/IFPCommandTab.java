package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class IFPCommandTab implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return args.length <= 2;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1){

            list.add("all");
            Player player = (Player) sender;

            if (player.hasPermission("ifp.op")){
                list.add("admin");
                list.add("user");
            }

        }

        if (args.length == 2 && args[0].toLowerCase().equals("user")){

            Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();

            for (Player player : players){

                list.add(player.getName());

            }

        }

        return list;
    }
}
