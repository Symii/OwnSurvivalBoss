package me.symi.survivalboss.config;

import me.symi.survivalboss.Main;
import me.symi.survivalboss.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ConfigManager {

    private Main plugin;
    private String world_name;
    private List<String> boss_spawn_message;
    private int spawn_x, spawn_z;
    private int boss_spawn_range;

    public ConfigManager(Main plugin)
    {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        loadConfig();
    }

    public void loadConfig()
    {
        FileConfiguration config = plugin.getConfig();
        world_name = config.getString("world-name");
        spawn_x = config.getInt("spawn-location.x");
        spawn_z = config.getInt("spawn-location.z");
        boss_spawn_range = config.getInt("boss-spawn-range");
        boss_spawn_message = ChatUtil.fixColors(config.getStringList("boss-spawn-message"));


    }

    public String getWorld_name() {
        return world_name;
    }

    public List<String> getBoss_spawn_message() {
        return boss_spawn_message;
    }

    public int getSpawn_x() {
        return spawn_x;
    }

    public int getSpawn_z() {
        return spawn_z;
    }

    public int getBoss_spawn_range() {
        return boss_spawn_range;
    }

    public Inventory getBossyInventory()
    {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatUtil.fixColors("&4&lBossy &8| &eInformacje"));
        FileConfiguration config = plugin.getConfig();
        for(String s : config.getConfigurationSection("gui").getKeys(false))
        {
            int index = Integer.parseInt(s);
            ItemStack item = new ItemStack(Material.valueOf(config.getString("gui." + s + ".material")));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(ChatUtil.fixColors(config.getString("gui." + s + ".name")));

            List<String> lore = ChatUtil.fixColors(config.getStringList("gui." + s + ".lore"));
            for(int i = 0; i < lore.size(); i++)
            {
                lore.set(i, lore.get(i).replace("%minutes_left%", String.valueOf(Main.getInstance().getMinutes_left())));
            }
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
            inventory.setItem(index, item);
        }
        return inventory;
    }

}
