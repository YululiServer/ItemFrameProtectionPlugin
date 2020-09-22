package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

class ItemFrameCommand implements CommandExecutor {

    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("ItemFrameProtectionPlugin");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Thread thread = new Thread(() -> {
            boolean flag = true;
            while (flag){
                try {
                    if (args.length == 0 && sender instanceof Player) {
                        Player player = (Player) sender;

                        if (player.hasPermission("ifp.op")) {
                            player.sendMessage("----- ItemFrameProtectionPlugin Ver " + plugin.getDescription().getVersion());
                            player.sendMessage("とくに何も起きません。残念でした。");
                            // player.sendMessage("/ifp list --- 額縁ロックMyリスト");
                            // player.sendMessage("/ifp alllist --- 額縁ロック全ロックリスト");
                        } else {
                            player.sendMessage("----- ItemFrameProtectionPlugin Ver " + plugin.getDescription().getVersion());
                            player.sendMessage("とくに何も起きません。残念でした。");
                            // player.sendMessage("/ifp list --- 額縁ロックMyリスト");
                        }
                    } else if (args.length == 0) {
                        sender.sendMessage("----- ItemFrameProtectionPlugin Ver " + plugin.getDescription().getVersion());
                        sender.sendMessage("とくに何も起きません。残念でした。");
                        // sender.sendMessage("/ifp list --- 額縁ロックリスト");
                    }
                } catch (Exception e) {
                    if (plugin.getConfig().getBoolean("errorPrint")) {
                        plugin.getLogger().info(ChatColor.RED + "エラーを検知しました");
                        e.printStackTrace();
                    }
                }

                flag = false;
            }
        });

        thread.start();
        return true;
    }
}
