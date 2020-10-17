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
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;


class FrameListener implements Listener {

    private final Plugin plugin;
    private ItemFrameData ItemFrameData;

    public FrameListener(Plugin plugin, ItemFrameData itemFrameData){
        this.plugin = plugin;
        this.ItemFrameData = itemFrameData;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerInteractEntityEvent (PlayerInteractEntityEvent e){

        if (!(e.getRightClicked() instanceof ItemFrame)){
            return;
        }


        // スネークしながら右クリックでロックしたり解除するようにする。
        try {
            final Player player = e.getPlayer();
            final Entity rightClicked = e.getRightClicked();

            final List<FrameData> list = ItemFrameData.getItemFrameList();
            FrameData foundData = null;
            boolean foundFlag = false;

            synchronized(list) {

                for (FrameData data : list){

                    if (data.getItemFrame().equals(rightClicked.getUniqueId())){
                        foundFlag = true;
                        foundData = data;
                        break;
                    }
                }

            }

            // 保護あり
            if (foundFlag){

                if (foundData.getCreateUser().equals(player.getUniqueId())){
                    // 保護した人
                    ItemFrameData.delFrameList(foundData);
                    player.sendMessage(ChatColor.GREEN + "額縁を保護解除しました。 もう一度スニークしながら右クリックで再度保護できます。");
                } else {
                    // 保護してない人
                    if (player.hasPermission("ifp.op")){
                        ItemFrameData.delFrameList(foundData);
                        player.sendMessage(ChatColor.YELLOW + "額縁を代理で保護解除しました。 もう一度スニークしながら右クリックで再度保護できます。");

                        e.setCancelled(true);
                        return;
                    }
                    player.sendMessage(ChatColor.RED + "この額縁は別の方が保護しています！");
                }
                e.setCancelled(true);

            } else {

                FrameData frameData = new FrameData(player.getUniqueId(), rightClicked.getUniqueId());
                ItemFrameData.addFrameList(frameData);

                player.sendMessage(ChatColor.GREEN + "額縁を保護しました。 もう一度スニークしながら右クリックで保護を解除できます。");
                e.setCancelled(true);

            }
            return;
        } catch (Exception ex){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました");
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
        try {
            if (e.getEntity() instanceof ItemFrame && ItemFrameData.getItemFrame(e.getEntity().getUniqueId()) == null){
                ItemFrame frame = (ItemFrame) e.getEntity();
                if (frame.getItem().getType() != Material.AIR){
                    ItemStack stack = new ItemStack(Material.AIR);
                    frame.setItem(stack);
                }
            } else if (e.getEntity() instanceof ItemFrame && ItemFrameData.getItemFrame(e.getEntity().getUniqueId()) != null) {
                e.setCancelled(true);
            }
        } catch (Exception ex){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました");
                ex.printStackTrace();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityDamageEvent (EntityDamageEvent e){

        if (!(e.getEntity() instanceof ItemFrame)){
            return;
        }

        // 額縁の中身消されたとき
        try {
            if (e.getEntity() instanceof ItemFrame && ItemFrameData.getItemFrame(e.getEntity().getUniqueId()) == null) {
                ItemFrame frame = (ItemFrame) e.getEntity();
                if (frame.getItem().getType() != Material.AIR){
                    ItemStack stack = new ItemStack(Material.AIR);
                    frame.setItem(stack);
                    e.setCancelled(true);
                }
            }
            if (e.getEntity() instanceof ItemFrame && ItemFrameData.getItemFrame(e.getEntity().getUniqueId()) != null){
                e.setCancelled(true);
            }
        } catch (Exception ex){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました");
                ex.printStackTrace();
            }
        }
    }

    @EventHandler(priority = EventPriority.VERY_LOWEST)
    public void EntityDamageByEntityEvent (EntityDamageByEntityEvent e){

        if (!(e.getEntity() instanceof ItemFrame)){
            return;
        }

        // 額縁の中身を取り出されるとき
        Entity damager = e.getDamager();
        // System.out.println("【速報】プラグイン、イベントが発生したことを検知する。");
        if (damager instanceof Player){
            // System.out.println("【速報】プラグイン、人が殴ったことを認める。");
            if (e.getEntity() instanceof ItemFrame && ItemFrameData.getItemFrame(e.getEntity().getUniqueId()) == null){
                // System.out.println("【速報】プラグイン、ロックされてないやつ　かつ　額縁だったことを認める。");
                ItemFrame frame = (ItemFrame) e.getEntity();
                if (frame.getItem().getType() != Material.AIR){
                    // System.out.println("【速報】プラグイン、額縁に中身が入ってると認める。");
                    Player player = (Player) e.getDamager();

                    boolean itemNotAddflag = false;
                    int count = -1;
                    ItemStack frameItem = frame.getItem();

                    for (int i = 0; i < player.getInventory().getSize(); i++){
                        ItemStack item = player.getInventory().getItem(i);
                        if (item == null){
                            continue;
                        }

                        if (item.getType() == Material.AIR){
                            count = i;
                        }

                        if (ItemStackEqual(frameItem, player.getInventory().getItem(i))){
                            itemNotAddflag = true;
                            break;
                        }
                    }

                    boolean dropItemFlag = false;
                    List<DropData> dropList = ItemFrameData.getDropDataByUser(player.getUniqueId());
                    if (dropList != null && dropList.size() > 0){
                        List<World> worlds = Bukkit.getServer().getWorlds();
                        for (DropData item : dropList){

                            // System.out.println("チェック1");
                            for (World world : worlds){
                                Entity entity = world.getEntity(item.getItemUUID());
                                if (entity != null){
                                    //System.out.println("チェック2 : " + entity.getType());
                                }// else {
                                    // System.out.println("チェック2 : null");
                                // }

                                if (entity != null && entity.getType() == EntityType.DROPPED_ITEM){
                                    // System.out.println("うまくいってる？");
                                    Item dropItem = (Item) entity;
                                    dropItemFlag = ItemStackEqual(frameItem, dropItem.getItemStack());
                                }
                                if (dropItemFlag){
                                    itemNotAddflag = true;
                                    break;
                                }
                            }
                            if (itemNotAddflag){
                                break;
                            }
                        }
                    }

                    // System.out.println("アイテムどうなった？");
                    if (!itemNotAddflag && count != -1){
                        // System.out.println(" ---> アイテム追加されてる");
                        player.getInventory().setItem(count, frameItem);
                    } else if (!itemNotAddflag) {
                        // System.out.println(" ---> アイテムドロップされてる");
                        player.getLocation().getWorld().dropItem(player.getLocation(), frameItem);
                    } else {
                        // System.out.println(" ---> アイテム持ってるって認識したからなにもしない。");
                    }

                    if (!dropItemFlag){
                        ItemStack stack = new ItemStack(Material.AIR);
                        frame.setItem(stack);
                    }
                    e.setCancelled(true);
                } else {
                    // System.out.println("【速報】プラグイン、額縁に中身が入っていないと発表。");
                    // System.out.println("getItem().getType() : " + frame.getItem().getType());
                    e.setCancelled(true);
                }
            }
            if (e.getEntity() instanceof ItemFrame && ItemFrameData.getItemFrame(e.getEntity().getUniqueId()) != null){
                // System.out.println("【速報】プラグイン、ロックされてるのは無条件キャンセルとの発表");
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerDropItemEvent (PlayerDropItemEvent e){
        DropData data = new DropData(e.getPlayer().getUniqueId(), e.getItemDrop().getUniqueId());
        ItemFrameData.addDropList(data);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerAttemptPickupItemEvent(PlayerAttemptPickupItemEvent e){
        DropData data = new DropData(e.getPlayer().getUniqueId(), e.getItem().getUniqueId());
        ItemFrameData.delDropList(data);
    }

    private boolean ItemStackEqual(ItemStack item1, ItemStack item2){

        if (item1 == null && item2 == null){
            return true;
        }

        if (item1 != null && item2 != null && item1.getType() == item2.getType()){

            if (item1.getEnchantments().size() == item2.getEnchantments().size()){

                boolean flag = true;

                for (Map.Entry<Enchantment , Integer> e1 : item1.getEnchantments().entrySet()){
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
