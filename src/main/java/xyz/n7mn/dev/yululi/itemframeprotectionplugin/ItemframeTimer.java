package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.api.FrameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

class ItemframeTimer extends BukkitRunnable {

    Plugin plugin;
    Connection con;

    public ItemframeTimer(Plugin plugin, Connection con){
        this.plugin = plugin;
        this.con = con;
    }

    @Override
    public void run() {
        try {
            if (con != null && !con.isClosed()){
                List<World> worlds = Bukkit.getServer().getWorlds();
                List<FrameData> list = new FrameData(plugin).getDataList(con);
                int count = 0;
                for (World world : worlds){
                    List<Entity> entities = world.getEntities();
                    for (FrameData data : list){
                        boolean found = false;
                        for (Entity entity : entities){
                            if (entity.getType() != EntityType.ITEM_FRAME){
                                continue;
                            }

                            if (entity.getUniqueId().equals(data.getItemFrameUUID())){
                                found = true;
                                break;
                            }
                        }
                        if (!found){
                            new FrameData(plugin).setData(con, data.getCreateUser(), data.getItemFrameUUID());
                            count++;
                        }
                    }
                }

                // 20tick = 1sec
                // take : 6000L debug : 40L
                //
                new ItemframeTimer(plugin, con).runTaskLater(plugin, 6000L);
                //System.out.println(count + "件のゴミデータをお掃除完了。");
            }
        } catch (SQLException e){
            // e.printStackTrace();
        }
    }
}
