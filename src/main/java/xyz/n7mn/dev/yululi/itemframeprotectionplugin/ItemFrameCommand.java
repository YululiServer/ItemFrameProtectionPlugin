package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

class ItemFrameCommand implements CommandExecutor {

    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("ItemFrameProtectionPlugin");

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
                            sender.sendMessage("/ifp listで全額縁ロックリストが出る予定。");
                        }
                    } else if (args.length == 0) {
                        sender.sendMessage("----- ItemFrameProtectionPlugin Ver " + plugin.getDescription().getVersion());
                        sender.sendMessage("とくに何もまだ起きません。残念でした。");
                        // sender.sendMessage("/ifp list --- 額縁ロックリスト");
                    } else if (args.length == 1 && sender instanceof Player) {
                        Player player = (Player) sender;

                        if (args[0].toLowerCase().startsWith("list")){
                            sender.sendMessage("まだ何も起きません。");
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
