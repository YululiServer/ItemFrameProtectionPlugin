package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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

                        if (player.hasPermission("ifp.op")){
                            sender.sendMessage("----- ItemFrameProtectionPlugin Ver " + plugin.getDescription().getVersion());
                            sender.sendMessage("/ifp user <Username>でユーザーの額縁ロックリストが出る予定。");

                            TextComponent text = new TextComponent();
                            text.addExtra(ChatColor.YELLOW + "最新5件を表示しています。");
                            TextComponent click = new TextComponent(ChatColor.YELLOW + "すべてを表示する場合はこちらをクリック！");
                            click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ifp list"));
                            text.addExtra(click);
                            sender.sendMessage(text);
                        }
                    } else if (args.length == 0) {
                        // sender.sendMessage("----- ItemFrameProtectionPlugin Ver " + plugin.getDescription().getVersion());
                        TextComponent text = new TextComponent();
                        text.addExtra(ChatColor.YELLOW + "最新5件を表示しています。");
                        TextComponent click = new TextComponent(ChatColor.YELLOW + "すべてを表示する場合はこちらをクリック！");
                        click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ifp list"));
                        text.addExtra(click);
                        sender.sendMessage(text);
                        // sender.sendMessage("/ifp list --- 額縁ロックリスト");
                    } else if (args.length == 1 && sender instanceof Player) {
                        Player player = (Player) sender;

                        List<FrameData> itemFrameList = dataAPI.getItemFrameList();
                        if (args[0].toLowerCase().startsWith("count")) {
                            sender.sendMessage(ChatColor.GREEN + "現在 " + itemFrameList.size() + "件 保護されてます。");
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
