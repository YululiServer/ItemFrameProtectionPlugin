package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

class BoxDataList implements DataInteface {

    private final Connection con;
    private final Plugin plugin;
    private List<BoxData> BoxDataList;


    BoxDataList(Connection con, Plugin plugin){
        this.con = con;
        this.plugin = plugin;
        this.BoxDataList = Collections.synchronizedList(new ArrayList<>());
    }


    @Override
    public void createTable() {

        try {
            PreparedStatement statement = con.prepareStatement("CREATE TABLE `ItemFrameTable3` ( `BoxDataUUID` VARCHAR(36) NOT NULL, `BlockWorldUUID` VARCHAR(36) NOT NULL, `BlockX` INTEGER NOT NULL, `BlockY` INTEGER NOT NULL, `BlockZ` INTEGER NOT NULL  , PRIMARY KEY (`BoxDataUUID`))");
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

        synchronized (BoxDataList){

            BoxDataList.clear();

        }

    }

    @Override
    public void deleteAll() {

        deleteSQL();
        deleteCache();

    }

    @Override
    public int getCacheCount() {

        synchronized (BoxDataList){

            return BoxDataList.size();

        }

    }

    @Override
    public void forceCacheToSQL() {

        List<BoxData> boxList = new ArrayList<>();

        synchronized (BoxDataList){

            boxList.addAll(BoxDataList);
            BoxDataList.clear();
        }

        for (BoxData boxData : boxList){

            try {
                PreparedStatement statement = con.prepareStatement("INSERT INTO `ItemFrameTable3` (`BoxDataUUID`, `BlockWorldUUID`, `BlockX`, `BlockY`, `BlockZ`) VALUES (?, ?, ?, ?, ?) ");

                statement.setString(1, boxData.getBoxDataUUID().toString());
                statement.setString(2, boxData.getInventory().getLocation().getWorld().getUID().toString());
                statement.setInt(3, boxData.getInventory().getLocation().getBlockX());
                statement.setInt(4, boxData.getInventory().getLocation().getBlockY());
                statement.setInt(5, boxData.getInventory().getLocation().getBlockZ());
                statement.execute();
                statement.close();

            } catch (SQLException e){

                if (plugin.getConfig().getBoolean("errorPrint")){
                    plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                    e.printStackTrace();
                }

                return;
            }

        }

        boxList.clear();
    }


    public List<BoxData> getBoxDataList(){

        List<BoxData> boxList = new ArrayList<>();

        synchronized (BoxDataList) {
            boxList.addAll(BoxDataList);
        }

        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM ItemFrameTable3");
            ResultSet set = statement.executeQuery();

            while (set.next()){

                BoxData data = new BoxData(UUID.fromString(set.getString("BoxDataUUID")));
                Location location = new Location(Bukkit.getWorld(UUID.fromString(set.getString("BlockWorldUUID"))), set.getInt("BlockX"), set.getInt("BlockY"), set.getInt("BlockZ"));

                if (location.getBlock().getType() != Material.CHEST && location.getBlock().getType() != Material.SHULKER_BOX){
                    continue;
                }

                if (location.getBlock() instanceof Chest){

                    Chest chest = (Chest) location.getBlock();
                    data.setInventory(chest.getBlockInventory());

                }

                if (location.getBlock() instanceof ShulkerBox){

                    ShulkerBox shulkerBox = (ShulkerBox) location.getBlock();
                    data.setInventory(shulkerBox.getInventory());

                }

                boxList.add(data);
            }

            set.close();
            statement.close();
        } catch (SQLException e){

            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }
            return boxList;

        }

