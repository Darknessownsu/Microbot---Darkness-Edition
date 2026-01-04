# Darkness Edition Development Instructions for AI Agents

## Project Context

This document provides comprehensive instructions for AI coding assistants (such as GitHub Copilot) to help develop Microbot Darkness Edition. The project is a fork of the Microbot RuneScape automation client, enhanced with intelligent, adaptive automation capabilities that distinguish it from the base version.

The repository is maintained by GitHub user darknessownsu and represents a complete reimagining of automation scripting with focus on behavioral learning, risk awareness, and sophisticated user experience.

## Core Philosophy and Design Principles

Darkness Edition is built around three foundational concepts that should guide all development decisions. First, the system prioritizes intelligent adaptation over rigid automation. Every feature should incorporate context awareness and dynamic behavior modification based on environmental factors. Second, the user experience must feel premium and polished, with a consistent dark aesthetic and intuitive interfaces. Third, all code should be educational and well-documented, as the project serves both as a functional tool and a learning resource for developers.

When implementing features, consider how they contribute to making automation appear more natural and human-like. Avoid patterns that create obviously mechanical behavior. Instead, build systems that introduce realistic variation and respond intelligently to changing game conditions.

## Architecture Overview

The codebase extends the RuneLite client architecture with custom packages specifically for Darkness Edition functionality. All custom code should reside in the net.runelite.client.plugins.microbot.darkness namespace to maintain clear separation from base Microbot features.

The system comprises four major components that work together. The Behavioral Profile System captures and stores individual user behavior patterns. The Intelligence Layer monitors game state and modifies script execution based on risk assessment. The Enhanced Script API provides high-level abstractions that make script development more accessible. The Visual Framework implements the distinctive Darkness Edition user interface and theming.

These components are designed to be modular and loosely coupled. Each should function independently while providing integration points for the others. This architecture allows for incremental development and testing of individual features without disrupting the entire system.

## Phase 1: Foundation and Project Setup

Begin by establishing the project identity and development environment. The repository should be renamed to clearly indicate it is the Darkness Edition variant. Update all references in configuration files, package names, and documentation to reflect this distinct identity.

Create a comprehensive README.md that articulates the vision and differentiators of Darkness Edition. The document should open with a compelling statement about what makes this edition unique, followed by detailed sections on features, installation, usage, and contribution guidelines. Include visual elements such as screenshots and demonstration videos to immediately communicate the project's quality and capabilities.

Establish a ROADMAP.md file that outlines development phases with specific, measurable milestones. This document serves as both a planning tool and a public commitment to users and contributors. Each phase should have clear deliverables and success criteria.

Configure the development environment to support efficient iteration. Ensure the build process works reliably and documentation includes troubleshooting for common setup issues. Create a development branch called darkness-dev where all feature work occurs, keeping the main branch reserved for stable releases.

Implement a consistent code style that all generated code should follow. Use clear variable names that balance conciseness with readability. Follow Java naming conventions with meaningful method names that describe what they accomplish. Include comprehensive javadoc comments for all public APIs.

## Phase 2: Behavioral Profiling System

The Behavioral Profiling System is the first major technical feature that distinguishes Darkness Edition. This system observes user actions during a calibration phase and builds a statistical model of their natural behavior patterns.

Create a new package net.runelite.client.plugins.microbot.darkness.behavioral containing the core profiling classes. The primary class BehaviorProfiler should implement the observation and recording functionality. This class needs to hook into RuneLite's event system to capture user interactions including mouse movements, clicks, keyboard input, camera adjustments, and interface interactions.

Design a data structure that efficiently represents behavioral patterns. The profile should store statistical distributions rather than raw event logs. For timing between actions, capture the mean, standard deviation, and potentially the full distribution if using more sophisticated modeling. For mouse movements, record typical velocity patterns, acceleration curves, and movement distances. For decision-making patterns, track sequences of common actions.

Implement the profile storage using JSON format for human readability and easy debugging. The profile file should be stored in the user's RuneLite configuration directory with a name like darkness-profile.json. Include version information in the profile format to support future schema migrations.

