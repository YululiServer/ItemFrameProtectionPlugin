package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

class ItemFrameTimer extends BukkitRunnable implements Cancellable {

    private final Plugin plugin;
    private final Connection con;
    private boolean flag = false;
    private final ItemFrameData data;

    public ItemFrameTimer(Plugin plugin, Connection con, ItemFrameData data){
        this.plugin = plugin;
        this.con = con;
        this.data = data;
    }

    @Override
    public void run() {
        if (!flag){

            if (con != null){
                try {
                    List<FrameData> itemFrameList = data.getItemFrameList();
                    // System.out.println("Debug : LockFrame : " + itemFrameList.size());
                    for (FrameData data : itemFrameList){
                        PreparedStatement statement = con.prepareStatement("SELECT COUNT(*) FROM IFPTable WHERE ItemFrame = ?");
                        statement.setString(1, data.getItemFrame().toString());
                        ResultSet set = statement.executeQuery();
                        if (set.next()){
                            if (set.getInt("COUNT(*)") == 0){
                                statement.close();
                                PreparedStatement statement1 = con.prepareStatement("INSERT INTO `IFPTable` (`CreateUser`, `ItemFrame`) VALUES (?, ?);");
                                statement1.setString(1, data.getCreateUser().toString());
                                statement1.setString(2, data.getItemFrame().toString());
                                statement1.execute();
                                statement1.close();
                            }
                        }
                    }

                    List<DropData> dropList = data.getDropList();
                    // System.out.println("Debug : dropItem : " + dropList.size());
                    for (DropData data : dropList){
                        PreparedStatement statement = con.prepareStatement("SELECT COUNT(*) FROM IFPTable2 WHERE ItemUUID = ?");
                        statement.setString(1, data.getItemUUID().toString());
                        ResultSet set = statement.executeQuery();
                        if (set.next()){
                            if (set.getInt("COUNT(*)") == 0){
                                statement.close();
                                PreparedStatement statement1 = con.prepareStatement("INSERT INTO `IFPTable2` (`DropUser`, `ItemUUID`) VALUES (?, ?);");
                                statement1.setString(1, data.getDropUser().toString());
                                statement1.setString(2, data.getItemUUID().toString());
                                statement1.execute();
                                statement1.close();
                            }
                        }
                    }

                    // ここからゴミデータお掃除

                    List<World> worlds = plugin.getServer().getWorlds();
                    int i = 0;
                    for (World world : worlds){
                        i = i + world.getEntities().size();
                    }
                    if (i != 0){
                        PreparedStatement statement = con.prepareStatement("SELECT * FROM IFPTable");
                        ResultSet set = statement.executeQuery();
                        while(set.next()){
                            FrameData itemFrame = data.getItemFrame(UUID.fromString(set.getString("CreateUser")), UUID.fromString(set.getString("ItemFrame")));
                            if (itemFrame == null){
                                PreparedStatement statement1 = con.prepareStatement("DELETE FROM `IFPTable` WHERE `CreateUser` = ? AND `ItemFrame` = ?");
                                statement1.setString(1, set.getString("CreateUser"));
                                statement1.setString(2, set.getString("ItemFrame"));
                                statement1.execute();
                                statement1.close();
                            }
                        }
                        statement.close();

                        PreparedStatement statement2 = con.prepareStatement("SELECT * FROM IFPTable2");
                        ResultSet set2 = statement2.executeQuery();
                        while(set2.next()){
                            FrameData itemFrame = data.getItemFrame(UUID.fromString(set2.getString("DropUser")), UUID.fromString(set2.getString("ItemUUID")));
                            if (itemFrame == null){
                                PreparedStatement statement1 = con.prepareStatement("DELETE FROM `IFPTable2` WHERE `DropUser` = ? AND `ItemUUID` = ?");
                                statement1.setString(1, set.getString("DropUser"));
                                statement1.setString(2, set.getString("ItemUUID"));
                                statement1.execute();
                                statement1.close();
                            }
                        }
                        statement2.close();
                    }

                } catch (SQLException e){
                    if (plugin.getConfig().getBoolean("errorPrint")) {
                        plugin.getLogger().info(ChatColor.RED + "SQLエラーを検知しました。");
                        e.printStackTrace();
                    }
                }
            }


            new ItemFrameTimer(plugin, con, data).runTaskLaterAsynchronously(plugin, 120L);
        }
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.flag = cancel;
    }

    public boolean isCancelled(){
        return this.flag;
    }
}
