package xyz.n7mn.dev.yululi.itemframeprotectionplugin;


import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.DataAPI;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.DropItemData;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.FrameData;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.ItemFrameProtectDeleteEvent;

import java.util.*;

class ItemFrameListener implements Listener {

    final private DataAPI api;
    final private Plugin plugin = Bukkit.getPluginManager().getPlugin("ItemFrameProtectionPlugin");

    private Set<UUID> frameBreakList = Collections.synchronizedSet(new HashSet<>());
    private Set<UUID> tempUUID = Collections.synchronizedSet(new HashSet<>());

    public ItemFrameListener(DataAPI api){

        this.api = api;

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void PlayerInteractEntityEvent (PlayerInteractEntityEvent e){

        if (!(e.getRightClicked() instanceof ItemFrame)){
            return;
        }

        // System.out.println("あ");
        synchronized (tempUUID){
            for (UUID temp : tempUUID){
                if (temp.equals(e.getRightClicked().getUniqueId())){
                    e.setCancelled(true);
                    return;
                }
            }
        }

        synchronized (tempUUID){
            tempUUID.add(e.getRightClicked().getUniqueId());
        }


        new BukkitRunnable() {
            @Override
            public void run() {
                // スネークしながら右クリックでロックしたり解除するようにする。
                ItemFrame frame = (ItemFrame) e.getRightClicked();
                Player player = e.getPlayer();
                try {

                    FrameData foundData = api.getItemFrame(frame.getUniqueId());
                    boolean foundFlag = (foundData != null);


                    if (player.isSneaking()){

                        if (foundFlag){
                            // 保護解除

                            if (foundData.getProtectUser().equals(player.getUniqueId())){
                                api.deleteTableByFrame(frame.getUniqueId());
                                player.sendMessage(ChatColor.GREEN + "保護解除しました。 もう一度保護するにはスニークしながら右クリックしてください。");

                                synchronized (tempUUID){
                                    tempUUID.remove(e.getRightClicked().getUniqueId());
                                }
                                e.setCancelled(true);
                                return;
                            } else {

                                if (player.hasPermission("ifp.op")){
                                    api.deleteTableByFrame(frame.getUniqueId());
                                    player.sendMessage(ChatColor.YELLOW + "保護を代理解除しました。 もう一度保護するにはスニークしながら右クリックしてください。");
                                } else {
                                    player.sendMessage(ChatColor.RED + "他の人が保護しています。");
                                }

                                synchronized (tempUUID){
                                    tempUUID.remove(e.getRightClicked().getUniqueId());
                                }
                                e.setCancelled(true);
                                return;
                            }

                        } else {
                            // 新規保護
                            if (frame.getItem().getType() == Material.AIR && player.getInventory().getItemInMainHand().getType() != Material.AIR){
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
                            e.setCancelled(true);

                            synchronized (tempUUID){
                                tempUUID.remove(e.getRightClicked().getUniqueId());
                            }
                            return;
                        }
                    } else {

                        if (foundFlag){
                            synchronized (tempUUID){
                                tempUUID.remove(e.getRightClicked().getUniqueId());
                            }
                            e.setCancelled(true);
                            return;
                        }

                        if (frame.getItem().getType() == Material.AIR && e.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR){

                            synchronized (tempUUID){
                                tempUUID.remove(e.getRightClicked().getUniqueId());
                            }
                            frame.setItem(e.getPlayer().getInventory().getItemInMainHand());
                            e.setCancelled(true);
                            return;
                        }

                    }

                    synchronized (tempUUID){
                        tempUUID.remove(e.getRightClicked().getUniqueId());
                    }

                    if (e.getPlayer().getGameMode() != GameMode.CREATIVE && e.getPlayer().getGameMode() != GameMode.SPECTATOR){


                        ItemStack itemInMainHand = e.getPlayer().getInventory().getItemInMainHand();
                        if (itemInMainHand.getAmount() > 1){
                            e.getPlayer().getInventory().addItem(itemInMainHand);
                        }


                    }

                } catch (Exception ex){
                    if (plugin.getConfig().getBoolean("errorPrint")){
                        plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                        ex.printStackTrace();
                    }
                }
            }
        }.runTaskLaterAsynchronously(plugin, 0L);



    }


    @EventHandler (priority = EventPriority.HIGHEST)
    public void BlockBreakEvent (HangingBreakEvent e){


        if (!(e.getEntity() instanceof ItemFrame)){
            return;
        }

        //System.out.println("あ");

        // 額縁壊されるとき
        ItemFrame frame = (ItemFrame) e.getEntity();

        synchronized (frameBreakList){
            for (UUID uuid : frameBreakList){
                if (uuid.equals(frame.getUniqueId())){
                    e.setCancelled(true);
                    return;
                }
            }

            FrameData itemFrame = api.getItemFrame(frame.getUniqueId());
            if (itemFrame != null){
                frameBreakList.add(frame.getUniqueId());
                e.setCancelled(true);
                return;
            }
        }

        // 無限増殖対策
        if (frame.getItem().getType() != Material.AIR){
            ItemStack stack = new ItemStack(Material.AIR);
            frame.setItem(stack);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityDamageEvent (EntityDamageEvent e){

        if (!(e.getEntity() instanceof ItemFrame)){
            return;
        }

        //System.out.println("い");

        // 額縁の中身消されたとき
        ItemFrame frame = (ItemFrame) e.getEntity();

        synchronized (frameBreakList){
            for (UUID uuid : frameBreakList){
                if (uuid.equals(frame.getUniqueId())){
                    e.setCancelled(true);
                    return;
                }
            }

            FrameData itemFrame = api.getItemFrame(frame.getUniqueId());
            if (itemFrame != null){

                frameBreakList.add(frame.getUniqueId());
                e.setCancelled(true);
                return;
            }
        }


        if (frame.getItem().getType() != Material.AIR){

            frame.setItem(new ItemStack(Material.AIR));
            e.setCancelled(true);

        }
    }

    @EventHandler(priority = EventPriority.VERY_LOWEST)
    public void EntityDamageByEntityEvent (EntityDamageByEntityEvent e){

        if (!(e.getEntity() instanceof ItemFrame)){
            return;
        }

        //System.out.println("う");

        // 額縁の中身を取り出されるとき
        ItemFrame frame = (ItemFrame) e.getEntity();

        synchronized (frameBreakList){
            for (UUID uuid : frameBreakList) {
                if (uuid.equals(frame.getUniqueId())) {

                    e.setCancelled(true);
                    return;
                }
            }

            FrameData itemFrame = api.getItemFrame(frame.getUniqueId());
            if (itemFrame != null){
                e.setCancelled(true);
                frameBreakList.add(frame.getUniqueId());
                return;
            }
        }

        if (e.getDamager() instanceof Player){


            //System.out.println("ああ");
            Player player = (Player) e.getDamager();
            PlayerInventory inventory = player.getInventory();

            ItemStack frameData = frame.getItem();

            for (int i = 0; i < inventory.getSize(); i++){

                if (inventory.getItem(i) == null){
                    continue;
                }

                if (inventory.getItem(i).getType() == Material.AIR){
                    continue;
                }

                if (inventory.getItem(i).getType() == frame.getItem().getType() && ItemStackEqual(inventory.getItem(i), frame.getItem())){
                    frame.setItem(new ItemStack(Material.AIR));
                    e.setCancelled(true);
                    return;
                }

            }

            //System.out.println("いい");

            List<DropItemData> itemList = api.getListByDropItem();

            for (DropItemData item : itemList){
                Entity entity = Bukkit.getEntity(item.getDropItemUUID());

                if (entity != null){
                    Item i = (Item) entity;

                    if (i.getItemStack().getType() == frame.getItem().getType() && ItemStackEqual(frame.getItem(), i.getItemStack())){



                        frame.setItem(new ItemStack(Material.AIR));
                        e.setCancelled(true);
                        return;

                    }
                }
            }

            player.getInventory().addItem(frameData);

        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityTeleportEvent (EntityTeleportEvent e){

        if (e.getEntityType() != EntityType.ITEM_FRAME){
            return;
        }

        // 額縁がtpされそうなとき

        ItemFrame frame = (ItemFrame) e.getEntity();

        synchronized (frameBreakList){
            for (UUID uuid : frameBreakList) {
                if (uuid.equals(frame.getUniqueId())) {

                    e.setCancelled(true);
                    return;
                }
            }
        }


        FrameData itemFrame = api.getItemFrame(frame.getUniqueId());
        if (itemFrame != null){
            frameBreakList.add(frame.getUniqueId());
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);
    }


    private Set<UUID> frameBreakList2 = Collections.synchronizedSet(new HashSet<>());
    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityRemoveFromWorldEvent(EntityRemoveFromWorldEvent e){

        // 額縁がkillされそうなとき

        if (e.getEntity().getType() != EntityType.ITEM_FRAME){
            return;
        }

        ItemFrame frame = (ItemFrame) e.getEntity();

        synchronized (frameBreakList2){
            for (UUID uuid : frameBreakList2) {
                if (uuid.equals(frame.getUniqueId())) {
                    return;
                }
            }

            frameBreakList2.add(frame.getUniqueId());
        }

        FrameData itemFrame = api.getItemFrame(frame.getUniqueId());
        if (itemFrame != null){
            //ItemFramePlace(e.getEntity().getLocation(), frame);
            api.deleteTableByFrame(frame.getUniqueId());
        }

        synchronized (frameBreakList2){
            frameBreakList2.remove(frame.getUniqueId());
        }
    }


    private void ItemFramePlace(Location loc, ItemFrame frame){


        World world = loc.getWorld();
        boolean AirFlag = false;
        int mode = 0;

        if (loc.getPitch() == 90){
            Location location = new Location(world, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());

            // System.out.println("a");
            if (location.getBlock().getType() == Material.AIR) {

                AirFlag = true;
                mode = -1;

            }

            System.out.println("Debug1 : " + location.getBlock().getType());
        }

        if (loc.getPitch() == -90){
            Location location = new Location(world, loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());

            // System.out.println("a");
            if (location.getBlock().getType() == Material.AIR) {

                AirFlag = true;
                mode = -2;

            }

            System.out.println("Debug2 : " + location.getBlock().getType());
        }

        if (loc.getYaw() == 0){

            Location location = new Location(world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() - 1);

            // System.out.println("a");
            if (location.getBlock().getType() == Material.AIR) {

                AirFlag = true;
                mode = 1;

            }

        }

        if (loc.getYaw() == 90){

            Location location = new Location(world, loc.getBlockX() + 1, loc.getBlockY(), loc.getBlockZ());

            // System.out.println("a");
            if (location.getBlock().getType() == Material.AIR) {

                AirFlag = true;
                mode = 2;

            }
        }

        if (loc.getYaw() == 180){
            Location location = new Location(world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() + 1);

            // System.out.println("a");
            if (location.getBlock().getType() == Material.AIR) {

                AirFlag = true;
                mode = 3;

            }

        }

        if (loc.getYaw() == 270){
            Location location = new Location(world, loc.getBlockX() - 1, loc.getBlockY(), loc.getBlockZ());

            // System.out.println("a");
            if (location.getBlock().getType() == Material.AIR) {

                AirFlag = true;
                mode = 4;

            }

        }

        FrameData data;
        ItemFrame spawn;
        if (AirFlag){
            Location location1;
            Location location2;
            if (mode == -1){

                location1 = new Location(world, loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());


            } else if (mode == -2) {

                location1 = new Location(world, loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());

            } else if (mode == 1) {

                location1 = new Location(world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() - 1);

            } else if (mode == 2) {

                location1 = new Location(world, loc.getBlockX() + 1, loc.getBlockY(), loc.getBlockZ());

            } else if (mode == 3) {

                location1 = new Location(world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() + 1);

            } else {

                location1 = new Location(world, loc.getBlockX() - 1, loc.getBlockY(), loc.getBlockZ());

            }


            Material locTemp = null;

            location1.getBlock().setType(Material.GLASS);
            if (loc.getBlock().getType() != Material.AIR){
                locTemp = loc.getBlock().getType();
                loc.getBlock().setType(Material.AIR);
            }
            spawn = world.spawn(loc, ItemFrame.class);
            spawn.setItem(frame.getItem());
            data = api.getItemFrame(frame.getUniqueId());


            api.deleteTableByFrame(frame.getUniqueId());
            synchronized (frameBreakList){
                frameBreakList.remove(frame.getUniqueId());
            }

            api.addItemFrame(new FrameData(spawn.getUniqueId(), frame.getItem(), data.getProtectUser(), new Date(), true));
            location1.getBlock().setType(Material.AIR);
            if (locTemp != null){
                loc.getBlock().setType(locTemp);
            }

        } else {

            spawn = world.spawn(loc, ItemFrame.class);
            spawn.setItem(frame.getItem());
            data = api.getItemFrame(frame.getUniqueId());

            api.deleteTableByFrame(frame.getUniqueId());
            synchronized (frameBreakList){
                frameBreakList.remove(frame.getUniqueId());
            }

            api.addItemFrame(new FrameData(spawn.getUniqueId(), frame.getItem(), data.getProtectUser(), new Date(), true));
        }





    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void ItemFrameProtectDeleteEvent(ItemFrameProtectDeleteEvent e){
        // キャッシュ削除
        synchronized (frameBreakList){
            frameBreakList.remove(e.getItemFrameUUID());
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
