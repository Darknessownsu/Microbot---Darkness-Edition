# Getting Started with Darkness Edition

Welcome to Microbot Darkness Edition! This guide will walk you through installation, initial configuration, profile calibration, and running your first automated script with intelligent adaptation.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Installation](#installation)
3. [First Launch](#first-launch)
4. [Profile Calibration](#profile-calibration)
5. [Running Your First Script](#running-your-first-script)
6. [Understanding the Intelligence Layer](#understanding-the-intelligence-layer)
7. [Configuration Options](#configuration-options)
8. [Troubleshooting](#troubleshooting)
9. [Next Steps](#next-steps)

## Prerequisites

Before you begin, make sure you have:

- **Java 11 or higher** installed on your system
- **Old School RuneScape** account
- **Basic understanding** of RuneScape game mechanics
- **~500 MB** of free disk space

Check your Java version:
```bash
java -version
```

## Installation

### Option 1: Download Pre-built Release (Recommended)

1. Visit the [Releases page](https://github.com/Darknessownsu/Microbot---Darkness-Edition/releases)
2. Download the latest `Microbot-DarknessEdition-{version}.jar`
3. Save it to a folder of your choice (e.g., `~/Microbot/`)
4. Double-click the JAR file or run from command line:
   ```bash
   java -jar Microbot-DarknessEdition-{version}.jar
   ```

### Option 2: Build from Source

For developers who want the latest features:

```bash
# Clone the repository
git clone https://github.com/Darknessownsu/Microbot---Darkness-Edition.git
cd Microbot---Darkness-Edition

# Build the project
./gradlew :runelite-client:shadowJar

# The built JAR will be in: runelite-client/build/libs/
java -jar runelite-client/build/libs/client-*.jar
```

## First Launch

When you first launch Darkness Edition:

1. **Client Window Opens** - The RuneLite client window appears with Microbot and Darkness Edition features enabled

2. **Login to OSRS** - Use your Old School RuneScape credentials to log in

3. **Darkness Edition Initializes** - Check the logs for:
   ```
   [INFO] Darkness Edition: Manager initialized. Profile status: not calibrated
   ```

4. **Configuration Directory Created** - Darkness Edition creates `~/.runelite/darkness/` for storing profiles

## Profile Calibration

Profile calibration is the most important step for making your automation look natural and human-like. During calibration, Darkness Edition observes how you naturally play and builds a statistical model of your behavior.

### Why Calibrate?

- **Unique to You** - Your profile captures YOUR specific behavior patterns
- **More Human-Like** - Automated actions match your natural timing and movements
- **Better Safety** - Natural variation makes automation less detectable

### Calibration Process

#### Step 1: Start Calibration

In your script or through code:
```java
DarknessManager manager = DarknessManager.getInstance();
manager.startCalibration();
```

Or via logging:
```
[INFO] Darkness Edition: Behavioral calibration started
```

#### Step 2: Play Naturally (10-15 minutes)

During calibration, play the game as you normally would:

- ✅ **Do normal activities** - Woodcutting, fishing, combat, banking
- ✅ **Move your mouse naturally** - Don't try to be perfect or robotic
- ✅ **Click at your normal pace** - Fast or slow, just be yourself
- ✅ **Use different interfaces** - Inventory, bank, combat tab, etc.
- ✅ **Mix activities** - Switch between tasks naturally

**Tips for Good Calibration:**
- Play for at least 10-15 minutes
- Perform actions you plan to automate
- Don't try to be "optimal" - be natural
- Include both fast and slow activities
- Make mistakes (it's human!)

#### Step 3: Check Progress

The profile needs:
- **30+ timing samples** - Delays between your actions
- **50+ mouse samples** - Mouse movement patterns

Monitor progress:
```java
double progress = manager.getCalibrationProgress();
System.out.println("Calibration: " + (progress * 100) + "%");
```

#### Step 4: Stop and Save

Once calibration is complete:
```java
manager.stopCalibration();  // Automatically saves to ~/.runelite/darkness/darkness-profile.json
```

You'll see:
```
[INFO] Darkness Edition: Calibration stopped and profile saved
```

### Verifying Your Profile

Check if your profile is ready:
```java
if (manager.isProfileCalibrated()) {
    System.out.println("Profile is ready to use!");
    System.out.println(manager.getStatusSummary());
}
```

## Running Your First Script

Now that you have a calibrated profile, let's run a script with Darkness Edition features.

### Example: Smart Woodcutting Script

Here's a simple script that uses Darkness Edition features:

```java
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.darkness.DarknessManager;
import net.runelite.client.plugins.microbot.darkness.intelligence.ScriptIntelligence;
import net.runelite.client.plugins.microbot.util.tree.Rs2TreeObject;
import net.runelite.client.plugins.microbot.util.Global;

public class SmartWoodcuttingScript extends Script {
    private final DarknessManager darkness = DarknessManager.getInstance();
    private final ScriptIntelligence intelligence = darkness.getIntelligence();

    @Override
    public boolean run() {
        System.out.println("Starting smart woodcutting...");
        System.out.println(darkness.getStatusSummary());

        while (Microbot.isLoggedIn()) {
            // Check if intelligence layer recommends continuing
            if (!intelligence.shouldProceed()) {
                System.out.println("High risk detected, pausing...");
                Global.sleep(intelligence.getSmartDelay(5000, 10000));
                continue;
            }

            // Find and chop tree
            Rs2TreeObject tree = findNearestTree();
            if (tree != null && !Rs2Player.isAnimating()) {
                tree.interact("Chop down");

                // Report action to intelligence layer
                intelligence.afterAction("chop_tree");

                // Use smart delay based on your profile and current risk
                Global.sleep(intelligence.getSmartDelay());
            }

            // Small delay between checks
            Global.sleep(intelligence.getSmartDelay(100, 300));
        }

        return true;
    }
}
```

### Key Differences from Regular Scripts

**Before (Fixed Delays):**
```java
tree.interact("Chop down");
sleep(600);  // Always 600ms - robotic
```

**After (Smart Delays):**
```java
tree.interact("Chop down");
intelligence.afterAction("chop_tree");
sleep(intelligence.getSmartDelay());  // Uses your profile + adjusts for risk
```

## Understanding the Intelligence Layer

The intelligence layer continuously monitors your environment and adjusts behavior in real-time.

### Risk Levels

| Level | Score | Behavior |
|-------|-------|----------|
| **LOW** | 0-30 | Normal operation with your behavioral profile |
| **MODERATE** | 31-60 | 1.3x slower, 10% random behaviors |
| **ELEVATED** | 61-80 | 1.6x slower, 25% random behaviors |
| **HIGH** | 81-100 | 2.0x slower, 50% random behaviors, may pause |

### What Increases Risk?

- **Nearby Players** - More players = higher risk
- **Repetitive Actions** - Doing the same thing for too long
- **No Randomness** - Going too long without random behaviors
- **Environmental Factors** - (Future: location, time of day)

### What Reduces Risk?

- **Random Behaviors** - The intelligence layer performs these automatically
- **Activity Changes** - Switching between tasks
- **Natural Pauses** - Built into smart delays
- **Player Departure** - When other players leave the area

## Configuration Options

### Preset Configurations

Darkness Edition comes with three presets:

#### Default (Balanced)
```java
manager.applyPreset(DarknessManager.ConfigPreset.DEFAULT);
```
- Balanced safety and efficiency
- Good for general use
- Recommended for most users

#### Cautious (Maximum Safety)
```java
manager.applyPreset(DarknessManager.ConfigPreset.CAUTIOUS);
```
- Prioritizes safety over speed
- More conservative risk thresholds
- More frequent random behaviors
- Recommended for: High-value accounts, risky activities

#### Efficient (Maximum Speed)
```java
manager.applyPreset(DarknessManager.ConfigPreset.EFFICIENT);
```
- Prioritizes speed over maximum safety
- Higher risk tolerance
- Fewer random interruptions
- Recommended for: Low-risk activities, testing

### Custom Configuration

For advanced users:

```java
IntelligenceConfig config = IntelligenceConfig.createDefault();

// Adjust risk factor weights (must sum to 1.0)
config.setPlayerPresenceWeight(0.40);  // Focus more on nearby players
config.setRepetitiveDurationWeight(0.30);

// Adjust thresholds
config.setMaxRepetitiveDurationMs(240000);  // 4 minutes instead of 5
config.setMaxNearbyPlayers(3);  // More sensitive to players

// Normalize and apply
config.normalizeWeights();
config.apply(manager.getIntelligence());
```

## Troubleshooting

### Problem: Profile Won't Save

**Check directory permissions:**
```java
File dir = ProfileStorage.getProfileDirectory();
System.out.println("Directory: " + dir.getAbsolutePath());
System.out.println("Exists: " + dir.exists());
System.out.println("Writable: " + dir.canWrite());
```

**Try manual save:**
```java
boolean saved = manager.saveProfile();
System.out.println("Save result: " + saved);
```

### Problem: Calibration Progress Stuck

**Check sample counts:**
```java
BehaviorProfile profile = manager.getProfiler().getProfile();
System.out.println("Action samples: " + profile.getActionDelaySamples().size());
System.out.println("Mouse samples: " + profile.getMouseVelocitySamples().size());
System.out.println("Progress: " + (profile.getCalibrationProgress() * 100) + "%");
```

**Solution:** Play more actively with mouse movements and clicks

### Problem: Risk Always High

**Check detailed assessment:**
```java
String details = intelligence.getRiskAssessment().getDetailedAssessment();
System.out.println(details);
```

**Solutions:**
- Use EFFICIENT preset for lower sensitivity
- Adjust player presence weight if many players nearby
- Call `intelligence.performRandomBehavior()` manually
- Use `intelligence.onActivityChange()` when switching tasks

### Problem: Scripts Running Too Slow

**Current risk level:**
```java
RiskLevel level = intelligence.getCurrentRiskLevel();
System.out.println("Risk Level: " + level + " (multiplier: " + level.getDelayMultiplier() + "x)");
```

**Solutions:**
- Wait for risk to decrease naturally
- Use EFFICIENT preset
- Increase risk thresholds in custom config
- Perform random behaviors to reset timer

## Next Steps

Now that you're set up:

1. **Explore Examples** - Check `darkness/examples/` for more script templates
2. **Read API Docs** - See `darkness/README.md` for comprehensive API documentation
3. **Join the Community** - [Discord](https://discord.gg/zaGrfqFEWE) for help and discussion
4. **Write Scripts** - Apply what you've learned to create your own scripts
5. **Experiment** - Try different configurations and see what works best for you

### Recommended Reading Order

1. ✅ This Getting Started Guide (you're here!)
2. `darkness/README.md` - Complete API reference
3. `darkness/examples/DarknessExampleScript.java` - Working example
4. `ROADMAP.md` - See what's coming next
5. Architecture Overview (coming soon) - Understand the internals

## Learning Resources

- **API Documentation**: `runelite-client/src/main/java/net/runelite/client/plugins/microbot/darkness/README.md`
- **Example Scripts**: `runelite-client/src/main/java/net/runelite/client/plugins/microbot/darkness/examples/`
- **Discord Community**: https://discord.gg/zaGrfqFEWE
- **GitHub Issues**: Report bugs or request features

## Contributing

Want to improve Darkness Edition? See `CONTRIBUTING.md` (coming soon) for guidelines.

---

**Welcome to Darkness Edition - Where intelligent automation meets elegant design.** 🌙

For questions or support, join our [Discord](https://discord.gg/zaGrfqFEWE) server.
