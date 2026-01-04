# Darkness Edition Architecture Overview

This document explains how Darkness Edition works internally, covering the major components, design patterns, and architectural decisions.

## High-Level Architecture

Darkness Edition extends the Microbot/RuneLite client with four major subsystems:

```
┌─────────────────────────────────────────────────────────────┐
│                    RuneLite Client                          │
│  ┌────────────────────────────────────────────────────────┐ │
│  │              Microbot Base System                      │ │
│  │  ┌──────────────────────────────────────────────────┐ │ │
│  │  │         Darkness Edition                         │ │ │
│  │  │                                                  │ │ │
│  │  │  ┌──────────────┐  ┌────────────────────────┐  │ │ │
│  │  │  │  Behavioral  │  │  Intelligence Layer   │  │ │ │
│  │  │  │   Profiling  │  │                        │  │ │ │
│  │  │  └──────────────┘  └────────────────────────┘  │ │ │
│  │  │         │                    │                 │ │ │
│  │  │         └────────┬───────────┘                 │ │ │
│  │  │                  │                             │ │ │
│  │  │         ┌────────▼────────┐                    │ │ │
│  │  │         │ DarknessManager │                    │ │ │
│  │  │         └─────────────────┘                    │ │ │
│  │  │                  │                             │ │ │
│  │  └──────────────────┼─────────────────────────────┘ │ │
│  │                     │                               │ │
│  │         ┌───────────▼──────────────┐                │ │
│  │         │    Script Interface      │                │ │
│  │         └──────────────────────────┘                │ │
│  └─────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## Core Components

### 1. Behavioral Profiling System

**Purpose**: Capture and model individual user behavior patterns.

#### Component Diagram

```
┌─────────────────┐      observes      ┌──────────────┐
│ User Actions    │─────────────────────▶│  Behavior    │
│ (Mouse, Keys)   │                     │  Profiler    │
└─────────────────┘                     └──────┬───────┘
                                               │ updates
                                               ▼
                                        ┌──────────────┐
                                        │  Behavior    │
                                        │  Profile     │
                                        └──────┬───────┘
                                               │ persisted by
                                               ▼
                                        ┌──────────────┐
                                        │   Profile    │
                                        │   Storage    │
                                        └──────────────┘
                                               │ JSON
                                               ▼
                                        ┌──────────────┐
                                        │ ~/.runelite/ │
                                        │  darkness/   │
                                        └──────────────┘
```

#### Key Classes

**BehaviorProfile**
- **Responsibility**: Store statistical distributions of behavior patterns
- **Data Model**: Mean and standard deviation for timing, velocity, etc.
- **Serialization**: JSON format for human-readable storage
- **Validation**: Minimum sample requirements for reliable statistics

**BehaviorProfiler**
- **Responsibility**: Observe and record user actions during calibration
- **Event Sources**: Mouse events, action timing, user interactions
- **Algorithm**: Incremental statistical calculation
- **State Machine**: Calibrating / Not Calibrating states

**BehaviorSimulator**
- **Responsibility**: Generate human-like values from profiles
- **Distribution**: Normal (Gaussian) distribution sampling
- **Safety**: Clamps values to reasonable bounds
- **Fallback**: Uses defaults when profile not calibrated

**ProfileStorage**
- **Responsibility**: Persist profiles to disk
- **Location**: `~/.runelite/darkness/darkness-profile.json`
- **Format**: JSON with version number for schema migration
- **Safety**: Creates directories, handles errors gracefully

#### Design Patterns Used

1. **Builder Pattern** - Profile construction with incremental sample addition
2. **Singleton Pattern** - Single profiler instance per application
3. **Strategy Pattern** - Different sampling strategies for different event types

#### Data Flow

```
User Action → Event Listener → Profiler.record()
                                     ↓
                          Profile.addSample()
                                     ↓
                          Recalculate statistics
                                     ↓
                          Check calibration status
                                     ↓
                          ProfileStorage.save()
