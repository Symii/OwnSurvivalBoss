package me.symi.survivalboss.managers;

import me.symi.survivalboss.Main;
import me.symi.survivalboss.config.ConfigManager;
import me.symi.survivalboss.utils.ChatUtil;
import me.symi.survivalboss.utils.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BossManager {

    private Main plugin;
    private File file;
    private FileConfiguration config;

    private EntityType mob_type;
    private int health;
    private String name;
    private int exp_drop_amount;

    private boolean thunder_ability, explosion_ability, throw_ability, potions_ability, fast_ability, blocking_damage_ability;
    private ItemStack weapon;
    private HashMap<ItemStack, Double> drop_items = new HashMap<>();
    private int minions_amount;

    public BossManager(Main plugin, File file)
    {
        this.plugin = plugin;
        this.file = file;
        try
        {
            config = new YamlConfiguration();
            config.load(file);
            loadBossConfig();
            load_drop_items();
        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
    }

    public void spawnBoss()
    {
        ConfigManager configManager = plugin.getConfigManager();
        final Location location = RandomUtil.getRandomLocation();



        for(String message : configManager.getBoss_spawn_message())
        {
            message = message.replace("%boss_name%", name);
            message = message.replace("%boss_x%", String.valueOf(location.getBlockX()));
            message = message.replace("%boss_y%", String.valueOf(location.getBlockY()));
            message = message.replace("%boss_z%", String.valueOf(location.getBlockZ()));
            Bukkit.broadcastMessage(message);
        }

        new BukkitRunnable()
        {
            int counter = 600;

            @Override
            public void run()
            {
                if(counter <= 0)
                {
                    this.cancel();
                    return;
                }

                for(Entity entity : location.getChunk().getEntities())
                {
                    if(entity instanceof Player)
                    {
                        Entity boss = Bukkit.getWorld(configManager.getWorld_name()).spawnEntity(location, mob_type);
                        boss.setCustomNameVisible(true);
                        boss.setCustomName(name);
                        if(boss instanceof LivingEntity)
                        {
                            LivingEntity livingEntity = (LivingEntity) boss;
                            livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
                            livingEntity.setHealth(health);
                            livingEntity.getEquipment().setItemInMainHand(weapon);
                            ItemStack helmet = new ItemStack(Material.NETHERITE_HELMET);
                            helmet.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
                            helmet.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
                            livingEntity.getEquipment().setHelmet(helmet);
                            if(isFast_ability())
                            {
                                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2300000, 2));
                            }
                        }
                        this.cancel();
                        return;
                    }
                }

                counter--;
            }
        }.runTaskTimer(Main.getInstance(), 20, 20);

    }

    private void loadBossConfig()
    {
        mob_type = EntityType.valueOf(config.getString("mob-type"));
        health = config.getInt("health");
        name = ChatUtil.fixColors(config.getString("name"));
        exp_drop_amount = config.getInt("exp-drop-amount");

        thunder_ability = config.getBoolean("abilities.thunder-on-player");
        minions_amount = config.getInt("abilities.spawn-minions");
        explosion_ability = config.getBoolean("abilities.explosions");
        throw_ability = config.getBoolean("abilities.throw-player");
        potions_ability = config.getBoolean("abilities.apply-potion-effects-to-player");
        fast_ability = config.getBoolean("abilities.mob-is-fast");
        blocking_damage_ability = config.getBoolean("abilities.block-player-damage");

        weapon = new ItemStack(Material.valueOf(config.getString("weapon.material")));
        ItemMeta boss_weapon_meta = weapon.getItemMeta();
        boss_weapon_meta.setDisplayName(ChatUtil.fixColors(config.getString("weapon.name")));
        boss_weapon_meta.setLore(ChatUtil.fixColors(config.getStringList("weapon.lore")));
        weapon.setItemMeta(boss_weapon_meta);

        weapon.addUnsafeEnchantment(Enchantment.DURABILITY, config.getInt("weapon.durability"));
        weapon.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, config.getInt("weapon.sharpness"));
        weapon.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, config.getInt("weapon.fire-aspect"));
        weapon.addUnsafeEnchantment(Enchantment.KNOCKBACK, config.getInt("weapon.knockback"));
        weapon.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, config.getInt("weapon.power"));
        weapon.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, config.getInt("weapon.punch"));
        weapon.addUnsafeEnchantment(Enchantment.ARROW_FIRE, config.getInt("weapon.flame"));
        weapon.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, config.getInt("weapon.infinity"));
        weapon.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, config.getInt("weapon.protection"));

    }



    private void load_drop_items()
    {
        for(String s : config.getConfigurationSection("drop").getKeys(false))
        {
            double chance = config.getDouble("drop." + s + ".chance");
            boolean custom_name_enabled = config.getBoolean("drop." + s + ".custom-name-enabled");
            Material material = Material.valueOf(config.getString("drop." + s + ".material").toUpperCase());
            List<String> lore;
            String itemName = ChatUtil.fixColors(config.getString("drop." + s + ".name"));
            int amount = config.getInt("drop." + s + ".amount");
            ItemStack item = new ItemStack(material, amount);

            if(custom_name_enabled)
            {
                lore = ChatUtil.fixColors(config.getStringList("drop." + s + ".lore"));
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(itemName);
                itemMeta.setLore(lore);
                item.setItemMeta(itemMeta);
            }
            drop_items.put(item, chance);
        }
    }

    public EntityType getMob_type() {
        return mob_type;
    }

    public int getHealth() {
        return health;
    }

    public String getName() {
        return name;
    }

    public int getExp_drop_amount() {
        return exp_drop_amount;
    }

    public boolean isThunder_ability() {
        return thunder_ability;
    }

    public int getMinions_amount() {
        return minions_amount;
    }

    public boolean isExplosion_ability() {
        return explosion_ability;
    }

    public boolean isThrow_ability() {
        return throw_ability;
    }

    public boolean isPotions_ability() {
        return potions_ability;
    }

    public boolean isFast_ability() {
        return fast_ability;
    }

    public boolean isBlocking_damage_ability() {
        return blocking_damage_ability;
    }

    public ItemStack getWeapon() {
        return weapon;
    }

    public List<ItemStack> getDrop_items()
    {
        ArrayList<ItemStack> items = new ArrayList<>();

        for(ItemStack item : drop_items.keySet())
        {
            if(RandomUtil.getRandomNumber(0, 100) <= drop_items.get(item))
            {
                items.add(item);
            }
        }

        return items;
    }
}
