package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class ProtectListener implements Listener {

    private Connection con;
    private final Plugin plugin;
    private final String MySQLServer;
    private final int MySQLPort;
    private final String MySQLUsername;
    private final String MySQLPassword;
    private final String MySQLDatabase;
    private final String MySQLOption;

    public ProtectListener(Connection con, Plugin plugin){

        this.con = con;
        this.plugin = plugin;


        this.MySQLServer = plugin.getConfig().getString("MySQLServer");
        int MySQLPort = 3306;
        if (plugin.getConfig().getInt("MySQLPort") != 3306 && plugin.getConfig().getInt("MySQLPort") <= 65535){
            MySQLPort = plugin.getConfig().getInt("MySQLPort");
        }
        this.MySQLPort = MySQLPort;

        this.MySQLUsername = plugin.getConfig().getString("MySQLUsername");
        this.MySQLPassword = plugin.getConfig().getString("MySQLPassword");
        this.MySQLDatabase = plugin.getConfig().getString("MySQLDatabase");
        this.MySQLOption = plugin.getConfig().getString("MySQLOption");

    }


    private boolean flag = false;

    @EventHandler
    public void PlayerInteractEntityEvent (PlayerInteractEntityEvent e){

        if (flag){
            flag = false;
            return;
        }

        flag = true;

        if (!(e.getRightClicked() instanceof ItemFrame)){
            return;
        }

        ItemFrame frame = (ItemFrame) e.getRightClicked();
        Player player = e.getPlayer();

        new Thread(()->{
            List<IFPData> dataList = new ArrayList<>();
            try {

                try {
                    PreparedStatement statement = con.prepareStatement("SELECT * FROM IFPDataList");
                    statement.execute();
                    statement.close();
                } catch (Exception ex){
                    try {
                        // DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                        Connection connection = DriverManager.getConnection("" +
                                        "jdbc:mysql://" + MySQLServer + ":" + MySQLPort + "/" + MySQLDatabase + MySQLOption,
                                MySQLUsername,
                                MySQLPassword
                        );

                        con = connection;
                        con.setAutoCommit(true);
                    } catch (SQLException ex1){

                        ex1.printStackTrace();
                        Bukkit.getServer().getPluginManager().disablePlugin(plugin);
                    }
                }

                PreparedStatement statement = con.prepareStatement("SELECT * FROM IFPDataList");
                ResultSet set = statement.executeQuery();
                while (set.next()){
                    dataList.add(new IFPData(UUID.fromString(set.getString("ItemFrameUUID")),UUID.fromString("UserUUID")));
                }
                set.close();

                statement.close();

            } catch (Exception ex){
                ex.printStackTrace();
                Bukkit.getServer().getPluginManager().disablePlugin(plugin);
            }

            if (player.isSneaking()){
                // スニークしたとき
                UUID uuid = frame.getUniqueId();

                for (IFPData data : dataList){
                    if (uuid.equals(data.getItemFrameUUID())){

                        if (player.getUniqueId().equals(data.getUserUUID())){

                            try {
                                PreparedStatement statement = con.prepareStatement("DELETE FROM IFPDataList WHERE ItemFrameUUID = ?");
                                statement.execute();
                                statement.close();
                            } catch (SQLException ex){
                                ex.printStackTrace();
                                Bukkit.getServer().getPluginManager().disablePlugin(plugin);
                            }

                            player.sendMessage(ChatColor.GREEN + "[IFP] 保護解除しました。");
                            return;
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "[IFP] 他の人の保護は解除できません。");
                            return;
                        }

                    }
                }

                try {
                    PreparedStatement statement = con.prepareStatement("INSERT INTO `IFPDataList`(`ItemFrameUUID`, `UserUUID`) VALUES (?,?)");
                    statement.setString(1, frame.getUniqueId().toString());
                    statement.setString(2, player.getUniqueId().toString());
                    statement.execute();
                    statement.close();
                } catch (SQLException ex){
                    ex.printStackTrace();
                    Bukkit.getServer().getPluginManager().disablePlugin(plugin);
                }

                return;
            }

            for (IFPData data : dataList) {
                if (frame.getUniqueId().equals(data.getItemFrameUUID())) {
                    e.setCancelled(true);
                    return;
                }
            }
        }).start();

    }

    @EventHandler
    public void HangingBreakEvent (HangingBreakEvent e){

        if (!(e.getEntity() instanceof ItemFrame)){
            return;
        }

        new Thread(()->{
            List<IFPData> dataList = new ArrayList<>();
            try {

                try {
                    PreparedStatement statement = con.prepareStatement("SELECT * FROM IFPDataList");
                    statement.execute();
                    statement.close();
                } catch (Exception ex){
                    try {
                        // DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                        Connection connection = DriverManager.getConnection("" +
                                        "jdbc:mysql://" + MySQLServer + ":" + MySQLPort + "/" + MySQLDatabase + MySQLOption,
                                MySQLUsername,
                                MySQLPassword
                        );

                        con = connection;
                        con.setAutoCommit(true);
                    } catch (SQLException ex1){

                        ex1.printStackTrace();
                        Bukkit.getServer().getPluginManager().disablePlugin(plugin);
                    }
                }

                PreparedStatement statement = con.prepareStatement("SELECT * FROM IFPDataList");
                ResultSet set = statement.executeQuery();
                while (set.next()){
                    dataList.add(new IFPData(UUID.fromString(set.getString("ItemFrameUUID")),UUID.fromString("UserUUID")));
                }
                set.close();
                statement.close();

            } catch (Exception ex){
                ex.printStackTrace();
                Bukkit.getServer().getPluginManager().disablePlugin(plugin);
            }

            for (IFPData data : dataList){
                ItemFrame frame = (ItemFrame) e.getEntity();
                if (data.getItemFrameUUID().equals(frame.getUniqueId())){
                    e.setCancelled(true);
                    return;
                }
            }
        }).start();
    }

    @EventHandler
    public void EntityDamageEvent (EntityDamageEvent e) {

        if (!(e.getEntity() instanceof ItemFrame)) {
            return;
        }

        new Thread(()->{
            List<IFPData> dataList = new ArrayList<>();
            try {

                try {
                    PreparedStatement statement = con.prepareStatement("SELECT * FROM IFPDataList");
                    statement.execute();
                    statement.close();
                } catch (Exception ex){
                    try {
                        // DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                        Connection connection = DriverManager.getConnection("" +
                                        "jdbc:mysql://" + MySQLServer + ":" + MySQLPort + "/" + MySQLDatabase + MySQLOption,
                                MySQLUsername,
                                MySQLPassword
                        );

                        con = connection;
                        con.setAutoCommit(true);
                    } catch (SQLException ex1){

                        ex1.printStackTrace();
                        Bukkit.getServer().getPluginManager().disablePlugin(plugin);
                    }
                }

                PreparedStatement statement = con.prepareStatement("SELECT * FROM IFPDataList");
                ResultSet set = statement.executeQuery();
                while (set.next()){
                    dataList.add(new IFPData(UUID.fromString(set.getString("ItemFrameUUID")),UUID.fromString("UserUUID")));
                }
                set.close();
                statement.close();

            } catch (Exception ex){
                ex.printStackTrace();
                Bukkit.getServer().getPluginManager().disablePlugin(plugin);
            }

            for (IFPData data : dataList){
                ItemFrame frame = (ItemFrame) e.getEntity();
                if (data.getItemFrameUUID().equals(frame.getUniqueId())){
                    e.setCancelled(true);
                    return;
                }
            }
        }).start();
    }

    @EventHandler
    public void EntityDamageByEntityEvent (EntityDamageByEntityEvent e) {

        if (!(e.getEntity() instanceof ItemFrame)) {
            return;
        }

        new Thread(()->{
            List<IFPData> dataList = new ArrayList<>();
            try {

                try {
                    PreparedStatement statement = con.prepareStatement("SELECT * FROM IFPDataList");
                    statement.execute();
                    statement.close();
                } catch (Exception ex){
                    try {
                        // DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                        Connection connection = DriverManager.getConnection("" +
                                        "jdbc:mysql://" + MySQLServer + ":" + MySQLPort + "/" + MySQLDatabase + MySQLOption,
                                MySQLUsername,
                                MySQLPassword
                        );

                        con = connection;
                        con.setAutoCommit(true);
                    } catch (SQLException ex1){

                        ex1.printStackTrace();
                        Bukkit.getServer().getPluginManager().disablePlugin(plugin);
                    }
                }

                PreparedStatement statement = con.prepareStatement("SELECT * FROM IFPDataList");
                ResultSet set = statement.executeQuery();
                while (set.next()){
                    dataList.add(new IFPData(UUID.fromString(set.getString("ItemFrameUUID")),UUID.fromString("UserUUID")));
                }
                set.close();
                statement.close();

            } catch (Exception ex){
                ex.printStackTrace();
                Bukkit.getServer().getPluginManager().disablePlugin(plugin);
            }

            for (IFPData data : dataList){
                ItemFrame frame = (ItemFrame) e.getEntity();
                if (data.getItemFrameUUID().equals(frame.getUniqueId())){
                    e.setCancelled(true);
                    return;
                }
            }
        }).start();
    }

}
