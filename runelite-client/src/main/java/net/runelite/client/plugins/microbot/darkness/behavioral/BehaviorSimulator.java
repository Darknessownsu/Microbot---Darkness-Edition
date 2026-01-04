package net.runelite.client.plugins.microbot.darkness.behavioral;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * Applies captured behavioral profiles to automated actions.
 * Instead of using fixed delays or simple random ranges, this simulator
 * generates values based on the user's actual behavior patterns.
 *
 * <p>Usage:
 * <pre>
 * BehaviorSimulator simulator = new BehaviorSimulator(profile);
 *
 * // Instead of: sleep(200);
 * // Use: sleep(simulator.getActionDelay());
 *
 * // Instead of: sleep(random(100, 300));
 * // Use: sleep(simulator.getClickDelay());
 * </pre>
 *
 * <p>The simulator uses normal distributions with parameters learned from
 * the user's actual behavior to generate realistic variations.
 *
 * @see BehaviorProfile
 * @see BehaviorProfiler
 */
@Slf4j
public class BehaviorSimulator {

    @Getter
    @Setter
    private BehaviorProfile profile;

    private final Random random;

    /**
     * Default minimum delay to prevent overly fast actions (milliseconds).
     */
    private static final int MIN_DELAY_MS = 50;

    /**
     * Default maximum delay to prevent excessive waits (milliseconds).
     */
    private static final int MAX_DELAY_MS = 10000;

    /**
     * Creates a new BehaviorSimulator with the specified profile.
     *
     * @param profile the behavioral profile to use for generating values
     */
    public BehaviorSimulator(BehaviorProfile profile) {
        this.profile = profile != null ? profile : new BehaviorProfile();
        this.random = new Random();
    }

    /**
     * Creates a new BehaviorSimulator with an empty (default) profile.
     */
    public BehaviorSimulator() {
        this(new BehaviorProfile());
    }

    /**
     * Generates a delay between actions based on the user's behavioral profile.
     * Uses a normal distribution centered on the user's mean action delay.
     *
     * @return delay in milliseconds, clamped to reasonable bounds
     */
    public int getActionDelay() {
        double delay = sampleNormal(profile.getActionDelayMean(), profile.getActionDelayStdDev());
        return clampDelay((int) delay);
    }

    /**
     * Generates a click delay based on the user's behavioral profile.
     *
     * @return delay in milliseconds
     */
    public int getClickDelay() {
        double delay = sampleNormal(profile.getClickDelayMean(), profile.getClickDelayStdDev());
        return clampDelay((int) delay, 10, 1000);
    }

    /**
     * Generates a delay with a specified multiplier applied to the mean.
     * Useful for intentionally slower or faster actions.
     *
     * @param multiplier factor to apply to the mean (1.0 = normal, 2.0 = double)
     * @return delay in milliseconds
     */
    public int getActionDelayWithMultiplier(double multiplier) {
        double adjustedMean = profile.getActionDelayMean() * multiplier;
        double delay = sampleNormal(adjustedMean, profile.getActionDelayStdDev());
        return clampDelay((int) delay);
    }

    /**
     * Generates a mouse velocity based on the user's behavioral profile.
     *
     * @return velocity in pixels per second
     */
    public double getMouseVelocity() {
        double velocity = sampleNormal(profile.getMouseVelocityMean(), profile.getMouseVelocityStdDev());
        return Math.max(100.0, Math.min(velocity, 5000.0)); // Clamp to reasonable range
    }

    /**
     * Determines whether to take a micro-break based on behavioral patterns.
     * Returns true occasionally to simulate natural human breaks.
     *
     * @param baseChance the base probability (0.0 to 1.0)
     * @return true if a break should be taken
     */
    public boolean shouldTakeMicroBreak(double baseChance) {
        // Add some variation to the break chance
        double adjustedChance = baseChance * (0.8 + random.nextDouble() * 0.4);
        return random.nextDouble() < adjustedChance;
    }

    /**
     * Generates a random delay within a range, with distribution influenced
     * by the behavioral profile.
     *
     * @param minMs minimum delay in milliseconds
     * @param maxMs maximum delay in milliseconds
     * @return delay in milliseconds
     */
    public int getDelayInRange(int minMs, int maxMs) {
        // Use profile's standard deviation to determine how much variation
        double range = maxMs - minMs;
        double mean = minMs + (range / 2);
        double stdDev = range / 4; // 95% of values within range

        // Blend with user's natural variation
        if (profile.isCalibrated()) {
            double userVariation = profile.getActionDelayStdDev() / profile.getActionDelayMean();
            stdDev = stdDev * (1 + userVariation);
        }

        double delay = sampleNormal(mean, stdDev);
        return clampDelay((int) delay, minMs, maxMs);
    }

    /**
     * Samples from a normal (Gaussian) distribution.
     *
     * @param mean   the mean of the distribution
     * @param stdDev the standard deviation
     * @return a random sample from the distribution
     */
    private double sampleNormal(double mean, double stdDev) {
        if (stdDev <= 0) {
            return mean;
        }
        return mean + random.nextGaussian() * stdDev;
    }

    /**
     * Clamps a delay to default reasonable bounds.
     */
    private int clampDelay(int delay) {
        return clampDelay(delay, MIN_DELAY_MS, MAX_DELAY_MS);
    }

    /**
     * Clamps a delay to specified bounds.
     */
    private int clampDelay(int delay, int min, int max) {
        return Math.max(min, Math.min(delay, max));
    }

    /**
     * Checks if the current profile is calibrated and ready for use.
     *
     * @return true if the profile has sufficient data
     */
    public boolean isProfileReady() {
        return profile != null && profile.isCalibrated();
    }

    /**
     * Gets a human-readable summary of the simulator status.
     *
     * @return status string
     */
    public String getStatusSummary() {
        if (profile == null) {
            return "No profile loaded";
        } else if (profile.isCalibrated()) {
            return String.format("Ready (delay: %.0f±%.0f ms)",
                    profile.getActionDelayMean(), profile.getActionDelayStdDev());
        } else {
            return String.format("Using defaults (%.0f%% calibrated)",
                    profile.getCalibrationProgress() * 100);
        }
    }
}
