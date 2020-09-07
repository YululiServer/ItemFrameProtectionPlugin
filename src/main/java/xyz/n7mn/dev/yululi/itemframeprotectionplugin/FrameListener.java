package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.Plugin;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.api.FrameData;

import java.sql.Connection;
import java.util.List;


class FrameListener implements Listener {

    private Plugin plugin;
    private Connection con;
    private FrameData dataAPI;

    @Deprecated
    public FrameListener(){

    }

    public FrameListener(Plugin plugin, Connection con){
        this.plugin = plugin;
        this.con = con;
        this.dataAPI = new FrameData(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerInteractEntityEvent (PlayerInteractEntityEvent e){
        // スネークしながら右クリックでロックしたり解除するようにする。
        if (e.getRightClicked().getType() == EntityType.ITEM_FRAME){

            FrameData data = new FrameData(plugin).getData(con, e.getRightClicked().getUniqueId());
            if (e.getPlayer().isSneaking()){
                // System.out.println();

                ItemFrame frame = (ItemFrame) e.getRightClicked();
                if (data == null && e.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR){
                    if (frame.getItem().getType() == Material.AIR){
                        frame.setItem(e.getPlayer().getInventory().getItemInMainHand());
                    }

                    new FrameData(plugin).setData(con, e.getPlayer().getUniqueId(), e.getRightClicked().getUniqueId());
                    e.getPlayer().sendMessage(ChatColor.GREEN + "額縁を保護しました。 もう一度スネークしながら右クリックで保護を解除できます。");
                    e.setCancelled(true);
                }else if (data != null && data.getCreateUser().equals(e.getPlayer().getUniqueId())){
                    new FrameData(plugin).setData(con, e.getPlayer().getUniqueId(), e.getRightClicked().getUniqueId());
                    e.getPlayer().sendMessage(ChatColor.GREEN + "額縁を保護解除しました。 もう一度スネークしながら右クリックで再度保護できます。");
                    e.setCancelled(true);
                }
            }

            if (data != null && !e.getPlayer().hasPermission("ifp.op")){
                if (!data.getCreateUser().equals(e.getPlayer().getPlayer().getUniqueId())){
                    e.getPlayer().sendMessage(ChatColor.GREEN + "この額縁は保護されています。");
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void BlockBreakEvent (HangingBreakEvent e){
        // 額縁壊されるとき
        if (e.getEntity().getType() == EntityType.ITEM_FRAME){
            FrameData data = new FrameData(plugin).getData(con, e.getEntity().getUniqueId());
            if (data != null){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityDamageEvent (EntityDamageEvent e){
        // 額縁の中身消されたとき
        if (e.getEntity().getType() == EntityType.ITEM_FRAME){
            FrameData data = new FrameData(plugin).getData(con, e.getEntity().getUniqueId());
            if (data != null){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityDamageEvent (EntityDamageByEntityEvent e){
        // 額縁の中身を取り出されるとき
        if (e.getEntity().getType() == EntityType.ITEM_FRAME){
            FrameData data = new FrameData(plugin).getData(con, e.getEntity().getUniqueId());
            if (data != null){
                e.setCancelled(true);
            }
        }
    }
/*
    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityDeathEvent (EntityDeathEvent e){
        if (e.getEntity().getType() == EntityType.ITEM_FRAME){
            e.setCancelled(true);
        }
    }
*/
}
