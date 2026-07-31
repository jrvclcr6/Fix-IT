# FixIT codebase analysis

Date: 2026-07-31

## Executive assessment

FixIT is a local-first Android app built with Kotlin, Jetpack Compose, Navigation Compose, Room, coroutines, and a static troubleshooting catalog. Its strongest product decisions are offline operation, guided symptom questions, explicit safety interruptions, resumable sessions, history, and bookmarks.

The original project looked more complete than it was: category cards advertised 19 issues while the repository contained only 7, many answer weights referenced missing or cross-issue causes, and the confidence algorithm forced a minimum 65% match. Those defects could produce plausible-looking but unsupported results. The original test suite did not cover the product and still referenced template components.

The current implementation contains 19 working issues, validates every catalog relationship, requires complete answers, ranks only causes belonging to the selected issue, and exposes practical repair-readiness information.

## Architecture

- `MainActivity` owns the single Compose navigation graph.
- `MainViewModel` owns active diagnosis, selected answers, guide progress, search state, and Room-backed flows.
- `TroubleshootingRepository` is the offline source of truth for categories, issues, weighted options, causes, and repair steps.
- `ExpandedTroubleshootingContent` isolates the larger content set from diagnosis logic.
- Room stores bookmarks, completed history, and one unfinished session per issue.
- Compose screens render home/search, categories, issue details, questions, safety, results, repair overview, interactive steps, bookmarks, history, and about.

The application has no runtime need for Firebase, Gemini, Retrofit, OkHttp, Moshi, or Coil. Those unused dependencies and the misleading API-key README were removed.

## Implemented improvements

### Catalog and diagnosis integrity

- Expanded the catalog from 7 to 19 actual issues, matching all category counts.
- Added 12 issues: battery charging, missing storage, Windows boot failure, app crashes, unstable Wi-Fi, Ethernet/IP faults, slow startup, display flicker, graphics artifacts, printer offline, USB recognition, and POST/beep codes.
- Added 27 repair causes with actionable repair steps for the expanded and previously unsupported flows.
- Corrected every legacy option that referenced a missing or cross-issue cause.
- Corrected a duplicate `A` option label in the power diagnostic.
- Added `validateCatalog()` checks for category counts, categories, questions, unique option letters, cause links, repair steps, step order, and repair time.
- Diagnosis now returns no result until every question is answered.
- Ranking is deterministic and restricted to causes for the active issue.
- Confidence now reflects evidence strength and separation from the runner-up instead of forcing a misleading minimum.
- Alternative causes are limited to the two next-best relevant candidates.

### Search and repair experience

- Search now covers titles, descriptions, symptom aliases, questions, answer text, cause names, and cause descriptions.
- Causes now support difficulty, estimated time, required tools, and conditions for professional escalation.
- The repair overview presents this readiness information before instructions.
- Step guides restore the requested cause from the navigation route rather than relying only on transient ViewModel state.
- Steps without an illustration no longer display an unrelated PSU image.
- Result screens use a relevant issue icon when no cause-specific illustration exists.
- Safety acknowledgement returns to the diagnostic flow so remaining questions can be completed; it no longer attempts to produce an incomplete result.
- New issue icon names now map to real Material icons instead of a generic fallback.

### Build and maintainability

- Added a Gradle 9.3.1 wrapper.
- Aligned compile/target SDK with the installed API 35 toolchain and selected a compatible Core KTX release.
- Restored default debug signing instead of requiring a missing project keystore.
- Removed unused cloud, AI, networking, serialization, image-loading, and secrets dependencies.
- Removed destructive Room fallback behavior so a future missing migration cannot silently erase user history.
- Replaced generated setup instructions with an accurate offline build guide.
- Replaced placeholder tests with repository integrity, search, ranking, completeness, safety, and app-resource tests.

## Verification

- `testDebugUnitTest`: 5 tests, 0 failures, 0 errors.
- `assembleDebug`: successful.
- `lintDebug`: successful; report generated at `app/build/reports/lint-results-debug.html`.
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`.
- No Android device or emulator was connected, so physical interaction and accessibility testing remain unverified.

## Remaining recommendations

### High priority

1. Add Compose navigation tests for safety interruption, session resume, result-to-guide, bookmark toggling, and process recreation.
2. Add a repair outcome (`Fixed`, `Not fixed`, `Skipped`) and use it to recommend the next cause instead of ending every guide at Home.
3. Replace the legacy launcher bitmap variants. Lint reports inconsistent density metadata even though packaging succeeds.
4. Add explicit Room migrations before changing the schema; destructive fallback has intentionally been removed.

### Medium priority

1. Move the original seven issues into the expanded content module or split the catalog by category to reduce merge conflicts.
2. Add content versioning and a lightweight editorial review checklist for safety language, supported Windows versions, and manufacturer-specific procedures.
3. Add structured diagnostic evidence to the result screen so users can see which answers supported the match.
4. Add export/share for a diagnosis summary without exposing unrelated history.
5. Add accessibility QA for screen-reader ordering, dynamic font sizes, contrast, and 320 dp-wide layouts.

### Product boundaries

FixIT should describe results as likely causes, not confirmed diagnoses. Procedures involving swollen batteries, liquid exposure, mains power supplies, data recovery, soldered components, or warranty-covered devices should continue to lead with stop conditions and professional escalation. The catalog should remain useful offline; optional remote content updates should be signed, versioned, and never required for core diagnosis.
