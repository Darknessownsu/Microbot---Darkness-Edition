package net.runelite.client.plugins.microbot.darkness.intelligence;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration settings for the Intelligence Layer.
 * Allows tuning of risk assessment parameters and behavior modification strategies.
 *
 * <p>This class provides sensible defaults that balance safety and efficiency,
 * but all parameters can be adjusted by advanced users who want more control
 * over how the intelligence layer operates.
 *
 * <p>Usage:
 * <pre>
 * IntelligenceConfig config = IntelligenceConfig.createDefault();
 * config.setPlayerPresenceWeight(0.4); // Increase focus on nearby players
 * config.apply(riskAssessment);
 * </pre>
 *
 * @see RiskAssessment
 * @see ScriptIntelligence
 */
@Slf4j
@Getter
@Setter
public class IntelligenceConfig {

    // ============================================================
    // Risk Factor Weights (must sum to 1.0)
    // ============================================================

    /**
     * Weight for player presence factor in risk calculation.
     * Higher values make nearby players more impactful on risk score.
     * Default: 0.35 (35%)
     */
    private double playerPresenceWeight = 0.35;

    /**
     * Weight for repetitive action duration in risk calculation.
     * Higher values make longer repetitive periods more risky.
     * Default: 0.25 (25%)
     */
    private double repetitiveDurationWeight = 0.25;

    /**
     * Weight for time since random behavior in risk calculation.
     * Higher values make lack of randomness more risky.
     * Default: 0.20 (20%)
     */
    private double timeSinceRandomWeight = 0.20;

    /**
     * Weight for environmental factors in risk calculation.
     * Reserved for future expansion (location, time of day, etc.).
     * Default: 0.20 (20%)
     */
    private double environmentWeight = 0.20;

    // ============================================================
    // Detection Thresholds
    // ============================================================

    /**
     * Maximum number of nearby players before risk is at maximum for this factor.
     * Default: 5
     */
    private int maxNearbyPlayers = 5;

    /**
     * Detection radius for nearby players (in tiles).
     * Players beyond this distance are not considered in risk calculation.
     * Default: 15
     */
    private int playerDetectionRadius = 15;

    // ============================================================
    // Time-Based Thresholds
    // ============================================================

    /**
     * Maximum duration of repetitive actions before risk is at maximum (milliseconds).
     * Longer repetitive periods increase risk score.
     * Default: 300000 (5 minutes)
     */
    private long maxRepetitiveDurationMs = 300000;

    /**
     * Maximum time without random behavior before risk is at maximum (milliseconds).
     * Going longer without randomness increases risk score.
     * Default: 180000 (3 minutes)
     */
    private long maxTimeSinceRandomMs = 180000;

    // ============================================================
    // Behavior Modification Settings
    // ============================================================

    /**
     * Whether to enable the intelligence layer globally.
     * When false, scripts run without risk-based modifications.
     * Default: true
     */
    private boolean enabled = true;

    /**
     * Whether to integrate with the existing Rs2Antiban system.
     * When true, intelligence layer works alongside antiban features.
     * Default: true
     */
    private boolean integrateWithAntiban = true;

    /**
     * Number of actions at a new risk level before transitioning to it.
     * This smooths out rapid oscillations between risk states.
     * Default: 3
     */
    private int riskTransitionThreshold = 3;

    // ============================================================
    // Random Behavior Settings
    // ============================================================

    /**
     * Base chance for random mouse movement (0.0 to 1.0).
     * Actual chance is adjusted by risk level.
     * Default: 0.15 (15%)
     */
    private double randomMouseChance = 0.15;

    /**
     * Base chance for random pauses (0.0 to 1.0).
     * Actual chance is adjusted by risk level.
     * Default: 0.10 (10%)
     */
    private double randomPauseChance = 0.10;

    /**
     * Creates a configuration with default values.
     * These defaults balance safety and efficiency for typical use.
     *
     * @return new config instance with defaults
     */
    public static IntelligenceConfig createDefault() {
        return new IntelligenceConfig();
    }

    /**
     * Creates a configuration optimized for maximum caution.
     * Prioritizes safety over efficiency with more conservative settings.
     *
     * @return new config instance with cautious settings
     */
    public static IntelligenceConfig createCautious() {
        IntelligenceConfig config = new IntelligenceConfig();
        config.playerPresenceWeight = 0.45;  // More focus on players
        config.repetitiveDurationWeight = 0.30;  // Shorter repetitive periods trigger risk
        config.maxRepetitiveDurationMs = 180000;  // 3 minutes instead of 5
        config.maxTimeSinceRandomMs = 120000;  // 2 minutes instead of 3
        config.randomMouseChance = 0.25;  // More random behaviors
        config.randomPauseChance = 0.20;
        return config;
    }

