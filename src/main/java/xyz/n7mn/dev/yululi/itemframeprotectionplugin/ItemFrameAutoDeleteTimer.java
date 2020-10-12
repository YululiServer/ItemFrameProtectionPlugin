package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

class ItemFrameAutoDeleteTimer extends BukkitRunnable implements Cancellable {

    private ItemFrameData dataAPI;
    private final Plugin plugin;
    private boolean flag = true;

    public ItemFrameAutoDeleteTimer(Plugin plugin, ItemFrameData data){
        this.dataAPI = data;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        List<FrameData> itemFrameList = dataAPI.getItemFrameList();

        // System.out.println("Debug : "+ itemFrameList.size());
        synchronized(itemFrameList) {
            for (FrameData data : itemFrameList){
                boolean flag = false;
                final List<World> worlds = Bukkit.getServer().getWorlds();
                for (World world : worlds){
                    if (world.getEntities().size() == 0){
                        flag = true;
                        break;
                    }

                    for (Entity entity : world.getEntities()){
                        if (entity != null){
                            flag = true;
                            break;
                        }
                    }

                    if (flag){
                        break;
                    }
                }

                if (!flag){
                    dataAPI.delFrameList(data);
                }
            }
        }

        // System.out.println("Debug : "+ itemFrameList.size());


        List<DropData> dropList = dataAPI.getDropList();
        synchronized(dropList) {
            for (DropData drop : dropList){
                final List<World> worlds = Bukkit.getServer().getWorlds();
                for (World world : worlds){
                    if (world.getEntities().size() == 0){
                        break;
                    }

                    Entity entity = world.getEntity(drop.getItemUUID());
                    if (entity == null){
                        dataAPI.delDropList(drop);
                    }
                }
            }
        }

        if (flag){
            new ItemFrameAutoDeleteTimer(plugin, dataAPI).runTaskLaterAsynchronously(plugin, 200L);
        }
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.flag = !cancel;
    }
}
