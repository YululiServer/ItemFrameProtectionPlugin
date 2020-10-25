package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

class AutoSQLConnectCheckTimer extends BukkitRunnable {

    private final ItemFrameProtectionPlugin plugin;
    private final Connection con;

    AutoSQLConnectCheckTimer(ItemFrameProtectionPlugin plugin, Connection con){
        this.plugin = plugin;
        this.con = con;
    }

    @Override
    public void run() {

        //plugin.getLogger().info("DBに接続できるかチェックしています...");
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM ItemFrameTable1");
            statement.execute();
            statement.close();
        } catch (Exception e){

            plugin.getLogger().info("DBに再接続を試みています...");
            try {

                Connection connection;

                if (plugin.getConfig().getBoolean("useMySQL")) {
                    connection = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("MySQLServer") + "/" + plugin.getConfig().getString("MySQLDatabase") + plugin.getConfig().getString("MySQLOption"), plugin.getConfig().getString("MySQLUsername"), plugin.getConfig().getString("MySQLPassword"));
                    plugin.setConnect(connection);
                } else {
                    String pass = "./" + plugin.getDataFolder().getPath() + "/FrameData.db";
                    if (System.getProperty("os.name").toLowerCase().startsWith("windows")){
                        pass = pass.replaceAll("/", "\\\\");
                    }

                    if (!new File(pass).exists()){
                        try {
                            new File(pass).createNewFile();
                        } catch (IOException ex) {
                            // e.printStackTrace();
                        }
                    }
                    connection = DriverManager.getConnection("jdbc:sqlite:" + pass);
                    connection.setAutoCommit(true);
                    plugin.setConnect(connection);
                }


                if (plugin.isEnabled()){
                    new AutoSQLConnectCheckTimer(plugin, connection).runTaskLaterAsynchronously(plugin, 1200L);

                    return;
                }

            } catch (Exception ex){
                if (plugin.getConfig().getBoolean("errorPrint")){
                    plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                    e.printStackTrace();
                }
            }
            plugin.getLogger().info("DBに再接続をしました。");
        }

        if (plugin.isEnabled()){
            //plugin.getLogger().info("DBに接続きました。次回のチェックは1分後です。");
            new AutoSQLConnectCheckTimer(plugin, con).runTaskLaterAsynchronously(plugin, 1200L);
        }
    }
}
