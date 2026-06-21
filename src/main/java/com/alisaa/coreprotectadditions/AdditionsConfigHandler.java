package com.alisaa.coreprotectadditions;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import net.coreprotect.CoreProtect;
import net.coreprotect.utility.VersionUtils;

public class AdditionsConfigHandler {
    public static final String HEADER = """
            # CoreProtect-additions by itsAlisaa
            # Configuration file, for more info on the plugin check out the readme https://github.com/guss-alberto/CoreProtect-additions

            # Note: user comments in this file DO NOT persist.

            """;

    static Set<ConfigEntry> configEntries = new LinkedHashSet<>();
    static FileConfiguration config;
    static String mcVersion;

    public static boolean CHECK_FOR_UPDATES;
    public static boolean LOG_SPAWN_SET;
    public static boolean LOG_ENTITY_DYE;
    public static boolean LOG_ENTITY_RENAME;
    public static boolean LOG_AGE_LOCK;
    public static boolean LOG_GOLDEN_DADELION;
    public static boolean LOG_FROST_WALKER;
    public static boolean LOG_SILVERFISH_INFESTATION;
    public static boolean LOG_ZOMBIE_DOOR_BREAK;
    public static boolean LOG_FLOWER_POT;
    public static boolean LOG_SPONGE;
    public static boolean LOG_LEASHES;
    public static boolean LOG_RAIDS;
    public static boolean LOG_CREEPER;
    public static boolean LOG_FIREBALL;
    public static boolean LOG_FISH_BUCKETS;
    public static boolean LOG_WEAVING;
    public static boolean LOG_REDSTONE_TNT_IGNITE;
    public static boolean LOG_WIND_CHARGE_THROW;
    public static boolean LOG_WIND_CHARGE_CLICK;
    public static boolean LOG_BOAT;
    public static boolean LOG_CHEST_BOAT;
    public static boolean LOG_CHEST_MINECART;
    public static boolean LOG_HOPPER_MINECART;
    public static boolean LOG_MINECART;
    public static boolean LOG_MOB_RIDE;
    public static boolean LOG_CHESTED_HORSE_RIDE;
    public static boolean LOG_MINECART_RIDE;
    public static boolean LOG_CHEST_BOAT_RIDE;
    public static boolean LOG_BOAT_RIDE;
    public static boolean LOG_NON_PLAYER_RIDE;
    public static boolean LOG_RIDE_AS_CLICK;
    public static boolean LOG_ENTITY_CONTAINER_CLICK;
    public static boolean EXPERIMENTAL_ENTITY_CONTAINER_LOGGER;
    public static boolean LOG_LIGHTNING_CONVERSION;
    public static boolean LOG_VILLAGER_ITEM;
    public static boolean LOG_PIGLIN_ITEM;
    public static boolean LOG_ALLAY_ITEM;
    public static boolean LOG_OTHER_ENTITY_ITEM;
    public static final boolean LOG_LECTERN_INSERT = coVersion() < 2;

    private AdditionsConfigHandler() {
    }

