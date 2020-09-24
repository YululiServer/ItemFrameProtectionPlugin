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

            } else {
                String pass = "./" + getDataFolder().getPath() + "/data.db";
                con = DriverManager.getConnection("jdbc:sqlite:" + pass);
                con.setAutoCommit(true);
            }

            PreparedStatement statement = con.prepareStatement("SELECT * FROM IFPTable");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                final String createUser = resultSet.getString("CreateUser");
                final String itemFrame = resultSet.getString("ItemFrame");

                FrameData frameData = new FrameData(UUID.fromString(createUser), UUID.fromString(itemFrame));
                data.addFrameList(frameData);
            }

            resultSet.close();
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
