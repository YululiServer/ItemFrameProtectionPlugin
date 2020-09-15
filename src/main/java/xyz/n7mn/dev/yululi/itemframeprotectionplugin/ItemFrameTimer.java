package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class ItemFrameTimer extends BukkitRunnable {

    private final Plugin plugin;
    private final Connection con;

    public ItemFrameTimer(Plugin plugin, Connection con){
        this.plugin = plugin;
        this.con = con;
    }

    @Override
    public void run() {

        new Thread(()->{
            try {
                if (con != null){

                    PreparedStatement statement = con.prepareStatement("SELECT * FROM IFPTable");
                    List<FrameData> data = new ArrayList<>();
                    ResultSet set = statement.executeQuery();

                    while (set.next()){
                        FrameData frameData = new FrameData(UUID.fromString(set.getString("CreateUser")), UUID.fromString(set.getString("ItemFrame")));
                        data.add(frameData);
                    }

                    if (data.size() > 0){
                        for (FrameData temp : data){
                            final List<World> worlds = Bukkit.getServer().getWorlds();
                            boolean flag = false;
                            for (World world : worlds){
                                final List<Entity> entities = world.getEntities();
                                for (Entity entity : entities){
                                    if (entity.getUniqueId().equals(temp.getItemFrame())){
                                        flag = true;
                                        break;
                                    }
                                }
                                if (flag){
                                    break;
                                }
                            }
                            if (!flag){
                                PreparedStatement statement1 = con.prepareStatement("DELETE FROM `IFPTable` WHERE `CreateUser` = ? AND `ItemFrame` = ?");
                                statement1.setString(1, temp.getCreateUser().toString());
                                statement1.setString(2, temp.getItemFrame().toString());
                                statement1.execute();
                            }
                        }
                    }
                }
            } catch (Exception e){
                if (plugin.getConfig().getBoolean("errorPrint")){
                    plugin.getLogger().info(ChatColor.RED + "エラーを検知しました");
                    e.printStackTrace();
                }
            }
        }).start();

        new ItemFrameTimer(plugin, con).runTaskLaterAsynchronously(plugin, 20L);
    }
}
