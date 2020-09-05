package xyz.n7mn.dev.yululi.itemframeprotectionplugin.api;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class FrameData {

    private UUID CreateUser;
    private UUID ItemFrameUUID;

    private Plugin plugin = null;

    public FrameData(Plugin plugin){
        this.plugin = plugin;
    }


    public FrameData(UUID createUser, UUID itemFrameUUID){
        this.CreateUser = createUser;
        this.ItemFrameUUID = itemFrameUUID;
    }

    public UUID getCreateUser() {
        return CreateUser;
    }

    public UUID getItemFrameUUID(){
        return ItemFrameUUID;
    }

    public FrameData getData(Connection con, UUID itemFrameUUID){
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM IFPTable WHERE ItemFrame = ?");
            statement.setString(1, itemFrameUUID.toString());
            ResultSet set = statement.executeQuery();
            if (set.next()){
                return new FrameData(UUID.fromString(set.getString("CreateUser")), UUID.fromString(set.getString("ItemFrame")));
            } else {
                return null;
            }
        } catch (Exception e){
            if (plugin != null && plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "MySQL関係でエラーが発生しました" + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }

    public boolean setData(Connection con, UUID createUser, UUID itemFrameUUID){
        try {
            PreparedStatement statement;
            if (getData(con, itemFrameUUID) == null){
                statement = con.prepareStatement("INSERT INTO `IFPTable` (`CreateUser`, `ItemFrame`) VALUES (?, ?); ");
                statement.setString(1, createUser.toString());
                statement.setString(2, itemFrameUUID.toString());
            } else {
                statement = con.prepareStatement("DELETE FROM `IFPTable` WHERE `CreateUser` = ? AND `ItemFrame` = ?");
                statement.setString(1, createUser.toString());
                statement.setString(2, itemFrameUUID.toString());
            }
            statement.execute();
            return true;
        } catch (Exception e){
            if (plugin != null && plugin.getConfig().getBoolean("errorPrint")){
                plugin.getLogger().info(ChatColor.RED + "MySQL関係でエラーが発生しました");
                e.printStackTrace();
            }
            return false;
        }
    }
}
