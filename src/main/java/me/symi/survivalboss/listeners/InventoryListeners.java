package me.symi.survivalboss.listeners;

import me.symi.survivalboss.utils.ChatUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListeners implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        if(event.getClickedInventory() == null)
        {
            return;
        }
        if(event.getView().getTitle().equalsIgnoreCase(ChatUtil.fixColors("&4&lBossy &8| &eInformacje")))
        {
            event.setCancelled(true);
        }
    }


}
