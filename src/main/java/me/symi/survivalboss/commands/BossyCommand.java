package me.symi.survivalboss.commands;

import me.symi.survivalboss.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BossyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if(!(sender instanceof Player))
        {
            return true;
        }

        final Player player = (Player) sender;
        player.openInventory(Main.getInstance().getConfigManager().getBossyInventory());

        return true;
    }
}
