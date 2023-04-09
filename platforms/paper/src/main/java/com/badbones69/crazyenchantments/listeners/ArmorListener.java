package com.badbones69.crazyenchantments.listeners;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.api.enums.ArmorType;
import com.badbones69.crazyenchantments.api.events.ArmorEquipEvent;
import com.badbones69.crazyenchantments.api.events.ArmorEquipEvent.EquipMethod;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Arnah
 * @since Jul 30, 2015
 */
public class ArmorListener implements Listener {
    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();
    @EventHandler(ignoreCancelled = true)
    public void paperArmorChange(PlayerArmorChangeEvent event) {
        ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(event.getPlayer(), EquipMethod.HOTBAR, ArmorType.matchType(event.getNewItem()), event.getOldItem(), event.getNewItem());
        plugin.getServer().getPluginManager().callEvent(armorEquipEvent);
    }
}