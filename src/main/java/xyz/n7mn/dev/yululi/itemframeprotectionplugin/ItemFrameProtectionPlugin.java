package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Pose;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.paper.nbt.NBTTagCompound;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class ItemFrameProtectionPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        String MySQLServer = getConfig().getString("MySQLServer");
        String MySQLUsername = getConfig().getString("MySQLUsername");
        String MySQLPassword = getConfig().getString("MySQLPassword");
        String MySQLDatabase = getConfig().getString("MySQLDatabase");
        String MySQLOption = getConfig().getString("MySQLOption");

        getLogger().info("Started ItemFrameProtectionPlugin Ver "+getDescription().getVersion()+"!!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Disabled ItemFrameProtectionPlugin Ver "+getDescription().getVersion()+"!!");
    }
}
