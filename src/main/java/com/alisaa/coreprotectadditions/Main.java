package com.alisaa.coreprotectadditions;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.alisaa.coreprotectadditions.eventhandlers.*;

public class Main extends JavaPlugin {
    ApiWrapper api;
    private static JavaPlugin instance;

    public static JavaPlugin getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin depend = pluginManager.getPlugin("CoreProtect");
        if (depend == null) {
            getLogger().severe("CoreProtect was not found, disabling plugin");
            pluginManager.disablePlugin(this);
            return;
        }
        AdditionsConfigHandler.initConfig(this);
        api = new ApiWrapper();        
        instance = this;

        if (AdditionsConfigHandler.CHECK_FOR_UPDATES){
            Updater.checkForUpdates(this);
        }


        if (AdditionsConfigHandler.LOG_LEASHES){
            pluginManager.registerEvents(new LeashLogger(api), this);
        }
        if (AdditionsConfigHandler.LOG_SPONGE){
            pluginManager.registerEvents(new SpongeLogger(api), this);
        }
        if (AdditionsConfigHandler.LOG_FISH_BUCKETS){
            pluginManager.registerEvents(new FishBucketLogger(api), this);
        }
        if (AdditionsConfigHandler.LOG_AGE_LOCK){
            pluginManager.registerEvents(new GoldenDandelionLogger(api), this);
        }
        if (AdditionsConfigHandler.EXPERIMENTAL_ENTITY_CONTAINER_LOGGER){
            getLogger().warning("You are using the experimental entity container logger");
            pluginManager.registerEvents(new EntityInventoryLogger(api), this);
        }

        if (AdditionsConfigHandler.LOG_SULFUR_CUBE){
            pluginManager.registerEvents(new SulfurCubeLogger(api), this);
        }
        
        pluginManager.registerEvents(new EntityItemLogger(api), this);
        pluginManager.registerEvents(new WindChargeLogger(api), this);
        pluginManager.registerEvents(new MiscLogger(api), this);
        pluginManager.registerEvents(new EntityChangeBlockLogger(api), this);
        pluginManager.registerEvents(new MobExplosionLogger(api), this);
        pluginManager.registerEvents(new BedLogger(api), this);
        pluginManager.registerEvents(new TntLogger(api), this);
        pluginManager.registerEvents(new VehicleLogger(api), this);
        pluginManager.registerEvents(new AllayLogger(api), this);

    }
}
