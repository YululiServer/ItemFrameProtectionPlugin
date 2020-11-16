package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlastFurnace;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.*;

import javax.swing.*;
import java.util.List;

class AutoRemoveTimer extends BukkitRunnable {

    private final DataAPI api;
    private final Plugin plugin;

    public AutoRemoveTimer(DataAPI api, Plugin plugin){
        this.api = api;
        this.plugin = plugin;
    }

    @Override
    public void run() {

        if (plugin.isEnabled()){


            try {

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        api.cacheToSQL();

                        List<FrameData> FrameDataList = api.getListByFrameData(true);
                        List<DropItemData> DropItemList = api.getListByDropItem();
                        List<BoxData> BoxDataList = api.getBoxDataList();

                        for (FrameData frameData : FrameDataList){

                            Entity entity = Bukkit.getEntity(frameData.getItemFrameUUID());
                            if (entity == null){
                                api.deleteTableByFrame(frameData.getItemFrameUUID());

                                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().getPluginManager().callEvent(new ItemFrameProtectDeleteEvent(frameData.getItemFrameUUID())));
                                continue;
                            }

                            if (entity.getType() != EntityType.ITEM_FRAME){
                                api.deleteTableByFrame(frameData.getItemFrameUUID());
                                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getServer().getPluginManager().callEvent(new ItemFrameProtectDeleteEvent(frameData.getItemFrameUUID())));
                            }

                        }

                        for (DropItemData dropItem : DropItemList){

                            Entity entity = Bukkit.getEntity(dropItem.getDropItemUUID());
                            if (entity == null){
                                api.deleteTableByItem(dropItem.getDropItemUUID());
                                continue;
                            }


                            if (entity.getType() != EntityType.DROPPED_ITEM){
                                api.deleteTableByItem(dropItem.getDropItemUUID());
                            }

                        }

                        for (BoxData data : BoxDataList){

                            if (data.getInventory() == null){

                                api.deleteBoxData(data);
                                continue;

                            }

                            Location location = data.getInventory().getLocation();

                            if (location == null){
                                api.deleteBoxData(data);
                                continue;
                            }

                            Block block = location.getBlock();

                            if (block.getType() == Material.AIR){

                                api.deleteBoxData(data);
                                continue;

                            }

                            if (
                                    block.getType() == Material.CHEST ||
                                    block.getType().name().endsWith("SHULKER_BOX") ||
                                    block.getType() == Material.TRAPPED_CHEST ||
                                    block.getType() == Material.CHEST_MINECART ||
                                    block.getType() == Material.FURNACE ||
                                    block.getType() == Material.FURNACE_MINECART ||
                                    block.getType() == Material.BLAST_FURNACE ||
                                    block.getType() == Material.SMOKER ||
                                    block.getType() == Material.HOPPER ||
                                    block.getType() == Material.HOPPER_MINECART ||
                                    block.getType() == Material.DISPENSER ||
                                    block.getType() == Material.BARREL ||
                                    block.getType() == Material.ANVIL ||
                                    block.getType() == Material.CHIPPED_ANVIL ||
                                    block.getType() == Material.DAMAGED_ANVIL
                            ) {
                                continue;
                            }

                            api.deleteBoxData(data);
                        }

                        FrameDataList.clear();
                        DropItemList.clear();
                        BoxDataList.clear();
                    }
                }.runTaskLaterAsynchronously(plugin, 0L);

                if (plugin.isEnabled()){
                    new AutoRemoveTimer(api, plugin).runTaskLaterAsynchronously(plugin, 1200L);
                    // System.out.println("test");
                }

            } catch (Exception e){

                if (plugin.getConfig().getBoolean("errorPrint")){
                    plugin.getLogger().info(ChatColor.RED + "エラーを検知しました。");
                    e.printStackTrace();
                }

            }
        }

    }
}
