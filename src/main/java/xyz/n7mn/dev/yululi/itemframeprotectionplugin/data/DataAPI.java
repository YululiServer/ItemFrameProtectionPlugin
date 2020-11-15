package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public class DataAPI {

    final DropItemList drop;
    final ItemFrameList item;
    final Plugin plugin;

    final ChestInDataList chest;

    public DataAPI(Connection con, Plugin plugin){

        drop = new DropItemList(con, plugin);
        item = new ItemFrameList(con, plugin);

        this.plugin = plugin;

        chest = new ChestInDataList(con, plugin);
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

    public FrameData getItemFrame(UUID itemFrameUUID){

        return item.getFrameData(itemFrameUUID);

    }

    public DropItemData getDropItemData(UUID dropItemUUID) {
        return drop.getDropItemData(dropItemUUID);
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

    public long getCountByItemFrame(){
        return item.getCount();
    }

    public long getCountByDropItem(){
        return drop.getCount();
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

        chest.forceCacheToSQL();

    }

    public String getCacheCount(){
        return "Item : " + item.getCacheCount() +" Drop : " + drop.getDropCacheCount();
    }


    public List<ChestInData> getChestInDataList(){

        return chest.getChestInDataList();

    }

    public ChestInData getChestInData(UUID chestUUID) {

        return chest.getChestInData(chestUUID);
    }


    public void addChestInData(UUID chestUUID, UUID useUserUUID){

        chest.setChestInData(chestUUID, useUserUUID);

    }

    public void addChestInData(ChestInData data){

        chest.setChestInData(data);

    }

    public void deleteChestInData(UUID chestUUID, UUID useUserUUID){

        chest.deleteChestInData(chestUUID, useUserUUID);

    }

    public void deleteChestInData(ChestInData data){

        chest.deleteChestInData(data);

    }

    public void deleteAllTableByChest(){

        chest.deleteAll();

    }

    public void cacheClearByChest(){

        chest.deleteCache();

    }

}
