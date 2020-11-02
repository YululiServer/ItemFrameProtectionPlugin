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
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.DataAPI;
import xyz.n7mn.dev.yululi.itemframeprotectionplugin.data.FrameData;

import java.util.List;

class IFPCommand implements CommandExecutor {

    final DataAPI api;
    final Plugin plugin = Bukkit.getPluginManager().getPlugin("ItemframeProtectionPlugin");

    public IFPCommand(DataAPI api){
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        new BukkitRunnable() {
            @Override
            public void run() {
                if (sender instanceof Player){

                    Player player = (Player) sender;

                    if (args.length == 0){

                        sender.sendMessage("額縁保護プラグイン Ver " + plugin.getDescription().getVersion());
                        if (player.hasPermission("ifp.op")){

                            sender.sendMessage(ChatColor.GOLD + "/ifp admin --- 全部出す");
                            sender.sendMessage(ChatColor.GOLD + "/ifp user <UserName> --- 指定したユーザーの額縁リストを出す");

                        }

                        TextComponent text = new TextComponent();
                        TextComponent click = new TextComponent(ChatColor.YELLOW + "すべてを表示する場合はこちらをクリック！");
                        click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ifp all"));
                        text.addExtra(click);
                        sender.sendMessage(text);

                        int i = 0;

                        List<FrameData> list = api.getListByFrameData(true);
                        for (FrameData data : list){

                            Entity entity = Bukkit.getEntity(data.getItemFrameUUID());

                            if (entity == null){
                                continue;
                            }

                            if (!data.getProtectUser().equals(player.getUniqueId())){
                                continue;
                            }

                            if (i > 5){
                                break;
                            }

                            Location location = entity.getLocation();

                            StringBuffer sb = new StringBuffer();

                            sb.append("ワールド名 : ");
                            sb.append(location.getWorld().getName());
                            sb.append(" X: ");
                            sb.append(location.getBlockX());
                            sb.append(" Y: ");
                            sb.append(location.getBlockY());
                            sb.append(" Z: ");
                            sb.append(location.getBlockZ());

                            String dataText = sb.toString();

                            if (player.hasPermission("ifp.op")){

                                TextComponent text1 = new TextComponent(dataText);
                                TextComponent click1 = new TextComponent(ChatColor.AQUA + "[Teleport]");
                                click1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp "+ location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ()));
                                text1.addExtra(click1);
                                sender.sendMessage(text1);

                            } else {
                                sender.sendMessage(dataText);
                            }

                            i++;
                        }

                    }

                    if (args.length == 1){

                        if (args[0].toLowerCase().equals("admin") && player.hasPermission("ifp.op")){

                            List<FrameData> list = api.getListByFrameData(false);
                            sender.sendMessage("---- ItemFrameProtectPlugin Ver " + plugin.getDescription().getVersion() + " ----");
                            sender.sendMessage(ChatColor.GREEN + "保護件数 : " + list.size() + " 件");

                            for (FrameData data : list){

                                Entity entity = Bukkit.getEntity(data.getItemFrameUUID());
                                if (entity == null){
                                    continue;
                                }

                                if (data.isActive()){
                                    sender.sendMessage(ChatColor.GREEN + "■ "+ChatColor.RESET+"UUID: " + data.getItemFrameUUID() + " WorldName: " + entity.getLocation().getWorld().getName() + " X: " + entity.getLocation().getBlockX() + " Y: " + entity.getLocation().getBlockY() + " Z: " + entity.getLocation().getBlockZ() + " UserUUID: " + data.getCreateDate());
                                } else {
                                    sender.sendMessage(ChatColor.RED + "■ "+ChatColor.RESET+"UUID: " + data.getItemFrameUUID() + " WorldName: " + entity.getLocation().getWorld().getName() + " X: " + entity.getLocation().getBlockX() + " Y: " + entity.getLocation().getBlockY() + " Z: " + entity.getLocation().getBlockZ() + " UserUUID: " + data.getCreateDate());
                                }

                            }
                        }

                        if (args[0].toLowerCase().equals("all")){
                            List<FrameData> list = api.getListByFrameData(true);
                            for (FrameData data : list){

                                Entity entity = Bukkit.getEntity(data.getItemFrameUUID());

                                if (entity == null){
                                    continue;
                                }

                                if (!data.getProtectUser().equals(player.getUniqueId())){
                                    continue;
                                }

                                Location location = entity.getLocation();

                                StringBuffer sb = new StringBuffer();

                                sb.append("ワールド名 : ");
                                sb.append(location.getWorld().getName());
                                sb.append(" X: ");
                                sb.append(location.getBlockX());
                                sb.append(" Y: ");
                                sb.append(location.getBlockY());
                                sb.append(" Z: ");
                                sb.append(location.getBlockZ());

                                String dataText = sb.toString();

                                if (player.hasPermission("ifp.op")){

                                    TextComponent text1 = new TextComponent(dataText);
                                    TextComponent click1 = new TextComponent(ChatColor.AQUA + "[Teleport]");
                                    click1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp "+ location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ()));
                                    text1.addExtra(click1);
                                    sender.sendMessage(text1);

                                } else {
                                    sender.sendMessage(dataText);
                                }

                            }
                        }

                        if (args[0].toLowerCase().equals("forcecache")){
                            api.cacheToSQL();
                        }
                    }

                    if (args.length == 2){

                        if (args[0].toLowerCase().equals("user") && player.hasPermission("ifp.op")){

                            Player targetPlayer = Bukkit.getPlayer(args[1]);
                            if (targetPlayer == null){

                                sender.sendMessage(ChatColor.RED + "プレーヤーが見つかりません。");
                                return;
                            }

                            sender.sendMessage("---- " + targetPlayer.getName() + "さんの額縁保護リスト ----");
                            List<FrameData> list = api.getListByFrameData(true);
                            for (FrameData data : list){

                                Entity entity = Bukkit.getEntity(data.getItemFrameUUID());

                                if (entity == null){
                                    continue;
                                }

                                if (!data.getProtectUser().equals(targetPlayer.getUniqueId())){
                                    continue;
                                }

                                Location location = entity.getLocation();

                                StringBuffer sb = new StringBuffer();

                                sb.append("ワールド名 : ");
                                sb.append(location.getWorld().getName());
                                sb.append(" X: ");
                                sb.append(location.getBlockX());
                                sb.append(" Y: ");
                                sb.append(location.getBlockY());
                                sb.append(" Z: ");
                                sb.append(location.getBlockZ());

                                String dataText = sb.toString();

                                TextComponent text1 = new TextComponent(dataText);
                                TextComponent click1 = new TextComponent(ChatColor.AQUA + "[Teleport]");
                                click1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp "+ location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ()));
                                text1.addExtra(click1);
                                sender.sendMessage(text1);

                            }
                        }

                    }



                } else {

                    sender.sendMessage("---- ItemFrameProtectPlugin Ver " + plugin.getDescription().getVersion() + " ----");
                    List<FrameData> list = api.getListByFrameData(false);
                    sender.sendMessage(ChatColor.GREEN + "ProtectCount : " + list.size());

                    for (FrameData data : list){

                        Entity entity = Bukkit.getEntity(data.getItemFrameUUID());
                        if (entity == null){
                            continue;
                        }

                        if (data.isActive()){
                            sender.sendMessage(ChatColor.GREEN + "■ "+ChatColor.RESET+"UUID: " + data.getItemFrameUUID() + " WorldName: " + entity.getLocation().getWorld().getName() + " X: " + entity.getLocation().getBlockX() + " Y: " + entity.getLocation().getBlockY() + " Z: " + entity.getLocation().getBlockZ() + " UserUUID: " + data.getCreateDate());
                        } else {
                            sender.sendMessage(ChatColor.RED + "■ "+ChatColor.RESET+"UUID: " + data.getItemFrameUUID() + " WorldName: " + entity.getLocation().getWorld().getName() + " X: " + entity.getLocation().getBlockX() + " Y: " + entity.getLocation().getBlockY() + " Z: " + entity.getLocation().getBlockZ() + " UserUUID: " + data.getCreateDate());
                        }

                    }
                }
            }
        }.runTaskLaterAsynchronously(plugin, 0L);


        return true;
    }
}
