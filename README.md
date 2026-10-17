# 🛠️ Link Fixer for ReVanced YT
Vibe-coded with AI using Gemini 3.6 Flash and Gemini 3.5 Flash-lite directly in Android Studio.
<br>Link Fixer for ReVanced YT is a sleek, lightweight Android utility built with Jetpack Compose and Material You design. It fixes the annoying issue where Android continuously resets default link handling permissions upon reboot, forcing youtube.com, music.youtube.com, and youtu.be links to open in the browser instead of your favorite ReVanced apps.

<br>Powered by Shizuku, this app grants and applies the required package link selection states locally with a single tap—no root or connected PC required!
### ✨ Features
⚡ One-Tap Link Fixing: Instantly sets domain link handlers for YouTube and YouTube Music ReVanced via Shizuku.
<br>🔄 Revert Anytime: Easily restore default link selections with a dedicated revert option.
<br>🔐 No Root Required: Communicates directly with Android's package manager through Shizuku ADB permissions.
<br>🎨 Material You & Customization: Features a sleek UI with animated text, custom button styles, and full background image personalization via the built-in Photo Picker.
<br>🚀 How It Works
<br>Android's DomainVerificationManager often clears or resets unverified third-party app link defaults after system reboots.
Link Fixer for ReVanced YT bypasses this by leveraging Shizuku to run package manager shell commands directly on your device.

### 📋 Prerequisites
Before using the app, make sure you have:
<br>Shizuku installed and running on your Android device (via Wireless Debugging or ADB).
<br>YouTube ReVanced (app.revanced.android.youtube) and/or YouTube Music ReVanced (app.revanced.android.apps.youtube.music) installed.
### 📲 How to Use
Launch Shizuku and ensure the service is active.
<br>Open Link Fixer for ReVanced YT.
<br>Tap "Shizuku it! Dummy." to grant the required permission prompt.
<br>Tap "Fix it! Dummy" to apply the link-handling defaults.
### 🛠️ Built With
Kotlin & Jetpack Compose (Material 3 / Material You)
<br>Shizuku API (dev.rikka.shizuku)
<br>Assistance from Gemini 3.6 Flash & Gemini 3.5 Flash-lite