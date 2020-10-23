package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.DataAPI;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.DropItemData;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.FrameData;

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

        api.cacheToSQL();

        List<FrameData> FrameDataList = api.getListByFrameData(true);
        List<DropItemData> DropItemList = api.getListByDropItem();

        for (FrameData frameData : FrameDataList){

            Entity entity = Bukkit.getEntity(frameData.getItemFrameUUID());
            if (entity == null){
                api.deleteTableByFrame(frameData.getItemFrameUUID());
            }

        }

        for (DropItemData dropItem : DropItemList){

            Entity entity = Bukkit.getEntity(dropItem.getDropItemUUID());
            if (entity == null){
                api.deleteTableByFrame(dropItem.getDropItemUUID());
            }

        }

        FrameDataList.clear();
        DropItemList.clear();

        new AutoRemoveTimer(api, plugin).runTaskLaterAsynchronously(plugin, 240L);
    }
}
