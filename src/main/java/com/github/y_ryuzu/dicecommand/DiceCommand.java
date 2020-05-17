package com.github.y_ryuzu.dicecommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public final class DiceCommand extends JavaPlugin implements Listener {
    public static boolean CobblestoneMove = false;
    public HashMap<UUID, Integer> CT = new HashMap<>();
    private static DiceCommand plugin;
    public static Plugin getPlugin() {
        return plugin;
    }
    @Override
    public void onEnable() {
        plugin = this;
        // Plugin startup logic
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getLogger().info(ChatColor.GREEN + "プラグインが有効化されました");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info(ChatColor.BLUE + "プラグインが無効化されました");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        CT.put(e.getPlayer().getUniqueId() , 0);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // equals() の代わりに equalsIgnoreCase() を使うと、大文字小文字に関係なく、
        // 文字列の一致を確認できます。"ignite"でも"IgNiTe"でも指定可能になります。
        Player p = (Player) sender;
        if (command.getName().equalsIgnoreCase("dice")) {
            if(CT.get(p.getUniqueId()) == null) {
                CT.put(p.getUniqueId() , 0);
            }
            if(CT.get(p.getUniqueId()) > 0) {
                sender.sendMessage(ChatColor.RED + "クールダウン中");
                return true;
            }
            if(args.length < 1){
                sender.sendMessage(ChatColor.RED + "使い方: /" + label + " <2～99999999>");
                return true;
            }
            try {
                // キャストしてみる
                int V = Integer.parseInt(args[0]);
                if(V <= 1) {
                    sender.sendMessage(ChatColor.RED + "2以上の数を入れようぜ！！！");
                    p.getWorld().playSound(p.getLocation() , Sound.ENTITY_VILLAGER_NO , 1 , 1);
                    return true;
            }
                if(V >= 100000000) {
                    sender.sendMessage(ChatColor.RED + "数が大きすぎるぜ！！！");
                    p.getWorld().playSound(p.getLocation() , Sound.ENTITY_VILLAGER_NO , 1 , 1);
                    return true;
                }
                Random random = new Random();
                int randomValue = random.nextInt(V);
                p.getWorld().playSound(p.getLocation() , Sound.ENTITY_PLAYER_LEVELUP , 1 , 1);
                CoolDown(p , 3);
                Bukkit.broadcastMessage(ChatColor.GRAY + p.getName() + ChatColor.LIGHT_PURPLE + "が" + ChatColor.AQUA +args[0] + ChatColor.LIGHT_PURPLE + "ダイス振って" + ChatColor.GOLD + (randomValue + 1) + ChatColor.LIGHT_PURPLE + "を出しました");
                // 成功したのでConfigにセットする
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "数字入れようぜ！！！");
                p.getWorld().playSound(p.getLocation() , Sound.ENTITY_VILLAGER_NO , 1 , 1);
                return true;
            }
        }
        return true;
    }

    public void CoolDown (Player p , int CoolDown) {
        BukkitRunnable task = new BukkitRunnable() {
            int CTCount = CoolDown +1;
            public void run() {
                CTCount--;
                CT.put(p.getUniqueId() , CTCount);
                if(CTCount == 0) {
                    cancel();
                    return;
                }
            }
        };
        task.runTaskTimer(DiceCommand.getPlugin() , CoolDown , 20);
    }
}
