package net.runelite.client.plugins.microbot.darkness;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.darkness.behavioral.BehaviorProfile;
import net.runelite.client.plugins.microbot.darkness.behavioral.BehaviorProfiler;
import net.runelite.client.plugins.microbot.darkness.behavioral.ProfileStorage;
import net.runelite.client.plugins.microbot.darkness.intelligence.IntelligenceConfig;
import net.runelite.client.plugins.microbot.darkness.intelligence.ScriptIntelligence;

/**
 * Central manager for all Darkness Edition features.
 * This class provides a unified interface for accessing behavioral profiling
 * and intelligence layer functionality.
 *
 * <p>The DarknessManager is designed as a singleton to ensure consistent state
 * across the application. It automatically loads saved profiles on initialization
 * and can be configured through various methods.
 *
 * <p>Usage:
 * <pre>
 * DarknessManager manager = DarknessManager.getInstance();
 *
 * // Start calibration
 * manager.startCalibration();
 *
 * // Use in scripts
 * if (manager.getIntelligence().shouldProceed()) {
 *     // Perform action
 *     sleep(manager.getIntelligence().getSmartDelay());
 *     manager.getIntelligence().afterAction("gather");
 * }
 * </pre>
 *
 * @see BehaviorProfiler
 * @see ScriptIntelligence
 */
@Slf4j
public class DarknessManager {

    private static DarknessManager instance;

    @Getter
    private final BehaviorProfiler profiler;

    @Getter
    private final ScriptIntelligence intelligence;

    @Getter
    @Setter
    private IntelligenceConfig config;

    @Getter
    private boolean autoSaveEnabled = true;

    /**
     * Gets the singleton instance of DarknessManager.
     *
     * @return the DarknessManager instance
     */
    public static synchronized DarknessManager getInstance() {
        if (instance == null) {
            instance = new DarknessManager();
        }
        return instance;
    }

    /**
     * Private constructor initializes the manager with saved profile if available.
     */
    private DarknessManager() {
        log.info("Darkness Edition: Initializing manager");

        // Load saved profile or create new one
        BehaviorProfile savedProfile = ProfileStorage.loadProfile();
        this.profiler = new BehaviorProfiler(savedProfile);

        // Initialize intelligence with the profile
        this.intelligence = ScriptIntelligence.getInstance();
        this.intelligence.setProfile(savedProfile);

        // Create and apply default configuration
        this.config = IntelligenceConfig.createDefault();
        this.config.apply(intelligence);

        log.info("Darkness Edition: Manager initialized. Profile status: {}",
                savedProfile.isCalibrated() ? "calibrated" : "not calibrated");
    }

    /**
     * Starts behavioral calibration.
     * During calibration, the profiler records user actions to build a behavioral model.
     */
    public void startCalibration() {
        profiler.startCalibration();
        log.info("Darkness Edition: Calibration started");
    }

    /**
     * Stops behavioral calibration and saves the profile.
     *
     * @return true if profile was saved successfully
     */
    public boolean stopCalibration() {
        profiler.stopCalibration();

        if (autoSaveEnabled) {
            boolean saved = saveProfile();
            if (saved) {
                // Update intelligence with new profile
                intelligence.setProfile(profiler.getProfile());
                log.info("Darkness Edition: Calibration stopped and profile saved");
            } else {
                log.warn("Darkness Edition: Calibration stopped but profile save failed");
            }
            return saved;
        } else {
            log.info("Darkness Edition: Calibration stopped (auto-save disabled)");
            return true;
        }
    }

    /**
     * Checks if calibration is currently in progress.
     *
     * @return true if calibrating
     */
    public boolean isCalibrating() {
        return profiler.isCalibrating();
    }

    /**
     * Gets the current calibration progress.
     *
     * @return progress from 0.0 to 1.0
     */
    public double getCalibrationProgress() {
        return profiler.getCalibrationProgress();
    }

