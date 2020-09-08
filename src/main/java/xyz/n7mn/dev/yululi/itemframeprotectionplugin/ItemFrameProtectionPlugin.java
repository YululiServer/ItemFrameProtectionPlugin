package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.sql.*;

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

                PreparedStatement statement = con.prepareStatement("SHOW TABLES LIKE 'IFPTable';");
                ResultSet resultSet = statement.executeQuery();
                if (!resultSet.next()){
                    con.prepareStatement("CREATE TABLE `IFPTable` (\n" +
                            "  `CreateUser` varchar(36) COLLATE utf8mb4_ja_0900_as_cs_ks NOT NULL,\n" +
                            "  `ItemFrame` varchar(36) NOT NULL\n" +
                            ")").execute();
                } else {
                    try {
                        PreparedStatement statement1 = con.prepareStatement("SELECT ItemFrame FROM IFPTable");
                        statement1.execute();
                    } catch (Exception e){
                        con.prepareStatement("RENAME TABLE IFPTable TO IFPTable_old;").execute();
                        con.prepareStatement("CREATE TABLE `IFPTable` (\n" +
                                "  `CreateUser` varchar(36) COLLATE utf8mb4_ja_0900_as_cs_ks NOT NULL,\n" +
                                "  `ItemFrame` varchar(36) NOT NULL\n" +
                                ")").execute();
                    }
                }


            } catch (SQLException e){
                if (getConfig().getBoolean("errorPrint")){
                    getLogger().info(ChatColor.RED + "エラーが発生しました。");
                    e.printStackTrace();
                }
            }
        } else {
            try {
                String pass = "./" + getDataFolder().getPath() + "/data.db";
                if (System.getProperty("os.name").toLowerCase().startsWith("windows")){
                    pass = pass.replaceAll("/", "\\\\");
                }

                boolean cre = false;
                if (!new File(pass).exists()){
                    try {
                        new File(pass).createNewFile();
                        cre = true;
                    } catch (IOException e) {
                        // e.printStackTrace();
                    }
                }


                con = DriverManager.getConnection("jdbc:sqlite:"+pass);
                con.setAutoCommit(true);

                if (cre){
                    con.prepareStatement("create table IFPTable(CreateUser VARCHAR(36), ItemFrame VARCHAR(36)); ").execute();
                } else {
                    try {
                        con.prepareStatement("SELECT ItemFlame FROM IFPTable").executeQuery();
                    } catch (SQLException e){
                        try {
                            con.prepareStatement("ALTER TABLE IFPTable RENAME TO IFPTable_old; ").execute();
                            con.prepareStatement("create table IFPTable(CreateUser VARCHAR(36), ItemFrame VARCHAR(36)); ").execute();
                        } catch (Exception ex){
                            // ex.printStackTrace();
                        }
                    }
                }

            } catch (SQLException e){
                if (getConfig().getBoolean("errorPrint")){
                    getLogger().info(ChatColor.RED + "エラーが発生しました。");
                    e.printStackTrace();
                }
            }
        }

        if (con != null){
            getServer().getPluginManager().registerEvents(new FrameListener(this, con), this);
            new ItemframeTimer(this, con).runTaskLaterAsynchronously(this, 0L);
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
        } else {
            try {
                con.prepareStatement("SELECT ItemFlame FROM IFPTable").execute();
            } catch (Exception e){
                try {
                    con.prepareStatement("ALTER TABLE IFPTable RENAME TO IFPTable_old; ").execute();
                    con.prepareStatement("create table IFPTable(CreateUser VARCHAR(36), ItemFrame VARCHAR(36)); ").execute();
                } catch (Exception ex){
                    // ex.printStackTrace();
                }
            }
        }
    }
}
