package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import com.destroystokyo.paper.loottable.LootableBlockInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.entity.minecart.HopperMinecart;
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
            PreparedStatement statement = con.prepareStatement("CREATE TABLE `ItemFrameTable3` ( `BoxDataUUID` VARCHAR(36) NOT NULL, `UseUserUUID` VARCHAR(36) NOT NULL, `BlockWorldUUID` VARCHAR(36) NOT NULL, `BlockX` INTEGER NOT NULL, `BlockY` INTEGER NOT NULL, `BlockZ` INTEGER NOT NULL  , PRIMARY KEY (`BoxDataUUID`,`UseUserUUID`))");
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
                PreparedStatement statement = con.prepareStatement("INSERT INTO `ItemFrameTable3` (`BoxDataUUID`, `UseUserUUID`, `BlockWorldUUID`, `BlockX`, `BlockY`, `BlockZ`) VALUES (?, ?, ?, ?, ?, ?) ");

                statement.setString(1, boxData.getBoxDataUUID().toString());
                statement.setString(2, boxData.getBoxUseUUID().toString());
                statement.setString(3, boxData.getInventory().getLocation().getWorld().getUID().toString());
                statement.setInt(4, boxData.getInventory().getLocation().getBlockX());
                statement.setInt(5, boxData.getInventory().getLocation().getBlockY());
                statement.setInt(6, boxData.getInventory().getLocation().getBlockZ());
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

                if (Bukkit.getServer().getWorld(UUID.fromString(set.getString("BlockWorldUUID"))) == null){
                    continue;
                }

                if (Bukkit.getWorld(UUID.fromString(set.getString("BlockWorldUUID"))) == null){
                    continue;
                }

                BoxData data = new BoxData(UUID.fromString(set.getString("BoxDataUUID")));
                Location location = new Location(Bukkit.getWorld(UUID.fromString(set.getString("BlockWorldUUID"))), set.getInt("BlockX"), set.getInt("BlockY"), set.getInt("BlockZ"));


                data.setBoxDataUUID(UUID.fromString(set.getString("UseUserUUID")));
                Block block = location.getBlock();

                // チェスト
                if (block.getState() instanceof Chest){
                    Chest chest = (Chest) block.getState();
                    data.setInventory(chest.getBlockInventory());
                }

                // ラージチェスト？
                if (block.getState() instanceof DoubleChest){
                    DoubleChest chest = (DoubleChest) block.getState();
                    data.setInventory(chest.getInventory());
                }

                // シュルカー
                if (block.getState() instanceof ShulkerBox){
                    ShulkerBox shulkerBox = (ShulkerBox) block.getState();
                    data.setInventory(shulkerBox.getInventory());
                }

                // かまど
                if (block.getState() instanceof Furnace){

                    Furnace furnace = (Furnace) block.getState();
                    data.setInventory(furnace.getInventory());

                }

                // 溶鉱炉
                if (block.getState() instanceof BlastFurnace){

                    BlastFurnace blastFurnace = (BlastFurnace) block.getState();
                    data.setInventory(blastFurnace.getInventory());

                }

                // 燻製器
                if (block.getState() instanceof Smoker){

                    Smoker smoker = (Smoker) block.getState();
                    data.setInventory(smoker.getInventory());

                }

                // ホッパー
                if (block.getState() instanceof Hopper){

                    Hopper hopper = (Hopper) block.getState();
                    data.setInventory(hopper.getInventory());
                }

                // ディスペンサー
                if (block.getState() instanceof Dispenser){

                    Dispenser dispenser = (Dispenser) block.getState();
                    data.setInventory(dispenser.getInventory());
                }

                // ホッパーカート
                if (block.getState() instanceof HopperMinecart){

                    HopperMinecart hopperMinecart = (HopperMinecart) block.getState();
                    data.setInventory(hopperMinecart.getInventory());
                }

                // ドロッパー
                if (block.getState() instanceof Dropper){

                    Dropper dropper = (Dropper) block.getState();
                    data.setInventory(dropper.getInventory());
                }

                // 樽
                if (block.getState() instanceof Barrel){

                    Barrel barrel = (Barrel) block.getState();
                    data.setInventory(barrel.getInventory());
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
                    BoxData data = new BoxData(UUID.fromString(set.getString("BoxDataUUID")), UUID.fromString(set.getString("UseUserUUID")), chest.getBlockInventory());

                    set.close();
                    statement.close();
                    return data;
                }

                if (location.getBlock() instanceof ShulkerBox){

                    ShulkerBox shulkerBox = (ShulkerBox) location.getBlock();
                    BoxData data = new BoxData(UUID.fromString(set.getString("BoxDataUUID")), UUID.fromString(set.getString("UseUserUUID")), shulkerBox.getInventory());

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
                    BoxData boxData = new BoxData(UUID.fromString(set.getString("BoxDataUUID")), UUID.fromString(set.getString("UseUserUUID")), chest.getBlockInventory());

                    set.close();
                    statement.close();

                    return boxData;

                }

                if (loc.getBlock() instanceof ShulkerBox){

                    ShulkerBox shulkerBox = (ShulkerBox) loc.getBlock();
                    BoxData boxData = new BoxData(UUID.fromString("BoxDataUUID"), UUID.fromString(set.getString("UseUserUUID")), shulkerBox.getInventory());
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

        // System.out.println("debug2 : " + BoxDataList.size());

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

            PreparedStatement statement = con.prepareStatement("DELETE FROM `ItemFrameTable3` WHERE BoxDataUUID = ? AND UseUserUUID = ?");
            statement.setString(1, data.getBoxDataUUID().toString());
            statement.setString(2, data.getBoxUseUUID().toString());
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
