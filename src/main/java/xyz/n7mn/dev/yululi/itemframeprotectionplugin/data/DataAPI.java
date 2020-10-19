package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public class DataAPI {

    final DropItemList drop;
    final ItemFrameList item;
    final Plugin plugin;

    public DataAPI(Connection con, Plugin plugin){

        drop = new DropItemList(con, plugin);
        item = new ItemFrameList(con, plugin);
        this.plugin = plugin;

    }

    public List<FrameData> getListByFrameData(boolean activeOnly){


        List<FrameData> list = null;

        if (activeOnly){
            list = item.getFrameDataListByActive();
        } else {
            list = item.getFrameDataList();
        }

        return list;

    }

    public List<DropItemData> getListByDropItem(){

        return drop.getDropItemDataList();

    }

    public void createAllTable(){

        item.createTable();
        drop.createTable();

    }

    public void createTableByItem(){

        item.createTable();
    }

    public void createTableByDrop(){

        drop.createTable();
    }

    public void addItemFrame(FrameData data){

        item.addFrameData(data);

    }

    public void addDropItem(DropItemData data){

        drop.addDropItemDataList(data);

    }

    public void deleteAllTableByFrame(){

        item.deleteAll();

    }

    public void deleteAllTableByItem(){

        drop.deleteAll();

    }

    public void deleteTableByFrame(UUID FrameUUID){

        item.deleteFrameData(FrameUUID);

    }

    public void deleteTableByItem(UUID ItemUUID){

        drop.deleteDropItemDataList(ItemUUID);

    }

    public void cacheClearByFrame(){

        item.forceCacheToSQL();
    }

    public void cacheClearByItem(){

        drop.deleteCache();
    }

    public void cacheToSQL(){

        item.forceCacheToSQL();
        drop.forceCacheToSQL();

    }

}
