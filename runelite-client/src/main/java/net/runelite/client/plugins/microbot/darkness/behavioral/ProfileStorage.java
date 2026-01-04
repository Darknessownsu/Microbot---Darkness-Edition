package net.runelite.client.plugins.microbot.darkness.behavioral;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Handles persistent storage of behavioral profiles.
 * Profiles are stored as JSON files in the RuneLite configuration directory.
 *
 * <p>The default profile is stored at: {@code ~/.runelite/darkness-profile.json}
 *
 * <p>Usage:
 * <pre>
 * // Save a profile
 * ProfileStorage.saveProfile(profile);
 *
 * // Load a profile
 * BehaviorProfile profile = ProfileStorage.loadProfile();
 * </pre>
 *
 * @see BehaviorProfile
 * @see BehaviorProfiler
 */
@Slf4j
public class ProfileStorage {

    /**
     * Directory where Darkness Edition data is stored.
     */
    private static final File DARKNESS_DIR = new File(RuneLite.RUNELITE_DIR, "darkness");

    /**
     * Default profile file name.
     */
    private static final String DEFAULT_PROFILE_NAME = "darkness-profile.json";

    /**
     * Gets the default profile file path.
     *
     * @return the profile file
     */
    private static File getDefaultProfileFile() {
        // Ensure directory exists
        if (!DARKNESS_DIR.exists()) {
            if (!DARKNESS_DIR.mkdirs()) {
                log.warn("Failed to create Darkness Edition directory: {}", DARKNESS_DIR);
            }
        }
        return new File(DARKNESS_DIR, DEFAULT_PROFILE_NAME);
    }

    /**
     * Saves a profile to the default location.
     *
     * @param profile the profile to save
     * @return true if save was successful, false otherwise
     */
    public static boolean saveProfile(BehaviorProfile profile) {
        return saveProfile(profile, getDefaultProfileFile());
    }

    /**
     * Saves a profile to a specific file.
     *
     * @param profile the profile to save
     * @param file    the destination file
     * @return true if save was successful, false otherwise
     */
    public static boolean saveProfile(BehaviorProfile profile, File file) {
        if (profile == null) {
            log.warn("Cannot save null profile");
            return false;
        }

        try {
            String json = profile.toJson();
            Path path = file.toPath();

            // Create parent directories if needed
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            // Write JSON to file
            Files.writeString(path, json, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            log.info("Darkness Edition: Behavioral profile saved to {}", file.getAbsolutePath());
            return true;
        } catch (IOException e) {
            log.error("Failed to save behavioral profile to {}", file, e);
            return false;
        }
    }

    /**
     * Loads a profile from the default location.
     * If no profile exists, returns a new empty profile.
     *
     * @return the loaded profile, or a new empty profile if loading fails
     */
    public static BehaviorProfile loadProfile() {
        return loadProfile(getDefaultProfileFile());
    }

    /**
     * Loads a profile from a specific file.
     * If the file doesn't exist or loading fails, returns a new empty profile.
     *
     * @param file the file to load from
     * @return the loaded profile, or a new empty profile if loading fails
     */
    public static BehaviorProfile loadProfile(File file) {
        if (!file.exists()) {
            log.debug("No profile file exists at {}, using default profile", file);
            return new BehaviorProfile();
        }

        try {
            String json = Files.readString(file.toPath());
            BehaviorProfile profile = BehaviorProfile.fromJson(json);

            log.info("Darkness Edition: Behavioral profile loaded from {}", file.getAbsolutePath());
            return profile;
        } catch (IOException e) {
            log.error("Failed to load behavioral profile from {}", file, e);
            return new BehaviorProfile();
        }
    }

    /**
     * Checks if a profile file exists at the default location.
     *
     * @return true if a profile file exists
     */
    public static boolean profileExists() {
        return getDefaultProfileFile().exists();
    }

    /**
     * Deletes the profile file at the default location.
     *
     * @return true if deletion was successful or file didn't exist
     */
    public static boolean deleteProfile() {
        return deleteProfile(getDefaultProfileFile());
    }

    /**
     * Deletes a specific profile file.
     *
     * @param file the file to delete
     * @return true if deletion was successful or file didn't exist
     */
    public static boolean deleteProfile(File file) {
        if (!file.exists()) {
            return true;
        }

        try {
            Files.delete(file.toPath());
            log.info("Darkness Edition: Behavioral profile deleted from {}", file.getAbsolutePath());
            return true;
        } catch (IOException e) {
            log.error("Failed to delete behavioral profile from {}", file, e);
            return false;
        }
    }

    /**
     * Gets the path where profiles are stored.
     *
     * @return the darkness directory
     */
    public static File getProfileDirectory() {
        return DARKNESS_DIR;
    }

    /**
     * Gets information about the default profile file.
     *
     * @return human-readable status string
     */
    public static String getProfileInfo() {
        File file = getDefaultProfileFile();
        if (!file.exists()) {
            return "No profile saved";
        }

        try {
            long sizeBytes = Files.size(file.toPath());
            long lastModified = file.lastModified();
            return String.format("Profile exists: %d bytes, last modified: %tc", sizeBytes, lastModified);
        } catch (IOException e) {
            return "Profile file exists but cannot read info";
        }
    }
}
