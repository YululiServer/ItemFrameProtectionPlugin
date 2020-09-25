package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.*;
import java.util.UUID;

public final class ItemFrameProtectionPlugin extends JavaPlugin {

    private ItemFrameData data = null;
    private Connection con = null;
    private BukkitTask bukkitTask;

    @Override
    public void onEnable() {
        // Plugin startup logic

        try {

            data = new ItemFrameData(this);

            if (getConfig().getBoolean("useMySQL")){
                final String ServerName = getConfig().getString("MySQLServer");
                final String ServerOption = getConfig().getString("MySQLOption");
                final String Database = getConfig().getString("MySQLDatabase");
                final String Username = getConfig().getString("MySQLUsername");
                final String Password = getConfig().getString("MySQLPassword");

                con = DriverManager.getConnection("jdbc:mysql://" + ServerName + "/" + Database + ServerOption, Username, Password);

                try {
                    PreparedStatement statement = con.prepareStatement("CREATE TABLE `IFPTable` (\n" +
                            "  `CreateUser` varchar(36) NOT NULL,\n" +
                            "  `ItemFrame` varchar(36) NOT NULL\n" +
                            ")");
                    statement.execute();
                    statement.close();
                } catch (SQLException e){
                    try {
                        PreparedStatement statement = con.prepareStatement("SELECT ItemFrame FROM IFPTable");
                        ResultSet resultSet = statement.executeQuery();
                        if (!resultSet.next()){
                            statement.close();
                            throw new SQLException();
                        }
                        statement.close();
                    } catch (SQLException ex){
                        PreparedStatement statement1 = con.prepareStatement("RENAME TABLE IFPTable TO IFPTable_old;");
                        statement1.execute();
                        statement1.close();
                        PreparedStatement statement2 = con.prepareStatement("CREATE TABLE `IFPTable` (\n" +
                                "  `CreateUser` varchar(36) NOT NULL,\n" +
                                "  `ItemFrame` varchar(36) NOT NULL\n" +
                                ")");
                        statement2.execute();
                        statement2.close();
                    }


                }

                try {
                    PreparedStatement statement = con.prepareStatement("CREATE TABLE `IFPTable2` (\n" +
                            "  `DropUser` varchar(36) NOT NULL,\n" +
                            "  `ItemUUID` varchar(36) NOT NULL\n" +
                            ")");
                    statement.execute();
                    statement.close();
                } catch (SQLException e){
                    try {
                        PreparedStatement statement = con.prepareStatement("SELECT ItemUUID FROM IFPTable2");
                        ResultSet resultSet = statement.executeQuery();
                        if (!resultSet.next()){
                            statement.close();
                            throw new SQLException();
                        }
                        statement.close();
                    } catch (SQLException ex){
                        PreparedStatement statement1 = con.prepareStatement("RENAME TABLE IFPTable2 TO IFPTable2_old;");
                        statement1.execute();
                        statement1.close();
                        PreparedStatement statement2 = con.prepareStatement("CREATE TABLE `IFPTable2` (\n" +
                                "  `DropUser` varchar(36) NOT NULL,\n" +
                                "  `ItemUUID` varchar(36) NOT NULL\n" +
                                ")");
                        statement2.execute();
                        statement2.close();
                    }
                }

            } else {
                String pass = "./" + getDataFolder().getPath() + "/data.db";
                con = DriverManager.getConnection("jdbc:sqlite:" + pass);
                con.setAutoCommit(true);

                try {
                    PreparedStatement statement = con.prepareStatement("CREATE TABLE IFPTable (CreateUser TEXT NOT NULL, ItemFrame TEXT NOT NULL)");
                    statement.execute();
                    statement.close();
                } catch (SQLException e){
                    try {
                        PreparedStatement statement = con.prepareStatement("SELECT ItemFrame FROM IFPTable");
                        ResultSet set = statement.executeQuery();
                        if (!set.next()){
                            statement.close();
                            throw new SQLException();
                        }
                        statement.close();
                    } catch (SQLException ex){
                        PreparedStatement statement1 = con.prepareStatement("ALTER TABLE IFPTable RENAME TO IFPTable_old;");
                        statement1.execute();
                        statement1.close();

                        PreparedStatement statement2 = con.prepareStatement("CREATE TABLE IFPTable (CreateUser TEXT NOT NULL, ItemFrame TEXT NOT NULL)");
                        statement2.execute();
                        statement2.close();
                    }

                }

                try {
                    PreparedStatement statement = con.prepareStatement("CREATE TABLE IFPTable2 (DropUser TEXT NOT NULL, ItemUUID TEXT NOT NULL)");
                    statement.execute();
                    statement.close();
                } catch (SQLException e){
                    try {
                        PreparedStatement statement = con.prepareStatement("SELECT ItemUUID FROM IFPTable2");
                        ResultSet set = statement.executeQuery();
                        if (!set.next()){
                            statement.close();
                            throw new SQLException();
                        }
                        statement.close();
                    } catch (SQLException ex){
                        PreparedStatement statement1 = con.prepareStatement("ALTER TABLE IFPTable2 RENAME TO IFPTable2_old;");
                        statement1.execute();
                        statement1.close();

                        PreparedStatement statement2 = con.prepareStatement("CREATE TABLE IFPTable2 (DropUser TEXT NOT NULL, ItemUUID TEXT NOT NULL)");
                        statement2.execute();
                        statement2.close();
                    }

                }

            }

            PreparedStatement statement = con.prepareStatement("SELECT * FROM IFPTable");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                final String createUser = resultSet.getString("CreateUser");
                final String itemFrame = resultSet.getString("ItemFrame");

                FrameData frameData = new FrameData(UUID.fromString(createUser), UUID.fromString(itemFrame));
                data.addFrameList(frameData);
            }
            statement.close();

            getServer().getPluginManager().registerEvents(new FrameListener(this, data), this);

            bukkitTask = new ItemFrameTimer(this, con, data).runTaskLaterAsynchronously(this, 120L);
            new ItemFrameAutoDeleteTimer(this, data).runTaskLaterAsynchronously(this, 0L);

        } catch (Exception e){
            try {
                con.close();
                con = null;
            } catch (Exception ex){
                // ex.printStackTrace();
            }

            if (getConfig().getBoolean("errorPrint")) {
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
            try {
                new Thread(() -> {
                        try {
                            con.close();
                            bukkitTask.cancel();
                        } catch (Exception e){
                            // e.printStackTrace();
                        }
                    }
                ).start();
            } catch (Exception e){
                if (getConfig().getBoolean("errorPrint")) {
                    getLogger().info(ChatColor.RED + "エラーを検知しました。");
                    e.printStackTrace();
                }
            }
        }
    }
}
