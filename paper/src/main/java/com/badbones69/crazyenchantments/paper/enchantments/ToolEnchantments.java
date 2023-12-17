package com.badbones69.crazyenchantments.paper.enchantments;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Methods;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.enums.CEnchantments;
import com.badbones69.crazyenchantments.paper.api.events.EnchantmentUseEvent;
import com.badbones69.crazyenchantments.paper.api.objects.CEnchantment;
import com.badbones69.crazyenchantments.paper.controllers.settings.EnchantmentBookSettings;
import com.badbones69.crazyenchantments.paper.utilities.misc.EnchantUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ToolEnchantments implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final Methods methods = starter.getMethods();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    // Settings.
    private final EnchantmentBookSettings enchantmentBookSettings = starter.getEnchantmentBookSettings();

    @EventHandler()
    public void onPlayerClick(PlayerInteractEvent event) {
        //Check what hand is being used as the event fires for each hand.
        if (Objects.equals(event.getHand(), EquipmentSlot.HAND)) updateEffects(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTelepathy(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack tool = methods.getItemInHand(player);

        if (!EnchantUtils.isEventActive(CEnchantments.TELEPATHY, player, tool, enchantmentBookSettings.getEnchantments(tool))) return;

        event.setCancelled(true);
        methods.addItemToInventory(player, event.getItems());
    }

    private void updateEffects(Player player) {
        ItemStack item = methods.getItemInHand(player);
        Map<CEnchantment, Integer> enchantments = enchantmentBookSettings.getEnchantments(item);

        int potionTime = 5 * 20;

        if (EnchantUtils.isEventActive(CEnchantments.HASTE, player, item, enchantments)) {
            int power = enchantments.get(CEnchantments.HASTE.getEnchantment());
            player.removePotionEffect(PotionEffectType.FAST_DIGGING);
            player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, potionTime, power - 1));
        }

        if (EnchantUtils.isEventActive(CEnchantments.OXYGENATE, player, item, enchantments)) {
            player.removePotionEffect(PotionEffectType.WATER_BREATHING);
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, potionTime, 5));
        }

    }
}