package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

class ItemFrameAutoDeleteTimer extends BukkitRunnable {

    private ItemFrameData dataAPI;
    private final Plugin plugin;

    public ItemFrameAutoDeleteTimer(Plugin plugin, ItemFrameData data){
        this.dataAPI = data;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        List<FrameData> itemFrameList = dataAPI.getItemFrameList();

        for (FrameData data : itemFrameList){
            final List<World> worlds = Bukkit.getServer().getWorlds();
            for (World world : worlds){
                Entity entity = world.getEntity(data.getItemFrame());
                if (entity == null){
                    dataAPI.delFrameList(data);
                }
            }
        }

        List<DropData> dropList = dataAPI.getDropList();

        for (DropData drop : dropList){
            final List<World> worlds = Bukkit.getServer().getWorlds();
            for (World world : worlds){
                Entity entity = world.getEntity(drop.getItemUUID());
                if (entity == null){
                    dataAPI.delDropList(drop);
                }
            }
        }

        new ItemFrameAutoDeleteTimer(plugin, dataAPI).runTaskLaterAsynchronously(plugin, 200L);
    }
}
