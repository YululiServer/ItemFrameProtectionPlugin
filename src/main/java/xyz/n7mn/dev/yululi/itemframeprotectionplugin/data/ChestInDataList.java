package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

class ChestInDataList implements DataInteface {

    private List<ChestInData> chestInDataList;
    private final Connection con;
    private final Plugin plugin;

    ChestInDataList(Connection con, Plugin plugin){
        this.con = con;
        this.plugin = plugin;
        this.chestInDataList = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void createTable() {

        try {

            PreparedStatement statement = con.prepareStatement("CREATE TABLE `ItemFrameTable3` ( `ChestUUID` VARCHAR(36) NOT NULL , `UseUserUUID` VARCHAR(36) NULL)");
            statement.execute();

            statement.close();

        } catch (Exception e){

            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }

        }

    }

    @Override
    public void deleteSQL() {

        try {

            PreparedStatement statement = con.prepareStatement("TRUNCATE ItemFrameTable3;");
            statement.execute();

            statement.close();

        } catch (Exception e){

            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }

        }

    }

    @Override
    public void deleteCache() {

        try {
            synchronized (chestInDataList){

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        chestInDataList.clear();
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
    public void deleteAll() {

        deleteSQL();
        deleteCache();

    }

    @Override
    public int getCacheCount() {

        synchronized (chestInDataList){

            return chestInDataList.size();

        }

    }

    @Override
    public void forceCacheToSQL() {

        try {

            List<ChestInData> chest = new ArrayList<>();

            synchronized (chestInDataList){

                chest.addAll(chestInDataList);

            }

            for (ChestInData data : chest){

                PreparedStatement statement = con.prepareStatement("INSERT INTO `ItemFrameTable3` (`ChestUUID`, `UseUserUUID`) VALUES (?, ?)");
                statement.setString(1, data.getChestUUID().toString());
                statement.setString(2, data.getUseUserUUID().toString());

                statement.execute();
                statement.close();

            }

            chest.clear();

        } catch (Exception e){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }
        }

    }


    public List<ChestInData> getChestInDataList(){

        List<ChestInData> chestList = new ArrayList<>();

        try {

            synchronized (chestInDataList){

                chestList.addAll(chestInDataList);

            }

            PreparedStatement statement = con.prepareStatement("SELECT * FROM ItemFrameTable3");
            ResultSet set = statement.executeQuery();

            while (set.next()){

                chestList.add(new ChestInData(UUID.fromString(set.getString("ChestUUID")), UUID.fromString(set.getString("UseUserUUID"))));

            }

        } catch (Exception e){

            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }

        }

        return chestList;

    }

    public ChestInData getChestInData(UUID chestUUID){

        try {

            PreparedStatement statement = con.prepareStatement("SELECT * FROM ItemFrameTable3");
            ResultSet set = statement.executeQuery();

            if (set.next()){

                ChestInData chest = new ChestInData(UUID.fromString(set.getString("ChestUUID")), UUID.fromString(set.getString("UseUserUUID")));
                statement.close();

                return chest;

            }
            statement.close();

            List<ChestInData> chestList = new ArrayList<>();
            synchronized (chestInDataList){

                chestList.addAll(chestInDataList);

            }

            for (ChestInData chestData : chestList){

                if (chestData.getChestUUID().equals(chestUUID)){
                    return chestData;
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

    public void setChestInData(UUID chestUUID, UUID useUserUUID){

        setChestInData(new ChestInData(chestUUID, useUserUUID));

    }

    public void setChestInData(ChestInData data){

        int count = 0;

        synchronized (chestInDataList){

            chestInDataList.add(data);

            count = chestInDataList.size();
        }

        if (count <= 15){
            return;
        }

        forceCacheToSQL();
    }

    public void deleteChestInData(UUID chestUUID, UUID useUserUUID){

        deleteChestInData(new ChestInData(chestUUID, useUserUUID));

    }

    public void deleteChestInData(ChestInData data){

        try {

            PreparedStatement statement = con.prepareStatement("DELETE FROM `ItemFrameTable3` WHERE ChestUUID = ? AND UseUserUUID = ?");
            statement.setString(1, data.getChestUUID().toString());
            statement.setString(2, data.getUseUserUUID().toString());

            statement.execute();
            statement.close();

        } catch (Exception e){

            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }

        }

    }
}
