package com.badbones69.crazyenchantments;

import com.badbones69.crazyenchantments.api.FileManager;
import com.badbones69.crazyenchantments.api.FileManager.Files;
import com.badbones69.crazyenchantments.api.PluginSupport.SupportedPlugins;
import com.badbones69.crazyenchantments.api.support.misc.spawners.SilkSpawnerSupport;
import com.badbones69.crazyenchantments.commands.BlackSmithCommand;
import com.badbones69.crazyenchantments.commands.CECommand;
import com.badbones69.crazyenchantments.commands.CETab;
import com.badbones69.crazyenchantments.commands.GkitzCommand;
import com.badbones69.crazyenchantments.commands.GkitzTab;
import com.badbones69.crazyenchantments.commands.TinkerCommand;
import com.badbones69.crazyenchantments.controllers.BlackSmith;
import com.badbones69.crazyenchantments.controllers.CommandChecker;
import com.badbones69.crazyenchantments.controllers.GKitzController;
import com.badbones69.crazyenchantments.controllers.InfoGUIControl;
import com.badbones69.crazyenchantments.controllers.LostBookController;
import com.badbones69.crazyenchantments.controllers.Tinkerer;
import com.badbones69.crazyenchantments.enchantments.AllyEnchantments;
import com.badbones69.crazyenchantments.enchantments.ArmorEnchantments;
import com.badbones69.crazyenchantments.enchantments.AxeEnchantments;
import com.badbones69.crazyenchantments.enchantments.BootEnchantments;
import com.badbones69.crazyenchantments.enchantments.BowEnchantments;
import com.badbones69.crazyenchantments.enchantments.HelmetEnchantments;
import com.badbones69.crazyenchantments.enchantments.HoeEnchantments;
import com.badbones69.crazyenchantments.enchantments.PickaxeEnchantments;
import com.badbones69.crazyenchantments.enchantments.SwordEnchantments;
import com.badbones69.crazyenchantments.enchantments.ToolEnchantments;
import com.badbones69.crazyenchantments.listeners.AuraListener;
import com.badbones69.crazyenchantments.listeners.DustControlListener;
import com.badbones69.crazyenchantments.listeners.FireworkDamageListener;
import com.badbones69.crazyenchantments.listeners.ProtectionCrystalListener;
import com.badbones69.crazyenchantments.listeners.ShopListener;
import com.badbones69.crazyenchantments.listeners.server.WorldSwitchListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyEnchantments extends JavaPlugin implements Listener {

    private static CrazyEnchantments plugin;

    private Starter starter;

    // Plugin Listeners.
    public PluginManager pluginManager = getServer().getPluginManager();

    private FireworkDamageListener fireworkDamageListener;
    private ShopListener shopListener;
    private ArmorEnchantments armorEnchantments;

    // Menus.
    private Tinkerer tinkerer;
    private BlackSmith blackSmith;
    private GKitzController gKitzController;

    @Override
    public void onEnable() {
        plugin = this;

        starter = new Starter();

        // Create all instances we need.
        starter.run();

        starter.getCurrencyAPI().loadCurrency();

        FileConfiguration config = Files.CONFIG.getFile();
        FileConfiguration tinker = Files.TINKER.getFile();

        String metricsPath = config.getString("Settings.Toggle-Metrics");
        boolean metricsEnabled = config.getBoolean("Settings.Toggle-Metrics");

        if (metricsPath == null) {
            config.set("Settings.Toggle-Metrics", false);

            Files.CONFIG.saveFile();
        }

        String refreshEffects = config.getString("Settings.Refresh-Potion-Effects-On-World-Change");

        if (refreshEffects == null) {
            config.set("Settings.Refresh-Potion-Effects-On-World-Change", false);
            
            Files.CONFIG.saveFile();
        }

        if (tinker.get("Settings.Tinker-Version") == null) {
            tinker.set("Settings.Tinker-Version", 1.0);

            FileManager.Files.TINKER.saveFile();
        }

        if (metricsEnabled) new Metrics(this, 4494);

        pluginManager.registerEvents(this, this);

        pluginManager.registerEvents(blackSmith = new BlackSmith(), this);
        pluginManager.registerEvents(tinkerer = new Tinkerer(), this);
        pluginManager.registerEvents(fireworkDamageListener = new FireworkDamageListener(), this);
        pluginManager.registerEvents(shopListener = new ShopListener(), this);

        enable();
    }

    @Override
    public void onDisable() {
        disable();
    }

    private void enable() {
        // Load what we need to properly enable the plugin.
        starter.getCrazyManager().load();

        pluginManager.registerEvents(new DustControlListener(), this);

        pluginManager.registerEvents(new PickaxeEnchantments(), this);
        pluginManager.registerEvents(new HelmetEnchantments(), this);
        pluginManager.registerEvents(new SwordEnchantments(), this);
        pluginManager.registerEvents(armorEnchantments = new ArmorEnchantments(), this);
        pluginManager.registerEvents(new AllyEnchantments(), this);
        pluginManager.registerEvents(new ToolEnchantments(), this);
        pluginManager.registerEvents(new BootEnchantments(), this);
        pluginManager.registerEvents(new AxeEnchantments(), this);
        pluginManager.registerEvents(new BowEnchantments(), this);
        pluginManager.registerEvents(new HoeEnchantments(), this);

        pluginManager.registerEvents(new ProtectionCrystalListener(), this);
        pluginManager.registerEvents(new FireworkDamageListener(), this);
        pluginManager.registerEvents(new AuraListener(), this);

        pluginManager.registerEvents(new InfoGUIControl(), this);
        pluginManager.registerEvents(new LostBookController(), this);
        pluginManager.registerEvents(new CommandChecker(), this);

        pluginManager.registerEvents(new WorldSwitchListener(), this);

        if (starter.getCrazyManager().isGkitzEnabled()) {
            getLogger().info("G-Kitz support is now enabled.");

            pluginManager.registerEvents(gKitzController = new GKitzController(), this);
        }

        if (SupportedPlugins.SILK_SPAWNERS.isCachedPluginLoaded()) pluginManager.registerEvents(new SilkSpawnerSupport(), this);

        registerCommand(getCommand("crazyenchantments"), new CETab(), new CECommand());

        registerCommand(getCommand("tinkerer"), null, new TinkerCommand());
        registerCommand(getCommand("blacksmith"), null, new BlackSmithCommand());

        registerCommand(getCommand("gkit"), new GkitzTab(), new GkitzCommand());
    }

    private void disable() {
        armorEnchantments.stop();

        if (starter.getAllyManager() != null) starter.getAllyManager().forceRemoveAllies();

        getServer().getOnlinePlayers().forEach(starter.getCrazyManager()::unloadCEPlayer);
    }

    private void registerCommand(PluginCommand pluginCommand, TabCompleter tabCompleter, CommandExecutor commandExecutor) {
        if (pluginCommand != null) {
            pluginCommand.setExecutor(commandExecutor);

            if (tabCompleter != null) pluginCommand.setTabCompleter(tabCompleter);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.starter.getCrazyManager().loadCEPlayer(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.starter.getCrazyManager().unloadCEPlayer(event.getPlayer());
    }

    public static CrazyEnchantments getPlugin() {
        return plugin;
    }

    public Starter getStarter() {
        return this.starter;
    }

    // Plugin Listeners.
    public FireworkDamageListener getFireworkDamageListener() {
        return this.fireworkDamageListener;
    }

    public ShopListener getShopListener() {
        return this.shopListener;
    }

    public PluginManager getPluginManager() {
        return this.pluginManager;
    }

    public Tinkerer getTinkerer() {
        return this.tinkerer;
    }

    public BlackSmith getBlackSmith() {
        return this.blackSmith;
    }

    public GKitzController getgKitzController() {
        return this.gKitzController;
    }
}