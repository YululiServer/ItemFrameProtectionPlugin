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
    private int BlockX;
    private int BlockY;
    private int BlockZ;

    private Plugin plugin = null;

    public FrameData(Plugin plugin){
        this.plugin = plugin;
    }

    public FrameData(UUID createUser, int blockX, int blockY, int blockZ){
        this.CreateUser = createUser;
        this.BlockX = blockX;
        this.BlockY = blockY;
        this.BlockZ = blockZ;
    }

    public UUID getCreateUser() {
        return CreateUser;
    }

    public int getBlockX() {
        return BlockX;
    }

    public int getBlockY() {
        return BlockY;
    }

    public int getBlockZ() {
        return BlockZ;
    }

    public FrameData getData(Connection con, int x, int y, int z){
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM IFPTable WHERE X = ? AND Y = ? AND Z = ?");
            statement.setInt(1, x);
            statement.setInt(2, y);
            statement.setInt(3, z);
            ResultSet set = statement.executeQuery();
            if (set.next()){
                return new FrameData(UUID.fromString(set.getString("CreateUser")), set.getInt("X"), set.getInt("Y"), set.getInt("Z"));
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

    public FrameData getData(Connection con, Location loc){
        return getData(con, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public boolean setData(Connection con, UUID createUser, int x, int y, int z){
        try {
            PreparedStatement statement;
            if (getData(con, x, y, z) == null){
                statement = con.prepareStatement("INSERT INTO `IFPTable` (`CreateUser`, `X`, `Y`, `Z`) VALUES (?, ?, ?, ?); ");
                statement.setString(1, createUser.toString());
                statement.setInt(2, x);
                statement.setInt(3, y);
                statement.setInt(4, z);
            } else {
                statement = con.prepareStatement("DELETE FROM `IFPTable` WHERE `CreateUser` = ? AND `X` = ? AND `Y` = ? AND `Z` = ?");
                statement.setString(1, createUser.toString());
                statement.setInt(2, x);
                statement.setInt(3, y);
                statement.setInt(4, z);
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

    public boolean setData(Connection con, UUID createUser, Location loc){
        return setData(con, createUser, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
}
