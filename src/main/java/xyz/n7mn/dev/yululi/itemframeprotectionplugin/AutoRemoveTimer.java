package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.*;

import javax.swing.*;
import java.util.List;

class AutoRemoveTimer extends BukkitRunnable {

    private final DataAPI api;
    private final Plugin plugin;

    public AutoRemoveTimer(DataAPI api, Plugin plugin){
        this.api = api;
        this.plugin = plugin;
    }

    @Override
    public void run() {

        if (plugin.isEnabled()){


            try {

                api.cacheToSQL();

                List<FrameData> FrameDataList = api.getListByFrameData(true);
                List<DropItemData> DropItemList = api.getListByDropItem();

                for (FrameData frameData : FrameDataList){

                    Entity entity = Bukkit.getEntity(frameData.getItemFrameUUID());
                    if (entity == null){
                        api.deleteTableByFrame(frameData.getItemFrameUUID());

                        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().getPluginManager().callEvent(new ItemFrameProtectDeleteEvent(frameData.getItemFrameUUID())));
                        continue;
                    }

                    if (entity.getType() != EntityType.ITEM_FRAME){
                        api.deleteTableByFrame(frameData.getItemFrameUUID());
                        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().getPluginManager().callEvent(new ItemFrameProtectDeleteEvent(frameData.getItemFrameUUID())));
                    }

                }

                for (DropItemData dropItem : DropItemList){

                    Entity entity = Bukkit.getEntity(dropItem.getDropItemUUID());
                    if (entity == null){
                        api.deleteTableByItem(dropItem.getDropItemUUID());
                        continue;
                    }


                    if (entity.getType() != EntityType.DROPPED_ITEM){
                        api.deleteTableByItem(dropItem.getDropItemUUID());
                    }

                }


                FrameDataList.clear();
                DropItemList.clear();

                if (plugin.isEnabled()){
                    new AutoRemoveTimer(api, plugin).runTaskLaterAsynchronously(plugin, 1200L);
                    // System.out.println("test");
                }

            } catch (Exception e){

                if (plugin.getConfig().getBoolean("errorPrint")){
                    plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                    e.printStackTrace();
                }

            }
        }

    }
}
