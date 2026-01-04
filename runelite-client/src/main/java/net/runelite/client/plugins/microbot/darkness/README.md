# Darkness Edition API Documentation

This directory contains the core Darkness Edition functionality that brings intelligent, adaptive automation to Microbot.

## Package Structure

```
darkness/
├── behavioral/          # Behavioral profiling system
│   ├── BehaviorProfile.java       # Statistical behavior model
│   ├── BehaviorProfiler.java      # Observes and records user actions
│   ├── BehaviorSimulator.java     # Applies profiles to automated actions
│   └── ProfileStorage.java        # Persistent storage for profiles
├── intelligence/        # Risk-aware intelligence layer
│   ├── RiskAssessment.java        # Multi-factor risk calculation
│   ├── RiskLevel.java             # Risk thresholds and strategies
│   ├── ScriptIntelligence.java    # Middleware for script execution
│   └── IntelligenceConfig.java    # Configuration system
├── examples/           # Example scripts and templates
│   └── DarknessExampleScript.java # Demo script showing usage
└── DarknessManager.java # Unified interface for all features
```

## Quick Start

### Basic Usage in Scripts

```java
import net.runelite.client.plugins.microbot.darkness.DarknessManager;
import net.runelite.client.plugins.microbot.darkness.intelligence.ScriptIntelligence;

public class MyScript extends Script {
    private final DarknessManager darkness = DarknessManager.getInstance();
    private final ScriptIntelligence intelligence = darkness.getIntelligence();

    @Override
    public boolean run() {
        while (Microbot.isLoggedIn()) {
            // Check if we should proceed
            if (!intelligence.shouldProceed()) {
                sleep(intelligence.getSmartDelay(5000, 10000));
                continue;
            }

            // Perform action
            performTask();

            // Report action to intelligence layer
            intelligence.afterAction("task_name");

            // Use smart delay instead of fixed sleep
            sleep(intelligence.getSmartDelay());
        }
        return true;
    }
}
```

### Calibration

To capture a behavioral profile:

```java
DarknessManager manager = DarknessManager.getInstance();

// Start recording user behavior
manager.startCalibration();

// ... user plays naturally ...

// Stop and save profile
manager.stopCalibration();  // Automatically saves
```

### Configuration

Apply preset configurations:

```java
DarknessManager manager = DarknessManager.getInstance();

// Use default balanced settings
manager.applyPreset(DarknessManager.ConfigPreset.DEFAULT);

// Or use cautious settings for maximum safety
manager.applyPreset(DarknessManager.ConfigPreset.CAUTIOUS);

// Or use efficient settings for faster execution
manager.applyPreset(DarknessManager.ConfigPreset.EFFICIENT);
```

Advanced configuration:

```java
IntelligenceConfig config = IntelligenceConfig.createDefault();
config.setPlayerPresenceWeight(0.4);  // Increase player awareness
config.setMaxRepetitiveDurationMs(240000);  // 4 minutes
config.normalizeWeights();  // Ensure weights sum to 1.0
config.apply(manager.getIntelligence());
```

## Core Components

### Behavioral Profiling System

The behavioral profiling system captures natural user behavior patterns and applies them to automated actions.

#### BehaviorProfile

Stores statistical distributions of user behavior:
- Action timing (mean and standard deviation)
- Mouse velocity patterns
- Click timing characteristics

#### BehaviorProfiler

Observes user actions during calibration:
- Mouse movements and velocity
- Click timing patterns
- Delays between actions

#### BehaviorSimulator

Generates human-like values based on captured profiles:
- `getActionDelay()` - Random delay based on user's typical timing
- `getClickDelay()` - Click-specific timing
- `getMouseVelocity()` - Mouse movement speed
- `getDelayInRange(min, max)` - Constrained random delay

#### ProfileStorage

Manages persistent storage:
- Saves profiles as JSON to `~/.runelite/darkness/darkness-profile.json`
- Automatically loads saved profiles on startup
- Supports profile reset and deletion

### Intelligence Layer

The intelligence layer provides risk-aware behavior modification based on environmental factors.

#### RiskAssessment

Calculates a risk score (0-100) based on:
- **Player Presence** (35% weight) - Nearby players and their behavior
- **Repetitive Duration** (25% weight) - How long the same action has been repeated
- **Time Since Random** (20% weight) - Time since last random/human-like behavior
- **Environment** (20% weight) - Reserved for future expansion

#### RiskLevel

Defines four risk levels with associated behavior modifications:
- **LOW (0-30)** - Normal operation with behavioral variation
- **MODERATE (31-60)** - 1.3x delay multiplier, 10% random behavior chance
- **ELEVATED (61-80)** - 1.6x delay multiplier, 25% random behavior chance
- **HIGH (81-100)** - 2.0x delay multiplier, 50% random behavior chance, consider pausing

#### ScriptIntelligence

Middleware that wraps script execution:
- `shouldProceed()` - Check if script should continue or pause
- `getSmartDelay()` - Get risk-adjusted delay
- `afterAction(type)` - Report action for risk tracking
- `performRandomBehavior()` - Execute random human-like action
- `onActivityChange()` - Reset tracking when switching tasks

#### IntelligenceConfig

Configuration system for tuning parameters:
- Risk factor weights
- Detection thresholds
- Time-based limits
- Behavior modification settings

