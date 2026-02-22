package com.alisaa.coreprotectadditions;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigHandler {
    public static final String HEADER = """
            # CoreProtect-additions by itsAlisaa
            # Configuration file, for more info on the plugin check out the readme https://github.com/guss-alberto/CoreProtect-additions

            # Note: user comments in this file DO NOT persist.

            """;
    public static boolean LOG_SPAWN_SET;
    public static boolean LOG_ENTITY_DYE;
    public static boolean LOG_ENTITY_RENAME;
    public static boolean LOG_LEASHES;
    public static boolean LOG_RAIDS;
    public static boolean LOG_REDSTONE_TNT_IGNITE;
    public static boolean LOG_MINECART;
    public static boolean LOG_CHEST_MINECART;
    public static boolean LOG_HOPPER_MINECART;
    public static boolean LOG_BOAT;
    public static boolean LOG_CHEST_BOAT;
    public static boolean LOG_MOB_RIDE;
    public static boolean LOG_CHESTED_HORSE_RIDE;
    public static boolean LOG_CHEST_BOAT_RIDE;
    public static boolean LOG_BOAT_RIDE;
    public static boolean LOG_MINECART_RIDE;
    public static boolean LOG_NON_PLAYER_RIDE;
    public static boolean LOG_ENTITY_CONTAINER_CLICK;
    public static boolean LOG_RIDE_AS_CLICK;
    public static boolean EXPERIMENTAL_ENTITY_CONTAINER_LOGGER;

    static FileConfiguration config;

    private ConfigHandler() {
    }

    static void initConfig(Plugin plugin){
        config = plugin.getConfig();
        final List<ConfigEntry> configEntries = new ArrayList<>();

        configEntries.add(new ConfigEntry("log-spawn-set", true, "# Wether to log setting spawn on a bed or respawn anchor\n" + //
                                                                                   "# Respawn block explosions will always be logged"));
        LOG_SPAWN_SET = configEntries.getLast().getValue();

        configEntries.add(new ConfigEntry("log-entity-dye", true, null));
        LOG_ENTITY_DYE = configEntries.getLast().getValue();

        configEntries.add(new ConfigEntry("log-entity-rename", true, null));
        LOG_ENTITY_RENAME = configEntries.getLast().getValue();

        configEntries.add(new ConfigEntry("log-leashes", true, null));
        LOG_LEASHES = configEntries.getLast().getValue();

        configEntries.add(new ConfigEntry("log-raids", true, null));
        LOG_RAIDS = configEntries.getLast().getValue();

        configEntries.add(new ConfigEntry("log-redstone-tnt-ignite", true, "# Useful for farms where TNT duping is enabled\n" + //
                                                                                             "# Finding TNT ignited by redstone torches or levers might"));
        LOG_REDSTONE_TNT_IGNITE = configEntries.getLast().getValue();

        configEntries.add(new ConfigEntry("log-chest-boats", true, null));
        LOG_CHEST_BOAT = configEntries.getLast().getValue();

        configEntries.add(new ConfigEntry("log-boats", true, "\n#Log breaking/placing for the following vehicle entities"));
        LOG_BOAT = configEntries.getLast().getValue();


        configEntries.add(new ConfigEntry("log-chest-minecarts", true, null));
        LOG_CHEST_MINECART = configEntries.getLast().getValue();

        configEntries.add(new ConfigEntry("log-hopper-minecarts", true, "# Disabling these is useful if you have lots of  farms with 'cart yeeters'"));
        LOG_HOPPER_MINECART = configEntries.getLast().getValue();

        configEntries.add(new ConfigEntry("log-minecarts", true, "# This includes also furnace minecarts. TNT carts are always logged"));
        LOG_MINECART = configEntries.getLast().getValue();

        
        configEntries.add(new ConfigEntry("log-mob-ride", true, "\n# Log riding/dismounting for rideable entities\n"));
        LOG_MOB_RIDE = configEntries.getLast().getValue();

        configEntries.add(new ConfigEntry("log-chested-mob-ride", true, "# Specific check for mobs with an inventory (donkey, llama, etc.)\n"));
        LOG_CHESTED_HORSE_RIDE = configEntries.getLast().getValue();
        
        configEntries.add(new ConfigEntry("log-minecart-ride", true, null));
        LOG_MINECART_RIDE = configEntries.getLast().getValue();

        configEntries.add(new ConfigEntry("log-chest-boat-ride", true, null));
        LOG_CHEST_BOAT_RIDE = configEntries.getLast().getValue();
        
        configEntries.add(new ConfigEntry("log-boat-ride", true, null));
        LOG_BOAT_RIDE = configEntries.getLast().getValue();

        configEntries.add(new ConfigEntry("log-cart-ride-dismount-as-click", true, """
        
        # Whether to log riding and dismounting as a click action, instead of place and break. ONLY applies to non-mob rides (minecarts and boats)
        # With this option enabled riding and dismounting are BOTH logged as click.
        # Clicking can be confused with chest boat inventory opening.
        # Setting this to FALSE to log as block actions, just as for mobs"""
        ));
        LOG_RIDE_AS_CLICK = configEntries.getLast().getValue();

        configEntries.add(new ConfigEntry("log-non-player-ride", false, "# Whether to also log entities entering vehicles, not just players"));
        LOG_NON_PLAYER_RIDE = configEntries.getLast().getValue();

        configEntries.add(new ConfigEntry("log-entity-container-click", true, "# Whether to log players opening an entity container (Chest boat, chest minecart, donkey etc.)"));
        LOG_ENTITY_CONTAINER_CLICK = configEntries.getLast().getValue();

        configEntries.add(new ConfigEntry("experimental-entity-container-logger", false, """

        # WARNING: THIS FEATURE IS EXPERIMENTAL; USE AT YOUR OWN RISK.
        # There is probably a reason CoreProtect doesn't do this, and we might find out why.
        # There are no official APIs for this, this is a hack and might not work on your server.
        # However this does seem to work on my testing server.
        # Note: this does NOT include hopers inserting or removing items from the container. Nor does it log breaking the container as removing all the contents.
        # Enabling this will log item interactions with donkeys, mules, chest boats, hopper minecarts, chest minecarts and more as action:container.
        """
        ));
        EXPERIMENTAL_ENTITY_CONTAINER_LOGGER = configEntries.getLast().getValue();


        saveConfigFile(new File(plugin.getDataFolder(), "config.yml"), configEntries);
    }

    private static void saveConfigFile(File file, List<ConfigEntry> configEntries) {
        try (final FileOutputStream fout = new FileOutputStream(file)) {
            fout.write(HEADER.getBytes());
            for (ConfigEntry configEntry : configEntries) {
                fout.write(configEntry.toString().getBytes());
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to save config file");
            e.printStackTrace();
        }
    }

    private static class ConfigEntry {
        String key;
        String descrption;
        boolean value;

        public ConfigEntry(String key, Boolean defaultValue, String descrption) {
            this.key = key;
            this.descrption = descrption;
            config.addDefault(key, defaultValue);
            this.value = config.getBoolean(key);
        }

        public boolean getValue() {
            return value;
        }

        public String toString() {
            String entry = this.key + ": " + this.value;
            if (descrption != null) {
                entry = this.descrption + "\n" + entry;
            }
            return entry + "\n";
        }

    }

}
