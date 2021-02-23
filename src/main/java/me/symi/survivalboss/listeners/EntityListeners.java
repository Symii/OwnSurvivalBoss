package me.symi.survivalboss.listeners;

import me.symi.survivalboss.Main;
import me.symi.survivalboss.managers.BossManager;
import me.symi.survivalboss.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class EntityListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event)
    {
        Entity entity = event.getEntity();
        if(entity.getCustomName() != null)
        {
            String entity_name = entity.getCustomName();
            for(BossManager bossManager : Main.getInstance().getBossManagers())
            {
                if(bossManager.getName().equalsIgnoreCase(entity_name))
                {
                    event.getDrops().clear();

                    for(ItemStack item : bossManager.getDrop_items())
                    {
                        event.getDrops().add(item);
                    }

                    ExperienceOrb orb = (ExperienceOrb) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.EXPERIENCE_ORB);
                    orb.setExperience(bossManager.getExp_drop_amount());
                    Bukkit.broadcastMessage(ChatUtil.fixColors(entity_name + " &ezostał pokonany przez &a" + event.getEntity().getKiller().getName()));
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof LivingEntity && entity.getCustomName() != null
            && event.getDamager() instanceof Player)
        {
            LivingEntity livingEntity = (LivingEntity) entity;
            String mob_name = livingEntity.getCustomName();
            Player damager = (Player) event.getDamager();
            for(BossManager bossManager : Main.getInstance().getBossManagers())
            {
                if(bossManager.getName().equalsIgnoreCase(mob_name))
                {
                    Random rand = new Random();
                    if(bossManager.isThunder_ability() && rand.nextInt(13) == 0)
                    {
                        for(Entity e : livingEntity.getNearbyEntities(15, 10, 15))
                        {
                            if(e instanceof Player)
                            {
                                Player p = (Player) e;
                                p.sendMessage(ChatUtil.fixColors(bossManager.getName() + " &cJuz po was AHAHAaaH!"));
                                strikeLightning(e, bossManager.getName());
                                new BukkitRunnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        strikeLightning(e, bossManager.getName());
                                    }
                                }.runTaskLater(Main.getInstance(), 30);
                            }
                        }
                        return;
                    }

                    if(bossManager.isBlocking_damage_ability() && rand.nextInt(12) == 0)
                    {
                        event.setCancelled(true);
                        damager.sendMessage(ChatUtil.fixColors(bossManager.getName() + " &6zablokowal twoj atak!"));
                        damager.playSound(damager.getLocation(), Sound.BLOCK_ANVIL_FALL, 1.5f, 2.0f);
                        return;
                    }

                    if(bossManager.isThrow_ability() && rand.nextInt(11) == 0)
                    {
                        for(Entity e : livingEntity.getNearbyEntities(15, 10, 15))
                        {
                            if(e instanceof Player)
                            {
                                Player p = (Player) e;
                                p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.4f, 1.4f);
                                p.sendMessage(ChatUtil.fixColors(bossManager.getName() + " &cMam nadzieje ze potraficie latac JHAHAADSADSA!"));
                                p.setVelocity(new Vector(0, 1.7, 0));
                            }
                        }
                        return;
                    }

                    if(bossManager.isExplosion_ability() && rand.nextInt(10) == 0)
                    {
                        damager.getWorld().createExplosion(damager.getLocation(), 2);
                        damager.sendMessage(ChatUtil.fixColors(bossManager.getName() + " &cTez lubisz wybuchy? BO JA TAK! BOOM!!!"));
                        return;
                    }

                    if(bossManager.isPotions_ability() && rand.nextInt(9) == 0)
                    {
                        damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 2));
                        damager.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 5));
                        damager.sendMessage(ChatUtil.fixColors(bossManager.getName() + " &cMasz, napij sie. AAHHAHA"));
                        damager.playSound(damager.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 1.3f, 1.4f);
                        return;
                    }

                    if(bossManager.getMinions_amount() >= 1 && rand.nextInt(11) == 0)
                    {
                        spawnMinions(livingEntity.getLocation(), bossManager.getName(), bossManager.getMinions_amount());
                        livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_HORSE_DEATH, 0.6f, 0.6f);
                        damager.sendMessage(ChatUtil.fixColors(bossManager.getName() + " &cMoje miniony! POWSTANCIE!!"));
                    }

                    return;
                }
            }

        }
    }

    private void spawnMinions(Location location, String boss_name, int amount)
    {
        for(int i = 1; i <= amount; i++)
        {
            Zombie zombie = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
            zombie.setCustomNameVisible(true);
            zombie.setCustomName(ChatUtil.fixColors("&eMinion &7- " + boss_name));
            zombie.setBaby();
            zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
            zombie.setHealth(30);
            ItemStack helmet = new ItemStack(Material.IRON_HELMET);
            helmet.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
            zombie.getEquipment().setHelmet(helmet);
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2300000, 1));
            ItemStack weapon = new ItemStack(Material.IRON_AXE);
            weapon.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
            zombie.getEquipment().setItemInMainHand(weapon);
        }

    }

    private void strikeLightning(Entity e, String boss_name)
    {
        if(e.getCustomName() != null && e.getCustomName().equalsIgnoreCase(boss_name))
        {
            return;
        }
        e.getWorld().strikeLightning(e.getLocation());
    }

}
