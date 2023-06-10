package com.badbones69.crazyenchantments.api.enums;

import com.badbones69.crazyenchantments.CrazyEnchantments;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.economy.Currency;
import com.badbones69.crazyenchantments.api.objects.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.jline.utils.Log;

import java.util.HashMap;

public enum ShopOption {
    
    GKITZ("GKitz", "GKitz", "Name", "Lore", false),
    BLACKSMITH("BlackSmith", "BlackSmith", "Name", "Lore", false),
    TINKER("Tinker", "Tinker", "Name", "Lore", false),
    INFO("Info", "Info", "Name", "Lore", false),
    
    PROTECTION_CRYSTAL("ProtectionCrystal", "ProtectionCrystal", "GUIName", "GUILore", true),
    SUCCESS_DUST("SuccessDust", "Dust.SuccessDust", "GUIName", "GUILore", true),
    DESTROY_DUST("DestroyDust", "Dust.DestroyDust", "GUIName", "GUILore", true),
    SCRAMBLER("Scrambler", "Scrambler", "GUIName", "GUILore", true),
    
    BLACK_SCROLL("BlackScroll", "BlackScroll", "GUIName", "Lore", true),
    WHITE_SCROLL("WhiteScroll", "WhiteScroll", "GUIName", "Lore", true),
    TRANSMOG_SCROLL("TransmogScroll", "TransmogScroll", "GUIName", "Lore", true);
    
    private static final HashMap<ShopOption, Option> shopOptions = new HashMap<>();
    private final String optionPath;
    private final String path;
    private final String namePath;
    private final String lorePath;
    private Option option;
    private final boolean buyable;
    
    ShopOption(String optionPath, String path, String namePath, String lorePath, boolean buyable) {
        this.optionPath = optionPath;
        this.path = path;
        this.namePath = namePath;
        this.lorePath = lorePath;
        this.buyable = buyable;
    }

    private final static CrazyEnchantments plugin = CrazyEnchantments.getPlugin();
    
    public static void loadShopOptions() {
        FileConfiguration config = Files.CONFIG.getFile();
        shopOptions.clear();

        for (ShopOption shopOption : values()) {
            String itemPath = "Settings." + shopOption.getPath() + ".";
            String costPath = "Settings.Costs." + shopOption.getOptionPath() + ".";

            try {
                shopOptions.put(shopOption, new Option(new ItemBuilder()
                .setName(config.getString(itemPath + shopOption.getNamePath()))
                .setLore(config.getStringList(itemPath + shopOption.getLorePath()))
                .setMaterial(config.getString(itemPath + "Item"))
                .setPlayerName(config.getString(itemPath + "Player"))
                .setGlow(config.getBoolean(itemPath + "Glowing")),
                config.getInt(itemPath + "Slot", 1) - 1,
                config.getBoolean(itemPath + "InGUI"),
                config.getInt(costPath + "Cost", 100),
                Currency.getCurrency(config.getString(costPath + "Currency", "Vault"))));
            } catch (Exception e) {
                plugin.getLogger().info("The option " + shopOption.getOptionPath() + " has failed to load.");
                Log.error(e);
            }
        }
    }
    
    public ItemStack getItem() {
        return getItemBuilder().build();
    }
    
    public ItemBuilder getItemBuilder() {
        return shopOptions.get(this).itemBuilder();
    }
    
    public int getSlot() {
        return shopOptions.get(this).slot();
    }
    
    public boolean isInGUI() {
        return shopOptions.get(this).inGUI();
    }
    
    public int getCost() {
        return shopOptions.get(this).cost();
    }
    
    public Currency getCurrency() {
        return shopOptions.get(this).currency();
    }
    
    private String getOptionPath() {
        return optionPath;
    }
    
    private String getPath() {
        return path;
    }
    
    private String getNamePath() {
        return namePath;
    }
    
    private String getLorePath() {
        return lorePath;
    }
    
    public boolean isBuyable() {
        return buyable;
    }

    private record Option(ItemBuilder itemBuilder, int slot, boolean inGUI, int cost, Currency currency) {}
}