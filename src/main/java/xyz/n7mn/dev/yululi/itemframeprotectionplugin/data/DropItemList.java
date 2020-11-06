package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

class DropItemList implements DataInteface {

    private List<DropItemData> dropItemDataList;
    private final Connection con;
    private final Plugin plugin;

    DropItemList(Connection con, Plugin plugin){
        this.con = con;
        this.plugin = plugin;
        this.dropItemDataList = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void createTable() {
        new BukkitRunnable() {
            @Override
            public void run() {

                try {

                    PreparedStatement statement = con.prepareStatement("CREATE TABLE `ItemFrameTable2` ( `DropItemUUID` VARCHAR(36) NOT NULL , `WorldUUID` VARCHAR(36) NOT NULL , `DropUser` VARCHAR(36) NULL , `DropDate` DATETIME NOT NULL , PRIMARY KEY (`DropItemUUID`))");
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

                    PreparedStatement statement = con.prepareStatement("TRUNCATE ItemFrameTable2;");
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
    public void deleteCache() {
        this.dropItemDataList.clear();
    }

    @Override
    public void deleteAll() {
        deleteCache();
        deleteSQL();
    }

    @Override
    public int getCacheCount() {
        return dropItemDataList.size();
    }

    @Override
    public void forceCacheToSQL() {

        if (plugin.isEnabled()){

            BukkitRunnable bukkitRunnable = new BukkitRunnable() {
                @Override
                public void run() {

                    List<DropItemData> temp = new ArrayList<>();

                    synchronized (dropItemDataList) {
                        if (dropItemDataList.size() == 0) {
                            return;
                        }

                        temp.addAll(dropItemDataList);
                        dropItemDataList.clear();
                    }

                    try {

                        for (DropItemData data : temp){

                            PreparedStatement statement = con.prepareStatement("INSERT INTO `ItemFrameTable2` ( `DropItemUUID` , `WorldUUID`, `DropUser` , `DropDate`) VALUES (?,?,?,?)");
                            statement.setString(1, data.getDropItemUUID().toString());
                            statement.setString(2, data.getWorldUUID().toString());
                            statement.setString(3, data.getDropUser().toString());
                            statement.setTimestamp(4, new java.sql.Timestamp(data.getDropDate().getTime()));
                            statement.execute();
                            statement.close();

                        }


                    } catch (Exception e){
                        if (plugin.getConfig().getBoolean("errorPrint")){
                            plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                            e.printStackTrace();
                        }
                    }

                    temp.clear();
                    this.cancel();
                }
            };
            bukkitRunnable.runTaskLaterAsynchronously(plugin, 0L);

        } else{

            new Thread(()->{

                List<DropItemData> temp = new ArrayList<>();

                synchronized (dropItemDataList) {
                    if (dropItemDataList.size() == 0) {
                        return;
                    }

                    temp.addAll(dropItemDataList);
                    dropItemDataList.clear();
                }

                try {

                    for (DropItemData data : temp){

                        PreparedStatement statement = con.prepareStatement("INSERT INTO `ItemFrameTable2` ( `DropItemUUID` , `WorldUUID`, `DropUser` , `DropDate`) VALUES (?,?,?,?)");
                        statement.setString(1, data.getDropItemUUID().toString());
                        statement.setString(2, data.getWorldUUID().toString());
                        statement.setString(3, data.getDropUser().toString());
                        statement.setTimestamp(4, new java.sql.Timestamp(data.getDropDate().getTime()));
                        statement.execute();
                        statement.close();

                    }


                } catch (Exception e){
                    if (plugin.getConfig().getBoolean("errorPrint")){
                        plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                        e.printStackTrace();
                    }
                }

                temp.clear();

            }).start();

        }



    }


    public List<DropItemData> getDropItemDataList() {
        List<DropItemData> dataList = new ArrayList<>();

        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM ItemFrameTable2");
            ResultSet set = statement.executeQuery();

            while (set.next()){
                DropItemData data = new DropItemData();
                data.setDropItemUUID(UUID.fromString(set.getString("DropItemUUID")));
                data.setWorldUUID(UUID.fromString(set.getString("WorldUUID")));
                data.setDropUser(UUID.fromString(set.getString("DropUser")));
                data.setDropDate(new Date(set.getTimestamp("DropDate").getTime()));

                dataList.add(data);
            }

            synchronized (dropItemDataList){
                dataList.addAll(dropItemDataList);
            }

            return dataList;

        } catch (Exception e){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }

            return null;
        }
    }

    public DropItemData getDropItemData(UUID dropItemUUID){
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM ItemFrameTable2 WHERE DropItemUUID = ?");
            statement.setString(1, dropItemUUID.toString());
            ResultSet set = statement.executeQuery();
            if (set.next()){

                DropItemData data = new DropItemData();
                data.setDropItemUUID(UUID.fromString(set.getString("DropItemUUID")));
                data.setWorldUUID(UUID.fromString(set.getString("WorldUUID")));
                data.setDropUser(UUID.fromString(set.getString("DropUser")));
                data.setDropDate(new Date(set.getTimestamp("DropDate").getTime()));

                set.close();
                statement.close();
                return data;
            }

            synchronized (dropItemDataList){

                for (DropItemData data : dropItemDataList){
                    if (data.getDropItemUUID().equals(dropItemUUID)){
                        return data;
                    }
                }

            }
        } catch (Exception e){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }
        }

        return null;
    }


    public void addDropItemDataList(DropItemData data){

        try {
            synchronized (dropItemDataList){

                if (dropItemDataList.size() > 20000){
                    forceCacheToSQL();
                }

                dropItemDataList.add(data);
            }
        } catch (Exception e){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }
        }

    }

    public void deleteDropItemDataList(UUID dropItemUUID){

        boolean flag = false;

        try {
            PreparedStatement statement = con.prepareStatement("SELECT COUNT(*) FROM ItemFrameTable2 WHERE DropItemUUID = ?;");
            statement.setString(1, dropItemUUID.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()){

                if (resultSet.getInt("COUNT(*)") != 0){
                    statement.close();
                    PreparedStatement statement2 = con.prepareStatement("DELETE FROM `ItemFrameTable2` WHERE DropItemUUID = ?");
                    statement2.setString(1, dropItemUUID.toString());
                    statement2.execute();
                    statement2.close();

                } else {
                    statement.close();
                }

            } else {
                statement.close();
            }

        } catch (SQLException e){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }
        }

    }

    public int getDropCacheCount(){
        synchronized (dropItemDataList){
            return dropItemDataList.size();
        }
    }


    public long getCount(){

        long count = 0;

        synchronized (dropItemDataList){
            count = count + dropItemDataList.size();
        }

        try {
            PreparedStatement statement = con.prepareStatement("SELECT COUNT(*) FROM ItemFrameTable2;");
            ResultSet set = statement.executeQuery();
            if (set.next()){

                count = count + set.getLong("COUNT(*)");

                set.close();
                statement.close();
                return count;
            }

        } catch (SQLException e){
            return count;
        }

        return count;
    }

}
