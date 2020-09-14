package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;


class FrameListener implements Listener {

    private final Plugin plugin;
    private final Connection con;

    public FrameListener(Plugin plugin, Connection con){
        this.plugin = plugin;
        this.con = con;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerInteractEntityEvent (PlayerInteractEntityEvent e){
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
        // 額縁壊されるとき
        try {
            if (e.getEntity() instanceof ItemFrame && getData(e.getEntity().getUniqueId()) == null){
                ItemFrame frame = (ItemFrame) e.getEntity();
                if (frame.getItem().getType() != Material.AIR){

                    frame.getLocation().getWorld().dropItem(frame.getLocation(), frame.getItem());
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
        // 額縁の中身消されたとき
        try {
            if (e.getEntity() instanceof ItemFrame && getData(e.getEntity().getUniqueId()) == null) {
                ItemFrame frame = (ItemFrame) e.getEntity();
                if (frame.getItem().getType() != Material.AIR){
                    frame.getLocation().getWorld().dropItem(frame.getLocation(), frame.getItem());
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityDamageEvent (EntityDamageByEntityEvent e){
        // 額縁の中身を取り出されるとき
        if (e.getEntity() instanceof ItemFrame && getData(e.getEntity().getUniqueId()) == null){
            ItemFrame frame = (ItemFrame) e.getEntity().getVehicle();
            if (frame != null && frame.getItem().getType() != Material.AIR){
                frame.getLocation().getWorld().dropItem(frame.getLocation(), frame.getItem());
                ItemStack stack = new ItemStack(Material.AIR);
                frame.setItem(stack);
                e.setCancelled(true);
            }
        }
        if (e.getEntity() instanceof ItemFrame && getData(e.getEntity().getUniqueId()) != null){
            e.setCancelled(true);
        }
    }


    private FrameData getData(UUID itemFlame){
        FrameData data = null;

        try {
            if (con != null){
                if (!con.isClosed()){
                    PreparedStatement statement = con.prepareStatement("SELECT * FROM IFPTable WHERE ItemFrame = ?");
                    statement.setString(1, itemFlame.toString());
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()){
                        String createUser = resultSet.getString("CreateUser");
                        String itemFrame = resultSet.getString("ItemFrame");

                        return new FrameData(UUID.fromString(createUser), UUID.fromString(itemFrame));
                    }
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

    private void setData(UUID createUser, UUID itemFlame){
        new Thread(() -> {
            try {
                if (con != null){
                    if (getData(itemFlame) == null){
                        PreparedStatement statement1 = con.prepareStatement("INSERT INTO `IFPTable` (`CreateUser`, `ItemFrame`) VALUES (?, ?);");
                        statement1.setString(1, createUser.toString());
                        statement1.setString(2, itemFlame.toString());
                        statement1.execute();
                    } else {
                        PreparedStatement statement1 = con.prepareStatement("DELETE FROM `IFPTable` WHERE `CreateUser` = ? AND `ItemFrame` = ?");
                        statement1.setString(1, createUser.toString());
                        statement1.setString(2, itemFlame.toString());
                        statement1.execute();
                    }
                }
            } catch (Exception e) {
                if (plugin.getConfig().getBoolean("errorPrint")){
                    plugin.getLogger().info(ChatColor.RED + "SQLエラーを検知しました。");
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
