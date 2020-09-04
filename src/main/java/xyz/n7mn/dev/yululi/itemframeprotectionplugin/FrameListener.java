package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;

import java.sql.*;


public class FrameListener implements Listener {

    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("ItemFrameProtectionPlugin");

    @EventHandler
    public void eee (HangingPlaceEvent e){

        System.out.println(e.getEventName());
        System.out.println(e.getBlock().getType());

        Bukkit.getLogger().info("あれ？？");

        if (e.getBlock().getType() == Material.ITEM_FRAME){



        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerInteractEntityEvent (PlayerInteractEntityEvent e){

        // System.out.println(e.getEventName());
        // System.out.println(e.getRightClicked().getType());

        Connection con = null;
        try {
            if (e.getRightClicked().getType() == EntityType.ITEM_FRAME){
                ItemFrame frame = (ItemFrame) e.getRightClicked();
                // System.out.println(frame.getItem().getType());
                if (frame.getItem().getType() == Material.AIR){
                    if (e.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR){

                        if (plugin.getConfig().getBoolean("useMySQL")){
                            try {
                                con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("MySQLServer") + "/" + plugin.getConfig().getString("MySQLDatabase") + plugin.getConfig().getString("MySQLOption"), plugin.getConfig().getString("MySQLUsername"), plugin.getConfig().getString("MySQLPassword"));
                                PreparedStatement st1 = con.prepareStatement("SHOW TABLES LIKE 'IFPTable';");

                                if (!st1.executeQuery().next()){
                                    try {
                                        PreparedStatement statement = con.prepareStatement("CREATE TABLE `IFPTable` ( `ID` INT NOT NULL , `CreateUser` VARCHAR(36) NOT NULL , `x` INT NOT NULL , `y` INT NOT NULL , `z` INT NOT NULL , `Active` BOOLEAN NOT NULL , PRIMARY KEY (`ID`))");
                                        statement.execute();
                                    } catch (SQLException xx){
                                        if (plugin.getConfig().getBoolean("errorPrint")){
                                            plugin.getLogger().info(ChatColor.RED + "MySQLの権限が足りないみたい。。。" + xx.getMessage());
                                        }
                                    }
                                }

                                PreparedStatement st2 = con.prepareStatement("SELECT * FROM IFPTable WHERE x = ? AND y = ? AND z = ? AND Active = 1");
                                st2.setInt(1, e.getRightClicked().getLocation().getBlockX());
                                st2.setInt(2, e.getRightClicked().getLocation().getBlockY());
                                st2.setInt(3, e.getRightClicked().getLocation().getBlockZ());

                                ResultSet set = st2.executeQuery();
                                if (!set.next()){
                                    try {
                                        PreparedStatement statement = con.prepareStatement("SELECT COUNT(*) FROM IFPTable");
                                        ResultSet set1 = statement.executeQuery();
                                        PreparedStatement st3 = con.prepareStatement("INSERT INTO `IFPTable` (`ID`, `CreateUser`, `x`, `y`, `z`, `Active`) VALUES (?, ?, ?, ?, ?, ?) ");
                                        if (set1.next()){
                                            int i = set1.getInt("COUNT(*)") + 1;
                                            st3.setInt(1, i);
                                        } else {
                                            st3.setInt(1, 1);
                                        }
                                        st3.setString(2, e.getPlayer().getUniqueId().toString());
                                        st3.setInt(3, e.getRightClicked().getLocation().getBlockX());
                                        st3.setInt(4, e.getRightClicked().getLocation().getBlockY());
                                        st3.setInt(5, e.getRightClicked().getLocation().getBlockZ());
                                        st3.setBoolean(6, true);
                                        st3.execute();
                                    } catch (SQLException exx){
                                        if (plugin.getConfig().getBoolean("errorPrint")){
                                            plugin.getLogger().info(ChatColor.RED + "保護データを追加できなかったようです？");
                                        }
                                    }
                                }
                                con.close();
                            } catch (SQLException sql){
                                if (plugin.getConfig().getBoolean("errorPrint")){
                                    sql.printStackTrace();
                                    plugin.getLogger().info(ChatColor.RED + "MySQLサーバー関係でエラーが発生しました。 " + sql.getMessage());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (Exception ex2){
                // ex2.printStackTrace();
            }
        }

    }
}
