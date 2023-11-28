package com.badbones69.crazyenchantments.paper.controllers;

import com.badbones69.crazyenchantments.paper.CrazyEnchantments;
import com.badbones69.crazyenchantments.paper.Starter;
import com.badbones69.crazyenchantments.paper.api.CrazyManager;
import com.badbones69.crazyenchantments.paper.api.FileManager.Files;
import com.badbones69.crazyenchantments.paper.api.enums.Messages;
import com.badbones69.crazyenchantments.paper.api.managers.guis.InfoMenuManager;
import com.badbones69.crazyenchantments.paper.api.objects.CEPlayer;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GkitCoolDown;
import com.badbones69.crazyenchantments.paper.api.objects.gkitz.GKitz;
import com.badbones69.crazyenchantments.paper.api.objects.ItemBuilder;
import com.badbones69.crazyenchantments.paper.utilities.misc.ColorUtils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GKitzController implements Listener {

    private final CrazyEnchantments plugin = CrazyEnchantments.getPlugin();

    private final Starter starter = plugin.getStarter();

    private final CrazyManager crazyManager = starter.getCrazyManager();

    // Plugin Managers.
    private final InfoMenuManager infoMenuManager = starter.getInfoMenuManager();

    public void openGUI(Player player) {
        FileConfiguration gkitz = Files.GKITZ.getFile();
        Inventory inventory = plugin.getServer().createInventory(null, gkitz.getInt("Settings.GUI-Size"), ColorUtils.legacyTranslateColourCodes(gkitz.getString("Settings.Inventory-Name")));

        for (String customItemString : gkitz.getStringList("Settings.GUI-Customization")) {
            int slot = 0;

            for (String option : customItemString.split(", ")) {
                if (option.contains("Slot:")) {
                    option = option.replace("Slot:", "");
                    slot = Integer.parseInt(option);
                    break;
                }
            }

            slot--;
            inventory.setItem(slot, ItemBuilder.convertString(customItemString).build());
        }

        CEPlayer cePlayer = crazyManager.getCEPlayer(player);

        for (GKitz kit : crazyManager.getGKitz()) {
            ItemStack displayItem = kit.getDisplayItem().clone();
            ItemMeta itemMeta = displayItem.getItemMeta();
            List<Component> lore = new ArrayList<>();
            GkitCoolDown gkitCooldown = !cePlayer.canUseGKit(kit) && cePlayer.hasGkitPermission(kit) ? cePlayer.getCoolDown(kit) : new GkitCoolDown();

            if (displayItem.lore() != null) {
                for (Component line : displayItem.lore()) {
                    String legacyLoreLine = ColorUtils.toLegacy(line);
                    if (legacyLoreLine.toLowerCase().matches(".*%(day|hour|minute|second)%.*"))
                        line = ColorUtils.legacyTranslateColourCodes(gkitCooldown.getCoolDownLeft(legacyLoreLine));
                    lore.add(line);
                }
            }

            itemMeta.lore(lore);
            displayItem.setItemMeta(itemMeta);
            inventory.setItem(kit.getSlot() - 1, displayItem);
        }

        player.openInventory(inventory);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        ItemStack item = event.getCurrentItem();

        if (item == null || item.getType() == Material.AIR || item.getAmount() <= 0) return;

        Player player = (Player) event.getWhoClicked();
        CEPlayer cePlayer = crazyManager.getCEPlayer(player);
        NBTItem nbtItem = new NBTItem(item);

        for (GKitz kit : crazyManager.getGKitz()) {
            if (!event.getView().title().equals(kit.getDisplayItem().displayName())) continue;

            event.setCancelled(true);

            if (event.getRawSlot() < inventory.getSize() && item.isSimilar(infoMenuManager.getBackRightButton())) openGUI(player);

            return;
        }

        if (!event.getView().title().equals(ColorUtils.legacyTranslateColourCodes(Files.GKITZ.getFile().getString("Settings.Inventory-Name")))) return;

        event.setCancelled(true);

        if (event.getRawSlot() >= inventory.getSize() || !nbtItem.hasTag("gkit")) return;

        GKitz kit = crazyManager.getGKitFromName(nbtItem.getString("gkit"));

        if (event.getAction() == InventoryAction.PICKUP_HALF) {
            List<ItemStack> items = kit.getPreviewItems();
            int slots = Math.min(((items.size() / 9) + (items.size() % 9 > 0 ? 1 : 0)) * 9, 54);

            Inventory previewInventory = plugin.getServer().createInventory(null, slots, kit.getDisplayItem().displayName());

            for (ItemStack itemStack : items) {
                previewInventory.addItem(itemStack);
            }

            previewInventory.setItem(slots - 1, infoMenuManager.getBackRightButton());
            player.openInventory(previewInventory);
        } else {
            Map<String, String> placeholders = new HashMap<>(1) {{ put("%Kit%", kit.getName()); }};

            if (cePlayer.hasGkitPermission(kit)) {
                if (cePlayer.canUseGKit(kit)) {
                    cePlayer.giveGKit(kit);
                    cePlayer.addCoolDown(kit);
                    player.sendMessage(Messages.RECEIVED_GKIT.getMessage(placeholders));
                } else {
                    player.sendMessage(ColorUtils.getPrefix() + cePlayer.getCoolDown(kit).getCoolDownLeft(Messages.STILL_IN_COOLDOWN.getMessage(placeholders)));
                }
            } else {
                player.sendMessage(Messages.NO_GKIT_PERMISSION.getMessage(placeholders));
            }
        }
    }
}