    /**
     * Creates a configuration optimized for efficiency.
     * Accepts more risk for faster execution with less intervention.
     *
     * @return new config instance with efficient settings
     */
    public static IntelligenceConfig createEfficient() {
        IntelligenceConfig config = new IntelligenceConfig();
        config.playerPresenceWeight = 0.25;  // Less focus on players
        config.repetitiveDurationWeight = 0.20;  // Longer repetitive periods allowed
        config.maxRepetitiveDurationMs = 480000;  // 8 minutes
        config.maxTimeSinceRandomMs = 300000;  // 5 minutes
        config.randomMouseChance = 0.08;  // Fewer random behaviors
        config.randomPauseChance = 0.05;
        return config;
    }

    /**
     * Applies this configuration to a RiskAssessment instance.
     *
     * @param riskAssessment the risk assessment to configure
     */
    public void apply(RiskAssessment riskAssessment) {
        if (riskAssessment == null) {
            log.warn("Cannot apply config to null RiskAssessment");
            return;
        }

        riskAssessment.setPlayerPresenceWeight(playerPresenceWeight);
        riskAssessment.setRepetitiveDurationWeight(repetitiveDurationWeight);
        riskAssessment.setTimeSinceRandomWeight(timeSinceRandomWeight);
        riskAssessment.setEnvironmentWeight(environmentWeight);

        riskAssessment.setMaxNearbyPlayers(maxNearbyPlayers);
        riskAssessment.setPlayerDetectionRadius(playerDetectionRadius);
        riskAssessment.setMaxRepetitiveDurationMs(maxRepetitiveDurationMs);
        riskAssessment.setMaxTimeSinceRandomMs(maxTimeSinceRandomMs);

        log.debug("Darkness Edition: Intelligence configuration applied");
    }

    /**
     * Applies this configuration to a ScriptIntelligence instance.
     *
     * @param intelligence the script intelligence to configure
     */
    public void apply(ScriptIntelligence intelligence) {
        if (intelligence == null) {
            log.warn("Cannot apply config to null ScriptIntelligence");
            return;
        }

        intelligence.setEnabled(enabled);
        intelligence.setIntegrateWithAntiban(integrateWithAntiban);
        apply(intelligence.getRiskAssessment());

        log.debug("Darkness Edition: Intelligence configuration applied");
    }

    /**
     * Validates that the configuration is consistent and usable.
     * Logs warnings for problematic settings but does not throw exceptions.
     *
     * @return true if configuration is valid
     */
    public boolean validate() {
        boolean valid = true;

        // Check weight sum
        double weightSum = playerPresenceWeight + repetitiveDurationWeight +
                timeSinceRandomWeight + environmentWeight;
        if (Math.abs(weightSum - 1.0) > 0.01) {
            log.warn("Risk factor weights sum to {} instead of 1.0", weightSum);
            valid = false;
        }

        // Check individual weights are in valid range
        if (playerPresenceWeight < 0 || playerPresenceWeight > 1.0) {
            log.warn("Player presence weight {} is out of range [0, 1]", playerPresenceWeight);
            valid = false;
        }

        // Check thresholds are positive
        if (maxRepetitiveDurationMs <= 0 || maxTimeSinceRandomMs <= 0) {
            log.warn("Time thresholds must be positive");
            valid = false;
        }

        if (maxNearbyPlayers <= 0) {
            log.warn("Max nearby players must be positive");
            valid = false;
        }

        return valid;
    }

    /**
     * Normalizes the risk factor weights to sum to exactly 1.0.
     * Useful after manually adjusting individual weights.
     */
    public void normalizeWeights() {
        double sum = playerPresenceWeight + repetitiveDurationWeight +
                timeSinceRandomWeight + environmentWeight;

        if (sum > 0 && Math.abs(sum - 1.0) > 0.01) {
            playerPresenceWeight /= sum;
            repetitiveDurationWeight /= sum;
            timeSinceRandomWeight /= sum;
            environmentWeight /= sum;

            log.debug("Darkness Edition: Weights normalized to sum to 1.0");
        }
    }

    @Override
    public String toString() {
        return String.format(
                "IntelligenceConfig[enabled=%s, weights=(%.2f,%.2f,%.2f,%.2f), " +
                        "maxPlayers=%d, maxRepetitive=%dms, maxRandom=%dms]",
                enabled,
                playerPresenceWeight, repetitiveDurationWeight,
                timeSinceRandomWeight, environmentWeight,
                maxNearbyPlayers, maxRepetitiveDurationMs, maxTimeSinceRandomMs
        );
    }
}
