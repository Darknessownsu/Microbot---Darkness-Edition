# Contributing to Darkness Edition

Thank you for your interest in contributing to Microbot Darkness Edition! This guide will help you get started with contributing code, documentation, or ideas to the project.

## Table of Contents

1. [Code of Conduct](#code-of-conduct)
2. [How Can I Contribute?](#how-can-i-contribute)
3. [Development Setup](#development-setup)
4. [Coding Standards](#coding-standards)
5. [Pull Request Process](#pull-request-process)
6. [Testing Guidelines](#testing-guidelines)
7. [Documentation](#documentation)
8. [Community](#community)

## Code of Conduct

### Our Pledge

We are committed to providing a welcoming and inclusive environment for all contributors, regardless of experience level, background, or identity.

### Expected Behavior

- Be respectful and constructive in discussions
- Accept constructive criticism gracefully
- Focus on what's best for the project and community
- Show empathy toward other community members

### Unacceptable Behavior

- Harassment, discrimination, or offensive comments
- Publishing others' private information
- Trolling or deliberately derailing discussions
- Any conduct inappropriate for a professional setting

## How Can I Contribute?

### Reporting Bugs

Before creating a bug report:
1. Check the [existing issues](https://github.com/Darknessownsu/Microbot---Darkness-Edition/issues)
2. Update to the latest version to see if the issue persists
3. Collect relevant information (logs, screenshots, steps to reproduce)

**Good Bug Report Template:**

```markdown
**Describe the Bug**
A clear description of what the bug is.

**To Reproduce**
Steps to reproduce the behavior:
1. Go to '...'
2. Click on '...'
3. See error

**Expected Behavior**
What you expected to happen.

**Actual Behavior**
What actually happened.

**Environment**
- OS: [e.g., Windows 10, macOS 12.0]
- Java Version: [e.g., Java 11]
- Darkness Edition Version: [e.g., 0.3.0]

**Logs**
Relevant log output (check ~/.runelite/logs/)

**Screenshots**
If applicable, add screenshots to help explain.
```

### Suggesting Features

We love feature suggestions! Before suggesting:
1. Check the [ROADMAP.md](../ROADMAP.md) to see if it's already planned
2. Search [existing issues](https://github.com/Darknessownsu/Microbot---Darkness-Edition/issues) for similar suggestions
3. Consider if it aligns with Darkness Edition's core philosophy

**Good Feature Request Template:**

```markdown
**Is your feature request related to a problem?**
A clear description of the problem. Ex. "I'm frustrated when..."

**Describe the solution you'd like**
A clear description of what you want to happen.

**Describe alternatives you've considered**
Any alternative solutions or features you've considered.

**How does this align with Darkness Edition philosophy?**
- Does it improve intelligent adaptation?
- Does it enhance user experience?
- Is it educational and well-documented?

**Additional context**
Add any other context or screenshots.
```

### Contributing Code

We welcome code contributions! Here's what we're looking for:

**High Priority:**
- UI components (calibration panel, settings, dashboard)
- Enhanced script API (task abstractions)
- Additional risk factors for intelligence layer
- Documentation improvements
- Bug fixes

**Medium Priority:**
- Example scripts demonstrating features
- Performance optimizations
- Test coverage improvements
- Visual theme enhancements

**Future/Experimental:**
- Cloud synchronization
- AI-assisted script generation
- Advanced pathfinding
- Machine learning enhancements

## Development Setup

### Prerequisites

- **Java 11 or higher** (Java 17 recommended)
- **Git** for version control
- **Gradle** (included via wrapper)
- **IDE** (IntelliJ IDEA recommended)

### Setting Up Your Development Environment

1. **Fork the repository** on GitHub

2. **Clone your fork:**
   ```bash
   git clone https://github.com/YOUR_USERNAME/Microbot---Darkness-Edition.git
   cd Microbot---Darkness-Edition
   ```

3. **Add upstream remote:**
   ```bash
   git remote add upstream https://github.com/Darknessownsu/Microbot---Darkness-Edition.git
   ```

4. **Create a branch for your changes:**
   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b fix/your-bug-fix
   ```

5. **Build the project:**
   ```bash
   ./gradlew :runelite-client:shadowJar
   ```

6. **Run the client:**
   ```bash
   java -jar runelite-client/build/libs/client-*.jar
   ```

### Project Structure

```
Microbot---Darkness-Edition/
├── docs/                   # Documentation
├── runelite-client/
│   └── src/
│       └── main/
│           └── java/
│               └── net/runelite/client/plugins/microbot/
│                   ├── darkness/        # Darkness Edition code
│                   │   ├── behavioral/  # Profiling system
│                   │   ├── intelligence/ # Risk system
│                   │   ├── examples/    # Example scripts
│                   │   └── README.md    # API docs
│                   └── [other plugins]
├── ROADMAP.md             # Development roadmap
└── README.md              # Main readme
```

## Coding Standards

### Namespace Convention

**All Darkness Edition code must be in:**
```
net.runelite.client.plugins.microbot.darkness
```

**Subpackages:**
- `darkness.behavioral` - Behavioral profiling
- `darkness.intelligence` - Risk assessment and intelligence
- `darkness.ui` - User interface components (future)
- `darkness.tasks` - Task-based API (future)
- `darkness.examples` - Example scripts

### Java Code Style

We follow standard Java conventions with some specific preferences:

#### Naming Conventions

```java
// Classes: PascalCase
public class BehaviorProfile { }

// Methods: camelCase, descriptive
public void startCalibration() { }
public double getCalibrationProgress() { }

// Variables: camelCase, meaningful names
private int actionDelayMean;
private boolean calibrating;

// Constants: UPPER_SNAKE_CASE
private static final int MIN_TIMING_SAMPLES = 30;
```

#### Documentation

**Every public class and method must have Javadoc:**

```java
/**
 * Stores statistical distributions of user behavior patterns.
 * This profile is used by {@link BehaviorSimulator} to generate
 * human-like variations in automated actions.
 *
 * <p>Usage:
 * <pre>
 * BehaviorProfile profile = new BehaviorProfile();
 * profile.addActionDelaySample(250);
 * profile.addMouseVelocitySample(800.0);
 * </pre>
 *
 * @see BehaviorProfiler
 * @see BehaviorSimulator
 */
public class BehaviorProfile {
    /**
     * Gets the calibration progress as a percentage.
     *
     * @return progress from 0.0 to 1.0
     */
    public double getCalibrationProgress() {
        // Implementation
    }
}
```

#### Code Organization

```java
public class ExampleClass {
    // 1. Constants
    private static final int DEFAULT_VALUE = 100;

    // 2. Static fields
    private static ExampleClass instance;

    // 3. Instance fields (with Lombok annotations)
    @Getter
    @Setter
    private String name;

    // 4. Constructors
    public ExampleClass() { }

    // 5. Public methods
    public void doSomething() { }

    // 6. Protected methods
    protected void doSomethingProtected() { }

    // 7. Private methods
    private void doSomethingPrivate() { }

    // 8. Inner classes (if needed)
    private static class Helper { }
}
```

#### Error Handling

```java
// Use Lombok @Slf4j for logging
@Slf4j
public class ExampleClass {
    public void doSomething() {
        try {
            // Risky operation
        } catch (Exception e) {
            log.error("Failed to do something", e);
            // Handle or propagate appropriately
        }
    }
}

// Provide fallbacks for non-critical operations
public BehaviorProfile loadProfile() {
    try {
        return ProfileStorage.loadProfile();
    } catch (Exception e) {
        log.warn("Failed to load profile, using default", e);
        return new BehaviorProfile();
    }
}
```

### Design Principles

#### 1. Educational Code

Code should teach as it functions:

```java
// Good: Clear intent, well-documented
/**
 * Calculates risk score from nearby player presence.
 * More players within detection radius = higher risk.
 *
 * @return normalized score from 0.0 to 1.0
 */
private double calculatePlayerPresenceScore() {
    long playerCount = Rs2Player.getPlayers(player -> true).count();
    if (playerCount == 0) {
        return 0.0;
    }
    // Clamp to maximum for proper normalization
    int clampedCount = (int) Math.min(playerCount, maxNearbyPlayers);
    return (double) clampedCount / maxNearbyPlayers;
}

// Bad: Unclear, undocumented
private double calc() {
    long c = Rs2Player.getPlayers(p -> true).count();
    return c == 0 ? 0.0 : (double) Math.min(c, max) / max;
}
```

#### 2. Defensive Programming

Validate inputs and handle edge cases:

```java
public void setWeight(double weight) {
    if (weight < 0.0 || weight > 1.0) {
        log.warn("Weight {} out of range [0, 1], clamping", weight);
        weight = Math.max(0.0, Math.min(1.0, weight));
    }
    this.weight = weight;
}
```

#### 3. Fail Safely

Prefer safe defaults over exceptions:

```java
public int getSmartDelay() {
    try {
        // Calculate delay with profile
        return calculateDelayFromProfile();
    } catch (Exception e) {
        log.debug("Failed to calculate smart delay, using default", e);
        return 300; // Safe default
    }
}
```

## Pull Request Process

### Before Submitting

1. **Test your changes** thoroughly
2. **Update documentation** if adding features
3. **Follow code style** guidelines
4. **Write clear commit messages**
5. **Ensure builds succeed** (`./gradlew build`)

### Commit Message Format

Use clear, descriptive commit messages:

```
<type>: <subject>

<body>

<footer>
```

**Types:**
- `feat:` New feature
- `fix:` Bug fix
- `docs:` Documentation changes
- `style:` Code style changes (formatting, etc.)
- `refactor:` Code refactoring
- `test:` Adding or updating tests
- `chore:` Maintenance tasks

**Examples:**

```
feat: Add calibration progress UI panel

Implements a Swing panel showing real-time calibration progress
with sample counts and estimated completion time.

Closes #123
```

```
fix: Profile not saving on Windows due to path separator

Changed to use File.separator instead of hardcoded '/' for
cross-platform compatibility.

Fixes #456
```

### Submitting the Pull Request

1. **Push to your fork:**
   ```bash
   git push origin feature/your-feature-name
   ```

2. **Create Pull Request** on GitHub

3. **Fill out the template:**
   ```markdown
   **Description**
   Brief description of changes

   **Type of Change**
   - [ ] Bug fix
   - [ ] New feature
   - [ ] Documentation
   - [ ] Refactoring

   **Testing Done**
   - Tested on [OS]
   - Manual testing: [describe]
   - Automated tests: [added/updated]

   **Checklist**
   - [ ] Code follows style guidelines
   - [ ] Documentation updated
   - [ ] No new warnings/errors
   - [ ] Related issues linked
   ```

4. **Respond to feedback** promptly and graciously

### Review Process

- Maintainers will review your PR within a few days
- Address any requested changes
- Once approved, your PR will be merged
- You'll be added to the contributors list!

## Testing Guidelines

### Manual Testing

For behavioral features:
1. Test calibration with various playstyles
2. Verify smart delays feel natural
3. Check risk responses are appropriate
4. Ensure profile save/load works

### Automated Testing

When adding tests (encouraged!):

```java
import org.junit.Test;
import static org.junit.Assert.*;

public class BehaviorProfileTest {
    @Test
    public void testCalibrationProgress() {
        BehaviorProfile profile = new BehaviorProfile();
        assertEquals(0.0, profile.getCalibrationProgress(), 0.01);

        // Add samples
        for (int i = 0; i < 30; i++) {
            profile.addActionDelaySample(300);
        }

        assertTrue(profile.getCalibrationProgress() > 0.0);
    }
}
```

## Documentation

### Types of Documentation

1. **Code Comments** - Explain complex logic
2. **Javadoc** - Document public APIs
3. **README Files** - Package-level documentation
4. **Guides** - User-facing documentation
5. **Architecture Docs** - Design decisions

### Documentation Standards

- Write for your audience (users vs. developers)
- Use clear, concise language
- Include examples and code snippets
- Keep documentation up-to-date with code changes

## Community

### Getting Help

- **Discord**: https://discord.gg/zaGrfqFEWE
- **GitHub Issues**: For bugs and features
- **Discussions**: For questions and ideas

### Recognition

Contributors are recognized in:
- CONTRIBUTORS.md file
- Release notes
- GitHub contributors page
- Special thanks in major releases

## Questions?

If you have questions not covered here:
- Ask in our [Discord](https://discord.gg/zaGrfqFEWE)
- Open a [Discussion](https://github.com/Darknessownsu/Microbot---Darkness-Edition/discussions)
- Reach out to maintainers

---

**Thank you for contributing to Darkness Edition!** Your contributions help make intelligent automation accessible and well-documented for everyone. 🌙
