package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;

public final class ItemFrameProtectionPlugin extends JavaPlugin {

    private Connection con = null;

    @Override
    public void onEnable() {
        // Plugin startup logic

        try {




            getLogger().info("Started ItemFrameProtectionPlugin Ver "+getDescription().getVersion()+"!!");

        } catch (Exception e){

            onDisable();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        getLogger().info("Disabled ItemFrameProtectionPlugin Ver "+getDescription().getVersion()+"!!");
    }
}
