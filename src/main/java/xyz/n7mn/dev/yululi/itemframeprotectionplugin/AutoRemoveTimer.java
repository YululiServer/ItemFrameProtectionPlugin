package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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

        if (plugin.isEnabled()){
            try {
                api.cacheToSQL();

                List<FrameData> FrameDataList = api.getListByFrameData(true);
                List<DropItemData> DropItemList = api.getListByDropItem();

                for (FrameData frameData : FrameDataList){

                    Entity entity = Bukkit.getEntity(frameData.getItemFrameUUID());
                    if (entity == null){
                        api.deleteTableByFrame(frameData.getItemFrameUUID());
                        continue;
                    }

                    if (entity.getType() != EntityType.ITEM_FRAME){
                        api.deleteTableByFrame(frameData.getItemFrameUUID());
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

                new AutoRemoveTimer(api, plugin).runTaskLaterAsynchronously(plugin, 240L);
            } catch (Exception e){

                if (plugin.getConfig().getBoolean("errorPrint")){
                    plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                    e.printStackTrace();
                }

            }
        }

    }
}
