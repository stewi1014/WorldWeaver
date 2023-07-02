package org.betterx.wover.core.api;

import de.ambertation.wunderlib.utils.Version;

import net.minecraft.resources.ResourceLocation;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.util.HashMap;
import java.util.Optional;


/**
 * This class is used to identify your mod and provide some helpfull utilities
 * specific to your Mod.
 * <p>
 * It is considered best practice to create, and store an instance of this class
 * for you mod in your main Entrypoint (the class that implements
 * {@link net.fabricmc.api.ModInitializer}).
 */
public final class ModCore implements Version.ModVersionProvider {
    private static final HashMap<String, ModCore> cache = new HashMap<>();
    /**
     * This logger is used to write text to the console and the log file.
     * The mod id is used as the logger's name, making it clear which mod wrote info,
     * warnings, and errors.
     */
    public final Logger LOG;

    /**
     * alias for {@link #LOG}
     */
    public final Logger log;

    /**
     * The mod id is used to identify your mod.
     */
    public final String MOD_ID;
    private final Version modVersion;

    private ModCore(String modID) {
        LOG = Logger.create(modID);
        log = LOG;
        MOD_ID = modID;

        Optional<ModContainer> optional = FabricLoader.getInstance().getModContainer(MOD_ID);
        if (optional.isPresent()) {
            ModContainer modContainer = optional.get();
            modVersion = new Version(modContainer.getMetadata().getVersion().toString());
        } else {
            modVersion = new Version(0, 0, 0);
            ;
        }
    }


    /**
     * Returns the {@link Version} of this mod.
     *
     * @return the {@link Version} of this mod.
     */
    public Version getModVersion() {
        return modVersion;
    }

    /**
     * Returns the {@link #MOD_ID} of this mod.
     *
     * @return the {@link #MOD_ID} of this mod.
     */
    @Override
    public String getModID() {
        return MOD_ID;
    }


    /**
     * Returns the {@link ResourceLocation} for the given name in the namespace of this mod.
     * <p>
     * You should always prefer this method over {@link ResourceLocation#ResourceLocation(String, String)}.
     *
     * @param name The name or path of the resource.
     * @return The {@link ResourceLocation} for the given name.
     */
    public ResourceLocation id(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    /**
     * alias for {@link #id(String)}
     *
     * @param key The name or path of the resource.
     * @return The {@link ResourceLocation} for the given name.
     */
    @Override
    public ResourceLocation mk(String key) {
        return new ResourceLocation(MOD_ID, key);
    }


    /**
     * Returns the instance of {@link ModCore} for the given mod id. Every mod id has a unique, single
     * instance. Calling this method multiple times with the same mod id is guaranteed to return
     * the same instance.
     *
     * @param modID The mod id of the mod.
     * @return The instance of {@link ModCore} for the given mod id.
     */
    public static ModCore create(String modID) {
        return cache.computeIfAbsent(modID, id -> new ModCore(id));
    }

    /**
     * Returns true if the game is currently running in a data generation environment.
     *
     * @return true if the game is currently running in a data generation environment.
     */
    public static boolean isDatagen() {
        return System.getProperty("fabric-api.datagen") != null;
    }

    /**
     * Returns true if the game is currently running in a development environment.
     *
     * @return true if the game is currently running in a development environment.
     */
    public static boolean isDevEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    /**
     * Returns true if the game is currently running on the client.
     *
     * @return true if the game is currently running on the client.
     */
    public static boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

}
