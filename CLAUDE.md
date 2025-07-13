# Focus App - Android Productivity Timer

## 📋 Project Overview

**Focus App** is a minimalist Android productivity application that helps users limit phone usage time through an intelligent timer system.

### 🎯 Core Features
- **Automatic timer**: Starts on screen unlock (5 min default, configurable 1-30 min)
- **Smart notifications**: 3-button system ("5 min", "15 min", "Disable")
- **Background service**: Permanent foreground service with notification
- **Call detection**: Auto-pauses during phone calls
- **App whitelist**: Configurable apps that pause the timer (via AccessibilityService)
- **Manual controls**: Pause/resume button in main interface
- **Configurable settings**: Timer delays, vibration, reminder intervals

## 📁 Repository Structure

### Core Application Files
```
app/src/main/java/com/focusapp/
├── MainActivity.kt              # Main UI with pause/resume + settings
├── FocusTimerService.kt        # Background timer service (foreground)
├── ScreenReceiver.kt           # BroadcastReceiver (screen unlock, calls)
├── NotificationActionReceiver.kt # Handles notification button actions
├── WhitelistActivity.kt        # App whitelist management UI
└── AppWhitelistService.kt      # AccessibilityService for app detection
```

### Android Resources
```
app/src/main/res/
├── layout/
│   ├── activity_main.xml      # Main interface with settings
│   └── activity_whitelist.xml # Whitelist management UI
├── values/
│   ├── strings.xml            # App strings and notifications
│   ├── colors.xml             # Material Design colors
│   └── themes.xml             # Material3 theme
├── mipmap-*/                  # App icons (all densities)
│   ├── ic_launcher.png        # Stop/blocked icon from uxwing
│   └── ic_launcher_round.png  # Round variant
└── xml/
    ├── accessibility_service_config.xml # AccessibilityService config
    ├── backup_rules.xml       # Backup configuration
    └── data_extraction_rules.xml # Data extraction rules
```

### Build Configuration
```
├── build.gradle.kts           # Root build file (AGP 8.6.1, Kotlin 1.9.25)
├── app/build.gradle.kts       # App module build (compileSdk 34)
├── settings.gradle.kts        # Project settings
├── gradle.properties          # Gradle properties
├── gradlew                    # Gradle wrapper script
├── gradlew.bat               # Windows Gradle wrapper
└── gradle/wrapper/           # Gradle wrapper files (v8.14.2)
```

## 🔧 Development Environment

### Prerequisites Installed
- **Java**: OpenJDK 17.0.15 (Ubuntu)
- **Gradle**: 8.14.2 (via SDKMAN!)
- **Android SDK**: Platform 34, Build Tools 34.0.0
- **Android Emulator**: 35.6.11 with system images

### Build Commands
```bash
# Compile debug APK
./gradlew assembleDebug

# Output: app/build/outputs/apk/debug/app-debug.apk (~6MB)

# Install on connected device/emulator
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 🏗️ Architecture Details

### Android Components
1. **MainActivity**: Settings UI, pause/resume controls, SharedPreferences
2. **FocusTimerService**: Foreground service for persistent timer
3. **ScreenReceiver**: Detects screen unlock and phone state changes
4. **NotificationActionReceiver**: Handles "5 min", "15 min", "Disable" actions
5. **WhitelistActivity**: UI for selecting apps that pause timer
6. **AppWhitelistService**: AccessibilityService to monitor foreground apps

### Data Storage
- **SharedPreferences** (`focus_app_prefs`):
  - `initial_delay` (int): Timer duration in minutes (default: 5)
  - `short_reminder` (int): Short reminder interval (default: 5)  
  - `long_reminder` (int): Long reminder interval (default: 15)
  - `vibration_enabled` (boolean): Vibration setting (default: true)
  - `timer_paused` (boolean): Manual pause state
  - `whitelisted_apps` (StringSet): Package names of whitelisted apps

### Permissions Required
```xml
<!-- Core functionality -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />

<!-- Optional: App whitelist feature -->
<uses-permission android:name="android.permission.BIND_ACCESSIBILITY_SERVICE" />
```

## 🚀 Installation & Testing

### Quick Start
1. **Clone/Download** this repository
2. **Open in Android Studio** or use command line
3. **Build**: `./gradlew assembleDebug`
4. **Install**: `adb install app/build/outputs/apk/debug/app-debug.apk`
5. **Grant permissions**: Notifications, Phone access, Accessibility (optional)

### Testing Checklist
- [ ] App launches and shows main interface
- [ ] Lock/unlock phone → Timer starts automatically
- [ ] Wait 5 minutes → Notification appears with 3 buttons
- [ ] Test notification actions ("5 min", "15 min", "Disable")
- [ ] Manual pause/resume works
- [ ] Settings are persisted (change timer duration, test)
- [ ] Phone calls pause timer automatically
- [ ] Whitelist: Enable accessibility service, select apps, test auto-pause

## 📝 Git History

### Commits
1. **Initial implementation** (3a2f583): Core app structure, timer, notifications
2. **Whitelist functionality** (202decd): AccessibilityService, app selection UI
3. **Compilation success** (8dc20b9): Gradle 8.14.2, Android SDK, icons, APK

### Key Files Status
- **✅ Compiled successfully**: APK generated and ready for installation
- **✅ All features implemented**: Timer, notifications, whitelist, settings
- **✅ Modern toolchain**: Gradle 8.14.2, AGP 8.6.1, Android 34
- **✅ Documentation**: README.md, COMPILATION_GUIDE.md, this CLAUDE.md

## 💡 Development Notes

### Code Style
- **Minimal codebase**: ~8 Kotlin files, simple and maintainable
- **No external dependencies**: Uses only AndroidX and Material3
- **Clear architecture**: Each component has single responsibility
- **Defensive coding**: Error handling, permission checks, null safety

### Known Limitations
- **Emulator setup**: Requires system image installation (~20 min download)
- **Accessibility permission**: Manual user action required for whitelist feature
- **Android version**: Targets API 34, minimum API 24

### Next Development Session
When resuming development:
1. Android SDK and emulator are installed in `/workspace/focus-app/android-sdk/`
2. APK is ready for testing at `app/build/outputs/apk/debug/app-debug.apk`
3. All source code is complete and documented
4. Use `./gradlew assembleDebug` to rebuild if needed
5. For emulator: Complete system image installation if needed, create AVD, launch emulator, install APK

## 📚 Resources

- **Android Developer Docs**: [developer.android.com](https://developer.android.com)
- **Material Design 3**: [m3.material.io](https://m3.material.io)
- **Gradle User Guide**: [docs.gradle.org](https://docs.gradle.org)
- **ADB Commands**: See COMPILATION_GUIDE.md for detailed ADB usage