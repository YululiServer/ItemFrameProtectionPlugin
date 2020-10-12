package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class ItemFrameCommandTab implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (sender instanceof Player){
            Player player = (Player)sender;
            if (args.length == 0){

                list.add("list");
                if (player.hasPermission("ifp.op")){
                    list.add("count");
                    list.add("user");
                }

                return list;
            }

            if (args.length == 1 && player.hasPermission("ifp.op")){

                for (Player p : sender.getServer().getOnlinePlayers()){
                    list.add(p.getName());
                }

                return list;
            }
        }

        return list;
    }
}
