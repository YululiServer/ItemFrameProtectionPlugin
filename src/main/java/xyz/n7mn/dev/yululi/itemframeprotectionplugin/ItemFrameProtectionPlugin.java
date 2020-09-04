package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ItemFrameProtectionPlugin extends JavaPlugin {

    private Connection con = null;
    private String MySQLServer = "localhost";
    private String MySQLDatabase = "database";
    private String MySQLOption = "?allowPublicKeyRetrieval=true&useSSL=false";
    private String MySQLUsername = "user";
    private String MySQLPassword = "password";

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();

        MySQLServer = getConfig().getString("MySQLServer");
        MySQLUsername = getConfig().getString("MySQLUsername");
        MySQLPassword = getConfig().getString("MySQLPassword");
        MySQLDatabase = getConfig().getString("MySQLDatabase");
        MySQLOption = getConfig().getString("MySQLOption");

        if (getConfig().getBoolean("useMySQL")){
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + MySQLServer + "/" + MySQLDatabase + MySQLOption, MySQLUsername, MySQLPassword);
            } catch (SQLException e){
                if (getConfig().getBoolean("errorPrint")){
                    getLogger().info(ChatColor.RED + "エラーが発生しました。");
                    e.printStackTrace();
                }
            }
        }

        if (con != null){
            getServer().getPluginManager().registerEvents(new FrameListener(this, con), this);
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            con.close();
        } catch (Exception e){
            // e.printStackTrace();
        }
    }

    @Override
    public void onLoad() {
        // super.onLoad();
        if (con != null){
            try {
                con.close();
            } catch (SQLException e){
                // e.printStackTrace();
            }
        }

        reloadConfig();

        MySQLServer = getConfig().getString("MySQLServer");
        MySQLUsername = getConfig().getString("MySQLUsername");
        MySQLPassword = getConfig().getString("MySQLPassword");
        MySQLDatabase = getConfig().getString("MySQLDatabase");
        MySQLOption = getConfig().getString("MySQLOption");

        if (getConfig().getBoolean("useMySQL")){
            try {
                con = DriverManager.getConnection("jdbc:mysql://" + MySQLServer + "/" + MySQLDatabase + MySQLOption, MySQLUsername, MySQLPassword);
            } catch (SQLException e){
                con = null;
                if (getConfig().getBoolean("errorPrint")){
                    getLogger().info(ChatColor.RED + "エラーが発生しました。");
                    e.printStackTrace();
                }
            }
        }
    }
}
