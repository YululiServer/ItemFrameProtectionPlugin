package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void BlockBreakEvent (HangingBreakEvent e){
        // 額縁壊されるとき

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityDamageEvent (EntityDamageEvent e){
        // 額縁の中身消されたとき

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void EntityDamageEvent (EntityDamageByEntityEvent e){
        // 額縁の中身を取り出されるとき

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
                plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
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
                    plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                    e.printStackTrace();
                }
            }
        });
    }

}
