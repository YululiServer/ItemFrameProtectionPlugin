package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import com.google.gson.Gson;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

class ItemFrameList implements DataInteface {

    private List<FrameData> frameDataList;
    private final Connection con;
    private final Plugin plugin;

    ItemFrameList(Connection con, Plugin plugin){
        this.con = con;
        this.plugin = plugin;
        this.frameDataList = Collections.synchronizedList(new ArrayList<>());
    }

    public static ItemFrameList newInstance(Connection con, Plugin plugin){
        return new ItemFrameList(con, plugin);
    }


    @Override
    public void createTable(){
        // CREATE TABLE `tina-server`.`ItemFrameTable1` ( `ItemFrameUUID` VARCHAR(36) NOT NULL , `FrameItem` JSON NOT NULL , `ProtectUser` VARCHAR(36) NULL , `CreateDate` DATETIME NOT NULL , `Active` BOOLEAN NOT NULL , PRIMARY KEY (`ItemFrameUUID`))
        new BukkitRunnable() {
            @Override
            public void run() {

                try {

                    PreparedStatement statement = con.prepareStatement("CREATE TABLE `ItemFrameTable1` ( `ItemFrameUUID` VARCHAR(36) NOT NULL , `FrameItem` JSON NOT NULL , `ProtectUser` VARCHAR(36) NULL , `CreateDate` DATETIME NOT NULL , `Active` BOOLEAN NOT NULL , PRIMARY KEY (`ItemFrameUUID`))");
                    statement.execute();
                    statement.close();

                } catch (SQLException e){
                    if (plugin.getConfig().getBoolean("errorPrint")){
                        plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                        e.printStackTrace();
                    }
                }

            }
        }.runTaskLaterAsynchronously(plugin, 0L);
    }

    @Override
    public void deleteSQL() {

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement statement = con.prepareStatement("TRUNCATE ItemFrameTable1;");
                    statement.execute();
                    statement.close();
                } catch (Exception e){
                    if (plugin.getConfig().getBoolean("errorPrint")){
                        plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskLaterAsynchronously(plugin, 0L);

    }

    @Override
    public void deleteCache() {

        try {
            synchronized (frameDataList){

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        frameDataList.clear();
                    }
                }.runTaskLaterAsynchronously(plugin, 0L);

            }
        } catch (Exception e){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }
        }

    }

    @Override
    public void deleteAll(){

        this.deleteSQL();
        this.deleteCache();

    }


    @Override
    public int getCacheCount() {
        synchronized (frameDataList){
            return frameDataList.size();
        }
    }

    @Override
    public void forceCacheToSQL(){

        try {
            synchronized (frameDataList){

                for (FrameData data : frameDataList){

                    PreparedStatement statement = con.prepareStatement("INSERT INTO `ItemFrameTable1` ( `ItemFrameUUID` , `FrameItem`, `ProtectUser` , `CreateDate` , `Active` ) VALUES (?,?,?,?,?)");
                    statement.setString(1, data.getItemFrameUUID().toString());
                    statement.setString(2, new Gson().toJson(data.getFrameItem()));
                    statement.setString(3, data.getProtectUser().toString());
                    statement.setTimestamp(4, new java.sql.Timestamp(data.getCreateDate().getTime()));
                    statement.setBoolean(5, data.isActive());
                    statement.execute();
                    statement.close();

                }

                frameDataList.clear();
            }
        } catch (Exception e){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }
        }

    }

    @Deprecated
    public List<FrameData> getFrameDataList(){
        List<FrameData> dataList = new ArrayList<>();

        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM ItemFrameTable1");
            ResultSet set = statement.executeQuery();
            while (set.next()){
                FrameData data = new FrameData(UUID.fromString(set.getString("ItemFrameUUID")), new Gson().fromJson(set.getString("FrameItem"), ItemStack.class), UUID.fromString(set.getString("ProtectUser")), new Date(set.getTimestamp("CreateDate").getTime()), set.getBoolean("Active"));
                dataList.add(data);
            }
        } catch (SQLException e){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }
        }

        synchronized (frameDataList){
            if (frameDataList.size() != 0){
                dataList.addAll(frameDataList);
            }
        }

        return dataList;
    }

    public List<FrameData> getFrameDataListByActive() {

        List<FrameData> dataList = new ArrayList<>();

        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM ItemFrameTable1 WHERE Active = 1");
            ResultSet set = statement.executeQuery();
            while (set.next()){
                FrameData data = new FrameData(UUID.fromString(set.getString("ItemFrameUUID")), new Gson().fromJson(set.getString("FrameItem"), ItemStack.class), UUID.fromString(set.getString("ProtectUser")), new Date(set.getTimestamp("CreateDate").getTime()), set.getBoolean("Active"));
                dataList.add(data);
            }
        } catch (SQLException e){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }
        }

        synchronized (frameDataList){
            if (frameDataList.size() != 0){
                dataList.addAll(frameDataList);
            }
        }

        return dataList;
    }

    public void addFrameData(FrameData data){
        try {
            synchronized (frameDataList){

                frameDataList.add(data);
                if (frameDataList.size() > 15){
                    this.forceCacheToSQL();
                }


            }
        } catch (Exception e){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }
        }
    }

    public void deleteFrameData(UUID itemFrameUUID){

        try {
            boolean flag = false;
            synchronized (frameDataList){
                int i = 0;
                for (FrameData data : frameDataList){
                    if (data.getItemFrameUUID().equals(itemFrameUUID)){
                        frameDataList.remove(i);
                        flag = true;
                        break;
                    }
                    i++;
                }

                if (!flag){
                    PreparedStatement statement = con.prepareStatement("UPDATE WhereList SET Active = 0 WHERE ItemFrameUUID = ?");
                    statement.setString(1, itemFrameUUID.toString());
                    statement.execute();
                    statement.close();
                }
            }

        } catch (Exception e){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }
        }

    }
}
