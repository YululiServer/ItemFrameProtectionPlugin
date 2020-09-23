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
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;


class FrameListener implements Listener {

    private final Plugin plugin;
    private final Connection con;
    private Player player = null;
    private boolean runflag = false;

    public FrameListener(Plugin plugin, Connection con){
        this.plugin = plugin;
        this.con = con;
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
            final FrameData data = getData(rightClicked.getUniqueId());

            // 保護してない
            if (data == null && rightClicked instanceof ItemFrame){
                ItemFrame frame = (ItemFrame) rightClicked;
                if (player.isSneaking()){
                    if (frame.getItem().getType() == Material.AIR){
                        frame.setItem(player.getInventory().getItemInMainHand());
                    }
                    setData(player.getUniqueId(), rightClicked.getUniqueId());
                    player.sendMessage(ChatColor.GREEN + "額縁を保護しました。 もう一度スネークしながら右クリックで保護を解除できます。");
                    e.setCancelled(true);
                } else {

                    if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE){

                        if (frame.getItem().getType() == Material.AIR){
                            ItemStack hand = player.getInventory().getItemInMainHand();
                            frame.setItem(hand);
                            // player.getInventory().addItem(hand);
                            e.setCancelled(true);
                        }
                    }

                }
            }
            if (rightClicked instanceof ItemFrame && data != null) {
                // 保護してる
                if (player.isSneaking()){
                    if (data.getCreateUser().equals(player.getUniqueId()) || player.hasPermission("ifp.op")){
                        setData(data.getCreateUser(), data.getItemFrame());
                        player.sendMessage(ChatColor.GREEN + "額縁を保護解除しました。 もう一度スネークしながら右クリックで再度保護できます。");
                    } else {
                        player.sendMessage(ChatColor.GREEN + "この額縁は保護されています。");
                    }
                    e.setCancelled(true);
                }
            }
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
            if (e.getEntity() instanceof ItemFrame && getData(e.getEntity().getUniqueId()) == null){
                ItemFrame frame = (ItemFrame) e.getEntity();
                if (frame.getItem().getType() != Material.AIR){

                    // frame.getLocation().getWorld().dropItem(frame.getLocation(), frame.getItem());
                    ItemStack stack = new ItemStack(Material.AIR);
                    frame.setItem(stack);
                }
            } else if (e.getEntity() instanceof ItemFrame && getData(e.getEntity().getUniqueId()) != null) {
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
            if (e.getEntity() instanceof ItemFrame && getData(e.getEntity().getUniqueId()) == null) {
                ItemFrame frame = (ItemFrame) e.getEntity();
                if (frame.getItem().getType() != Material.AIR){
                    ItemStack stack = new ItemStack(Material.AIR);
                    frame.setItem(stack);
                    e.setCancelled(true);
                }
            }
            if (e.getEntity() instanceof ItemFrame && getData(e.getEntity().getUniqueId()) != null){
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
            if (e.getEntity() instanceof ItemFrame && getData(e.getEntity().getUniqueId()) == null){
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
                    List<Item> dropList = getDrop(player.getUniqueId());
                    if (dropList != null && dropList.size() > 0){
                        List<World> worlds = Bukkit.getServer().getWorlds();
                        for (Item item : dropList){

                            // System.out.println("チェック1");
                            for (World world : worlds){
                                Entity entity = world.getEntity(item.getUniqueId());
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
            if (e.getEntity() instanceof ItemFrame && getData(e.getEntity().getUniqueId()) != null){
                // System.out.println("【速報】プラグイン、ロックされてるのは無条件キャンセルとの発表");
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void PlayerDropItemEvent (PlayerDropItemEvent e){
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {

                try {
                    PreparedStatement statement = con.prepareStatement("INSERT INTO `IFPTable2` (`DropUser`, `ItemUUID`) VALUES (?, ?);");
                    statement.setString(1, e.getPlayer().getUniqueId().toString());
                    statement.setString(2, e.getItemDrop().getUniqueId().toString());
                    statement.execute();
                    statement.close();
                } catch (SQLException ex) {
                    if (plugin.getConfig().getBoolean("errorPrint")) {
                        plugin.getLogger().info(ChatColor.RED + "SQLエラーを検知しました。");
                        ex.printStackTrace();
                    }
                }
            }
        };
        bukkitRunnable.runTaskLaterAsynchronously(plugin, 10L);
    }

    @EventHandler
    public void PlayerAttemptPickupItemEvent(PlayerAttemptPickupItemEvent e){
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    PreparedStatement statement = con.prepareStatement("DELETE FROM `IFPTable2` WHERE `DropUser` = ? AND `ItemUUID` = ?");
                    statement.setString(1, e.getPlayer().getUniqueId().toString());
                    statement.setString(2, e.getItem().getUniqueId().toString());
                    statement.execute();
                    statement.close();
                } catch (SQLException ex) {
                    if (plugin.getConfig().getBoolean("errorPrint")) {
                        plugin.getLogger().info(ChatColor.RED + "SQLエラーを検知しました。");
                        ex.printStackTrace();
                    }
                }
            }
        };
        bukkitRunnable.runTaskLaterAsynchronously(plugin, 10L);
    }



    private FrameData getData(UUID itemFlame){
        FrameData data = null;

        try {
            if (con != null){
                if (!con.isClosed()){
                    PreparedStatement statement = con.prepareStatement("SELECT * FROM IFPTable2 WHERE ItemFrame = ?");
                    statement.setString(1, itemFlame.toString());
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()){
                        String createUser = resultSet.getString("CreateUser");
                        String itemFrame = resultSet.getString("ItemFrame");

                        return new FrameData(UUID.fromString(createUser), UUID.fromString(itemFrame));
                    }
                    statement.close();
                }
            }
        } catch (Exception e){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "SQLエラーを検知しました。");
                e.printStackTrace();
            }
            return null;
        }

        return data;
    }

