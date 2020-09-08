package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.api.FrameData;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;


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
            } else {
                if (e.getPlayer().getGameMode() == GameMode.SURVIVAL){
                    ItemFrame frame = (ItemFrame) e.getRightClicked();
                    if (frame.getItem().getType() == Material.AIR){
                        ItemStack hand = e.getPlayer().getInventory().getItemInMainHand();
                        int handCount = -1;
                        for (int i = 0; i < e.getPlayer().getInventory().getSize(); i++){
                            if (Objects.requireNonNull(e.getPlayer().getInventory().getItem(i)).getType() == hand.getType() && Objects.requireNonNull(e.getPlayer().getInventory().getItem(i)).getItemMeta() == hand.getItemMeta()){
                                handCount = i;
                                break;
                            }
                        }
                        int amount = hand.getAmount();
                        // System.out.println("あ : " + amount);
                        hand.add();
                        if (amount != hand.getAmount()){
                            if (handCount != -1){
                                e.getPlayer().getInventory().setItem(handCount, e.getPlayer().getInventory().getItemInMainHand());
                            }
                        }
                    }
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
            } else {
                ItemFrame frame = (ItemFrame) e.getEntity();
                if (frame.getItem().getType() != Material.AIR){
                    ItemStack stack = new ItemStack(Material.AIR);
                    frame.setItem(stack);
                }
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
            } else {
                ItemFrame frame = (ItemFrame) e.getEntity();
                ItemStack stack = new ItemStack(Material.AIR);
                frame.setItem(stack);
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
            } else {
                ItemFrame frame = (ItemFrame) e.getEntity().getVehicle();
                if (frame != null && frame.getItem().getType() != Material.AIR){
                    ItemStack stack = new ItemStack(Material.AIR);
                    frame.setItem(stack);
                }
            }
        }
    }

}
