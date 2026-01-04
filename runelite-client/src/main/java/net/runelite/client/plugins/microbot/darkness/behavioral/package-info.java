/**
 * Darkness Edition Behavioral Profiling System.
 *
 * <p>This package provides the behavioral profiling functionality for capturing
 * and applying user behavior patterns to automated actions. The system consists of:
 *
 * <ul>
 *   <li>{@link net.runelite.client.plugins.microbot.darkness.behavioral.BehaviorProfile} -
 *       Data model storing statistical distributions of user behavior</li>
 *   <li>{@link net.runelite.client.plugins.microbot.darkness.behavioral.BehaviorProfiler} -
 *       Observes and records user actions during calibration</li>
 *   <li>{@link net.runelite.client.plugins.microbot.darkness.behavioral.BehaviorSimulator} -
 *       Applies captured profiles to generate human-like action variations</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>
 * // Calibration phase
 * BehaviorProfiler profiler = new BehaviorProfiler();
 * profiler.startCalibration();
 * // ... user plays naturally, events are captured ...
 * profiler.stopCalibration();
 *
 * // Usage phase
 * BehaviorSimulator simulator = new BehaviorSimulator(profiler.getProfile());
 * int delay = simulator.getActionDelay(); // Returns human-like delay
 * </pre>
 *
 * @see net.runelite.client.plugins.microbot.darkness.intelligence
 */
package net.runelite.client.plugins.microbot.darkness.behavioral;
