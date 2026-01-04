# Darkness Edition Development Roadmap

This roadmap outlines the development phases for Microbot Darkness Edition, with specific milestones and success criteria for each phase.

## Phase 1: Foundation ✅

**Status: Complete**

### Milestones
- [x] Update README with Darkness Edition branding
- [x] Create development roadmap documentation
- [x] Establish `net.runelite.client.plugins.microbot.darkness` namespace

### Success Criteria
- Project identity clearly communicates Darkness Edition features
- Development path is well-documented and accessible

---

## Phase 2: Behavioral Profiling System 🚧

**Status: In Progress**

### Milestones
- [x] Create `darkness.behavioral` package structure
- [x] Implement `BehaviorProfile` data model
- [x] Implement `BehaviorProfiler` observation class
- [x] Implement `BehaviorSimulator` for applying profiles
- [ ] Create calibration UI panel (future)
- [ ] Add profile storage with JSON persistence (future)

### Success Criteria
- System can capture timing distributions and mouse patterns
- Captured profiles can be applied to script actions
- Profile data persists between sessions

### Key Classes
- `BehaviorProfile` - Stores statistical distributions of user behavior patterns
- `BehaviorProfiler` - Observes and records user actions during calibration
- `BehaviorSimulator` - Applies captured patterns to automated actions

---

## Phase 3: Intelligence Layer 🚧

**Status: In Progress**

### Milestones
- [x] Create `darkness.intelligence` package structure
- [x] Implement `RiskAssessment` component
- [x] Implement `ScriptIntelligence` middleware
- [ ] Configure risk thresholds and responses (future)
- [ ] Add comprehensive logging and debug panel (future)

### Success Criteria
- Risk score calculated from multiple environmental factors
- Script behavior adjusts based on risk level
- Smooth transitions between risk states

### Key Classes
- `RiskAssessment` - Calculates numerical risk score (0-100)
- `RiskLevel` - Enum defining risk thresholds and responses
- `ScriptIntelligence` - Middleware that wraps script execution

---

## Phase 4: Enhanced Script API (Future)

**Status: Planned**

### Milestones
- [ ] Design task-based abstractions (GatheringTask, CombatTask, etc.)
- [ ] Implement fluent interface for script configuration
- [ ] Create reference implementations
- [ ] Write comprehensive API documentation

### Success Criteria
- Scripts using enhanced API automatically benefit from profiling and intelligence
- API is intuitive and well-documented
- Reference implementations serve as templates

---

## Phase 5: Visual Identity (Future)

**Status: Planned**

### Milestones
- [ ] Implement dark theme with purple accents
- [ ] Create custom icons for Darkness Edition features
- [ ] Build statistics dashboard
- [ ] Design comprehensive settings panel

### Success Criteria
- Visual presentation distinguishes Darkness Edition from base Microbot
- Theme is consistent and reduces eye strain
- Dashboard provides valuable runtime insights

---

## Phase 6: Documentation and Community (Future)

**Status: Planned**

### Milestones
- [ ] Write Getting Started guide
- [ ] Create Architecture Overview document
- [ ] Develop Contribution Guide
- [ ] Maintain comprehensive changelog

### Success Criteria
- New users can successfully install and configure the system
- Contributors understand codebase architecture
- Changes are well-documented in changelog

---

## Version History

| Version | Phase | Key Features |
|---------|-------|--------------|
| 0.1.0   | 1     | Project setup, README, ROADMAP |
| 0.2.0   | 2     | Behavioral profiling foundation |
| 0.3.0   | 3     | Intelligence layer foundation |

---

## Contributing

We welcome contributions to any phase! Please check the current status of each phase before starting work. All Darkness Edition code should reside in the `net.runelite.client.plugins.microbot.darkness` namespace.

For questions or discussion, join our [Discord](https://discord.gg/zaGrfqFEWE) server.
