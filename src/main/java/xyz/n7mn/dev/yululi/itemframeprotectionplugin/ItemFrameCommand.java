package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

class ItemFrameCommand implements CommandExecutor {

    private final Plugin plugin;
    private final ItemFrameData dataAPI;

    public ItemFrameCommand(Plugin plugin, ItemFrameData data){
        this.plugin = plugin;
        this.dataAPI = data;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        new BukkitRunnable(){

            @Override
            public void run() {
                try {
                    if (args.length == 0 && sender instanceof Player) {
                        Player player = (Player) sender;
                        sender.sendMessage("----- ItemFrameProtectionPlugin Ver " + plugin.getDescription().getVersion() + " -----");
                        if (player.hasPermission("ifp.op")) {
                            sender.sendMessage(ChatColor.GOLD + "/ifp count --- 全体保護数");
                            sender.sendMessage(ChatColor.GOLD + "/ifp user <Username> --- ユーザーロックリスト");
                        }

                        TextComponent text = new TextComponent();
                        text.addExtra(ChatColor.YELLOW + "5件を表示しています。");
                        TextComponent click = new TextComponent(ChatColor.YELLOW + "すべてを表示する場合はこちらをクリック！");
                        click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ifp list"));
                        text.addExtra(click);
                        sender.sendMessage(text);

                        List<FrameData> list = dataAPI.getItemFrameList();

                        synchronized(list) {

                            int count = 0;

                            for (FrameData data : list){

                                if (data.getCreateUser().equals(player.getUniqueId())){

                                    Entity entity = Bukkit.getServer().getEntity(data.getItemFrame());
                                    if (entity != null){
                                        Location loc = entity.getLocation();
                                        sender.sendMessage("ワールド名： " + loc.getWorld().getName());
                                        sender.sendMessage("X: " + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ());
                                        count++;
                                    }

                                }
                                if (count >= 5){
                                    break;
                                }
                            }

                        }


                    } else if (args.length == 0) {
                        sender.sendMessage("----- ItemFrameProtectionPlugin Ver " + plugin.getDescription().getVersion() + " -----");
                        List<FrameData> itemFrameList = dataAPI.getItemFrameList();
                        int i = 1;
                        synchronized(itemFrameList) {
                            for (FrameData data : itemFrameList) {
                                sender.sendMessage(ChatColor.YELLOW + (i + " : "));
                                sender.sendMessage(ChatColor.YELLOW + "   UUID : " + data.getItemFrame());
                                String user = UUID2UserName.getUser(data.getCreateUser());
                                if (user != null){
                                    sender.sendMessage(ChatColor.YELLOW + "   LockedUser : " + user);
                                } else {
                                    sender.sendMessage(ChatColor.YELLOW + "   LockedUser : " + data.getCreateUser());
                                }

                                Entity entity = Bukkit.getServer().getEntity(data.getItemFrame());
                                if (entity != null){
                                    Location loc = entity.getLocation();
                                    String name = loc.getWorld().getName();
                                    int blockX = loc.getBlockX();
                                    int blockY = loc.getBlockY();
                                    int blockZ = loc.getBlockZ();
                                    sender.sendMessage(ChatColor.YELLOW + "   WorldName : " + name);
                                    sender.sendMessage(ChatColor.YELLOW + "   X : " + blockX);
                                    sender.sendMessage(ChatColor.YELLOW + "   Y : " + blockY);
                                    sender.sendMessage(ChatColor.YELLOW + "   Z : " + blockZ);
                                }
                            }
                        }
                    } else if (args.length == 1 && sender instanceof Player) {
                        Player player = (Player) sender;

                        List<FrameData> itemFrameList = dataAPI.getItemFrameList();
                        if (args[0].toLowerCase().startsWith("count") && player.hasPermission("ifp.op")) {
                            sender.sendMessage(ChatColor.GREEN + "現在 " + itemFrameList.size() + "件 保護されてます。");
                        }

                        if (args[0].toLowerCase().startsWith("list")) {
                            sender.sendMessage(ChatColor.GREEN + "準備中...");
                        }
                    }
                } catch (Exception e) {
                    if (plugin.getConfig().getBoolean("errorPrint")) {
                        plugin.getLogger().info(ChatColor.RED + "エラーを検知しました");
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskLaterAsynchronously(plugin, 0L);

        return true;
    }
}
