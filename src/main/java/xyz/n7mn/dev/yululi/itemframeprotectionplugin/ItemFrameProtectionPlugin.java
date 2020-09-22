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

            boolean useMySQL = getConfig().getBoolean("useMySQL");

            if (useMySQL){
                final String MySQLServer = getConfig().getString("MySQLServer");
                final String MySQLUsername = getConfig().getString("MySQLUsername");
                final String MySQLPassword = getConfig().getString("MySQLPassword");
                final String MySQLDatabase = getConfig().getString("MySQLDatabase");
                final String MySQLOption = getConfig().getString("MySQLOption");
                con = DriverManager.getConnection("jdbc:mysql://" + MySQLServer + "/" + MySQLDatabase + MySQLOption, MySQLUsername, MySQLPassword);

                try {
                    PreparedStatement statement = con.prepareStatement("SELECT 1 FROM IFPTable LIMIT 1;");
                    statement.execute();
                    statement.close();
                } catch (Exception e){
                    try {
                        PreparedStatement statement = con.prepareStatement("CREATE TABLE `IFPTable` (\n" +
                                "  `CreateUser` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_ja_0900_as_cs_ks NOT NULL,\n" +
                                "  `ItemFrame` varchar(36) COLLATE utf8mb4_ja_0900_as_cs_ks NOT NULL\n" +
                                ")");
                        statement.execute();
                        statement.close();
                    } catch (Exception ex){
                        PreparedStatement statement = con.prepareStatement(" RENAME TABLE `IFPTable` TO `IFPTable_old`; ");
                        statement.execute();
                        statement.close();
                        statement = con.prepareStatement("CREATE TABLE `IFPTable` (\n" +
                                "  `CreateUser` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_ja_0900_as_cs_ks NOT NULL,\n" +
                                "  `ItemFrame` varchar(36) COLLATE utf8mb4_ja_0900_as_cs_ks NOT NULL\n" +
                                ")");
                        statement.execute();
                        statement.close();
                    }
                }

                try {
                    PreparedStatement statement = con.prepareStatement("SELECT 1 FROM IFPTable2 LIMIT 1;");
                    statement.execute();
                    statement.close();
                } catch (Exception e){
                    try {
                        PreparedStatement statement = con.prepareStatement("CREATE TABLE `IFPTable2` (\n" +
                                "  `DropUser` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_ja_0900_as_cs_ks NOT NULL,\n" +
                                "  `ItemUUID` varchar(36) COLLATE utf8mb4_ja_0900_as_cs_ks NOT NULL\n" +
                                ")");
                        statement.execute();
                        statement.close();
                    } catch (Exception ex){
                        PreparedStatement statement = con.prepareStatement(" RENAME TABLE `IFPTable2` TO `IFPTable2_old`; ");
                        statement.execute();
                        statement.close();
                        statement = con.prepareStatement("CREATE TABLE `IFPTable2` (\n" +
                                "  `DropUser` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_ja_0900_as_cs_ks NOT NULL,\n" +
                                "  `ItemUUID` varchar(36) COLLATE utf8mb4_ja_0900_as_cs_ks NOT NULL\n" +
                                ")");
                        statement.execute();
                        statement.close();
                    }
                }

            } else {
                String pass = "./" + getDataFolder().getPath() + "/data.db";
                con = DriverManager.getConnection("jdbc:sqlite:"+pass);
                con.setAutoCommit(true);

                PreparedStatement statement1 = con.prepareStatement("SELECT COUNT(*) FROM sqlite_master WHERE TYPE='table' AND name='IFPTable';");
                ResultSet set1 = statement1.executeQuery();
                PreparedStatement statement2 = con.prepareStatement("SELECT COUNT(*) FROM sqlite_master WHERE TYPE='table' AND name='IFPTable2';");
                ResultSet set2 = statement2.executeQuery();

                if (set1.next() && set1.getInt("COUNT(*)") == 0){
                    PreparedStatement statement = con.prepareStatement("CREATE TABLE IFPTable (CreateUser TEXT NOT NULL, ItemFrame TEXT NOT NULL)");
                    statement.execute();
                    statement.close();
                } else {
                    try {
                        PreparedStatement statement = con.prepareStatement("SELECT ItemFrame FROM IFPTable");
                        statement.execute();
                        statement.close();
                    } catch (Exception e){
                        PreparedStatement statement = con.prepareStatement("ALTER TABLE IFPTable RENAME TO IFPTable_old");
                        statement.execute();
                        statement.close();
                        statement = con.prepareStatement("CREATE TABLE IFPTable (CreateUser TEXT NOT NULL, ItemFrame TEXT NOT NULL)");
                        statement.execute();
                        statement.close();
                    }
                }

                if (set2.next() && set2.getInt("COUNT(*)") == 0){
                    PreparedStatement statement = con.prepareStatement("CREATE TABLE IFPTable2 (DropUser TEXT NOT NULL, ItemUUID TEXT NOT NULL)");
                    statement.execute();
                    statement.close();
                } else {
                    try {
                        PreparedStatement statement = con.prepareStatement("SELECT ItemUUID FROM IFPTable2");
                        statement.execute();
                        statement.close();
                    } catch (Exception e){
                        PreparedStatement statement = con.prepareStatement("ALTER TABLE IFPTable2 RENAME TO IFPTable2_old");
                        statement.execute();
                        statement.close();
                        statement = con.prepareStatement("CREATE TABLE IFPTable2 (DropUser TEXT NOT NULL, ItemUUID TEXT NOT NULL)");
                        statement.execute();
                        statement.close();
                    }
                }

                statement1.close();
                statement2.close();
            }

            getServer().getPluginManager().registerEvents(new FrameListener(this, con),this);

            new ItemFrameTimer(this, con).runTaskLaterAsynchronously(this, 0L);

            getCommand("ifp").setExecutor(new ItemFrameCommand());
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
                        boolean flag = true;
                        while (flag){
                            try {
                                con.close();
                            } catch (Exception e){
                                if (getConfig().getBoolean("errorPrint")){
                                    getLogger().info(ChatColor.RED + "エラーを検知しました。");
                                    e.printStackTrace();
                                }
                            }

                            flag = false;
                        }
                    }
            ).start();
        }
    }
}