```

### 2. Intelligence Layer

**Purpose**: Provide context-aware, risk-based behavior modification.

#### Component Diagram

```
┌────────────────┐
│  Game State    │
│ (Players, etc) │
└───────┬────────┘
        │ monitors
        ▼
┌────────────────┐      calculates     ┌──────────────┐
│     Risk       │───────────────────▶ │  Risk Level  │
│  Assessment    │                     │   (enum)     │
└────────────────┘                     └──────────────┘
        │                                      │
        │ uses                                 │ defines
        │                                      │ response
        ▼                                      ▼
┌────────────────┐                     ┌──────────────┐
│  Intelligence  │                     │   Behavior   │
│    Config      │                     │ Modification │
└────────────────┘                     └──────────────┘
        │                                      │
        └──────────┬───────────────────────────┘
                   │ applied by
                   ▼
          ┌────────────────┐
          │    Script      │
          │ Intelligence   │
          └────────────────┘
```

#### Key Classes

**RiskAssessment**
- **Responsibility**: Calculate numerical risk score (0-100)
- **Factors**: Player presence, repetitive actions, time since random, environment
- **Algorithm**: Weighted sum of normalized factor scores
- **Configurability**: Factor weights can be adjusted

**RiskLevel (Enum)**
- **Responsibility**: Define risk thresholds and response strategies
- **Levels**: LOW, MODERATE, ELEVATED, HIGH
- **Strategies**: Delay multipliers, random behavior chances
- **Transitions**: Smooth transitions prevent oscillation

**ScriptIntelligence**
- **Responsibility**: Middleware wrapping script execution
- **Integration**: Combines profiling with risk assessment
- **Methods**: shouldProceed(), getSmartDelay(), afterAction()
- **State Management**: Tracks actions, manages transitions

**IntelligenceConfig**
- **Responsibility**: Configuration and tuning of intelligence layer
- **Presets**: DEFAULT, CAUTIOUS, EFFICIENT
- **Validation**: Ensures weights sum to 1.0, values in range
- **Application**: Applies settings to assessment and intelligence

#### Design Patterns Used

1. **Middleware Pattern** - ScriptIntelligence wraps script execution
2. **Strategy Pattern** - Different risk level strategies
3. **Observer Pattern** - Monitors game state changes
4. **Singleton Pattern** - Single intelligence instance
5. **Builder/Fluent Pattern** - Config creation and modification

#### Risk Calculation Algorithm

```python
# Pseudocode for risk calculation

def calculate_risk_score():
    # Calculate individual factor scores (0.0 to 1.0)
    player_score = min(nearby_players / max_players, 1.0)
    repetitive_score = min(repetitive_duration / max_duration, 1.0)
    random_score = min(time_since_random / max_time, 1.0)
    environment_score = 0.0  # Reserved for future

    # Weighted sum
    weighted_score = (
        player_score * player_weight +
        repetitive_score * repetitive_weight +
        random_score * random_weight +
        environment_score * environment_weight
    )

    # Convert to 0-100 scale
    return int(weighted_score * 100)
```

#### State Transition Diagram

```
         Risk Score Increases
              ┌─────────┐
              │         │
              ▼         │
    ┌─────────────┐    │    ┌──────────────┐
    │    LOW      │────┼───▶│   MODERATE   │
    │   (0-30)    │◀───┼────│   (31-60)    │
    └─────────────┘    │    └──────────────┘
              │        │            │
              │        │            │
              │        │    ┌──────────────┐
              │        └───▶│   ELEVATED   │
              │        ┌────│   (61-80)    │
              │        │    └──────────────┘
              │        │            │
              │        │            │
              │        │    ┌──────────────┐
              └────────┼───▶│     HIGH     │
                       └────│   (81-100)   │
                            └──────────────┘
         Risk Score Decreases
