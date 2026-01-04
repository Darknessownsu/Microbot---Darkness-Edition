package net.runelite.client.plugins.microbot.darkness.behavioral;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores statistical distributions of user behavior patterns captured during calibration.
 * This profile is used by {@link BehaviorSimulator} to generate human-like variations
 * in automated actions.
 *
 * <p>The profile captures:
 * <ul>
 *   <li>Timing distributions between actions (mean, standard deviation)</li>
 *   <li>Mouse movement characteristics (velocity, acceleration patterns)</li>
 *   <li>Click timing patterns</li>
 * </ul>
 *
 * <p>Profiles can be serialized to JSON for persistent storage.
 *
 * @see BehaviorProfiler
 * @see BehaviorSimulator
 */
@Slf4j
@Getter
@Setter
public class BehaviorProfile {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int PROFILE_VERSION = 1;

    /**
     * Profile version for schema migration support.
     */
    private int version = PROFILE_VERSION;

    /**
     * Whether this profile has sufficient data for reliable simulation.
     */
    private boolean calibrated = false;

    /**
     * Minimum samples required for reliable calibration.
     */
    private static final int MIN_TIMING_SAMPLES = 30;
    private static final int MIN_MOUSE_SAMPLES = 50;

    // Timing distributions
    private double actionDelayMean = 300.0; // milliseconds
    private double actionDelayStdDev = 100.0;
    private double clickDelayMean = 50.0;
    private double clickDelayStdDev = 20.0;

    // Mouse movement characteristics
    private double mouseVelocityMean = 800.0; // pixels per second
    private double mouseVelocityStdDev = 200.0;
    private double mouseAccelerationMean = 2000.0;
    private double mouseAccelerationStdDev = 500.0;

    // Raw samples for statistical computation
    private List<Long> actionDelaySamples = new ArrayList<>();
    private List<Long> clickDelaySamples = new ArrayList<>();
    private List<Double> mouseVelocitySamples = new ArrayList<>();

    // Metadata
    private long calibrationStartTime = 0;
    private long calibrationEndTime = 0;
    private int totalActionsSampled = 0;

    /**
     * Creates a new empty behavior profile with default values.
     */
    public BehaviorProfile() {
        // Default constructor with reasonable defaults
    }

    /**
     * Adds a timing sample between user actions.
     *
     * @param delayMs the delay in milliseconds between actions
     */
    public void addActionDelaySample(long delayMs) {
        if (delayMs > 0 && delayMs < 30000) { // Ignore unrealistic values
            actionDelaySamples.add(delayMs);
            totalActionsSampled++;
            recalculateTimingStats();
        }
    }

    /**
     * Adds a click timing sample.
     *
     * @param delayMs the delay in milliseconds for the click action
     */
    public void addClickDelaySample(long delayMs) {
        if (delayMs > 0 && delayMs < 5000) {
            clickDelaySamples.add(delayMs);
            recalculateClickStats();
        }
    }

    /**
     * Adds a mouse velocity sample.
     *
     * @param velocityPps the mouse velocity in pixels per second
     */
    public void addMouseVelocitySample(double velocityPps) {
        if (velocityPps > 0 && velocityPps < 10000) {
            mouseVelocitySamples.add(velocityPps);
            recalculateMouseStats();
        }
    }

    /**
     * Recalculates timing statistics from collected samples.
     */
    private void recalculateTimingStats() {
        if (actionDelaySamples.size() >= 2) {
            actionDelayMean = calculateMean(actionDelaySamples);
            actionDelayStdDev = calculateStdDev(actionDelaySamples, actionDelayMean);
        }
        checkCalibrationStatus();
    }

    /**
     * Recalculates click statistics from collected samples.
     */
    private void recalculateClickStats() {
        if (clickDelaySamples.size() >= 2) {
            clickDelayMean = calculateMean(clickDelaySamples);
            clickDelayStdDev = calculateStdDev(clickDelaySamples, clickDelayMean);
        }
        checkCalibrationStatus();
    }

    /**
     * Recalculates mouse movement statistics from collected samples.
     */
    private void recalculateMouseStats() {
        if (mouseVelocitySamples.size() >= 2) {
            mouseVelocityMean = calculateMeanDouble(mouseVelocitySamples);
            mouseVelocityStdDev = calculateStdDevDouble(mouseVelocitySamples, mouseVelocityMean);
        }
        checkCalibrationStatus();
    }

    /**
     * Checks if sufficient data has been collected for reliable simulation.
     */
    private void checkCalibrationStatus() {
        calibrated = actionDelaySamples.size() >= MIN_TIMING_SAMPLES
                && mouseVelocitySamples.size() >= MIN_MOUSE_SAMPLES;
    }

    /**
     * Gets the calibration progress as a percentage.
     *
     * @return calibration progress from 0.0 to 1.0
     */
    public double getCalibrationProgress() {
        double timingProgress = Math.min(1.0, (double) actionDelaySamples.size() / MIN_TIMING_SAMPLES);
        double mouseProgress = Math.min(1.0, (double) mouseVelocitySamples.size() / MIN_MOUSE_SAMPLES);
        return (timingProgress + mouseProgress) / 2.0;
    }

    /**
     * Resets the profile to initial state.
     */
    public void reset() {
        calibrated = false;
        actionDelaySamples.clear();
        clickDelaySamples.clear();
        mouseVelocitySamples.clear();
        totalActionsSampled = 0;
        calibrationStartTime = 0;
        calibrationEndTime = 0;

        // Reset to defaults
        actionDelayMean = 300.0;
        actionDelayStdDev = 100.0;
        clickDelayMean = 50.0;
        clickDelayStdDev = 20.0;
        mouseVelocityMean = 800.0;
        mouseVelocityStdDev = 200.0;
    }

    /**
     * Serializes this profile to JSON.
     *
     * @return JSON string representation
     */
    public String toJson() {
        return GSON.toJson(this);
    }

    /**
     * Deserializes a profile from JSON.
     *
     * @param json the JSON string
     * @return the deserialized profile, or a new empty profile if parsing fails
     */
    public static BehaviorProfile fromJson(String json) {
        try {
            BehaviorProfile profile = GSON.fromJson(json, BehaviorProfile.class);
            if (profile != null && profile.version <= PROFILE_VERSION) {
                return profile;
            }
        } catch (Exception e) {
            log.warn("Failed to parse behavior profile from JSON", e);
        }
        return new BehaviorProfile();
    }

    // Statistical helper methods
    private double calculateMean(List<Long> samples) {
        return samples.stream().mapToLong(Long::longValue).average().orElse(0.0);
    }

    private double calculateMeanDouble(List<Double> samples) {
        return samples.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private double calculateStdDev(List<Long> samples, double mean) {
        if (samples.size() < 2) return 0.0;
        double variance = samples.stream()
                .mapToDouble(s -> Math.pow(s - mean, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }

    private double calculateStdDevDouble(List<Double> samples, double mean) {
        if (samples.size() < 2) return 0.0;
        double variance = samples.stream()
                .mapToDouble(s -> Math.pow(s - mean, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }

    @Override
    public String toString() {
        return String.format("BehaviorProfile[calibrated=%s, samples=%d, actionDelay=%.1f±%.1f ms]",
                calibrated, totalActionsSampled, actionDelayMean, actionDelayStdDev);
    }
}
