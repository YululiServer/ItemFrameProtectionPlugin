package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executor;

public final class ItemFrameProtectionPlugin extends JavaPlugin {

    private Connection con = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        try {
            saveDefaultConfig();

            boolean useMySQL = false; // getConfig().getBoolean("useMySQL");

            if (useMySQL){
                final String MySQLServer = getConfig().getString("MySQLServer");
                final String MySQLUsername = getConfig().getString("MySQLUsername");
                final String MySQLPassword = getConfig().getString("MySQLPassword");
                final String MySQLDatabase = getConfig().getString("MySQLDatabase");
                final String MySQLOption = getConfig().getString("MySQLOption");
                con = DriverManager.getConnection("jdbc:mysql://" + MySQLServer + "/" + MySQLDatabase + MySQLOption, MySQLUsername, MySQLPassword);
            } else {
                String pass = "./" + getDataFolder().getPath() + "/data.db";
                con = DriverManager.getConnection("jdbc:sqlite:"+pass);
                con.setAutoCommit(true);
            }

            getPluginLoader().createRegisteredListeners(new FrameListener(this, con),this);
        } catch (Exception e){
            if (getConfig().getBoolean("errorPrint")){
                getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }
            onDisable();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (con != null){
            new Thread(
                    () -> {
                        try {
                            con.close();
                        } catch (Exception e){
                            if (getConfig().getBoolean("errorPrint")){
                                getLogger().info(ChatColor.RED + "エラーを検知しました。");
                                e.printStackTrace();
                            }
                        }
                    }
            );
        }
    }
}
