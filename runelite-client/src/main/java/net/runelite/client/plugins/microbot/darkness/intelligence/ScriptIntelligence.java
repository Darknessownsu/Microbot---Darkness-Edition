package net.runelite.client.plugins.microbot.darkness.intelligence;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.darkness.behavioral.BehaviorProfile;
import net.runelite.client.plugins.microbot.darkness.behavioral.BehaviorSimulator;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;

import java.util.Random;

/**
 * Intelligence middleware that wraps script execution to provide context-aware
 * behavior modification based on environmental factors and risk assessment.
 *
 * <p>This class integrates:
 * <ul>
 *   <li>{@link BehaviorSimulator} for human-like timing variations</li>
 *   <li>{@link RiskAssessment} for dynamic risk-based adjustments</li>
 *   <li>Existing Rs2Antiban system for compatibility</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>
 * ScriptIntelligence intelligence = ScriptIntelligence.getInstance();
 *
 * // Before executing an action:
 * if (intelligence.shouldProceed()) {
 *     // Perform action
 *     intelligence.afterAction("gather");
 * }
 *
 * // For delays:
 * sleep(intelligence.getSmartDelay());
 * </pre>
 *
 * @see RiskAssessment
 * @see BehaviorSimulator
 */
@Slf4j
public class ScriptIntelligence {

    private static ScriptIntelligence instance;

    @Getter
    private final RiskAssessment riskAssessment;

    @Getter
    private final BehaviorSimulator behaviorSimulator;

    @Getter @Setter
    private boolean enabled = true;

    @Getter @Setter
    private boolean integrateWithAntiban = true;

    private final Random random;

    // Smoothing for risk level transitions
    private RiskLevel previousRiskLevel = RiskLevel.LOW;
    private int actionsAtCurrentLevel = 0;
    private static final int TRANSITION_THRESHOLD = 3;

    /**
     * Gets the singleton instance of ScriptIntelligence.
     *
     * @return the ScriptIntelligence instance
     */
    public static synchronized ScriptIntelligence getInstance() {
        if (instance == null) {
            instance = new ScriptIntelligence();
        }
        return instance;
    }

    /**
     * Creates a new ScriptIntelligence instance with default settings.
     */
    private ScriptIntelligence() {
        this.riskAssessment = new RiskAssessment();
        this.behaviorSimulator = new BehaviorSimulator();
        this.random = new Random();
    }

    /**
     * Sets the behavioral profile for the simulator.
     *
     * @param profile the profile to use
     */
    public void setProfile(BehaviorProfile profile) {
        behaviorSimulator.setProfile(profile);
    }

    /**
     * Determines whether the script should proceed with the next action.
     * May return false if risk is high and a pause is recommended.
     *
     * @return true if the script should continue, false if it should pause
     */
    public boolean shouldProceed() {
        if (!enabled) {
            return true;
        }

        RiskLevel currentLevel = getCurrentRiskLevel();

        // At high risk, occasionally suggest pausing
        if (currentLevel.shouldConsiderPausing()) {
            if (random.nextDouble() < 0.1) { // 10% chance to pause
                log.info("Darkness Edition: Suggesting pause due to high risk");
                return false;
            }
        }

        return true;
    }

    /**
     * Gets a smart delay based on behavioral profile and current risk level.
     * This should be used instead of fixed sleep values.
     *
     * @return delay in milliseconds
     */
    public int getSmartDelay() {
        if (!enabled) {
            return behaviorSimulator.getActionDelay();
        }

        int baseDelay = behaviorSimulator.getActionDelay();
        RiskLevel level = getSmoothedRiskLevel();

        // Apply risk-based multiplier
        int adjustedDelay = (int) (baseDelay * level.getDelayMultiplier());

        log.debug("Smart delay: base={}, adjusted={} (risk={})", baseDelay, adjustedDelay, level);
        return adjustedDelay;
    }

