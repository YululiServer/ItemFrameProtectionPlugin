package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import com.google.gson.Gson;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.*;
import java.util.Date;
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

    @Override
    public void createTable(){
        // CREATE TABLE `tina-server`.`ItemFrameTable1` ( `ItemFrameUUID` VARCHAR(36) NOT NULL , `FrameItem` JSON NOT NULL , `ProtectUser` VARCHAR(36) NULL , `CreateDate` DATETIME NOT NULL , `Active` BOOLEAN NOT NULL , PRIMARY KEY (`ItemFrameUUID`))
        new BukkitRunnable() {
            @Override
            public void run() {

                try {
                    PreparedStatement statement;
                    if (plugin.getConfig().getBoolean("useMySQL")){
                        statement = con.prepareStatement("CREATE TABLE `ItemFrameTable1` ( `ItemFrameUUID` VARCHAR(36) NOT NULL , `FrameItem` JSON NOT NULL , `ProtectUser` VARCHAR(36) NULL , `CreateDate` DATETIME NOT NULL , `Active` BOOLEAN NOT NULL)");
                    } else {
                        statement = con.prepareStatement("CREATE TABLE `ItemFrameTable1` ( `ItemFrameUUID` VARCHAR(36) NOT NULL , `FrameItem` JSON NOT NULL , `ProtectUser` VARCHAR(36) NULL , `CreateDate` DATETIME NOT NULL , `Active` INTEGER)");
                    }

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

        Gson gson = new Gson();

        try {
            List<FrameData> frameData = new ArrayList<>();
            synchronized (frameDataList){

                frameData.addAll(frameDataList);
                frameDataList.clear();

            }

            int i = 0;
            for (FrameData data : frameData) {

                PreparedStatement statement_f = con.prepareStatement("INSERT INTO `ItemFrameTable1` (`ItemFrameUUID`, `FrameItem`, `ProtectUser`, `CreateDate`, `Active`) VALUES (?, ?, ?, ?, ?) ");
                statement_f.setString(1, data.getItemFrameUUID().toString());
                statement_f.setString(2, gson.toJson(new ItemStackJSON(data.getFrameItem())));
                statement_f.setString(3, data.getProtectUser().toString());
                statement_f.setTimestamp(4, new java.sql.Timestamp(data.getCreateDate().getTime()));
                statement_f.setBoolean(5, data.isActive());

                statement_f.execute();

                statement_f.close();

            }

        } catch (Exception e){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }
        }

    }

    public List<FrameData> getFrameDataList(){
        List<FrameData> dataList = new ArrayList<>(Math.toIntExact(this.getCount()));

        synchronized (frameDataList){
            if (frameDataList.size() != 0){
                dataList.addAll(frameDataList);
            }
        }

        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM ItemFrameTable1");
            statement.setFetchSize(10000);
            ResultSet set = statement.executeQuery();

            while (set.next()){

                ItemStackJSON item = new Gson().fromJson(set.getString("FrameItem"), ItemStackJSON.class);

                ItemStack stack = new ItemStack(item.getType());
                stack.setAmount(item.getAmount());
                stack.setData(item.getMaterialData());
                stack.setItemMeta(item.getItemMeta());
                stack.setLore(item.getLore());

                FrameData data = new FrameData(UUID.fromString(set.getString("ItemFrameUUID")), stack, UUID.fromString(set.getString("ProtectUser")), new Date(set.getTimestamp("CreateDate").getTime()), set.getBoolean("Active"));
                dataList.add(data);

            }
        } catch (SQLException e){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }
        }

        return dataList;
    }

    public List<FrameData> getFrameDataListByActive() {

        List<FrameData> dataList = new ArrayList<>(Math.toIntExact(this.getCount()));

        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM ItemFrameTable1 WHERE Active = 1");
            ResultSet set = statement.executeQuery();
            while (set.next()){

                ItemStackJSON item = new Gson().fromJson(set.getString("FrameItem"), ItemStackJSON.class);
                ItemStack stack = new ItemStack(item.getType());
                stack.setAmount(item.getAmount());
                stack.setData(item.getMaterialData());
                stack.setItemMeta(item.getItemMeta());
                stack.setLore(item.getLore());

                FrameData data = new FrameData(UUID.fromString(set.getString("ItemFrameUUID")), stack, UUID.fromString(set.getString("ProtectUser")), new Date(set.getTimestamp("CreateDate").getTime()), set.getBoolean("Active"));
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

    public FrameData getFrameData(UUID itemFrameUUID){

        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM ItemFrameTable1 WHERE ItemFrameUUID = ? AND Active = 1");

            statement.setString(1, itemFrameUUID.toString());
            ResultSet set = statement.executeQuery();
            if (set.next()){

                ItemStackJSON item = new Gson().fromJson(set.getString("FrameItem"), ItemStackJSON.class);
                ItemStack stack = new ItemStack(item.getType());
                stack.setAmount(item.getAmount());
                stack.setData(item.getMaterialData());
                stack.setItemMeta(item.getItemMeta());
                stack.setLore(item.getLore());

                FrameData data = new FrameData(UUID.fromString(set.getString("ItemFrameUUID")), stack, UUID.fromString(set.getString("ProtectUser")), new Date(set.getTimestamp("CreateDate").getTime()), set.getBoolean("Active"));
                set.close();
                statement.close();

                return data;
            }
            set.close();
            statement.close();

            List<FrameData> frameList = new ArrayList<>();
            synchronized (frameDataList){
                frameList.addAll(frameDataList);
            }

            for (FrameData data : frameList){

                if (data.getItemFrameUUID().equals(itemFrameUUID)){
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

        synchronized (frameDataList){
            for (FrameData data : frameDataList){

                if (data.getItemFrameUUID().equals(itemFrameUUID)){

                    frameDataList.remove(data);
                    return;
                }

            }
        }

        try {

            PreparedStatement statement = con.prepareStatement("UPDATE ItemFrameTable1 SET Active = 0 WHERE ItemFrameUUID = ?");
            statement.setString(1, itemFrameUUID.toString());
            statement.execute();
            statement.close();

        } catch (Exception e){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }
        }

    }

    public long getCount(){

        long count = 0;

        synchronized (frameDataList){
            count = count + frameDataList.size();
        }

        try {
            PreparedStatement statement = con.prepareStatement("SELECT COUNT(*) FROM ItemFrameTable1;");
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
