package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;

class ProtectListener implements Listener {

    private Connection con;
    private final Plugin plugin;

    public ProtectListener(Connection con, Plugin plugin){

        this.con = con;
        this.plugin = plugin;

    }

}