        return boxList;
    }

    public BoxData getBoxData(UUID boxDataUUID){

        List<BoxData> boxList = new ArrayList<>();

        synchronized (BoxDataList) {
            boxList.addAll(BoxDataList);
        }

        for (BoxData boxData : boxList){

            if (boxData.getBoxDataUUID().equals(boxDataUUID)){

                return boxData;

            }

        }

        try {

            PreparedStatement statement = con.prepareStatement("SELECT * FROM ItemFrameTable3 WHERE BoxDataUUID = ?");
            ResultSet set = statement.executeQuery();

            if (set.next()){

                Location location = new Location(Bukkit.getWorld(UUID.fromString(set.getString("BlockWorldUUID"))), set.getInt("BlockX"), set.getInt("BlockY"), set.getInt("BlockZ"));

                if (location.getBlock().getType() != Material.CHEST && location.getBlock().getType() != Material.SHULKER_BOX){

                    set.close();
                    statement.close();
                    return null;

                }

                if (location.getBlock() instanceof Chest){

                    Chest chest = (Chest) location.getBlock();
                    BoxData data = new BoxData(UUID.fromString(set.getString("BoxDataUUID")), chest.getBlockInventory());

                    set.close();
                    statement.close();
                    return data;
                }

                if (location.getBlock() instanceof ShulkerBox){

                    ShulkerBox shulkerBox = (ShulkerBox) location.getBlock();
                    BoxData data = new BoxData(UUID.fromString(set.getString("BoxDataUUID")), shulkerBox.getInventory());

                    set.close();
                    statement.close();
                    return data;

                }
            }


        } catch (SQLException e){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }
        }


        return null;
    }

    public BoxData getBoxDataBySearch(Location loc){

        List<BoxData> boxList = new ArrayList<>();

        synchronized (BoxDataList) {
            boxList.addAll(BoxDataList);
        }

        for (BoxData boxData : boxList){

            Location location = boxData.getInventory().getLocation();
            if (loc.getWorld().getUID().equals(location.getWorld().getUID()) && loc.getBlockX() == location.getBlockX() && loc.getBlockY() == location.getBlockY() && loc.getBlockZ() == location.getBlockZ()){

                return boxData;

            }

        }

        try {

            PreparedStatement statement = con.prepareStatement("SELECT * FROM ItemFrameTable3 WHERE BlockWorldUUID = ? AND BlockX = ? AND BlockY = ? AND BlockZ = ?");

            statement.setString(1, loc.getWorld().getUID().toString());
            statement.setInt(2, loc.getBlockX());
            statement.setInt(3, loc.getBlockY());
            statement.setInt(4, loc.getBlockZ());

            ResultSet set = statement.executeQuery();

            if (set.next()){

                if (loc.getBlock() instanceof Chest){

                    Chest chest = (Chest) loc.getBlock();
                    BoxData boxData = new BoxData(UUID.fromString("BoxDataUUID"), chest.getBlockInventory());

                    set.close();
                    statement.close();

                    return boxData;

                }

                if (loc.getBlock() instanceof ShulkerBox){

                    ShulkerBox shulkerBox = (ShulkerBox) loc.getBlock();
                    BoxData boxData = new BoxData(UUID.fromString("BoxDataUUID"), shulkerBox.getInventory());
                    set.close();
                    statement.close();

                    return boxData;

                }
            }

        } catch (SQLException e){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }
        }

        return null;
    }

    public void setBoxData(UUID uuid, Inventory inventory){

        setBoxData(new BoxData(uuid, inventory));

    }


    public void setBoxData(BoxData data) {

        int listCount = 0;

        synchronized (BoxDataList){

            //System.out.println("debug : " + BoxDataList.size());

            BoxDataList.add(data);
            listCount = BoxDataList.size();

        }

        //System.out.println("debug2 : " + BoxDataList.size());

        if (listCount <= 15){
            return;
        }

        forceCacheToSQL();
    }


    public void deleteBoxData(UUID boxDataUUID){

        deleteBoxData(getBoxData(boxDataUUID));

    }

    public void deleteBoxData(BoxData data){

        List<BoxData> boxDataList = new ArrayList<>();
        synchronized (BoxDataList){
            boxDataList.addAll(BoxDataList);
        }

        int i = boxDataList.indexOf(data);

        if (i > 0){
            synchronized (BoxDataList){
                BoxDataList.remove(i);
            }

            return;
        }

        try {

            PreparedStatement statement = con.prepareStatement("DELETE FROM `ItemFrameTable3` WHERE BoxDataUUID = ?");
            statement.setString(1, data.getBoxDataUUID().toString());
            statement.execute();
            statement.close();


        } catch (SQLException e){

            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }

        }

    }


}