    /**
     * Gets a smart delay within a specified range.
     *
     * @param minMs minimum delay
     * @param maxMs maximum delay
     * @return delay in milliseconds
     */
    public int getSmartDelay(int minMs, int maxMs) {
        if (!enabled) {
            return behaviorSimulator.getDelayInRange(minMs, maxMs);
        }

        int baseDelay = behaviorSimulator.getDelayInRange(minMs, maxMs);
        RiskLevel level = getSmoothedRiskLevel();

        // Apply risk-based multiplier, but keep within bounds
        int adjustedDelay = (int) (baseDelay * level.getDelayMultiplier());
        return Math.max(minMs, Math.min(adjustedDelay, maxMs * 2));
    }

    /**
     * Should be called after executing an action.
     * Updates risk assessment and may trigger random behaviors.
     *
     * @param actionType identifier for the action type
     */
    public void afterAction(String actionType) {
        if (!enabled) {
            return;
        }

        riskAssessment.recordAction(actionType);

        RiskLevel level = getCurrentRiskLevel();

        // Maybe inject random behavior
        if (random.nextDouble() < level.getRandomBehaviorChance()) {
            performRandomBehavior();
        }

        // Integrate with existing antiban if enabled
        if (integrateWithAntiban && Rs2AntibanSettings.antibanEnabled) {
            Rs2Antiban.actionCooldown();
        }
    }

    /**
     * Performs a random human-like behavior to reduce risk.
     */
    public void performRandomBehavior() {
        log.debug("Darkness Edition: Performing random behavior");

        // Choose a random behavior
        int choice = random.nextInt(4);
        switch (choice) {
            case 0:
                // Random mouse movement
                Rs2Antiban.moveMouseRandomly();
                break;
            case 1:
                // Short pause
                Global.sleep(behaviorSimulator.getDelayInRange(500, 2000));
                break;
            case 2:
                // Camera adjustment (if available)
                // This would integrate with camera utilities
                break;
            case 3:
                // Just wait a moment
                Global.sleep(behaviorSimulator.getDelayInRange(200, 800));
                break;
        }

        riskAssessment.recordRandomBehavior();
    }

    /**
     * Gets the current risk level with smoothing to prevent
     * rapid oscillation between states.
     *
     * @return smoothed risk level
     */
    private RiskLevel getSmoothedRiskLevel() {
        RiskLevel currentLevel = riskAssessment.getCurrentRiskLevel();

        if (currentLevel == previousRiskLevel) {
            actionsAtCurrentLevel++;
            return currentLevel;
        }

        // Require sustained change before transitioning
        actionsAtCurrentLevel++;
        if (actionsAtCurrentLevel >= TRANSITION_THRESHOLD) {
            previousRiskLevel = currentLevel;
            actionsAtCurrentLevel = 0;
            log.info("Darkness Edition: Risk level changed to {}", currentLevel);
        }

        return previousRiskLevel;
    }

    /**
     * Gets the current risk level directly (without smoothing).
     *
     * @return current risk level
     */
    public RiskLevel getCurrentRiskLevel() {
        return riskAssessment.getCurrentRiskLevel();
    }

    /**
     * Gets the current risk score (0-100).
     *
     * @return risk score
     */
    public int getCurrentRiskScore() {
        return riskAssessment.calculateRiskScore();
    }

    /**
     * Notifies the intelligence layer that the activity has changed.
     * This resets repetitive action tracking.
     */
    public void onActivityChange() {
        riskAssessment.resetRepetitiveTracking();
        previousRiskLevel = RiskLevel.LOW;
        actionsAtCurrentLevel = 0;
    }

    /**
     * Gets a status summary for display.
     *
     * @return human-readable status
     */
    public String getStatusSummary() {
        if (!enabled) {
            return "Intelligence: Disabled";
        }

        RiskLevel level = getSmoothedRiskLevel();
        int score = getCurrentRiskScore();

        return String.format("Risk: %d/100 (%s) | Delay: x%.1f",
                score, level.name(), level.getDelayMultiplier());
    }

    /**
     * Resets the intelligence layer to default state.
     */
    public void reset() {
        riskAssessment.resetRepetitiveTracking();
        riskAssessment.recordRandomBehavior(); // Reset time tracking
        previousRiskLevel = RiskLevel.LOW;
        actionsAtCurrentLevel = 0;
    }
}