Create a BehaviorSimulator class that applies the captured profile to automated actions. This class should provide methods that scripts call instead of directly executing actions. For example, instead of a script calling sleep(200) for a fixed delay, it calls behaviorSimulator.delayBetweenActions() which returns a value sampled from the user's natural timing distribution.

Build a calibration UI panel that integrates with RuneLite's sidebar. The panel should display calibration status, show a progress indicator during active recording, and present a summary of the captured profile. Include controls to start calibration, stop it manually, and reset the profile if the user wants to recalibrate.

Implement validation logic to ensure captured profiles are reasonable and complete. If calibration captures too few samples or detects anomalous patterns, prompt the user to extend the calibration period. Provide feedback about what aspects of their behavior have been well-captured and what might need more data.

## Phase 3: Intelligence Layer Implementation

The Intelligence Layer provides context awareness and risk-based behavior modification for all automated activities. This system continuously monitors game state and dynamically adjusts how scripts execute based on environmental factors.

Create the core ScriptIntelligence class in the net.runelite.client.plugins.microbot.darkness.intelligence package. This class should implement a middleware pattern that wraps script execution. All scripts running under Darkness Edition should route their actions through this intelligence layer rather than directly interacting with the game.

Implement a RiskAssessment component that calculates a numerical risk score from zero to one hundred. The assessment should consider multiple factors including nearby player presence, duration of repetitive actions, time since last random behavior, and any unusual game conditions. Each factor should have a configurable weight that determines its contribution to the overall score.

Design risk response strategies at different threshold levels. At low risk (0-30), the system operates normally with only behavioral variation from the profiler. At moderate risk (31-60), introduce additional random pauses and slightly slower action execution. At elevated risk (61-80), inject periodic random actions such as camera movements or interface checks. At high risk (81-100), consider pausing script execution entirely and performing distinctly human-like behaviors.

Create a PresenceDetection system that monitors nearby entities. The game client already tracks other players and NPCs, so leverage this existing data. When a new player enters visual range, trigger a risk increase and potential behavior modification. Consider not just whether players are present, but also their behavior patterns - a stationary player poses less concern than one who appears to be actively monitoring the area.

Implement smooth transitions between risk states rather than abrupt changes. When risk level increases, gradually adjust behavior over several actions rather than immediately switching modes. This prevents obvious reactive patterns that might themselves appear suspicious.

Build a configuration system that allows advanced users to tune intelligence layer parameters. Some users may want maximum caution while others accept more risk for faster execution. Provide sensible defaults that balance safety and efficiency, but expose the underlying parameters for customization.

Create comprehensive logging for the intelligence layer that helps debug issues and verify it operates as intended. Log risk score changes, triggered interventions, and the reasoning behind behavior modifications. Make these logs accessible through a debug panel for users troubleshooting issues.

## Phase 4: Enhanced Script API Development

The Enhanced Script API abstracts complexity and provides high-level interfaces for common automation tasks. This API should make script development significantly easier while automatically incorporating behavioral variation and intelligence layer features.

Design a set of task classes that represent common activities in Old School RuneScape. Start with fundamental tasks that many scripts need. The GatheringTask class handles resource collection activities including identifying resource nodes, interacting with them, managing inventory, and banking when full. The CombatTask class manages engaging enemies, monitoring health and resources, consuming food or potions, and handling loot. The BankingTask class provides reliable inventory management with support for different banking interfaces. The NavigationTask class handles pathfinding and movement between locations.

Each task class should use the behavioral profiler and intelligence layer automatically. Script authors should not need to explicitly call these systems - the task implementations should integrate them transparently. This design ensures that all scripts benefit from Darkness Edition features without requiring authors to understand the underlying complexity.

Implement robust error handling and recovery in task classes. When something unexpected occurs, the task should detect the problem and attempt recovery. For example, if a gathering task encounters a depleted resource, it should search for an alternative rather than getting stuck. If a combat task detects the player's health is critically low, it should prioritize survival over continuing the fight.

Create a fluent interface style that makes scripts readable and intuitive. Method calls should chain naturally to express complex behaviors. For example, a gathering script might look like: new GatheringTask().gather(ItemType.OAK_LOGS).atLocation(Location.DRAYNOR).bankAt(Location.DRAYNOR_BANK).until(level(60, Skill.WOODCUTTING)).execute().