```

### 3. DarknessManager

**Purpose**: Unified interface and lifecycle management.

#### Responsibilities

1. **Initialization** - Load saved profiles on startup
2. **Lifecycle Management** - Start/stop calibration, save profiles
3. **Configuration** - Apply presets, manage settings
4. **Status Reporting** - Provide summary information
5. **Integration** - Connect profiling and intelligence layers

#### Class Structure

```java
public class DarknessManager {
    // Core components
    private BehaviorProfiler profiler;
    private ScriptIntelligence intelligence;
    private IntelligenceConfig config;

    // Lifecycle methods
    public void startCalibration()
    public void stopCalibration()
    public void saveProfile()
    public void loadProfile()

    // Configuration
    public void applyPreset(ConfigPreset)
    public void setConfig(IntelligenceConfig)

    // Access
    public BehaviorProfiler getProfiler()
    public ScriptIntelligence getIntelligence()

    // Status
    public String getStatusSummary()
    public String getShortStatus()
}
```

#### Design Patterns Used

1. **Singleton Pattern** - Single manager instance
2. **Facade Pattern** - Simplifies access to subsystems
3. **Factory Pattern** - Creates and configures components

### 4. Integration Layer

**Purpose**: Connect Darkness Edition to scripts and existing systems.

#### Script Integration Points

```java
// Scripts interact through DarknessManager
DarknessManager manager = DarknessManager.getInstance();
ScriptIntelligence intelligence = manager.getIntelligence();

// Three main integration points:
1. intelligence.shouldProceed()    // Before actions
2. intelligence.getSmartDelay()    // For timing
3. intelligence.afterAction(type)  // After actions
```

#### Compatibility with Rs2Antiban

```
┌────────────────────┐
│  Script Logic      │
└─────────┬──────────┘
          │
          ▼
┌────────────────────┐       Optional
│ ScriptIntelligence │──────────────┐
└─────────┬──────────┘              │
          │                         ▼
          │                 ┌────────────────┐
          │                 │  Rs2Antiban    │
          │                 │ .actionCooldown│
          │                 └────────────────┘
          ▼
┌────────────────────┐
│  Game Interaction  │
└────────────────────┘
```

## Data Flow

### Calibration Flow

```
1. User starts calibration
   ↓
2. BehaviorProfiler hooks into event system
   ↓
3. User performs actions naturally
   ↓
4. Events captured: Mouse movements, clicks, timings
   ↓
5. Profile updated incrementally with statistics
   ↓
6. Progress checked against minimum samples
   ↓
7. User stops calibration
   ↓
8. Profile saved to JSON file
   ↓
9. ScriptIntelligence updated with new profile
```

### Script Execution Flow

```
1. Script starts, gets DarknessManager instance
   ↓
2. Check intelligence.shouldProceed()
   ├─ YES → Continue
   └─ NO → Pause with smart delay
   ↓
3. Perform game action
   ↓
4. Call intelligence.afterAction(type)
   ├─ Updates risk assessment
   ├─ Records repetitive tracking
   └─ Maybe performs random behavior
   ↓
5. Get smart delay: intelligence.getSmartDelay()
   ├─ BehaviorSimulator generates base delay
   └─ RiskLevel applies multiplier
   ↓
6. Sleep for calculated duration
   ↓
