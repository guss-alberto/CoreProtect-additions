# CoreProtect-additions

CoreProtect-additions is an addon for [CoreProtect](https://modrinth.com/plugin/coreprotect) that adds logging for extra features that are currently unsupported by CoreProtect.

Logging has been added for
- Renaming mobs with a name tag
- Starting raids
- Dyeing entities
- Placing and breaking boats and Minecarts
- Mounting and dismounting entities
- Igniting TNT
- Cusing Creeper or Ghast fireballs to explode
- Leashing and unleashing mobs
- Setting spawn on a Bed or Respawn Anchor
- Exploding a Bed or Respawn Anchor
- Opening entity container GUIs.

# Logging details

The formats used to specify the action are the ones used in the `/co lookup` command. For the `a:block` action, if no `+` or `-` is specified, both versions are used as expected.

Due to limitations of the CoreProtect API, these events are logged in sometimes unintuitive ways as `a:block` actions, using items to differentiate them. 

-  `a:+block i:<dye>`: dyeing a mob (sheep, dog collar etc), with the, coordinates of the entity dye will refer to the applied dye item.
-  `a:+block i:name_tag`: renaming a mob (neither new or old mob name are stored), coordinates of the animal.
-  `a:-block i:ominous_bottle`: triggering a raid, coordinates of the raid origin.
-  `a:-block i:creeper_spawn_egg`: causing a creeper to explode, coordinates of the creeper.
-  `a:-block i:fire_charge`: causing a Ghast fireball to explode, coordinates of the fireball.

----

-  `a:block i:<vehicle>`: placing or breaking a Minecart or Boat of any kind (vehicle will refer to the particular vehicle e.g. `oak_chest_boat`)
-  `a:block i:<spawn_egg>`: entering or exiting a Minecart of Boat (if enabled in config)
-  `a:block i:<spawn_egg>`: mounting or dismounting a Mob, spawn egg reprsents the mob ridden.
-  `a:block i:lead`: placing or breaking a Leash. This includes riding away with a leashed mob. At the coordinates of the leash knot or the entity that was unleashed depending on the situation.

---

The `a:click` action is used for everything else:
- `a:click i:tnt`: igniting TNT
- `a:click i:creeper_spawn_egg`: igniying a creeper with a flint and steel, coordinates of the creeper at time of explosion (NOT logged when the creeper was ignited).
- `a:click i:<bed>`: setting spawn on a Bed or exploding it (with a specified bed colour e.g. `magenta_bed`) 
- `a:click i:respawn_anchor`: setting spawn on a Respawn Anchor or exploding it.
- `a:click i:<spawn_egg>`: clicking on a chested mob (Llama, donkey, etc..).
- `a:click i:<vehicle>`: clicking on an inventory vehicle (Hopper cart, chest boat, etc..), or mounting/dismounting it (boat, minecart).

# Config
Config is available, entries should be fairly self-explanatory, and comments are present in the config.

Note: user comments in the config will be deleted next plugin reload.

## Notes and limitations:
Breaking a fence to which a mob is leashed will be logged as `a:-block i:lead` by `#physics` at the location of the fence post. A manual lookup is needed to verify who broke the fence.

TNT triggered by a Bed or Respawn Anchor explosion currently gets logged as `a:click i:tnt` by `#block` ideally it should be traced back to the player who clicked it.

None of these actions are rollback-able. This is not supported by the current CoreProtect API.