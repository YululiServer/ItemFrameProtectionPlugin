package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.DataAPI;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public final class ItemFrameProtectionPlugin extends JavaPlugin {

    private Connection con = null;
    private DataAPI dataAPI = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        String MySQLServer = getConfig().getString("MySQLServer");
        String MySQLUsername = getConfig().getString("MySQLUsername");
        String MySQLPassword = getConfig().getString("MySQLPassword");
        String MySQLDatabase = getConfig().getString("MySQLDatabase");
        String MySQLOption = getConfig().getString("MySQLOption");

        try {

            if (getConfig().getBoolean("useMySQL")) {

                con = DriverManager.getConnection("jdbc:mysql://" + MySQLServer + "/" + MySQLDatabase + MySQLOption, MySQLUsername, MySQLPassword);
                dataAPI = new DataAPI(con, this);

                try {
                    PreparedStatement statement = con.prepareStatement("SELECT 1 FROM ItemFrameTable1 LIMIT 1;");
                    statement.execute();
                    statement.close();
                } catch (SQLException e){
                    dataAPI.createTableByItem();
                }

                try {
                    PreparedStatement statement = con.prepareStatement("SELECT 1 FROM ItemFrameTable2 LIMIT 1;");
                    statement.execute();
                    statement.close();
                } catch (SQLException e){
                    dataAPI.createTableByDrop();
                }

            } else {

                String pass = "./" + getDataFolder().getPath() + "/FrameData.db";
                if (System.getProperty("os.name").toLowerCase().startsWith("windows")){
                    pass = pass.replaceAll("/", "\\\\");
                }

                boolean cre = false;
                if (!new File(pass).exists()){
                    try {
                        cre = new File(pass).createNewFile();
                    } catch (IOException e) {
                        // e.printStackTrace();
                    }
                }
                con = DriverManager.getConnection("jdbc:sqlite:"+pass);
                con.setAutoCommit(true);

                dataAPI = new DataAPI(con, this);
                if (cre) {
                    dataAPI.createAllTable();
                }

            }

            getCommand("ifp").setExecutor(new IFPCommand(dataAPI));
            getCommand("ifp").setTabCompleter(new IFPCommandTab());

            getServer().getPluginManager().registerEvents(new ItemFrameListener(dataAPI), this);

            new AutoRemoveTimer(dataAPI, this).runTaskLaterAsynchronously(this, 0L);

            getLogger().info("Started ItemFrameProtectionPlugin Ver "+getDescription().getVersion()+"!!");

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

        new Thread(()->{

            try {
                if (con != null){
                    con.close();
                }
            } catch (Exception e){

                if (getConfig().getBoolean("errorPrint")){
                    getLogger().info(ChatColor.RED + "エラーを検知しました。");
                    e.printStackTrace();
                }

            }

        }).start();

        getLogger().info("Disabled ItemFrameProtectionPlugin Ver "+getDescription().getVersion()+"!!");
    }
}
