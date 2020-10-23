package xyz.n7mn.dev.yululi.itemframeprotectionplugin;


import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.DataAPI;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.DropItemData;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.FrameData;

import java.util.Date;
import java.util.List;
import java.util.Map;

class ItemFrameListener implements Listener {

    final private DataAPI api;
    final private Plugin plugin = Bukkit.getPluginManager().getPlugin("ItemFrameProtectionPlugin");

    public ItemFrameListener(DataAPI api){
        this.api = api;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerInteractEntityEvent (PlayerInteractEntityEvent e){

        if (!(e.getRightClicked() instanceof ItemFrame)){
            return;
        }

        // スネークしながら右クリックでロックしたり解除するようにする。
        ItemFrame frame = (ItemFrame) e.getRightClicked();
        Player player = e.getPlayer();
        try {

            boolean foundFlag = false;
            FrameData foundData = null;

            List<FrameData> list = api.getListByFrameData(true);
            for (FrameData data : list){
                if (data.getItemFrameUUID().equals(frame.getUniqueId())){
                    foundFlag = true;
                    foundData = data;
                    break;
                }
            }

            if (player.isSneaking()){

                if (foundFlag){
                    // 保護解除

                    if (foundData.getProtectUser().equals(player.getUniqueId())){
                        api.deleteTableByFrame(frame.getUniqueId());
                        player.sendMessage(ChatColor.GREEN + "保護解除しました。 もう一度保護するにはスニークしながら右クリックしてください。");
                    } else {

                        if (player.hasPermission("ifp.op")){
                            api.deleteTableByFrame(frame.getUniqueId());
                            player.sendMessage(ChatColor.YELLOW + "保護を代理解除しました。 もう一度保護するにはスニークしながら右クリックしてください。");
                        } else {
                            player.sendMessage(ChatColor.RED + "他の人が保護しています。");
                        }

                    }
                } else {
                    // 新規保護
                    if (frame.getItem().getType() == Material.AIR){
                        frame.setItem(player.getInventory().getItemInMainHand());
                    }

                    FrameData data = new FrameData();

                    data.setItemFrameUUID(frame.getUniqueId());
                    data.setFrameItem(frame.getItem());
                    data.setProtectUser(player.getUniqueId());
                    data.setCreateDate(new Date());
                    data.setActive(true);

                    api.addItemFrame(data);
                    player.sendMessage(ChatColor.GREEN + "保護しました。 保護解除するにはスニークしながら右クリックしてください。");
                }

                e.setCancelled(true);
            } else {

                if (foundFlag){
                    e.setCancelled(true);
                }

            }

        } catch (Exception ex){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                ex.printStackTrace();
            }
        }


    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void BlockBreakEvent (HangingBreakEvent e){

        if (!(e.getEntity() instanceof ItemFrame)){
            return;
        }

        // 額縁壊されるとき
        ItemFrame frame = (ItemFrame) e.getEntity();
        List<FrameData> list = api.getListByFrameData(true);
        for (FrameData data : list) {
            if (data.getItemFrameUUID().equals(frame.getUniqueId())) {
                e.setCancelled(true);
                return;
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityDamageEvent (EntityDamageEvent e){

        if (!(e.getEntity() instanceof ItemFrame)){
            return;
        }
        // 額縁の中身消されたとき
        ItemFrame frame = (ItemFrame) e.getEntity();
        List<FrameData> list = api.getListByFrameData(true);
        for (FrameData data : list) {
            if (data.getItemFrameUUID().equals(frame.getUniqueId())) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.VERY_LOWEST)
    public void EntityDamageByEntityEvent (EntityDamageByEntityEvent e){

        if (!(e.getEntity() instanceof ItemFrame)){
            return;
        }
        // 額縁の中身を取り出されるとき
        ItemFrame frame = (ItemFrame) e.getEntity();
        List<FrameData> list = api.getListByFrameData(true);
        for (FrameData data : list) {
            if (data.getItemFrameUUID().equals(frame.getUniqueId())) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerDropItemEvent (PlayerDropItemEvent e){

        Item itemDrop = e.getItemDrop();
        Location location = itemDrop.getLocation();

        DropItemData data = new DropItemData();
        data.setDropItemUUID(itemDrop.getUniqueId());
        data.setWorldUUID(location.getWorld().getUID());
        data.setDropDate(new Date());
        data.setDropUser(e.getPlayer().getUniqueId());
        api.addDropItem(data);

    }



    private boolean ItemStackEqual(ItemStack item1, ItemStack item2){

        if (item1 == null && item2 == null){
            return true;
        }

        if (item1 != null && item2 != null && item1.getType() == item2.getType()){

            if (item1.getEnchantments().size() == item2.getEnchantments().size()){

                boolean flag = true;

                for (Map.Entry<Enchantment, Integer> e1 : item1.getEnchantments().entrySet()){
                    Integer value = e1.getValue();

                    for (Map.Entry<Enchantment, Integer> e2 : item2.getEnchantments().entrySet()){
                        flag = value.equals(e2.getValue());
                    }
                }

                return flag;
            }

        }
        return false;
    }

}