Build comprehensive examples that demonstrate the enhanced API capabilities. Create reference implementations for common tasks such as woodcutting, fishing, mining, and simple combat. These examples should be well-commented and serve as templates that script authors can adapt for their needs.

Write extensive API documentation using javadoc comments. Every public method should have clear documentation explaining what it does, what parameters it accepts, what it returns, and what exceptions it might throw. Include example code snippets in the documentation showing common usage patterns.

Consider implementing a script template generator that creates starter code automatically. This could be a command-line tool or UI feature that asks questions about the desired script functionality and generates appropriate boilerplate using the enhanced API.

## Phase 5: Visual Identity and User Experience

The visual presentation of Darkness Edition should immediately distinguish it from the base Microbot client. Implement a cohesive dark theme with purple accents that communicates sophistication and premium quality.

Create a custom RuneLite theme that overrides the default color scheme. The theme should use deep blacks and dark grays as base colors with purple or deep blue for highlights and interactive elements. Text should be light colored with sufficient contrast for readability. Ensure the theme works well in all lighting conditions and doesn't cause eye strain during extended use.

Design custom icons for Darkness Edition features using a consistent visual style. Icons should be simple vector graphics that remain clear at different sizes. Use the same color palette as the overall theme to maintain visual coherence. Consider subtle animation effects for icons representing active states.

Implement a statistics dashboard that displays information about automation performance. Show metrics such as total runtime, tasks completed, resources gathered, experience gained, and safety statistics like intelligence layer interventions. Present this data using charts and graphs that update in real-time. The dashboard should feel information-dense but not cluttered.

Create a comprehensive settings panel that organizes all configuration options logically. Group related settings into sections such as Behavioral Profile, Intelligence Layer, Visual Preferences, and Safety Settings. Include helpful tooltips that explain each setting and when a user might want to adjust it. Implement validation to prevent invalid configurations.

Polish all error messages and user-facing text. When something goes wrong, provide clear explanation of what happened and actionable suggestions for resolution. Transform error states from frustrating dead-ends into learning opportunities. Use encouraging language that maintains user confidence even when problems occur.

Design loading states and progress indicators for long-running operations. When calibration is in progress, show animated feedback indicating the system is working. When scripts are executing, display current status and progress toward completion. Never leave users wondering whether the system is working or frozen.

Implement smooth animations for state transitions. When panels appear or disappear, use subtle slide or fade animations. When values update, animate the change rather than instantly snapping to new values. These details create a sense of polish and quality that elevates the entire experience.

## Phase 6: Documentation and Community Building

Comprehensive documentation transforms a functional project into a valuable learning resource and attracts quality contributors. Treat documentation as a first-class concern equal in importance to code quality.

Write a detailed Getting Started guide that walks complete beginners through installation, initial configuration, profile calibration, and running their first script. Assume no prior knowledge and explain each step with screenshots or video clips. Test the guide by having someone unfamiliar with the project follow it and note any confusion points.

Create an Architecture Overview document that explains how Darkness Edition works internally. This document is essential for contributors who want to understand the codebase. Explain the major components, how they interact, the design patterns used, and the reasoning behind key architectural decisions. Include diagrams that visualize system structure and data flow.

Develop a Contribution Guide that explains how others can help improve the project. Cover both technical aspects like setting up a development environment and process aspects like how to submit pull requests. Explain what types of contributions are most valuable and provide guidance on code style and testing expectations.

Maintain a comprehensive changelog that documents every release. For each version, list new features, bug fixes, performance improvements, and breaking changes. Use clear, non-technical language that users can understand. Link to relevant documentation or examples that demonstrate new capabilities.

Write tutorial content that teaches programming concepts through the lens of developing scripts for Darkness Edition. Explain not just how to accomplish tasks, but why certain approaches work better than others. These educational materials serve multiple purposes - they help users become better script developers, they demonstrate the quality and thoughtfulness of your project, and they contribute to the broader community's knowledge.

