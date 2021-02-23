package me.symi.survivalboss;

import me.symi.survivalboss.commands.BossyCommand;
import me.symi.survivalboss.commands.OwnSurvivalBossCommand;
import me.symi.survivalboss.config.ConfigManager;
import me.symi.survivalboss.config.FileManager;
import me.symi.survivalboss.listeners.EntityListeners;
import me.symi.survivalboss.listeners.InventoryListeners;
import me.symi.survivalboss.managers.BossManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;

public class Main extends JavaPlugin {

    private static Main INSTANCE;
    private ConfigManager configManager;
    private FileManager fileManager;
    private ArrayList<BossManager> bossManagers = new ArrayList<>();
    private int minutes_left = 30;

    @Override
    public void onLoad()
    {
        INSTANCE = this;
    }

    @Override
    public void onEnable()
    {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new EntityListeners(), this);
        pluginManager.registerEvents(new InventoryListeners(), this);
        getCommand("ownsurvivalboss").setExecutor(new OwnSurvivalBossCommand());
        getCommand("bossy").setExecutor(new BossyCommand());
        configManager = new ConfigManager(this);
        fileManager = new FileManager(this);
        File dir = new File(getDataFolder() + File.separator + "bossy");
        for(File file : dir.listFiles())
        {
            bossManagers.add(new BossManager(this, file));
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if(minutes_left <= 1)
                {
                    setMinutes_left(60);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ownsurvivalboss");
                }
                else
                {
                    minutes_left--;
                }
            }
        }.runTaskTimer(this, 1200L, 1200L);
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public ArrayList<BossManager> getBossManagers() {
        return bossManagers;
    }

    public static Main getInstance()
    {
        return INSTANCE;
    }

    public int getMinutes_left() {
        return minutes_left;
    }

    public void setMinutes_left(int minutes_left) {
        this.minutes_left = minutes_left;
    }
}
