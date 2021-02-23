package me.symi.survivalboss.commands;

import me.symi.survivalboss.Main;
import me.symi.survivalboss.managers.BossManager;
import me.symi.survivalboss.utils.ChatUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Random;

public class OwnSurvivalBossCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if(sender.hasPermission("ownsurvivalboss.spawn"))
        {
            sender.sendMessage(ChatUtil.fixColors("&eBossy &8| &apomyslnie przywolano bossa na mape!"));
            Main.getInstance().setMinutes_left(60);
            BossManager bossManager = Main.getInstance().getBossManagers().get(new Random().nextInt(Main.getInstance().getBossManagers().size()));
            bossManager.spawnBoss();
        }
        else
        {
            sender.sendMessage(ChatUtil.fixColors("&eBossy &8| &cnie posiadasz permisji &6ownsurvivalboss.spawn"));
        }

        return true;
    }
}
