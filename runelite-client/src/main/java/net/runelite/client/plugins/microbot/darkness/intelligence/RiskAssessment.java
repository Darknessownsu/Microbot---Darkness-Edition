package net.runelite.client.plugins.microbot.darkness.intelligence;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

/**
 * Calculates a numerical risk score based on environmental factors.
 * The score ranges from 0 (minimal risk) to 100 (maximum risk).
 *
 * <p>Risk factors considered:
 * <ul>
 *   <li>Nearby player presence (weighted by proximity and behavior)</li>
 *   <li>Duration of repetitive actions</li>
 *   <li>Time since last random/human-like behavior</li>
 *   <li>Environmental game conditions</li>
 * </ul>
 *
 * <p>Each factor has a configurable weight that determines its contribution
 * to the overall score.
 *
 * @see RiskLevel
 * @see ScriptIntelligence
 */
@Slf4j
public class RiskAssessment {

    // Factor weights (must sum to 1.0 for proper score normalization)
    @Getter @Setter private double playerPresenceWeight = 0.35;
    @Getter @Setter private double repetitiveDurationWeight = 0.25;
    @Getter @Setter private double timeSinceRandomWeight = 0.20;
    @Getter @Setter private double environmentWeight = 0.20;

    // State tracking
    private long lastRandomBehaviorTime = System.currentTimeMillis();
    private long repetitiveActionStartTime = System.currentTimeMillis();
    private int consecutiveSameActions = 0;
    private String lastActionType = "";

    // Configurable thresholds
    @Getter @Setter private int maxNearbyPlayers = 5;
    @Getter @Setter private int playerDetectionRadius = 15; // tiles
    @Getter @Setter private long maxRepetitiveDurationMs = 300000; // 5 minutes
    @Getter @Setter private long maxTimeSinceRandomMs = 180000; // 3 minutes

    /**
     * Creates a new RiskAssessment with default weights.
     */
    public RiskAssessment() {
        // Default configuration
    }

    /**
     * Calculates the current risk score based on all factors.
     *
     * @return risk score from 0 to 100
     */
    public int calculateRiskScore() {
        double playerScore = calculatePlayerPresenceScore();
        double repetitiveScore = calculateRepetitiveDurationScore();
        double randomTimeScore = calculateTimeSinceRandomScore();
        double environmentScore = calculateEnvironmentScore();

        double weightedScore =
                (playerScore * playerPresenceWeight) +
                        (repetitiveScore * repetitiveDurationWeight) +
                        (randomTimeScore * timeSinceRandomWeight) +
                        (environmentScore * environmentWeight);

        // Normalize to 0-100
        int finalScore = (int) Math.round(weightedScore * 100);
        return Math.max(0, Math.min(100, finalScore));
    }

    /**
     * Gets the current risk level based on the calculated score.
     *
     * @return the current risk level
     */
    public RiskLevel getCurrentRiskLevel() {
        return RiskLevel.fromScore(calculateRiskScore());
    }

    /**
     * Calculates risk score from nearby player presence.
     *
     * @return normalized score from 0.0 to 1.0
     */
    private double calculatePlayerPresenceScore() {
        if (!Microbot.isLoggedIn()) {
            return 0.0;
        }

        try {
            // Use getPlayers with a filter that accepts all players
            long playerCount = Rs2Player.getPlayers(player -> true).count();
            if (playerCount == 0) {
                return 0.0;
            }

            // More players = higher risk
            int clampedCount = (int) Math.min(playerCount, maxNearbyPlayers);
            return (double) clampedCount / maxNearbyPlayers;
        } catch (Exception e) {
            log.debug("Error calculating player presence score", e);
            return 0.0;
        }
    }

    /**
     * Calculates risk score from duration of repetitive actions.
     *
     * @return normalized score from 0.0 to 1.0
     */
    private double calculateRepetitiveDurationScore() {
        long duration = System.currentTimeMillis() - repetitiveActionStartTime;
        return Math.min(1.0, (double) duration / maxRepetitiveDurationMs);
    }

    /**
     * Calculates risk score from time since last random behavior.
     *
     * @return normalized score from 0.0 to 1.0
     */
    private double calculateTimeSinceRandomScore() {
        long timeSinceRandom = System.currentTimeMillis() - lastRandomBehaviorTime;
        return Math.min(1.0, (double) timeSinceRandom / maxTimeSinceRandomMs);
    }

    /**
     * Calculates risk score from environmental conditions.
     * This is a placeholder for future expansion.
     *
     * @return normalized score from 0.0 to 1.0
     */
    private double calculateEnvironmentScore() {
        // Future: Consider factors like:
        // - Time of day (game and real-world)
        // - Current location (popular vs remote areas)
        // - Recent game events (deaths, teleports, etc.)
        return 0.0;
    }

    /**
     * Records that a random/human-like behavior was performed.
     * This reduces the time-since-random risk factor.
     */
    public void recordRandomBehavior() {
        lastRandomBehaviorTime = System.currentTimeMillis();
        log.debug("Random behavior recorded, risk factor reduced");
    }

    /**
     * Records an action for tracking repetitive behavior.
     *
     * @param actionType identifier for the type of action
     */
    public void recordAction(String actionType) {
        if (actionType == null) {
            actionType = "unknown";
        }

        if (actionType.equals(lastActionType)) {
            consecutiveSameActions++;
        } else {
            // Different action - reset repetitive tracking
            consecutiveSameActions = 1;
            lastActionType = actionType;
            repetitiveActionStartTime = System.currentTimeMillis();
        }
    }

    /**
     * Resets the repetitive action tracking.
     * Call this when switching activities.
     */
    public void resetRepetitiveTracking() {
        consecutiveSameActions = 0;
        lastActionType = "";
        repetitiveActionStartTime = System.currentTimeMillis();
    }

    /**
     * Gets a detailed breakdown of the current risk assessment.
     *
     * @return human-readable risk breakdown
     */
    public String getDetailedAssessment() {
        int score = calculateRiskScore();
        RiskLevel level = RiskLevel.fromScore(score);

        return String.format(
                "Risk: %d/100 (%s)\n" +
                        "  Players: %.0f%%\n" +
                        "  Repetitive: %.0f%%\n" +
                        "  Time since random: %.0f%%\n" +
                        "  Environment: %.0f%%",
                score, level.getDescription(),
                calculatePlayerPresenceScore() * 100,
                calculateRepetitiveDurationScore() * 100,
                calculateTimeSinceRandomScore() * 100,
                calculateEnvironmentScore() * 100
        );
    }

    /**
     * Gets a brief status summary.
     *
     * @return short status string
     */
    public String getStatusSummary() {
        int score = calculateRiskScore();
        RiskLevel level = RiskLevel.fromScore(score);
        return String.format("Risk: %d (%s)", score, level.name());
    }
}
