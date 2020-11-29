package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class ItemFrameProtectionPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        String MySQLServer = getConfig().getString("MySQLServer");
        String MySQLUsername = getConfig().getString("MySQLUsername");
        String MySQLPassword = getConfig().getString("MySQLPassword");
        String MySQLDatabase = getConfig().getString("MySQLDatabase");
        String MySQLOption = getConfig().getString("MySQLOption");

        getLogger().info("Started ItemFrameProtectionPlugin Ver "+getDescription().getVersion()+"!!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Disabled ItemFrameProtectionPlugin Ver "+getDescription().getVersion()+"!!");
    }
}
