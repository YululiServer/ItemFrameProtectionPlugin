package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DebugTimer extends BukkitRunnable {
    private final DataAPI api;
    private final Plugin plugin;

    public DebugTimer(DataAPI api, Plugin plugin){
        this.api = api;
        this.plugin = plugin;
    }

    @Override
    public void run() {

        System.out.println(api.getCacheCount());

        new DebugTimer(api, plugin).runTaskLaterAsynchronously(plugin, 20L);
    }
}
