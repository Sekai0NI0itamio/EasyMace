package com.easymace.config;

import com.easymace.EasyMaceMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EasyMaceConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("easymace.json");

    private static EasyMaceConfig INSTANCE;

    public boolean enabled = true;

    /** Fall distance (blocks) at which an attack counts as a "slam" (uses Density). */
    public double slamHeightThreshold = 1.5;

    /** Preferred enchantment for normal (non-slam) hits. */
    public String preferredEnchantForNormal = "breach";

    /** Preferred enchantment for slam hits. */
    public String preferredEnchantForSlam = "density";

    /** Entity hitbox expansion multiplier (1.0 = vanilla, 5.0 = 5x). */
    public double hitboxExpand = 1.0;

    /** Whether hitbox expansion is enabled. */
    public boolean hitboxEnabled = true;

    /** Expand hitboxes for ALL entities (true) or only those in hitboxEntities (false). */
    public boolean hitboxAllEntities = true;

    /** Entity type IDs to expand when hitboxAllEntities is false (e.g. "minecraft:player"). */
    public List<String> hitboxEntities = new ArrayList<>();

    /** Hotbar slot to return to after swap (-1 = track automatically). */
    public int returnSlot = -1;

    public static EasyMaceConfig get() {
        if (INSTANCE == null) load();
        return INSTANCE;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader r = Files.newBufferedReader(CONFIG_PATH)) {
                INSTANCE = GSON.fromJson(r, EasyMaceConfig.class);
                if (INSTANCE == null) INSTANCE = new EasyMaceConfig();
            } catch (Exception e) {
                EasyMaceMod.LOG.warn("[EasyMace] Failed to load config, using defaults: {}", e.toString());
                INSTANCE = new EasyMaceConfig();
            }
        } else {
            INSTANCE = new EasyMaceConfig();
            save();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer w = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(INSTANCE, w);
            }
        } catch (IOException e) {
            EasyMaceMod.LOG.warn("[EasyMace] Failed to save config: {}", e.toString());
        }
    }
}
