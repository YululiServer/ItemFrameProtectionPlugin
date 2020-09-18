package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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

                try {
                    con.prepareStatement("SELECT 1 FROM IFPTable LIMIT 1;").execute();
                } catch (Exception e){
                    con.prepareStatement("CREATE TABLE `IFPTable` (\n" +
                            "  `CreateUser` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_ja_0900_as_cs_ks NOT NULL,\n" +
                            "  `ItemFrame` varchar(36) COLLATE utf8mb4_ja_0900_as_cs_ks NOT NULL\n" +
                            ")").execute();
                }

            } else {
                String pass = "./" + getDataFolder().getPath() + "/data.db";
                con = DriverManager.getConnection("jdbc:sqlite:"+pass);
                con.setAutoCommit(true);

                PreparedStatement statement = con.prepareStatement("SELECT COUNT(*) FROM sqlite_master WHERE TYPE='table' AND name='IFPTable';");
                ResultSet set = statement.executeQuery();
                if (set.next()){
                    if (set.getInt("COUNT(*)") == 0){
                        PreparedStatement statement1 = con.prepareStatement("CREATE TABLE IFPTable (CreateUser TEXT NOT NULL, ItemFrame TEXT NOT NULL)");
                        statement1.execute();
                    } else {
                        try {
                            con.prepareStatement("SELECT ItemFlame FROM IFPTable").execute();
                        } catch (Exception e){
                            con.prepareStatement("ALTER TABLE IFPTable RENAME TO IFPTable_old").execute();
                            con.prepareStatement("CREATE TABLE IFPTable (CreateUser TEXT NOT NULL, ItemFrame TEXT NOT NULL)").execute();
                        }
                    }
                }

            }

            getServer().getPluginManager().registerEvents(new FrameListener(this, con),this);

            new ItemFrameTimer(this, con).runTaskLaterAsynchronously(this, 0L);
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
            ).start();
        }
    }
}