    /**
     * Checks if the current profile is sufficiently calibrated for use.
     *
     * @return true if profile has enough data
     */
    public boolean isProfileCalibrated() {
        return profiler.isCalibrated();
    }

    /**
     * Saves the current profile to storage.
     *
     * @return true if save was successful
     */
    public boolean saveProfile() {
        return ProfileStorage.saveProfile(profiler.getProfile());
    }

    /**
     * Loads a profile from storage and applies it.
     *
     * @return true if load was successful
     */
    public boolean loadProfile() {
        BehaviorProfile loaded = ProfileStorage.loadProfile();
        if (loaded != null) {
            profiler.getProfile().reset();
            // Copy data from loaded profile to current profile
            // This preserves the profiler's reference while updating the data
            intelligence.setProfile(loaded);
            log.info("Darkness Edition: Profile loaded");
            return true;
        }
        return false;
    }

    /**
     * Resets the behavioral profile to default values.
     */
    public void resetProfile() {
        profiler.reset();
        intelligence.setProfile(profiler.getProfile());
        log.info("Darkness Edition: Profile reset");
    }

    /**
     * Deletes the saved profile file.
     *
     * @return true if deletion was successful
     */
    public boolean deleteProfile() {
        return ProfileStorage.deleteProfile();
    }

    /**
     * Applies a preset configuration.
     *
     * @param preset the configuration preset to apply
     */
    public void applyPreset(ConfigPreset preset) {
        switch (preset) {
            case DEFAULT:
                this.config = IntelligenceConfig.createDefault();
                break;
            case CAUTIOUS:
                this.config = IntelligenceConfig.createCautious();
                break;
            case EFFICIENT:
                this.config = IntelligenceConfig.createEfficient();
                break;
        }
        this.config.apply(intelligence);
        log.info("Darkness Edition: Applied {} preset", preset.name());
    }

    /**
     * Gets a comprehensive status summary for display.
     *
     * @return multi-line status summary
     */
    public String getStatusSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Darkness Edition Status ===\n");
        sb.append("Behavioral Profile: ");
        sb.append(profiler.getStatusSummary()).append("\n");
        sb.append("Intelligence Layer: ");
        sb.append(intelligence.getStatusSummary()).append("\n");
        sb.append("Configuration: ").append(config.toString()).append("\n");
        sb.append("Storage: ").append(ProfileStorage.getProfileInfo());
        return sb.toString();
    }

    /**
     * Gets a brief status string for overlay display.
     *
     * @return short status string
     */
    public String getShortStatus() {
        if (isCalibrating()) {
            return String.format("Calibrating: %.0f%%", getCalibrationProgress() * 100);
        } else if (isProfileCalibrated()) {
            return intelligence.getStatusSummary();
        } else {
            return "No profile - start calibration";
        }
    }

    /**
     * Enables or disables the intelligence layer.
     *
     * @param enabled true to enable, false to disable
     */
    public void setIntelligenceEnabled(boolean enabled) {
        intelligence.setEnabled(enabled);
        log.info("Darkness Edition: Intelligence layer {}", enabled ? "enabled" : "disabled");
    }

    /**
     * Checks if the intelligence layer is enabled.
     *
     * @return true if enabled
     */
    public boolean isIntelligenceEnabled() {
        return intelligence.isEnabled();
    }

    /**
     * Enables or disables automatic profile saving.
     *
     * @param enabled true to enable auto-save
     */
    public void setAutoSaveEnabled(boolean enabled) {
        this.autoSaveEnabled = enabled;
        log.debug("Darkness Edition: Auto-save {}", enabled ? "enabled" : "disabled");
    }

    /**
     * Configuration presets for quick setup.
     */
    public enum ConfigPreset {
        /**
         * Balanced configuration suitable for most users.
         */
        DEFAULT,

        /**
         * Conservative configuration prioritizing safety over speed.
         */
        CAUTIOUS,

        /**
         * Aggressive configuration prioritizing speed over maximum safety.
         */
        EFFICIENT
    }
}
