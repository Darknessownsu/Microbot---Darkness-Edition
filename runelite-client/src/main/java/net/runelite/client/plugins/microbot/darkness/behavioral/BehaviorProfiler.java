package net.runelite.client.plugins.microbot.darkness.behavioral;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Point;
import net.runelite.client.plugins.microbot.Microbot;

import java.awt.event.MouseEvent;

/**
 * Observes and records user behavior patterns during a calibration phase.
 * This class captures mouse movements, click timings, and action patterns
 * to build a statistical model of natural user behavior.
 *
 * <p>Usage:
 * <pre>
 * BehaviorProfiler profiler = new BehaviorProfiler();
 * profiler.startCalibration();
 * // ... user plays naturally ...
 * profiler.stopCalibration();
 * BehaviorProfile profile = profiler.getProfile();
 * </pre>
 *
 * <p>The profiler hooks into RuneLite's event system to capture:
 * <ul>
 *   <li>Mouse movements and velocity</li>
 *   <li>Click timing patterns</li>
 *   <li>Delays between user actions</li>
 * </ul>
 *
 * @see BehaviorProfile
 * @see BehaviorSimulator
 */
@Slf4j
public class BehaviorProfiler {

    @Getter
    private final BehaviorProfile profile;

    @Getter
    private boolean calibrating = false;

    private long lastActionTime = 0;
    private long lastClickTime = 0;
    private Point lastMousePosition = null;
    private long lastMouseMoveTime = 0;

    /**
     * Creates a new BehaviorProfiler with an empty profile.
     */
    public BehaviorProfiler() {
        this.profile = new BehaviorProfile();
    }

    /**
     * Creates a new BehaviorProfiler with an existing profile.
     *
     * @param existingProfile the profile to continue building on
     */
    public BehaviorProfiler(BehaviorProfile existingProfile) {
        this.profile = existingProfile != null ? existingProfile : new BehaviorProfile();
    }

    /**
     * Starts the calibration phase. During calibration, the profiler
     * records user actions to build a behavioral model.
     */
    public void startCalibration() {
        if (calibrating) {
            log.debug("Calibration already in progress");
            return;
        }

        calibrating = true;
        profile.setCalibrationStartTime(System.currentTimeMillis());
        lastActionTime = System.currentTimeMillis();
        lastClickTime = System.currentTimeMillis();
        lastMouseMoveTime = System.currentTimeMillis();
        lastMousePosition = null;

        log.info("Darkness Edition: Behavioral calibration started");
    }

    /**
     * Stops the calibration phase and finalizes the profile.
     */
    public void stopCalibration() {
        if (!calibrating) {
            log.debug("No calibration in progress");
            return;
        }

        calibrating = false;
        profile.setCalibrationEndTime(System.currentTimeMillis());

        log.info("Darkness Edition: Behavioral calibration stopped. Profile: {}", profile);
    }

    /**
     * Records a mouse click event. Should be called from a mouse listener.
     *
     * @param event the mouse event
     */
    public void onMouseClick(MouseEvent event) {
        if (!calibrating) return;

        long now = System.currentTimeMillis();

        // Record time since last click
        if (lastClickTime > 0) {
            long clickDelay = now - lastClickTime;
            profile.addClickDelaySample(clickDelay);
        }
        lastClickTime = now;

        // Record as an action
        recordAction();
    }

    /**
     * Records a mouse movement event. Should be called from a mouse listener.
     *
     * @param event the mouse event
     */
    public void onMouseMove(MouseEvent event) {
        if (!calibrating) return;

        long now = System.currentTimeMillis();
        Point currentPos = new Point(event.getX(), event.getY());

        if (lastMousePosition != null && lastMouseMoveTime > 0) {
            long timeDelta = now - lastMouseMoveTime;
            if (timeDelta > 0) {
                double distance = calculateDistance(lastMousePosition, currentPos);
                double velocity = (distance / timeDelta) * 1000.0; // pixels per second

                if (velocity > 0) {
                    profile.addMouseVelocitySample(velocity);
                }
            }
        }

        lastMousePosition = currentPos;
        lastMouseMoveTime = now;
    }

    /**
     * Records an action event (any user interaction).
     * This captures the timing between user actions.
     */
    public void recordAction() {
        if (!calibrating) return;

        long now = System.currentTimeMillis();

        if (lastActionTime > 0) {
            long actionDelay = now - lastActionTime;
            profile.addActionDelaySample(actionDelay);
        }

        lastActionTime = now;
    }

    /**
     * Gets the current calibration progress.
     *
     * @return progress from 0.0 to 1.0
     */
    public double getCalibrationProgress() {
        return profile.getCalibrationProgress();
    }

    /**
     * Checks if the profile is sufficiently calibrated.
     *
     * @return true if the profile has enough data
     */
    public boolean isCalibrated() {
        return profile.isCalibrated();
    }

    /**
     * Resets the profile and profiler state.
     */
    public void reset() {
        calibrating = false;
        profile.reset();
        lastActionTime = 0;
        lastClickTime = 0;
        lastMousePosition = null;
        lastMouseMoveTime = 0;
        log.info("Darkness Edition: Behavioral profile reset");
    }

    /**
     * Calculates distance between two points.
     */
    private double calculateDistance(Point p1, Point p2) {
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Gets a summary string for display purposes.
     *
     * @return human-readable status summary
     */
    public String getStatusSummary() {
        if (calibrating) {
            return String.format("Calibrating... %.0f%% complete", getCalibrationProgress() * 100);
        } else if (isCalibrated()) {
            return "Profile ready";
        } else {
            return "Not calibrated - start calibration to build profile";
        }
    }
}