    static void initConfig(Plugin plugin) {
        config = plugin.getConfig();

        CHECK_FOR_UPDATES = addConfigOption("check-for-updates", true,
                "# Automatically check for new versions on server startup");
        LOG_SPAWN_SET = addConfigOption("log-spawn-set", true,
                "# Wether to log setting spawn on a bed or respawn anchor\n"
                        + "# Respawn block explosions will always be logged");
        LOG_ENTITY_DYE = addConfigOption("log-entity-dye", true);
        LOG_ENTITY_RENAME = addConfigOption("log-entity-rename", true);
        LOG_AGE_LOCK = addConfigOption("log-dandelion-age-lock", true, null, "26.1.2");
        LOG_GOLDEN_DADELION = addConfigOption("log-entity-rename", true);
        LOG_FROST_WALKER = addConfigOption("log-frost-walker", true);

        if (coVersion() < 2) {
            LOG_LIGHTNING_CONVERSION = addConfigOption("log-lightning-conversion", true,
                    "# Whether to log mobs getting converted by lightning as being killed by #lightning");
        }

        LOG_SILVERFISH_INFESTATION = addConfigOption("log-silverfish-infestation", true,
                "# Silverfish breaking blocks by exiting are already logged by coreprotect");
        if (coVersion() < 2){
            LOG_ZOMBIE_DOOR_BREAK = addConfigOption("log-zombie-break-door", true);
            LOG_FLOWER_POT = addConfigOption("log-flower-pot", true);
        }
        LOG_SPONGE = addConfigOption("log-sponge-absorbe", true,
                "# Logs sponge removing water as user '#sponge' reagrdless of who placed it");
        LOG_LEASHES = addConfigOption("log-leashes", true);
        LOG_RAIDS = addConfigOption("log-raids", true);
        LOG_CREEPER = addConfigOption("log-creeper-explode", true);
        LOG_FIREBALL = addConfigOption("log-fireball", true);
        LOG_FISH_BUCKETS = addConfigOption("log-fish-buckets", true,
                "# Whether to log picking up a and placing fish or axolotl in buckets as block actions");
        LOG_WEAVING = addConfigOption("log-weaving-cobwebs", true,
                "# Whether to log cobwebs placed by the weaving potion effect");
        LOG_REDSTONE_TNT_IGNITE = addConfigOption("log-redstone-tnt-ignite", true,
                "# Useful for farms where TNT duping is enabled\n"
                        + "# Finding TNT ignited by redstone torches or levers might");
        if (coVersion() < 2) {
            LOG_WIND_CHARGE_THROW = addConfigOption("log-wind-charge-throw", true,
                    "# Whether to log throwing wind charges as item actions like throwing ender pearls");
        }
        LOG_WIND_CHARGE_CLICK = addConfigOption("log-wind-charge-interact", true,
                "# Whether to log throwing wind charges toggling doors, lever and buttons as click actions by the player or entity who threw them");

        LOG_VILLAGER_ITEM = addConfigOption("log-villager-item", true,
                "\n#Log picking up/dropping items for the following mobs");
        LOG_PIGLIN_ITEM = addConfigOption("log-piglin-item", true);
        LOG_ALLAY_ITEM = addConfigOption("log-allay-item", true);
        LOG_OTHER_ENTITY_ITEM = addConfigOption("log-other-entity-item", true, "# For any other entity, except players, player item pickups is handled by CoreProtect");

        
        LOG_BOAT = addConfigOption("log-boats", true,
                "\n#Log breaking/placing for the following vehicle entities");
        LOG_CHEST_BOAT = addConfigOption("log-chest-boats", true);
        LOG_CHEST_MINECART = addConfigOption("log-chest-minecarts", true);
        LOG_HOPPER_MINECART = addConfigOption("log-hopper-minecarts", true,
                "# Disabling these is useful if you have lots of  farms with 'cart yeeters'");
        LOG_MINECART = addConfigOption("log-minecarts", true,
                "# This includes also furnace minecarts. TNT carts are always logged");
        LOG_MOB_RIDE = addConfigOption("log-mob-ride", true,
                "\n# Log riding/dismounting for rideable entities\n");
        LOG_CHESTED_HORSE_RIDE = addConfigOption("log-chested-mob-ride", true,
                "# Specific check for mobs with an inventory (donkey, llama, etc.)\n");
        LOG_MINECART_RIDE = addConfigOption("log-minecart-ride", true);
        LOG_CHEST_BOAT_RIDE = addConfigOption("log-chest-boat-ride", true);
        LOG_BOAT_RIDE = addConfigOption("log-boat-ride", true);
        LOG_NON_PLAYER_RIDE = addConfigOption("log-non-player-ride", false,
                "# Whether to also log entities entering vehicles, not just players");
        LOG_RIDE_AS_CLICK = addConfigOption(
                "log-cart-ride-dismount-as-click", true,
                """

                        # Whether to log riding and dismounting as a click action, instead of place and break. ONLY applies to non-mob rides (minecarts and boats)
                        # With this option enabled riding and dismounting are BOTH logged as click.
                        # Clicking can be confused with chest boat inventory opening.
                        # Setting this to FALSE to log as block actions, just as for mobs""");
        LOG_ENTITY_CONTAINER_CLICK = addConfigOption("log-entity-container-click", true,
                "# Whether to log players opening an entity container (Chest boat, chest minecart, donkey etc.)");
        LOG_ENTITY_CONTAINER_CLICK = addConfigOption("log-entity-container-click", true,
                "# Whether to log players opening an entity container (Chest boat, chest minecart, donkey etc.)");
        EXPERIMENTAL_ENTITY_CONTAINER_LOGGER = addConfigOption(
                "experimental-entity-container-logger",
                false,
                """

                        # WARNING: THIS FEATURE IS EXPERIMENTAL; USE AT YOUR OWN RISK.
                        # There is probably a reason CoreProtect doesn't do this, and we might find out why.
                        # There are no official APIs for this, this is a hack and might not work on your server.
                        # However this does seem to work on my testing server.
                        # Note: this does NOT include hopers inserting or removing items from the container. Nor does it log breaking the container as removing all the contents.
                        # Enabling this will log item interactions with donkeys, mules, chest boats, hopper minecarts, chest minecarts and more as action:container.""");

        saveConfig(plugin.getDataFolder());

    }

