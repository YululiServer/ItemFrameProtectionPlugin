package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class ItemFrameProtectionPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new FrameListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
