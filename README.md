<div align="center">
  <img src="app/src/main/res/drawable-nodpi/fixit_app_icon.png" width="160" alt="FixIT app icon" />
  <h1>FixIT</h1>
  <p><strong>Offline, guided computer troubleshooting for Android.</strong></p>
</div>

FixIT helps users identify likely computer faults through symptom-based questions and follow practical, safety-aware repair guides. Everything needed for diagnosis runs locally—no account, API key, or internet connection is required.

## Highlights

- 19 guided issues across hardware, Windows, networking, performance, display, peripherals, and safety hazards
- Weighted diagnosis restricted to causes relevant to the selected issue
- Safety interruptions for liquid exposure, swollen batteries, smoke, sparks, and other dangerous conditions
- Step-by-step repairs with difficulty, estimated time, tools, warnings, and professional-escalation guidance
- Symptom-aware search across issue descriptions, questions, answers, aliases, and repair causes
- Offline bookmarks, completed-diagnosis history, and resumable sessions using Room
- Catalog-integrity checks that catch missing questions, broken cause links, duplicate options, and invalid repair steps

## Troubleshooting coverage

| Area | Included examples |
| --- | --- |
| Hardware | No power, overheating, battery charging, missing SSD/HDD |
| Windows and apps | BSOD, boot failure, application crashes |
| Internet | Connected without internet, unstable Wi-Fi, Ethernet/IP faults |
| Performance | High CPU/RAM usage, slow startup and storage |
| Display | No signal, flicker, graphics artifacts |
| Peripherals | Printer offline, USB device not recognized |
| Safety and POST | Liquid spills, beep codes, diagnostic lights |

## Technology

- Kotlin and Jetpack Compose
- Navigation Compose
- Room and Kotlin coroutines/Flow
- Material 3
- JUnit and Robolectric

The app is intentionally local-first and does not ship Firebase, Gemini, Retrofit, or other cloud/network clients.

## Requirements

- Android Studio
- Android SDK 35
- JDK 17 or newer
- Android 7.0 (API 24) or newer device/emulator

## Build and test

Open this directory in Android Studio and run the `app` configuration, or use PowerShell:

```powershell
$env:ANDROID_HOME = "C:\path\to\Android\Sdk"
.\gradlew.bat testDebugUnitTest
.\gradlew.bat lintDebug
.\gradlew.bat assembleDebug
```

The resulting APK is written to:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Content integrity

`TroubleshootingRepository.validateCatalog()` verifies category totals, issue/question completeness, unique option labels, cause ownership, repair-step order, and repair-time metadata. The unit tests fail when an edit creates an unusable diagnostic path.

## Project map

```text
app/src/main/java/com/example/
├── data/local/          Room database, DAO, and persisted entities
├── data/model/          Troubleshooting and repair models
├── data/repository/     Catalog content, search, validation, and diagnosis logic
└── ui/                  ViewModel, navigation, components, screens, and theme
```

See [`CODEBASE_ANALYSIS.md`](CODEBASE_ANALYSIS.md) for the architecture review, implemented improvements, verification record, and prioritized recommendations.

## Safety notice

FixIT provides educational troubleshooting guidance, not a confirmed hardware diagnosis. Stop and seek qualified help when a device is swollen, wet, smoking, sparking, unusually hot, physically damaged, under warranty, or contains important data at risk. Never open a power-supply enclosure or work on energized hardware.
