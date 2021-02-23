package me.symi.survivalboss.utils;

import me.symi.survivalboss.Main;
import me.symi.survivalboss.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.Random;

public class RandomUtil {

    public static int getRandomNumber(int min, int max)
    {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static Location getRandomLocation()
    {
        ConfigManager configManager = Main.getInstance().getConfigManager();
        final World world = Bukkit.getWorld(configManager.getWorld_name());
        double x = getRandomNumber(0, configManager.getBoss_spawn_range());
        double z = getRandomNumber(0, configManager.getBoss_spawn_range());
        int y = 40;
        final int xMinus = new Random().nextInt(2);
        final int zMinus = new Random().nextInt(2);
        if (xMinus == 1) {
            x *= -1.0;
        }
        if (zMinus == 1) {
            z *= -1.0;
        }
        final Location temp = new Location(world, x, y, z);
        final Location temp2 = new Location(world, x, (y + 1), z);
        boolean canTeleport = false;
        while (!canTeleport)
        {
            if (temp.getBlock().getType() == Material.AIR && temp2.getBlock().getType() == Material.AIR) {
                canTeleport = true;
                break;
            }
            if (temp.getBlock().getType().toString().contains("LAVA") || temp.getBlock().getType().toString().contains("WATER") || y >= 100) {
                x = new Random().nextInt(configManager.getBoss_spawn_range());
                z = new Random().nextInt(configManager.getBoss_spawn_range());
                y = 40;
                temp.setX(x);
                temp.setY((double)y);
                temp.setZ(z);
                temp2.setX(x);
                temp2.setY((double)(y + 1));
                temp2.setZ(z);
            }
            else {
                ++y;
                temp.setY((double)y);
                temp2.setY((double)(y + 1));
            }
        }
        if (x >= 0.0) {
            x += 0.5;
        }
        else {
            x -= 0.5;
        }
        if (z >= 0.0) {
            z += 0.5;
        }
        else {
            z -= 0.5;
        }

        return new Location(world, x, y, z);
    }

}