7. Return to step 2
```

## Key Architectural Decisions

### Why Singleton Pattern?

**Decision**: Use singletons for DarknessManager and ScriptIntelligence

**Rationale**:
- Single source of truth for risk state
- Consistent profile across all scripts
- Simplified access pattern
- Prevents duplicate resource allocation

**Tradeoffs**:
- Less testable (but acceptable for this use case)
- Global state (but needed for cross-script coordination)

### Why Statistical Distributions?

**Decision**: Store mean and standard deviation rather than raw samples

**Rationale**:
- Compact storage (2 numbers vs. thousands)
- Fast simulation (sample from distribution)
- Natural variation automatically included
- Privacy-friendly (original actions not stored)

**Tradeoffs**:
- Assumes normal distribution (reasonable for human behavior)
- Cannot recover exact original samples

### Why Middleware Pattern?

**Decision**: ScriptIntelligence acts as middleware, not a base class

**Rationale**:
- Scripts can opt-in without changing inheritance
- Works with existing script hierarchy
- Easier to add/remove functionality
- Composition over inheritance

**Tradeoffs**:
- Requires manual integration in scripts
- Not automatic like aspect-oriented programming

### Why JSON for Storage?

**Decision**: Use JSON for profile persistence

**Rationale**:
- Human-readable for debugging
- Easy to edit manually if needed
- Well-supported libraries (Gson)
- Version field allows schema migration

**Tradeoffs**:
- Larger file size than binary
- Slower parsing than binary

## Performance Considerations

### Memory Usage

- **BehaviorProfile**: ~10 KB (with samples)
- **RiskAssessment**: ~1 KB (minimal state)
- **Total overhead**: ~50 KB per instance

### CPU Usage

- **Risk calculation**: ~0.1 ms per call
- **Delay generation**: ~0.01 ms per call
- **Profile updates**: ~0.05 ms per sample

### I/O Operations

- **Profile save**: ~10 ms (infrequent)
- **Profile load**: ~5 ms (startup only)

**Conclusion**: Negligible performance impact on script execution.

## Security Considerations

### Data Privacy

- **Behavioral profiles** contain timing statistics only, not game actions
- **No credentials** stored anywhere in Darkness Edition
- **Local storage** only, no cloud by default
- **User control** over profile save/load/delete

### Safe Defaults

- **Risk thresholds** are conservative (favor safety)
- **Validation** prevents invalid configurations
- **Fallback** to safe defaults if profile corrupt
- **Bounds checking** on all generated values

## Extension Points

Darkness Edition is designed for future expansion:

### 1. Additional Risk Factors

```java
// RiskAssessment.calculateEnvironmentScore()
// Currently returns 0.0, reserved for:
- Location-based risk (popular vs. remote areas)
- Time-of-day patterns
- Recent game events (deaths, teleports)
```

### 2. Enhanced Profiling

```java
// BehaviorProfile could add:
- Camera movement patterns
- Interface interaction sequences
- Common action chains
- Break timing preferences
```

### 3. UI Integration

```java
// Planned UI components:
- CalibrationPanel - Visual calibration progress
- ConfigPanel - GUI for configuration
- DashboardPanel - Real-time statistics
- DebugPanel - Risk assessment details
```

### 4. Task Abstractions

```java
// Planned task-based API:
- GatheringTask - Resource collection
- CombatTask - Combat management
- BankingTask - Inventory management
- NavigationTask - Pathfinding
```

## Testing Strategy

### Unit Testing

Focus on core logic:
- Statistical calculations in BehaviorProfile
- Risk score calculation in RiskAssessment
- Configuration validation in IntelligenceConfig

### Integration Testing

Test component interactions:
- Profile save/load cycle
- Config application to components
- Manager lifecycle operations

### Manual Testing

Required for behavioral aspects:
- Calibration captures realistic data
- Smart delays feel natural
- Risk responses are appropriate

## Conclusion

Darkness Edition's architecture balances several goals:

1. **Modularity** - Components can be used independently
2. **Simplicity** - Easy to understand and extend
3. **Performance** - Minimal overhead on script execution
4. **Safety** - Conservative defaults, validation, fallbacks
5. **Extensibility** - Clear extension points for future features

The design makes it easy for script authors to add intelligent adaptation while maintaining compatibility with existing Microbot infrastructure.

---

For implementation details, see the comprehensive API documentation in `darkness/README.md`.

For questions or discussion, join our [Discord](https://discord.gg/zaGrfqFEWE) server.