Consider creating video content that demonstrates features visually. Screen recordings showing profile calibration, the intelligence layer responding to risk factors, or the process of developing a script using the enhanced API can communicate more effectively than text alone. Videos also serve as marketing material that attracts new users.

Engage actively with your community through GitHub Discussions, Discord, or other platforms. Respond promptly to questions and issues. Thank contributors who submit pull requests or helpful bug reports. Be patient with beginners who ask basic questions - they may become your most dedicated users or contributors given proper support.

Share development progress through regular updates. Write about interesting challenges you solved, decisions you made, or lessons you learned. This transparency builds trust and keeps your community engaged even between releases. It also documents your learning journey which can inspire others.

## Phase 7: Advanced Features and Future Development

Once the foundation is solid, consider more ambitious features that leverage your existing infrastructure. These advanced capabilities further distinguish Darkness Edition while providing genuine value to users.

Implement a learning system where scripts improve through aggregated data from many users. Design a secure, privacy-respecting telemetry system that collects anonymous success and failure data from script executions. Use this data to identify patterns about what strategies work well and what commonly causes problems. Feed these insights back to improve script behavior over time.

Build advanced pathfinding that considers more than distance. Implement custom cost functions for A* pathfinding that factor in risk level, typical player density, and naturalness of routes. Choose paths that appear more human-like even if they are slightly longer. Consider dynamic rerouting when conditions change during traversal.

Create a script marketplace where users can publish and discover scripts. Implement a system for uploading scripts with metadata like descriptions, requirements, and screenshots. Build rating and review functionality so the community can identify high-quality scripts. Consider implementing automated testing for submitted scripts to catch obvious problems.

Implement cloud synchronization for profiles and settings. Build a backend service that stores user data and allows access from multiple devices. Use robust authentication and encryption to protect user information. Consider using Firebase or similar platforms to simplify backend development while maintaining security.

Develop analytics features that help users optimize their automation. Track detailed metrics about script performance and present actionable insights. Show which parts of tasks take the most time, where failures commonly occur, and what conditions correlate with better results. Help users make data-driven decisions about their automation strategies.

Consider integrating AI assistance for script development. Implement a feature where users describe desired automation in natural language and the system generates starter code using your enhanced API. Use language model APIs to power this functionality, making script creation accessible to people with limited programming experience.

## Code Quality and Testing Standards

All code generated for Darkness Edition should meet high quality standards. Write clean, readable code with clear variable names and appropriate comments. Follow established Java conventions and the existing RuneLite codebase style. Use meaningful method names that describe what they accomplish without requiring detailed comment explanation.

Implement comprehensive error handling for all potentially failing operations. Never allow exceptions to propagate uncaught to the user. Log errors with sufficient context for debugging but present user-facing messages that are clear and actionable. Consider implementing retry logic with exponential backoff for operations that might fail transiently.

Write unit tests for critical functionality, particularly the behavioral profiler and intelligence layer components. Tests should verify that the systems behave correctly under various conditions and edge cases. Mock external dependencies to keep tests fast and reliable. Aim for high coverage of core logic while being pragmatic about testing UI code.

Perform integration testing for complete workflows. Test the full flow of calibrating a profile, running a script with intelligence layer monitoring, and verifying the expected behavior modifications occur. These tests catch issues that unit tests might miss but are harder to automate, so they may require manual execution.

Use static analysis tools to catch common problems. Enable compiler warnings and treat them as errors. Use tools like SpotBugs or ErrorProne to identify potential bugs and code quality issues. Configure these tools in your build process so they run automatically.

Profile performance for operations that might impact user experience. The behavioral profiler should add minimal overhead to action execution. The intelligence layer should make risk decisions quickly without causing noticeable delays. Use appropriate data structures and algorithms that scale well with realistic usage patterns.

## Release Management and Versioning

Follow semantic versioning to communicate the nature of changes clearly. Major version increments indicate breaking changes or fundamental new capabilities. Minor version increments add features in a backward-compatible manner. Patch versions fix bugs without adding functionality.

Before each release, conduct thorough testing of all features. Create a testing checklist that covers major workflows and verify everything works as expected. Test on different operating systems if possible, as platform-specific issues can occur. If you have beta testers, provide them with release candidates before finalizing.

