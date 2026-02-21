package com.alisaa.coreprotectadditions;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.alisaa.coreprotectadditions.eventhandlers.*;

public class Main extends JavaPlugin {
    ApiWrapper api;

    @Override
    public void onEnable() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin depend = pluginManager.getPlugin("CoreProtect");
        if (depend == null) {
            getLogger().severe("CoreProtect was not found, disabling plugin");
            pluginManager.disablePlugin(this);
            return;
        }
        api = new ApiWrapper();        

        ConfigHandler.initConfig(this);

        if (ConfigHandler.LOG_LEASHES){
            pluginManager.registerEvents(new LeashLogger(api), this);
        }
        if (ConfigHandler.EXPERIMENTAL_ENTITY_CONTAINER_LOGGER){
            getLogger().warning("You are using the experimental entity container logger");
            pluginManager.registerEvents(new EntityInventoryLogger(api), this);
        }
        pluginManager.registerEvents(new MiscLogger(api), this);
        pluginManager.registerEvents(new CreeperLogger(api), this);
        pluginManager.registerEvents(new BedLogger(api), this);
        pluginManager.registerEvents(new TntLogger(api), this);
        pluginManager.registerEvents(new VehicleLogger(api), this);
    }
}
