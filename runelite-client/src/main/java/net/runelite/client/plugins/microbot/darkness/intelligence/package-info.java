/**
 * Darkness Edition Intelligence Layer.
 *
 * <p>This package provides context-aware behavior modification for script execution.
 * The intelligence layer continuously monitors game state and dynamically adjusts
 * how scripts execute based on environmental factors and risk assessment.
 *
 * <ul>
 *   <li>{@link net.runelite.client.plugins.microbot.darkness.intelligence.RiskLevel} -
 *       Enum defining risk thresholds and associated response strategies</li>
 *   <li>{@link net.runelite.client.plugins.microbot.darkness.intelligence.RiskAssessment} -
 *       Calculates numerical risk scores from environmental factors</li>
 *   <li>{@link net.runelite.client.plugins.microbot.darkness.intelligence.ScriptIntelligence} -
 *       Middleware that wraps script execution for intelligent behavior modification</li>
 * </ul>
 *
 * <h2>Risk Levels</h2>
 * <table>
 *   <tr><th>Level</th><th>Score Range</th><th>Response</th></tr>
 *   <tr><td>LOW</td><td>0-30</td><td>Normal operation with behavioral variation</td></tr>
 *   <tr><td>MODERATE</td><td>31-60</td><td>Additional pauses, slower execution</td></tr>
 *   <tr><td>ELEVATED</td><td>61-80</td><td>Inject periodic random actions</td></tr>
 *   <tr><td>HIGH</td><td>81-100</td><td>Consider pausing, very human-like behavior</td></tr>
 * </table>
 *
 * <h2>Usage Example</h2>
 * <pre>
 * ScriptIntelligence intelligence = ScriptIntelligence.getInstance();
 *
 * if (intelligence.shouldProceed()) {
 *     // Perform action
 *     sleep(intelligence.getSmartDelay());
 *     intelligence.afterAction("gather");
 * }
 * </pre>
 *
 * @see net.runelite.client.plugins.microbot.darkness.behavioral
 */
package net.runelite.client.plugins.microbot.darkness.intelligence;