Presets available:
- `DEFAULT` - Balanced settings
- `CAUTIOUS` - Prioritizes safety over speed
- `EFFICIENT` - Prioritizes speed over maximum safety

### DarknessManager

Unified interface for all Darkness Edition features:
- Manages behavioral profiler lifecycle
- Provides access to intelligence layer
- Handles configuration
- Manages profile storage

## Smart Delays

Instead of using fixed sleep values, use smart delays that adapt to risk level and behavioral profile:

```java
// Basic smart delay
sleep(intelligence.getSmartDelay());

// Smart delay within range
sleep(intelligence.getSmartDelay(500, 2000));

// Direct simulator access for more control
BehaviorSimulator sim = intelligence.getBehaviorSimulator();
sleep(sim.getActionDelay());
sleep(sim.getClickDelay());
sleep(sim.getDelayInRange(1000, 3000));
```

## Risk Management

Monitor and respond to risk levels:

```java
// Get current risk score
int riskScore = intelligence.getCurrentRiskScore();

// Get current risk level
RiskLevel level = intelligence.getCurrentRiskLevel();

// Check for high risk
if (level.shouldConsiderPausing()) {
    // Take extra precautions
    intelligence.performRandomBehavior();
    sleep(intelligence.getSmartDelay() * 2);
}

// Get detailed assessment
String details = intelligence.getRiskAssessment().getDetailedAssessment();
log.info(details);
```

## Activity Changes

When switching between different tasks, notify the intelligence layer to reset repetitive action tracking:

```java
// When starting a new task
intelligence.onActivityChange();

// Or through the manager
darkness.getIntelligence().onActivityChange();
```

## Status Information

Get status information for logging or display:

```java
DarknessManager manager = DarknessManager.getInstance();

// Comprehensive status
String fullStatus = manager.getStatusSummary();
log.info(fullStatus);

// Brief status for overlays
String shortStatus = manager.getShortStatus();

// Calibration progress
if (manager.isCalibrating()) {
    double progress = manager.getCalibrationProgress();
    log.info("Calibration: {}%", progress * 100);
}
```

## Best Practices

1. **Always use smart delays** instead of fixed sleep values
2. **Report actions** to the intelligence layer with `afterAction(type)`
3. **Check shouldProceed()** before executing actions
4. **Notify on activity changes** when switching tasks
5. **Save profiles** after successful calibration
6. **Configure appropriately** for your use case (default, cautious, or efficient)
7. **Monitor risk levels** and respond to elevated risk
8. **Test scripts** with different profiles and configurations

## Integration with Existing Code

Darkness Edition is designed to work alongside existing Microbot features:

### Compatibility with Rs2Antiban

The intelligence layer can integrate with Rs2Antiban:

```java
intelligence.setIntegrateWithAntiban(true);  // Default
```

When enabled, the intelligence layer calls `Rs2Antiban.actionCooldown()` after each action.

### Using with Existing Scripts

Minimal changes needed to add Darkness Edition features:

```java
// Before:
sleep(random(200, 500));

// After:
sleep(intelligence.getSmartDelay(200, 500));

// Before:
performAction();
sleep(300);

// After:
if (intelligence.shouldProceed()) {
    performAction();
    intelligence.afterAction("action_type");
    sleep(intelligence.getSmartDelay());
}
```

## File Storage

Profiles are stored at: `~/.runelite/darkness/darkness-profile.json`

The JSON format is human-readable and includes:
- Profile version for schema migration
- Calibration status and timestamps
- Statistical distributions (mean, std dev)
- Raw sample data for recalculation

## Troubleshooting

### Profile Not Saving

```java
// Check if directory is writable
File dir = ProfileStorage.getProfileDirectory();
log.info("Directory: {}, exists: {}, writable: {}",
    dir, dir.exists(), dir.canWrite());

// Try manual save
boolean saved = DarknessManager.getInstance().saveProfile();
log.info("Save result: {}", saved);
```

### Calibration Not Working

```java
// Check calibration status
BehaviorProfiler profiler = darkness.getProfiler();
log.info("Calibrating: {}", profiler.isCalibrating());
log.info("Progress: {}%", profiler.getCalibrationProgress() * 100);
log.info("Profile: {}", profiler.getProfile());
```

### Risk Score Always High

```java
// Check risk assessment details
RiskAssessment risk = intelligence.getRiskAssessment();
log.info(risk.getDetailedAssessment());

// Adjust configuration
IntelligenceConfig config = IntelligenceConfig.createEfficient();
config.apply(intelligence);
```

## Examples

See `examples/DarknessExampleScript.java` for a complete working example demonstrating all features.

## Future Development

Planned features for future releases:
- UI panel for calibration and configuration
- Visual theme integration
- Enhanced script API with task abstractions
- Advanced pathfinding with risk-aware routing
- Statistics dashboard
- Cloud profile synchronization

## Contributing

When adding features to Darkness Edition:
1. Place code in the appropriate package
2. Follow existing patterns and naming conventions
3. Add comprehensive javadoc comments
4. Include examples in docstrings
5. Update this README with new features
6. Write clear, educational code

---

For questions or discussion, join the [Discord](https://discord.gg/zaGrfqFEWE) server.