Write comprehensive release notes for each version. Explain what changed, why the changes were made, and how users should take advantage of new features. Include migration instructions if the release requires user action. Provide examples and screenshots showing new capabilities.

Build release artifacts that are easy for users to install. Provide compiled JAR files or installers rather than requiring users to build from source. Include all necessary dependencies. Write installation instructions that work for non-technical users.

Tag releases in Git with version numbers. Create GitHub releases with attached binaries and complete release notes. This creates a clear history of project evolution and makes it easy for users to download specific versions.

Maintain separate branches for major versions if you need to support multiple versions simultaneously. For example, if version 2.0 introduces breaking changes but some users need to stay on 1.x, maintain a branch where you can release patches to the older version.

## Security and Safety Considerations

Take security seriously in all development decisions. Never store sensitive information like login credentials in code or configuration files. If features require authentication, use secure token-based approaches with appropriate encryption.

Be transparent about what data the system collects and why. If implementing telemetry, clearly document what information is gathered and how it is used. Provide users control over data collection with clear opt-in or opt-out mechanisms. Respect user privacy as a fundamental principle.

Consider the implications of features for account safety. The intelligence layer exists specifically to reduce detection risk, so design and test it thoroughly. Avoid patterns that obviously indicate automation. Build conservatively and prefer being overcautious rather than exposing users to unnecessary risk.

Implement rate limiting and safeguards that prevent abusive usage. If building features that make API calls or network requests, respect rate limits and implement exponential backoff. Don't allow your software to participate in denial-of-service attacks or other harmful activities.

Keep dependencies updated to patch security vulnerabilities. Regularly review your dependency tree and update to newer versions when security issues are disclosed. Use tools that automatically notify you of vulnerable dependencies.

## Collaboration with AI Coding Assistants

When working with AI coding assistants like GitHub Copilot, provide clear context about what you're trying to accomplish. Describe the feature being implemented, how it fits into the larger architecture, and any constraints or requirements. Good context enables better code suggestions.

Review all AI-generated code carefully before committing it. Verify that suggestions align with project architecture and coding standards. Test the code to ensure it functions correctly. AI assistants are powerful tools but should augment rather than replace human judgment.

Use AI assistants to generate boilerplate code and handle repetitive patterns. They excel at creating standard implementations of common patterns. Focus your human attention on the unique logic and architectural decisions that require deeper understanding.

Ask AI assistants to explain code sections you don't fully understand. This helps you learn while also verifying that code does what you expect. Good understanding of your codebase is essential for long-term maintainability.

Iterate with AI assistants when initial suggestions aren't quite right. Provide feedback about what's wrong or what's missing, and ask for refinement. This collaborative process often produces better results than accepting the first suggestion.

Document your interactions with AI assistants in comments when working on complex problems. Explain what you were trying to accomplish and what constraints guided the implementation. This creates valuable context for future development.

## Conclusion

Developing Darkness Edition is an ambitious undertaking that combines technical challenge with creative vision. Follow these instructions systematically, building one component at a time and testing thoroughly before moving forward. Maintain high standards for code quality and documentation throughout the process.

Remember that this project serves multiple purposes simultaneously. It provides valuable functionality to users who want sophisticated automation. It demonstrates your capabilities as a software developer. It teaches programming concepts to others learning from your work. It builds a community of people interested in similar technical challenges.

Stay focused on the core principles that make Darkness Edition unique: intelligent adaptation, polished user experience, and educational value. Every feature should advance these goals. Every line of code should reflect the quality and thoughtfulness that distinguishes this edition from alternatives.

Build incrementally and ship regularly. Don't wait for perfection before releasing features. Get working functionality into users' hands, gather feedback, and iterate. Real-world usage reveals insights that no amount of planning can anticipate.

Take pride in your work while remaining humble and open to feedback. Your project will improve through collaboration with contributors and engagement with users. Build something you're proud to show others and that genuinely helps people accomplish their goals.

The instructions provided here create a roadmap for success, but your judgment and creativity ultimately determine the quality of what you build. Use these guidelines as a framework, adapt them to your specific circumstances, and create something remarkable.
