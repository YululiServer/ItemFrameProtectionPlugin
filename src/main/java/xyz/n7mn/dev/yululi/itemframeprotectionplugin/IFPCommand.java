package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.DataAPI;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.FrameData;

import java.util.List;

class IFPCommand implements CommandExecutor {

    final DataAPI api;
    final Plugin plugin = Bukkit.getPluginManager().getPlugin("ItemframeProtectionPlugin");

    public IFPCommand(DataAPI api){
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player){



        } else {

            sender.sendMessage("---- ItemFrameProtectPlugin Ver " + plugin.getDescription().getVersion() + " ----");
            List<FrameData> list = api.getListByFrameData(false);

            for (FrameData data : list){

                Entity entity = Bukkit.getEntity(data.getItemFrameUUID());
                if (entity == null){
                    continue;
                }

                if (data.isActive()){
                    sender.sendMessage(ChatColor.GREEN + "■ "+ChatColor.RESET+"UUID: " + data.getItemFrameUUID() + " WorldName: " + entity.getLocation().getWorld().getName() + " X: " + entity.getLocation().getBlockX() + " Y: " + entity.getLocation().getBlockY() + " Z: " + entity.getLocation().getBlockZ() + " UserUUID: " + data.getCreateDate());
                } else {
                    sender.sendMessage(ChatColor.RED + "■ "+ChatColor.RESET+"UUID: " + data.getItemFrameUUID() + " WorldName: " + entity.getLocation().getWorld().getName() + " X: " + entity.getLocation().getBlockX() + " Y: " + entity.getLocation().getBlockY() + " Z: " + entity.getLocation().getBlockZ() + " UserUUID: " + data.getCreateDate());
                }

            }
        }

        return true;
    }
}
