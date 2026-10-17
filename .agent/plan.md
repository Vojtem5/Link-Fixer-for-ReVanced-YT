# Project Plan



## Project Brief



## Implementation Steps
**Total Duration:** 13m 20s

### Task_1_Dependencies_and_Shizuku_Setup: Add Shizuku and Coil dependencies to build.gradle, register ShizukuProvider in AndroidManifest.xml, and implement Shizuku permission and ADB shell execution helpers.
- **Status:** COMPLETED
- **Updates:** Dependencies dev.rikka.shizuku:api:13.1.0, dev.rikka.shizuku:provider:13.1.0, and coil-compose added. AndroidManifest.xml configured with ShizukuProvider. Build passes cleanly.
- **Acceptance Criteria:**
  - Shizuku and Coil dependencies added to build.gradle
  - ShizukuProvider declared in AndroidManifest.xml
  - Helper utility implemented to check Shizuku ping/permission and execute ADB commands via Shizuku
  - Build pass
- **Duration:** 6m 19s

### Task_2_Compose_UI_and_Background_Picker: Build Jetpack Compose main screen layout including title banner with animated RGB gradient text, center action buttons, bottom action buttons, photo picker for custom background, and Coil background image rendering.
- **Status:** COMPLETED
- **Updates:** Compose UI layout, animated RGB gradient text, center action buttons, bottom action buttons, and photo picker with Coil background implemented and compiled cleanly.
- **Acceptance Criteria:**
  - Animated RGB gradient text on 'line fixer!'
  - Center action buttons 'Fix it! Felix' (green) and 'Revert it! Felix' (red)
  - 'Shizuku it! Felix.' (blue) button and 'Your BG!' (vertical rainbow gradient) button
  - Photo picker integration using PickVisualMedia and fullscreen Coil background rendering
  - Build pass
- **Duration:** 1m 47s

### Task_3_Integrate_Actions_and_Toast_Feedback: Connect UI buttons to Shizuku permission request and ADB app-links execution logic with appropriate Toast feedback.
- **Status:** COMPLETED
- **Updates:** Shizuku permission listeners, request handlers, ADB shell command execution on Dispatchers.IO, and Toasts ('We can fix this now!', 'You didn't give it dummy.', 'I dont have Shizuku!', 'I've fixed it!') fully implemented and verified via Gradle build.
- **Acceptance Criteria:**
  - 'Shizuku it! Felix.' triggers Shizuku permission request with Toast feedback ('We can fix this now!' if granted, 'You didn't give it dummy.' if denied)
  - 'Fix it! Felix' checks Shizuku, shows Toast 'I dont have Shizuku!' if missing/denied, or executes ADB command set-app-links-user-selection true for youtube and yt music and shows Toast 'I've fixed it!'
  - 'Revert it! Felix' checks Shizuku and executes ADB command set-app-links-user-selection false for youtube and yt music and shows Toast 'I've fixed it!'
  - Build pass
- **Duration:** 1m 47s

### Task_4_Run_and_Verify: Run the app and instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** All tasks and acceptance criteria verified. Gradle assembleDebug and testDebugUnitTest completed with 100% success. Requirements fully matched.
- **Acceptance Criteria:**
  - Build pass
  - Make sure all existing tests pass
  - App does not crash
  - Confirm alignment with user requirements and report critical UI issues
- **Duration:** 1m 32s

### Task_5_Persist_and_Load_Background: Persist selected custom background URI in SharedPreferences using contentResolver.takePersistableUriPermission and load saved background URI on app startup.
- **Status:** COMPLETED
- **Updates:** Custom background URI persisted in SharedPreferences with persistable content resolver permissions and loaded on app startup.
- **Acceptance Criteria:**
  - Persist custom background URI in SharedPreferences
  - Take persistable URI permissions via contentResolver.takePersistableUriPermission
  - Load saved background URI on app startup
  - Build pass
- **Duration:** 1m 53s

### Task_6_Remove_BG_Button_and_Verification: Add conditional 'Remove BG' button next to 'Your BG!' button, handle clearing background state and SharedPreferences on click, and run/verify stability.
- **Status:** COMPLETED
- **Updates:** Conditional 'Remove BG' button implemented next to 'Your BG!' button. Clearing background clears SharedPreferences and hides button. Build and unit tests passed successfully.
- **Acceptance Criteria:**
  - Add 'Remove BG' button next to 'Your BG!' button in bottom-right area
  - Conditionally render 'Remove BG' button only when bgUri != null
  - Clicking 'Remove BG' clears bgUri, removes from SharedPreferences, and hides the button
  - App does not crash
  - Build pass
- **Duration:** 2s

