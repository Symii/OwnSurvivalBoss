package me.symi.survivalboss.config;

import me.symi.survivalboss.Main;
import java.io.File;

public class FileManager {

    private final Main plugin;

    public FileManager(Main plugin)
    {
        this.plugin = plugin;
        createConfig();
    }

    private void createConfig()
    {
        File dark_knight_file = new File(plugin.getDataFolder() + File.separator + "bossy", "dark_knight.yml");
        if(!dark_knight_file.exists())
        {
            dark_knight_file.getParentFile().mkdir();
            plugin.saveResource("dark_knight.yml", false);
            File createdFile = new File(plugin.getDataFolder(), "dark_knight.yml");
            createdFile.renameTo(new File(plugin.getDataFolder() + File.separator + "bossy" + File.separator + "dark_knight.yml"));
        }

        File zombie_king_file = new File(plugin.getDataFolder() + File.separator + "bossy", "zombie_king.yml");
        if(!zombie_king_file.exists())
        {
            zombie_king_file.getParentFile().mkdir();
            plugin.saveResource("zombie_king.yml", false);
            File createdFile = new File(plugin.getDataFolder(), "zombie_king.yml");
            createdFile.renameTo(new File(plugin.getDataFolder() + File.separator + "bossy" + File.separator + "zombie_king.yml"));
        }

    }




}