    private List<Item> getDrop(UUID dropUser){
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM IFPTable2 WHERE DropUser = ?");
            statement.setString(1, dropUser.toString());
            ResultSet resultSet = statement.executeQuery();

            List<Item> list = new ArrayList<>();
            while (resultSet.next()){
                List<World> worlds = Bukkit.getServer().getWorlds();
                for (World world : worlds){
                    Entity entity = world.getEntity(UUID.fromString(resultSet.getString("ItemUUID")));
                    if (entity != null){
                        if (entity.getType() == EntityType.DROPPED_ITEM){
                            list.add((Item) entity);
                        }
                    }
                }
            }
            statement.close();
            return list;
        } catch (Exception e){
            if (plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                e.printStackTrace();
            }
            return null;
        }
    }


    private void setData(UUID createUser, UUID itemFlame){
        Thread thread = new Thread(() -> {
            boolean a = true;
            while(a){
                PreparedStatement statement = null;
                PreparedStatement statement1 = null;
                try {
                    if (con != null) {

                        statement = con.prepareStatement("SELECT COUNT(*) FROM IFPTable WHERE ItemFrame = ?");
                        statement.setString(1, itemFlame.toString());
                        ResultSet resultSet = statement.executeQuery();
                        if (resultSet.next()){
                            if (resultSet.getInt("COUNT(*)") == 0) {
                                statement1 = con.prepareStatement("INSERT INTO `IFPTable` (`CreateUser`, `ItemFrame`) VALUES (?, ?);");
                            } else {
                                statement1 = con.prepareStatement("DELETE FROM `IFPTable` WHERE `CreateUser` = ? AND `ItemFrame` = ?");
                            }
                            statement1.setString(1, createUser.toString());
                            statement1.setString(2, itemFlame.toString());

                            statement1.execute();
                            statement1.close();
                        }
                        statement.close();
                    }
                } catch (Exception e) {
                    if (plugin.getConfig().getBoolean("errorPrint")) {
                        plugin.getLogger().info(ChatColor.RED + "SQLエラーを検知しました。");
                        e.printStackTrace();
                    }
                } finally {
                    try {
                        statement1.close();
                    } catch (Exception ex){
                        // ex.printStackTrace();
                    }
                }

                a = false;
            }
        });

        thread.start();
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
