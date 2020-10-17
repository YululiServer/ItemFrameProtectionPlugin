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
                            TextComponent countText = new TextComponent();
                            TextComponent countClick = new TextComponent(ChatColor.GOLD + "/ifp count");
                            countClick.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ifp count"));
                            countText.addExtra(countClick);
                            countText.addExtra(" --- 全体保護数");
                            TextComponent userText = new TextComponent();
                            TextComponent userClick = new TextComponent(ChatColor.GOLD + "/ifp user <user>");
                            userClick.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ifp user " + player.getName()));
                            userText.addExtra(userClick);
                            userText.addExtra(" --- ユーザーロックリスト");

                            sender.sendMessage(countText);
                            sender.sendMessage(userText);
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
                                        if (player.hasPermission("ifp.op")){
                                            TextComponent text1 = new TextComponent();
                                            text1.addExtra("X: " + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ() + " ");
                                            TextComponent click1 = new TextComponent(ChatColor.AQUA + "[Teleport]");
                                            click1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + loc.getBlockX() + " " +loc.getBlockY() + " " + loc.getBlockZ()));
                                            text1.addExtra(click1);
                                            sender.sendMessage(text1);
                                        } else {
                                            sender.sendMessage("X: " + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ());
                                        }
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
                            sender.sendMessage("---- 額縁保護リスト ----");
                            for (FrameData data : itemFrameList) {
                                if (data.getCreateUser().equals(player.getUniqueId())) {
                                    Entity entity = Bukkit.getServer().getEntity(data.getItemFrame());
                                    if (entity != null){
                                        Location loc = entity.getLocation();
                                        sender.sendMessage("ワールド名： " + loc.getWorld().getName());
                                        if (player.hasPermission("ifp.op")){
                                            TextComponent text1 = new TextComponent();
                                            text1.addExtra("X: " + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ() + " ");
                                            TextComponent click1 = new TextComponent(ChatColor.AQUA + "[Teleport]");
                                            click1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + loc.getBlockX() + " " +loc.getBlockY() + " " + loc.getBlockZ()));
                                            text1.addExtra(click1);
                                            sender.sendMessage(text1);
                                        } else {
                                            sender.sendMessage("X: " + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ());
                                        }
                                    }
                                }
                            }
                        }

                        if (args[0].toLowerCase().startsWith("user") && !player.hasPermission("ifp.op")) {
                            sender.sendMessage(ChatColor.RED + "権限がありません。");
                        }

                        if (args[0].toLowerCase().startsWith("user") && player.hasPermission("ifp.op")) {
                            sender.sendMessage(ChatColor.RED + "ユーザー名選択するのが抜けているぞ");
                        }
                    } else if (args.length == 2 && sender instanceof Player) {
                        Player player = (Player) sender;
                        if (args[0].toLowerCase().startsWith("user") && !player.hasPermission("ifp.op")){
                            sender.sendMessage(ChatColor.RED + "権限がありません。");
                        }

                        if (args[0].toLowerCase().startsWith("user") && player.hasPermission("ifp.op")) {

                            List<FrameData> itemFrameList = dataAPI.getItemFrameList();
                            sender.sendMessage("---- "+args[1]+"さんの額縁保護リスト ----");
                            synchronized(itemFrameList) {
                                for (FrameData data : itemFrameList) {
                                    if (UUID2UserName.getUser(data.getCreateUser()).equals(args[1])){
                                        Entity entity = Bukkit.getServer().getEntity(data.getItemFrame());
                                        if (entity != null) {
                                            Location loc = entity.getLocation();
                                            TextComponent text1 = new TextComponent();
                                            text1.addExtra("X: " + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ() + " ");
                                            TextComponent click1 = new TextComponent(ChatColor.AQUA + "[Teleport]");
                                            click1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + loc.getBlockX() + " " +loc.getBlockY() + " " + loc.getBlockZ()));
                                            text1.addExtra(click1);
                                            sender.sendMessage(text1);
                                        }
                                    }
                                }
                            }
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
