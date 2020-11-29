package xyz.n7mn.dev.yululi.itemframeprotectionplugin;


import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.*;
import org.bukkit.material.FlowerPot;
import org.bukkit.plugin.Plugin;
import xyz.acrylicstyle.achievement.gui.ExtraInventoryGui;
import xyz.acrylicstyle.achievement.gui.ExtraInventoryGuiHolder;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.*;

import java.util.*;

class ItemFrameListener implements Listener {

    final private DataAPI api;
    final private Plugin plugin = Bukkit.getPluginManager().getPlugin("ItemFrameProtectionPlugin");

    private Set<UUID> frameBreakList = Collections.synchronizedSet(new HashSet<>());

    public ItemFrameListener(DataAPI api){

        this.api = api;

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void PlayerInteractEntityEvent (PlayerInteractEntityEvent e){

        if (!(e.getRightClicked() instanceof ItemFrame)){
            return;
        }

        if (e.getHand() != EquipmentSlot.HAND){
            e.setCancelled(true);
            return;
        }

        // スネークしながら右クリックでロックしたり解除するようにする。
        ItemFrame frame = (ItemFrame) e.getRightClicked();
        Player player = e.getPlayer();

        FrameData data = api.getItemFrame(frame.getUniqueId());

        try {

            if (player.isSneaking()){

                if (data == null){

                    if (frame.getItem().getType() == Material.AIR){

                        frame.setItem(player.getInventory().getItemInMainHand());

                    }

                    api.addItemFrame(new FrameData(frame.getUniqueId(), player.getInventory().getItemInMainHand(), player.getUniqueId(), new Date(), true));
                    player.sendMessage(ChatColor.GREEN + "[額縁保護] 保護しました。解除するにはスニークをしながら右クリックしてください。");
                    e.setCancelled(true);

                    synchronized (frameBreakList){
                        frameBreakList.add(e.getRightClicked().getUniqueId());
                    }

                } else {

                    if (data.getProtectUser().equals(player.getUniqueId())){
                        api.deleteTableByFrame(frame.getUniqueId());
                        player.sendMessage(ChatColor.GREEN + "[額縁保護] 保護解除しました。再度保護するにはスニークをしながら右クリックしてください。");

                        synchronized (frameBreakList){
                            frameBreakList.remove(e.getRightClicked().getUniqueId());
                        }

                        e.setCancelled(true);
                        return;
                    } else if (player.hasPermission("ifp.op")) {
                        api.deleteTableByFrame(frame.getUniqueId());
                        player.sendMessage(ChatColor.GOLD + "[額縁保護] 代理解除しました。再度保護するにはスニークをしながら右クリックしてください。");

                        synchronized (frameBreakList){
                            frameBreakList.remove(e.getRightClicked().getUniqueId());
                        }

                        e.setCancelled(true);
                        return;
                    }

                    player.sendMessage(ChatColor.RED + "[額縁保護] 他の人が保護している額縁です！！");
                    e.setCancelled(true);

                }

            } else {

                if (frame.getItem().getType() == Material.AIR){

                    frame.setItem(player.getInventory().getItemInMainHand());
                    if (plugin.getConfig().getBoolean("userMessage")){
                        player.sendMessage(ChatColor.DARK_GREEN + "[額縁保護] スニークしながら右クリックで額縁の保護を試してみませんか？");
                    }
                }

                e.setCancelled(true);

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

        }

        FrameData itemFrame = api.getItemFrame(frame.getUniqueId());
        if (itemFrame != null){

            synchronized (frameBreakList){
                frameBreakList.add(e.getEntity().getUniqueId());
            }

            e.setCancelled(true);
            return;
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
            // エクストラインベントリ
            ExtraInventoryGui ex = ExtraInventoryGuiHolder.getOrCreate(player.getUniqueId());
            Inventory extraInventory = ex.getInventory();

            for (int i = 0; i < extraInventory.getSize(); i++){

                // System.out.println("i : " + i);

                if (extraInventory.getItem(i) == null){
                    continue;
                }

                // System.out.println(extraInventory.getItem(i).getType());

                if (extraInventory.getItem(i).getType() == Material.AIR){
                    continue;
                }

                if (extraInventory.getItem(i).getType() == frame.getItem().getType() && ItemStackEqual(extraInventory.getItem(i), frame.getItem())){
                    frame.setItem(new ItemStack(Material.AIR));
                    e.setCancelled(true);
                    return;
                }

            }


            // ドロップ対策
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

            // チェスト・シュルカーボックス・かまどなどの対策
            List<BoxData> boxDataList = api.getBoxDataList();
            Location location = frame.getLocation();

            for (BoxData boxData : boxDataList){

                if (boxData.getBoxUseUUID() == null || boxData.getBoxDataUUID() == null || boxData.getInventory() == null){
                    continue;
                }

                if (!boxData.getBoxUseUUID().equals(player.getUniqueId())){
                    continue;
                }

                if (Math.abs(location.getBlockX() - boxData.getInventory().getLocation().getBlockX()) <= 5 && Math.abs(location.getBlockY() - boxData.getInventory().getLocation().getBlockY()) <= 5 && Math.abs(location.getBlockZ() - boxData.getInventory().getLocation().getBlockZ()) <= 5){


                    Inventory dataInventory = boxData.getInventory();
                    int size = dataInventory.getSize();

                    for (int i = 0; i < size; i++){

                        ItemStack item = dataInventory.getItem(i);

                        if (item != null && item.getType() == frame.getItem().getType() && ItemStackEqual(item, frame.getItem())){

                            frame.setItem(new ItemStack(Material.AIR));
                            e.setCancelled(true);
                            return;
                        }

                    }

                }

            }

            // エンダーチェスト対策
            Inventory enderChest = player.getEnderChest();
            int size = enderChest.getSize();

            for (int i = 0; i < size; i++){

                ItemStack item = enderChest.getItem(i);

                if (item != null && item.getType() == frame.getItem().getType() && ItemStackEqual(item, frame.getItem())){

                    frame.setItem(new ItemStack(Material.AIR));
                    e.setCancelled(true);
                    return;
                }

            }

            // チェストトロッコ・馬とかのEntity対策
            World world = Bukkit.getServer().getWorld(player.getLocation().getWorld().getUID());
            List<Entity> entities = world.getEntities();
            for (Entity entity : entities){

                if (entity instanceof StorageMinecart){

                    StorageMinecart minecart = (StorageMinecart) entity;
                    Inventory inventory1 = minecart.getInventory();
                    int size1 = inventory1.getSize();

                    for (int i = 0; i < size1; i++){

                        ItemStack item = inventory1.getItem(i);

                        if (item != null && item.getType() == frame.getItem().getType() && ItemStackEqual(item, frame.getItem())){

                            frame.setItem(new ItemStack(Material.AIR));
                            e.setCancelled(true);
                            return;
                        }

                    }
                }

                if (entity instanceof ChestedHorse){

                    ChestedHorse chestedHorse = (ChestedHorse) entity;
                    Inventory inventory1 = chestedHorse.getInventory();
                    int size1 = inventory1.getSize();

                    for (int i = 0; i < size1; i++){

                        ItemStack item = inventory1.getItem(i);

                        if (item != null && item.getType() == frame.getItem().getType() && ItemStackEqual(item, frame.getItem())){

                            frame.setItem(new ItemStack(Material.AIR));
                            e.setCancelled(true);
                            return;
                        }

                    }

                }

                if (entity instanceof HopperMinecart){

                    HopperMinecart minecart = (HopperMinecart) entity;
                    Inventory inventory1 = minecart.getInventory();
                    int size1 = inventory1.getSize();

                    for (int i = 0; i < size1; i++){

                        ItemStack item = inventory1.getItem(i);

                        if (item != null && item.getType() == frame.getItem().getType() && ItemStackEqual(item, frame.getItem())){

                            frame.setItem(new ItemStack(Material.AIR));
                            e.setCancelled(true);
                            return;
                        }

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

/*
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

            //System.out.println("Debug1 : " + location.getBlock().getType());
        }

        if (loc.getPitch() == -90){
            Location location = new Location(world, loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());

            // System.out.println("a");
            if (location.getBlock().getType() == Material.AIR) {

                AirFlag = true;
                mode = -2;

            }

            //System.out.println("Debug2 : " + location.getBlock().getType());
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
*/
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


    @EventHandler(priority = EventPriority.VERY_LOWEST)
    public void InventoryCloseEvent (InventoryCloseEvent e){

        Location location = e.getInventory().getLocation();

        if (location == null){
            return;
        }

        // System.out.println(location.getBlock().getType());

        Block block = location.getBlock();
        //System.out.println("debug 1 : " + block.getType());
        // チェスト
        if (block.getState() instanceof Chest){
            //System.out.println("debug 11");
            Chest chest = (Chest) block.getState();
            if (api.getBoxDataBySearch(chest.getLocation()) == null){

                BoxData boxData = new BoxData(UUID.randomUUID(), e.getPlayer().getUniqueId(), chest.getBlockInventory());
                api.addBoxData(boxData);

            }

            return;
        }

        // ラージチェスト？
        if (block.getState() instanceof DoubleChest){

            DoubleChest chest = (DoubleChest) block.getState();
            if (api.getBoxDataBySearch(chest.getLocation()) == null){

                BoxData boxData = new BoxData(UUID.randomUUID(), e.getPlayer().getUniqueId(), chest.getInventory());
                api.addBoxData(boxData);

            }

            return;

        }

        // シュルカー
        if (block.getState() instanceof ShulkerBox){
            //System.out.println("debug 12");
            ShulkerBox shulkerBox = (ShulkerBox) block.getState();
            if (api.getBoxDataBySearch(shulkerBox.getLocation()) == null){

                BoxData boxData = new BoxData(UUID.randomUUID(), e.getPlayer().getUniqueId(), shulkerBox.getInventory());
                api.addBoxData(boxData);

            }

            return;
        }

        // かまど
        if (block.getState() instanceof Furnace){

            Furnace furnace = (Furnace) block.getState();
            BoxData boxData = new BoxData(UUID.randomUUID(), e.getPlayer().getUniqueId(), furnace.getInventory());
            api.addBoxData(boxData);

            return;
        }

        // 溶鉱炉
        if (block.getState() instanceof BlastFurnace){

            BlastFurnace blastFurnace = (BlastFurnace) block.getState();
            BoxData boxData = new BoxData(UUID.randomUUID(), e.getPlayer().getUniqueId(), blastFurnace.getInventory());
            api.addBoxData(boxData);

            return;
        }

        // 燻製器
        if (block.getState() instanceof Smoker){

            Smoker smoker = (Smoker) block.getState();
            BoxData boxData = new BoxData(UUID.randomUUID(), e.getPlayer().getUniqueId(), smoker.getInventory());
            api.addBoxData(boxData);

            return;
        }

        // ホッパー
        if (block.getState() instanceof Hopper){

            Hopper hopper = (Hopper) block.getState();
            BoxData boxData = new BoxData(UUID.randomUUID(), e.getPlayer().getUniqueId(), hopper.getInventory());
            api.addBoxData(boxData);

            return;
        }

        // ディスペンサー
        if (block.getState() instanceof Dispenser){

            Dispenser dispenser = (Dispenser) block.getState();
            BoxData boxData = new BoxData(UUID.randomUUID(), e.getPlayer().getUniqueId(), dispenser.getInventory());
            api.addBoxData(boxData);

            return;
        }

        // ホッパーカート
        if (block.getState() instanceof HopperMinecart){

            HopperMinecart hopperMinecart = (HopperMinecart) block.getState();
            BoxData boxData = new BoxData(UUID.randomUUID(), e.getPlayer().getUniqueId(), hopperMinecart.getInventory());
            api.addBoxData(boxData);

            return;
        }

        // ドロッパー
        if (block.getState() instanceof Dropper){

            Dropper dropper = (Dropper) block.getState();
            BoxData boxData = new BoxData(UUID.randomUUID(), e.getPlayer().getUniqueId(), dropper.getInventory());
            api.addBoxData(boxData);

            return;
        }

        // 樽
        if (block.getState() instanceof Barrel){

            Barrel barrel = (Barrel) block.getState();
            BoxData boxData = new BoxData(UUID.randomUUID(), e.getPlayer().getUniqueId(), barrel.getInventory());
            api.addBoxData(boxData);

            return;
        }

        // トラップチェスト
        if (block.getType() == Material.TRAPPED_CHEST){

            BoxData boxData = new BoxData(UUID.randomUUID(), e.getPlayer().getUniqueId(), e.getInventory());
            api.addBoxData(boxData);

            return;
        }

        // 多分プレーヤー以外。
        Inventory inventory = e.getInventory();
        int size = e.getInventory().getSize();

        if (size != 5){

            BoxData boxData = new BoxData(UUID.randomUUID(), e.getPlayer().getUniqueId(), inventory);
            api.addBoxData(boxData);

        }

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