    private static void saveConfig(File dataFolder) {
        if (!dataFolder.exists()) {
            Bukkit.getLogger().info("Creating CoreProtect-additions config folder...");
            dataFolder.mkdirs();
        }

        File configFile = new File(dataFolder, "config.yml");

        try (final FileOutputStream fout = new FileOutputStream(configFile)) {
            fout.write(HEADER.getBytes());
            for (ConfigEntry configEntry : configEntries) {
                fout.write(configEntry.toString().getBytes());
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to save config file");
            e.printStackTrace();
        }
    }


    // -1: unable to determine version
    // 0: Anything before 23.0
    // 1: Between community 23.0 and edge 24.0
    // 2: After 24.0
    // everything else is reserved for future use
    // this garbage is necessary because the Community edition has different version numbering schemes
    // I should probably make this an actual doscstring but i don't care enough to change it 
    public static int coVersion(){
        String coVersion = VersionUtils.getPluginVersion();
        int apiVersion = CoreProtect.getInstance().getAPI().APIVersion();
        if (VersionUtils.newVersion(coVersion, "23.0")){
            return 0;
        }

        if(VersionUtils.isBranch("edge")){
            if (!VersionUtils.newVersion(coVersion, "24.0")){
                return 2;
            }
            if (!VersionUtils.newVersion(coVersion, "23.3")){
                return 1;
            }
        }

        if (!VersionUtils.newVersion(coVersion, "24.0") || !VersionUtils.newVersion(coVersion, "23.2") && apiVersion >= 12){
            return 2;
        }

        if (!VersionUtils.newVersion(coVersion, "23.0")){
            return 1;
        }

        Bukkit.getLogger().severe("Failed to determine CoreProtect version");
        return -1;
    }

    private static boolean addConfigOption(String key, Boolean defaultValue, String descrption) {
        ConfigEntry entry = new ConfigEntry(key, defaultValue, descrption);
        boolean added = configEntries.add(entry);
        assert added;
        return entry.getValue();
    }

    private static boolean addConfigOption(String key, Boolean defaultValue) {
        return addConfigOption(key, defaultValue, null);
    }

    private static boolean addConfigOption(String key, Boolean defaultValue, String description, String version) {
        if (!VersionUtils.newVersion(getMcversion(), version)) {
            return addConfigOption(key, defaultValue, description);
        }
        return false;
    }

    public static String getMcversion() {
        if (mcVersion != null) {
            return mcVersion;
        }
        mcVersion = Bukkit.getServer().getVersion().split("-")[0];
        return mcVersion;
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
        
        @Override
        public int hashCode(){
            return key.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ConfigEntry entry){
                return key.equals(entry.key);
            }
            return false;
        }
    }

}
