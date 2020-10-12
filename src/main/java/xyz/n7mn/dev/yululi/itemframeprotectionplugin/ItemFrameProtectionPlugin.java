package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.*;
import java.util.List;
import java.util.UUID;

public final class ItemFrameProtectionPlugin extends JavaPlugin {

    private ItemFrameData data = null;
    private Connection con = null;
    private BukkitTask bukkitTask;
    private BukkitTask bukkitTask2;

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
                        if (resultSet.next() && resultSet.getString("ItemFrame").length() == 0){
                            statement.close();
                            throw new SQLException();
                        }
                        statement.close();
                    } catch (SQLException ex){
                        try {
                            PreparedStatement statement1 = con.prepareStatement("RENAME TABLE IFPTable TO IFPTable_old;");
                            statement1.execute();
                            statement1.close();
                            PreparedStatement statement2 = con.prepareStatement("CREATE TABLE `IFPTable` (\n" +
                                    "  `CreateUser` varchar(36) NOT NULL,\n" +
                                    "  `ItemFrame` varchar(36) NOT NULL\n" +
                                    ")");
                            statement2.execute();
                            statement2.close();
                        } catch (SQLException ex1){
                            // ex1.printStackTrace();
                        }
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
                        try {
                            PreparedStatement statement1 = con.prepareStatement("RENAME TABLE IFPTable2 TO IFPTable2_old;");
                            statement1.execute();
                            statement1.close();
                            PreparedStatement statement2 = con.prepareStatement("CREATE TABLE `IFPTable2` (\n" +
                                    "  `DropUser` varchar(36) NOT NULL,\n" +
                                    "  `ItemUUID` varchar(36) NOT NULL\n" +
                                    ")");
                            statement2.execute();
                            statement2.close();
                        } catch (SQLException ex1){
                            // ex1.printStackTrace();
                        }
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
                        try {
                            PreparedStatement statement1 = con.prepareStatement("ALTER TABLE IFPTable RENAME TO IFPTable_old;");
                            statement1.execute();
                            statement1.close();

                            PreparedStatement statement2 = con.prepareStatement("CREATE TABLE IFPTable (CreateUser TEXT NOT NULL, ItemFrame TEXT NOT NULL)");
                            statement2.execute();
                            statement2.close();
                        } catch (SQLException exc){
                        // exc.printStackTrace();
                        }

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
                        if (set.next() && set.getString("ItemUUID").length() == 0){
                            statement.close();
                            throw new SQLException();
                        }
                        statement.close();
                    } catch (SQLException ex){
                        try {
                            PreparedStatement statement1 = con.prepareStatement("ALTER TABLE IFPTable2 RENAME TO IFPTable2_old;");
                            statement1.execute();
                            statement1.close();

                            PreparedStatement statement2 = con.prepareStatement("CREATE TABLE IFPTable2 (DropUser TEXT NOT NULL, ItemUUID TEXT NOT NULL)");
                            statement2.execute();
                            statement2.close();
                        } catch (SQLException exc){
                            // exc.printStackTrace();
                        }
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
            bukkitTask2 = new ItemFrameAutoDeleteTimer(this, data).runTaskLaterAsynchronously(this, 0L);

            getCommand("ifp").setExecutor(new ItemFrameCommand(this, data));
            getCommand("ifp").setTabCompleter(new ItemFrameCommandTab());

            getLogger().info("Started ItemFrameProtectionPlugin Ver "+getDescription().getVersion()+"!!");

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
        new BukkitRunnable() {
            @Override
            public void run() {
                if (con != null) {
                    try {
                        bukkitTask.cancel();
                        bukkitTask2.cancel();

                        try {
                            List<FrameData> itemFrameList = data.getItemFrameList();
                            // System.out.println("Debug : LockFrame : " + itemFrameList.size());
                            synchronized (itemFrameList) {
                                for (FrameData data : itemFrameList) {
                                    PreparedStatement statement = con.prepareStatement("SELECT COUNT(*) FROM IFPTable WHERE ItemFrame = ?");
                                    statement.setString(1, data.getItemFrame().toString());
                                    ResultSet set = statement.executeQuery();
                                    if (set.next()) {
                                        if (set.getInt("COUNT(*)") == 0) {
                                            statement.close();
                                            PreparedStatement statement1 = con.prepareStatement("INSERT INTO `IFPTable` (`CreateUser`, `ItemFrame`) VALUES (?, ?);");
                                            statement1.setString(1, data.getCreateUser().toString());
                                            statement1.setString(2, data.getItemFrame().toString());
                                            statement1.execute();
                                            statement1.close();
                                        }
                                    }
                                }
                            }
                            // System.out.println("Debug : LockFrame : " + itemFrameList.size());


                            List<DropData> dropList = data.getDropList();
                            // System.out.println("Debug : dropItem : " + dropList.size());
                            synchronized (dropList) {
                                for (DropData data : dropList) {
                                    PreparedStatement statement = con.prepareStatement("SELECT COUNT(*) FROM IFPTable2 WHERE ItemUUID = ?");
                                    statement.setString(1, data.getItemUUID().toString());
                                    ResultSet set = statement.executeQuery();
                                    if (set.next()) {
                                        if (set.getInt("COUNT(*)") == 0) {
                                            statement.close();
                                            PreparedStatement statement1 = con.prepareStatement("INSERT INTO `IFPTable2` (`DropUser`, `ItemUUID`) VALUES (?, ?);");
                                            statement1.setString(1, data.getDropUser().toString());
                                            statement1.setString(2, data.getItemUUID().toString());
                                            statement1.execute();
                                            statement1.close();
                                        }
                                    }
                                }
                            }


                            // ここからゴミデータお掃除

                            List<World> worlds = getServer().getWorlds();
                            int i = 0;
                            for (World world : worlds) {
                                i = i + world.getEntities().size();
                            }
                            if (i != 0) {
                                PreparedStatement statement = con.prepareStatement("SELECT * FROM IFPTable");
                                ResultSet set = statement.executeQuery();
                                while (set.next()) {
                                    FrameData itemFrame = data.getItemFrame(UUID.fromString(set.getString("CreateUser")), UUID.fromString(set.getString("ItemFrame")));
                                    if (itemFrame == null) {
                                        PreparedStatement statement1 = con.prepareStatement("DELETE FROM `IFPTable` WHERE `CreateUser` = ? AND `ItemFrame` = ?");
                                        statement1.setString(1, set.getString("CreateUser"));
                                        statement1.setString(2, set.getString("ItemFrame"));
                                        statement1.execute();
                                        statement1.close();
                                    }
                                }
                                statement.close();

                                PreparedStatement statement2 = con.prepareStatement("SELECT * FROM IFPTable2");
                                ResultSet set2 = statement2.executeQuery();
                                while (set2.next()) {
                                    FrameData itemFrame = data.getItemFrame(UUID.fromString(set2.getString("DropUser")), UUID.fromString(set2.getString("ItemUUID")));
                                    if (itemFrame == null) {
                                        PreparedStatement statement1 = con.prepareStatement("DELETE FROM `IFPTable2` WHERE `DropUser` = ? AND `ItemUUID` = ?");
                                        statement1.setString(1, set2.getString("DropUser"));
                                        statement1.setString(2, set2.getString("ItemUUID"));
                                        statement1.execute();
                                        statement1.close();
                                    }
                                }
                                statement2.close();
                            }

                            con.close();
                        } catch (Exception e) {
                            if (getConfig().getBoolean("errorPrint")) {
                                getLogger().info(ChatColor.RED + "エラーを検知しました。");
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        if (getConfig().getBoolean("errorPrint")) {
                            getLogger().info(ChatColor.RED + "エラーを検知しました。");
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.runTaskLaterAsynchronously(this, 0L);

    }
}
