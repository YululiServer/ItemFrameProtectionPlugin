package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ItemFrameProtectionPlugin extends JavaPlugin {

    private Connection con;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        String MySQLServer = getConfig().getString("MySQLServer");
        int MySQLPort = 3306;
        if (getConfig().getInt("MySQLPort") != 3306 && getConfig().getInt("MySQLPort") <= 65535){
            MySQLPort = getConfig().getInt("MySQLPort");
        }
        String MySQLUsername = getConfig().getString("MySQLUsername");
        String MySQLPassword = getConfig().getString("MySQLPassword");
        String MySQLDatabase = getConfig().getString("MySQLDatabase");
        String MySQLOption = getConfig().getString("MySQLOption");

        try {

            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());

            Connection connection = DriverManager.getConnection("" +
                            "jdbc:mysql://" + MySQLServer + ":" + MySQLPort + "/" + MySQLDatabase + MySQLOption,
                    MySQLUsername,
                    MySQLPassword
            );

            con = connection;
            con.setAutoCommit(true);
        } catch (Exception e){
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }

        getServer().getPluginManager().registerEvents(new ProtectListener(con, this), this);

        getLogger().info("Started ItemFrameProtectionPlugin Ver "+getDescription().getVersion()+"!!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        if (con != null){

            try {
                new Thread(() -> {
                    try {
                        con.close();
                    } catch (SQLException throwable) {
                        //throwable.printStackTrace();
                    }
                });
            } catch (Exception e){
                // e.printStackTrace();
            }

        }


        getLogger().info("Disabled ItemFrameProtectionPlugin Ver "+getDescription().getVersion()+"!!");
    }
}
