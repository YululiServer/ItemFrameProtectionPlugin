package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.ChatColor;
import org.bukkit.event.Cancellable;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

class ItemFrameTimer extends BukkitRunnable implements Cancellable {

    private final Plugin plugin;
    private final Connection con;
    private boolean flag = false;
    private final ItemFrameData data;

    public ItemFrameTimer(Plugin plugin, Connection con, ItemFrameData data){
        this.plugin = plugin;
        this.con = con;
        this.data = data;
    }

    @Override
    public void run() {
        if (!flag){

            if (con != null){
                try {
                    final PreparedStatement statement1;
                    final PreparedStatement statement2;
                    if (plugin.getConfig().getBoolean("useMySQL")){
                        statement1 = con.prepareStatement("TRUNCATE IFPTable;");
                        statement2 = con.prepareStatement("TRUNCATE IFPTable2;");
                    } else {
                        statement1 = con.prepareStatement("DELETE FROM IFPTable;");
                        statement2 = con.prepareStatement("DELETE FROM IFPTable2;");
                    }
                    statement1.execute();
                    statement1.close();
                    statement2.execute();
                    statement2.close();

                    List<FrameData> itemFrameList = data.getItemFrameList();
                    for (FrameData data : itemFrameList){
                        PreparedStatement statement3 = con.prepareStatement("INSERT INTO `IFPTable` (`CreateUser`, `ItemFrame`) VALUES (?, ?);");
                        statement3.setString(1, data.getCreateUser().toString());
                        statement3.setString(2, data.getItemFrame().toString());
                        statement3.execute();
                        statement3.close();
                    }

                    List<DropData> dropList = data.getDropList();
                    for (DropData data : dropList){
                        PreparedStatement statement4 = con.prepareStatement("INSERT INTO `IFPTable2` (`DropUser`, `ItemUUID`) VALUES (?, ?);");
                        statement4.setString(1, data.getDropUser().toString());
                        statement4.setString(2, data.getItemUUID().toString());
                        statement4.execute();
                        statement4.close();
                    }
                } catch (SQLException e){
                    if (plugin.getConfig().getBoolean("errorPrint")) {
                        plugin.getLogger().info(ChatColor.RED + "SQLエラーを検知しました。");
                        e.printStackTrace();
                    }
                }
            }


            new ItemFrameTimer(plugin, con, data).runTaskLaterAsynchronously(plugin, 120L);
        }
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.flag = cancel;
    }

    public boolean isCancelled(){
        return this.flag;
    }
}
