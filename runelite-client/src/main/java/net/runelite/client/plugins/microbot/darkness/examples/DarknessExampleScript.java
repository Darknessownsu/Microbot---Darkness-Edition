package net.runelite.client.plugins.microbot.darkness.examples;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.darkness.DarknessManager;
import net.runelite.client.plugins.microbot.darkness.intelligence.ScriptIntelligence;
import net.runelite.client.plugins.microbot.util.Global;

/**
 * Example script demonstrating Darkness Edition features.
 * This serves as a template for creating scripts that leverage intelligent
 * adaptation and behavioral profiling.
 *
 * <p>Key features demonstrated:
 * <ul>
 *   <li>Using DarknessManager for centralized access</li>
 *   <li>Smart delays based on behavioral profile</li>
 *   <li>Risk-aware behavior modification</li>
 *   <li>Proper integration with intelligence layer</li>
 * </ul>
 *
 * <p>This example implements a simple loop that demonstrates how to:
 * <ul>
 *   <li>Check if the script should proceed based on risk</li>
 *   <li>Use smart delays instead of fixed values</li>
 *   <li>Report actions to the intelligence layer</li>
 *   <li>Handle risk-based pauses gracefully</li>
 * </ul>
 *
 * @see DarknessManager
 * @see ScriptIntelligence
 */
@Slf4j
public class DarknessExampleScript extends Script {

    private final DarknessManager darknessManager;
    private final ScriptIntelligence intelligence;

    /**
     * Creates a new example script.
     * Initializes Darkness Edition components for intelligent automation.
     */
    public DarknessExampleScript() {
        this.darknessManager = DarknessManager.getInstance();
        this.intelligence = darknessManager.getIntelligence();
    }

    @Override
    public boolean run() {
        log.info("Darkness Edition Example Script starting...");
        log.info(darknessManager.getStatusSummary());

        mainLoop();

        return true;
    }

    /**
     * Main script loop demonstrating Darkness Edition patterns.
     */
    private void mainLoop() {
        int iterationCount = 0;

        while (Microbot.isLoggedIn()) {
            // Check if we should continue or pause
            if (!intelligence.shouldProceed()) {
                log.info("Intelligence layer recommends pause. Waiting...");
                Global.sleep(intelligence.getSmartDelay(5000, 10000));
                continue;
            }

            // Perform script action
            performAction();
            iterationCount++;

            // Notify intelligence layer about the action
            intelligence.afterAction("example_action");

            // Use smart delay instead of fixed sleep
            int delay = intelligence.getSmartDelay();
            log.debug("Iteration {}: Sleeping for {}ms (Risk: {})",
                    iterationCount, delay, intelligence.getCurrentRiskScore());
            Global.sleep(delay);

            // Periodic status update
            if (iterationCount % 10 == 0) {
                log.info("Status update: {}", darknessManager.getShortStatus());
            }

            // Example break condition - run for 100 iterations
            if (iterationCount >= 100) {
                log.info("Example script completed {} iterations", iterationCount);
                break;
            }
        }
    }

    /**
     * Placeholder for actual script logic.
     * Replace this with your script's core functionality.
     */
    private void performAction() {
        // Example: This would be where you interact with game objects,
        // click NPCs, manage inventory, etc.
        log.debug("Performing example action...");

        // Simulate some work
        Global.sleep(intelligence.getSmartDelay(100, 300));
    }

    /**
     * Demonstrates how to handle activity changes.
     * Call this when switching between different tasks.
     */
    private void onActivityChange(String newActivity) {
        log.info("Activity changed to: {}", newActivity);
        intelligence.onActivityChange();
    }

    /**
     * Example of using smart delays for different contexts.
     */
    private void demonstrateSmartDelays() {
        // Basic delay using behavioral profile
        int actionDelay = intelligence.getSmartDelay();
        Global.sleep(actionDelay);

        // Delay within a specific range
        int rangedDelay = intelligence.getSmartDelay(500, 1500);
        Global.sleep(rangedDelay);

        // Get the simulator directly for more control
        int clickDelay = intelligence.getBehaviorSimulator().getClickDelay();
        Global.sleep(clickDelay);
    }

    /**
     * Example of checking and responding to risk levels.
     */
    private void demonstrateRiskAwareness() {
        int riskScore = intelligence.getCurrentRiskScore();

        if (riskScore > 60) {
            log.warn("Elevated risk detected ({}). Taking extra precautions...", riskScore);

            // Perform a random behavior to reduce risk
            intelligence.performRandomBehavior();

            // Wait a bit longer
            Global.sleep(intelligence.getSmartDelay() * 2);
        } else if (riskScore > 80) {
            log.error("High risk detected ({}). Consider stopping script.", riskScore);
        } else {
            log.debug("Risk level normal ({})", riskScore);
        }
    }

    /**
     * Demonstrates best practices for script shutdown.
     */
    @Override
    public void shutdown() {
        log.info("Darkness Edition Example Script shutting down...");
        log.info("Final status: {}", darknessManager.getShortStatus());

        // If calibration is running, consider saving it
        if (darknessManager.isCalibrating()) {
            darknessManager.stopCalibration();
        }

        super.shutdown();
    }
}
