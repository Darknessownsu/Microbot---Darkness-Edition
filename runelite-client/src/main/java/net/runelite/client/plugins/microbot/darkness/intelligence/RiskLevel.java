package net.runelite.client.plugins.microbot.darkness.intelligence;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Defines risk levels with associated thresholds and response strategies.
 *
 * <p>Risk levels determine how the intelligence layer modifies script behavior:
 * <ul>
 *   <li>{@link #LOW} - Normal operation with behavioral variation only</li>
 *   <li>{@link #MODERATE} - Additional random pauses, slightly slower execution</li>
 *   <li>{@link #ELEVATED} - Periodic random actions (camera, interface checks)</li>
 *   <li>{@link #HIGH} - Consider pausing, perform distinctly human-like behaviors</li>
 * </ul>
 *
 * @see RiskAssessment
 * @see ScriptIntelligence
 */
@Getter
@RequiredArgsConstructor
public enum RiskLevel {

    /**
     * Low risk (0-30): Normal operation with behavioral variation from profiler.
     */
    LOW(0, 30, 1.0, 0.0, "Normal operation"),

    /**
     * Moderate risk (31-60): Add pauses, slightly slower actions.
     */
    MODERATE(31, 60, 1.3, 0.1, "Cautious mode"),

    /**
     * Elevated risk (61-80): Inject random behaviors periodically.
     */
    ELEVATED(61, 80, 1.6, 0.25, "Elevated caution"),

    /**
     * High risk (81-100): Consider pausing, very human-like behavior.
     */
    HIGH(81, 100, 2.0, 0.5, "High alert");

    /**
     * Minimum score for this risk level (inclusive).
     */
    private final int minScore;

    /**
     * Maximum score for this risk level (inclusive).
     */
    private final int maxScore;

    /**
     * Multiplier to apply to action delays at this risk level.
     * 1.0 means normal speed, 2.0 means twice as slow.
     */
    private final double delayMultiplier;

    /**
     * Probability of injecting a random behavior at this risk level.
     * 0.0 means never, 1.0 means always.
     */
    private final double randomBehaviorChance;

    /**
     * Human-readable description of this risk level.
     */
    private final String description;

    /**
     * Gets the risk level for a given score.
     *
     * @param score the risk score (0-100)
     * @return the corresponding risk level
     */
    public static RiskLevel fromScore(int score) {
        score = Math.max(0, Math.min(100, score));

        for (RiskLevel level : values()) {
            if (score >= level.minScore && score <= level.maxScore) {
                return level;
            }
        }
        return LOW; // Default fallback
    }

    /**
     * Checks if this risk level should pause script execution.
     *
     * @return true if scripts should consider pausing
     */
    public boolean shouldConsiderPausing() {
        return this == HIGH;
    }

    /**
     * Gets the color to use for displaying this risk level.
     *
     * @return RGB color value
     */
    public int getDisplayColor() {
        switch (this) {
            case LOW:
                return 0x4CAF50; // Green
            case MODERATE:
                return 0xFFC107; // Amber
            case ELEVATED:
                return 0xFF9800; // Orange
            case HIGH:
                return 0xF44336; // Red
            default:
                return 0xFFFFFF; // White
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%d-%d)", name(), minScore, maxScore);
    }
}
