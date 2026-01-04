# Changelog

All notable changes to Darkness Edition will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Planned Features
- Calibration UI panel with visual progress
- Configuration UI panel for settings
- Statistics dashboard for runtime metrics
- Dark theme with purple accents
- Enhanced script API with task abstractions
- Advanced pathfinding with risk awareness

## [0.3.0] - 2026-01-04

### Added - Core Infrastructure Complete

#### Behavioral Profiling System
- **ProfileStorage** - Persistent JSON storage for behavioral profiles
  - Saves to `~/.runelite/darkness/darkness-profile.json`
  - Automatic directory creation and error handling
  - Profile info and status methods

#### Intelligence Layer Configuration
- **IntelligenceConfig** - Comprehensive configuration system
  - Three presets: DEFAULT, CAUTIOUS, EFFICIENT
  - Configurable risk factor weights
  - Adjustable thresholds and detection parameters
  - Configuration validation and normalization

#### Unified Management
- **DarknessManager** - Central manager for all features
  - Singleton access pattern
  - Automatic profile loading on startup
  - Lifecycle management (start/stop calibration)
  - Configuration preset application
  - Status reporting for display and logging

#### Documentation
- **Getting Started Guide** (`docs/GETTING_STARTED.md`)
  - Complete installation walkthrough
  - Profile calibration tutorial
  - First script example
  - Configuration options explained
  - Troubleshooting section

- **Architecture Overview** (`docs/ARCHITECTURE.md`)
  - Component diagrams and data flow
  - Design patterns explained
  - Performance analysis
  - Security considerations
  - Extension points documented

- **Contributing Guide** (`docs/CONTRIBUTING.md`)
  - Code of conduct
  - Development setup instructions
  - Coding standards and style guide
  - Pull request process
  - Testing guidelines

- **API Documentation** (`darkness/README.md`)
  - Comprehensive API reference
  - Quick start examples
  - Integration patterns
  - Best practices guide

#### Examples
- **DarknessExampleScript** - Complete reference implementation
  - Demonstrates all major features
  - Shows best practices
  - Includes usage patterns for common scenarios

### Improved
- Updated main README with comprehensive documentation links
- Enhanced package documentation with cross-references
- Better organization of documentation by audience (users vs developers)

### Technical Details
- All code follows established Java conventions
- Comprehensive Javadoc for public APIs
- Defensive programming with safe fallbacks
- Minimal performance overhead (~50 KB memory, <1ms per operation)

## [0.2.0] - 2025-12-XX

### Added - Intelligence Layer Foundation

#### Risk Assessment
- **RiskAssessment** - Multi-factor risk calculation (0-100 score)
  - Player presence factor (35% weight)
  - Repetitive action duration (25% weight)
  - Time since random behavior (20% weight)
  - Environment factor (20% weight, reserved for future)

- **RiskLevel** - Enum defining four risk levels
  - LOW (0-30): Normal operation
  - MODERATE (31-60): 1.3x delay, 10% random behaviors
  - ELEVATED (61-80): 1.6x delay, 25% random behaviors
  - HIGH (81-100): 2.0x delay, 50% random behaviors, consider pausing

#### Script Integration
- **ScriptIntelligence** - Middleware for script execution
  - `shouldProceed()` - Check if script should continue
  - `getSmartDelay()` - Risk-adjusted delays
  - `afterAction()` - Report actions for tracking
  - `performRandomBehavior()` - Execute random actions
  - Smooth risk level transitions to prevent oscillation
  - Optional integration with Rs2Antiban

### Improved
- Added detailed risk assessment breakdown for debugging
- Configurable weights and thresholds
- Status summaries for logging and display

## [0.1.0] - 2025-11-XX

### Added - Behavioral Profiling Foundation

#### Core Profile System
- **BehaviorProfile** - Statistical behavior model
  - Action timing distributions (mean and standard deviation)
  - Mouse velocity patterns
  - Click timing characteristics
  - JSON serialization for storage
  - Calibration progress tracking

- **BehaviorProfiler** - User behavior observation
  - Mouse movement and velocity tracking
  - Click timing capture
  - Action delay recording
  - Minimum sample requirements (30 timing, 50 mouse)
  - Calibration lifecycle management

- **BehaviorSimulator** - Profile application
  - Normal distribution sampling
  - `getActionDelay()` - Profile-based delays
  - `getClickDelay()` - Click-specific timing
  - `getMouseVelocity()` - Movement speed
  - `getDelayInRange()` - Constrained random delays
  - Safe bounds checking

#### Project Infrastructure
- Created `darkness` package namespace
- Established `behavioral` subpackage
- Established `intelligence` subpackage
- Package-level documentation with Javadoc

### Documentation
- Project README with Darkness Edition branding
- ROADMAP with development phases
- Package-level API documentation

---

## Version History Summary

| Version | Date       | Key Features                                    |
|---------|------------|-------------------------------------------------|
| 0.3.0   | 2026-01-04 | Configuration, storage, manager, documentation  |
| 0.2.0   | 2025-12-XX | Intelligence layer, risk assessment             |
| 0.1.0   | 2025-11-XX | Behavioral profiling foundation                 |

---

## Upgrade Notes

### Upgrading to 0.3.0

No breaking changes. New features are additive:

```java
// Old way still works
ScriptIntelligence intelligence = ScriptIntelligence.getInstance();

// New unified way recommended
DarknessManager manager = DarknessManager.getInstance();
ScriptIntelligence intelligence = manager.getIntelligence();

// New features available
manager.applyPreset(ConfigPreset.CAUTIOUS);
manager.saveProfile();
```

### Upgrading to 0.2.0

No breaking changes. Intelligence layer is opt-in:

```java
// Scripts automatically benefit from intelligence if using getSmartDelay()
sleep(intelligence.getSmartDelay());

// Disable intelligence layer if needed
intelligence.setEnabled(false);
```

### Upgrading to 0.1.0

Initial release - no upgrade path.

---

## Development Philosophy

Darkness Edition follows these principles in all changes:

1. **Intelligent Adaptation** - Features should make automation more natural
2. **User Experience** - Polished, intuitive, premium feel
3. **Educational Code** - Well-documented, clear, teaching-oriented
4. **Backward Compatibility** - New features don't break existing code
5. **Safe Defaults** - Conservative settings favor safety over speed

---

## Contributing

See [CONTRIBUTING.md](docs/CONTRIBUTING.md) for how to contribute to future releases.

For the development roadmap, see [ROADMAP.md](ROADMAP.md).

For questions or discussion, join our [Discord](https://discord.gg/zaGrfqFEWE) server.

---

*Darkness Edition - Where intelligent automation meets elegant design.* 🌙
