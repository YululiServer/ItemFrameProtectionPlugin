package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

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
                    PreparedStatement statement1 = con.prepareStatement("CREATE TABLE `IFPTable` (\n" +
                            "  `CreateUser` varchar(36) COLLATE utf8mb4_ja_0900_as_cs_ks NOT NULL,\n" +
                            "  `X` int NOT NULL,\n" +
                            "  `Y` int NOT NULL,\n" +
                            "  `Z` int NOT NULL\n" +
                            ")");
                    statement1.execute();
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
                    PreparedStatement statement = con.prepareStatement("create table IFPTable(CreateUser VARCHAR(36), X INTEGER, Y INTEGER, Z INTEGER); ");
                    statement.execute();
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
