package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.api.FrameData;

import java.sql.Connection;


public class FrameListener implements Listener {

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

            FrameData data = new FrameData(plugin).getData(con, e.getRightClicked().getLocation());
            if (e.getPlayer().isSneaking()){
                dataAPI.setData(con, e.getPlayer().getUniqueId(), e.getRightClicked().getLocation());
                if (data == null){
                    e.getPlayer().sendMessage(ChatColor.GREEN + "額縁を保護しました。解除するにはもう一度スニーク状態で右クリックしてください。");
                } else {
                    e.getPlayer().sendMessage(ChatColor.GREEN + "額縁を保護解除しました。もう一度設定するにはもう一度スニーク状態で右クリックしてください。");
                }

                e.setCancelled(true);
            }

            if (data != null && !e.getPlayer().hasPermission("ifp.op")){
                if (!data.getCreateUser().equals(e.getPlayer().getPlayer().getUniqueId())){
                    e.getPlayer().sendMessage("この額縁は保護されています。");
                    e.setCancelled(true);
                }
            }
        }

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void BlockBreakEvent (HangingBreakEvent e){
        // 額縁壊されるとき
        if (e.getEntity().getType() == EntityType.ITEM_FRAME){
            FrameData data = new FrameData(plugin).getData(con, e.getEntity().getLocation());
            if (data != null){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityDamageEvent (EntityDamageEvent e){
        // 額縁の中身消されたとき
        if (e.getEntity().getType() == EntityType.ITEM_FRAME){
            FrameData data = new FrameData(plugin).getData(con, e.getEntity().getLocation());
            if (data != null){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityDamageEvent (EntityDamageByEntityEvent e){
        // 額縁の中身を取り出されるとき
        if (e.getEntity().getType() == EntityType.ITEM_FRAME){
            FrameData data = new FrameData(plugin).getData(con, e.getEntity().getLocation());
            if (data != null){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void HangingPlaceEvent (HangingPlaceEvent e){
        if (e.getBlock().getType() == Material.ITEM_FRAME){
            e.getPlayer().sendMessage(ChatColor.GOLD + "[IFP] 左Shiftを押しながら右クリックすると額縁を保護することができます。");
        }
    }
}
