# 🎲 Dice Roll

A small, complete, offline Android game: tap the dice (or the big **ROLL DICE** button) and see what
fate says. One dice or two, sound and vibration you can switch off, a history of your last 20 rolls,
and a light/dark theme. No ads, no accounts, no internet permission — everything happens on your
phone.

<p align="center">
  <img src="docs/app_icon.png" alt="Dice Roll app icon" width="180">
</p>

Written with **Kotlin + Jetpack Compose + Material 3**, clean architecture (UI → ViewModel →
storage), and nothing but official AndroidX libraries. It is meant to be read, understood and
changed by someone who is learning Android — every file has comments that explain *why*, not only
*what*.

---

## Table of contents

1. [What the app does](#1-what-the-app-does)
2. [Requirements](#2-requirements)
3. [Open the project in Android Studio](#3-open-the-project-in-android-studio)
4. [Run it on your phone](#4-run-it-on-your-phone)
5. [Run the automatic tests](#5-run-the-automatic-tests)
6. [Build a signed APK / AAB for Google Play](#6-build-a-signed-apk--aab-for-google-play)
7. [How the code is organised](#7-how-the-code-is-organised)
8. [How it works inside](#8-how-it-works-inside)
9. [Customising the app](#9-customising-the-app)
10. [Manual test checklist](#10-manual-test-checklist)
11. [Troubleshooting](#11-troubleshooting)
12. [Privacy](#12-privacy)

---

## 1. What the app does

| Feature | Details |
| --- | --- |
| **Roll** | Tap the dice **or** the orange **ROLL DICE** button. Results are real random numbers 1–6, never 0 or 7. |
| **Animation** | The dice spins, hops and shows changing faces; when it stops you get the true result. |
| **Two dice mode** | Switch "1 Dice / 2 Dice". Two independent dice, each 1–6, plus the total (always 2–12). |
| **Result text** | "You rolled a 6!" or "You rolled 4 + 5 = 9!" with a total pill. |
| **History** | The last 20 rolls are saved on the phone (newest first), with dice, result and time, plus small statistics. Clearing asks for confirmation first. |
| **Settings** | Sound on/off, vibration on/off, theme (Light / Dark / System), clear history, app version. Every switch is remembered. |
| **No double rolls** | While the dice are rolling, taps are ignored — both in the UI and in the ViewModel. |
| **Offline** | No internet permission, no ads, no trackers. Settings and history live in Android DataStore on the device. |

The screens: **Roll** (dice, result, button, mode switch, recent rolls) · **History** (statistics,
the list, clear button) · **Settings** (all switches and info) — reached from the bottom navigation
bar. The header shows a small dice logo, "Dice Roll" and the tagline "Roll your luck!".

---

## 2. Requirements

| Thing | What you need |
| --- | --- |
| Android Studio | **Narwhal 3 Feature Drop (2025.1.3) or newer** — older versions do not understand this build setup. *Help → Check for Updates* if you are unsure. |
| JDK | The **JDK that comes with Android Studio** (Java 17 or newer). Do not point Gradle at an older JDK. |
| Phone (optional) | Android 7.0 (API 24) or newer — that is 99 % of phones in use. An emulator works too. |
| Internet | Needed **once**, while Gradle downloads the build tools and libraries (~1 GB the first time). After that the app itself never needs the internet. |

The project uses these versions (all stable, all declared in `gradle/libs.versions.toml`):

```
Gradle 8.14.3   ·   Android Gradle Plugin 8.13.0   ·   Kotlin 2.2.21
compileSdk / targetSdk 36 (Android 16)   ·   minSdk 24 (Android 7.0)
Compose BOM 2025.10.00   ·   Material 3   ·   DataStore 1.1.7
```

---

## 3. Open the project in Android Studio

1. Start Android Studio → **Open** (not "New Project").
2. Select the folder that contains this `README.md` (the folder with `settings.gradle.kts`) → **OK**.
3. Wait for the **Gradle sync** at the bottom. The first sync downloads Gradle 8.14.3 and the
   libraries — this can take several minutes. You are done when the message
   *"Gradle sync finished"* appears and the **Run** button (green ▶) becomes clickable.
4. If Android Studio offers to upgrade the Gradle plugin or Kotlin version — you can say **No**. The
   pinned versions are known to work well together.

> **Tip:** open `app/src/main/java/com/farooq/diceroll/ui/screens/Previews.kt` and switch the editor
> to **Split** or **Design** view to see the app without running it.

---

## 4. Run it on your phone

**On a physical phone**

1. On the phone: **Settings → About phone → tap "Build number" 7 times** to unlock developer
   options.
2. **Settings → System → Developer options → enable "USB debugging"**.
3. Connect the phone with a USB cable, tap **Allow** on the phone when it asks for permission.
4. In Android Studio pick your phone in the device dropdown (top bar) and press **▶ Run**.

**On an emulator:** *Device Manager → Create device → Pixel → system image Android 14/15/16 → Run*.

The app opens on the splash screen and goes straight to the dice.

---

## 5. Run the automatic tests

The project ships 64 JVM unit tests covering the dice logic, the two dice mode, the history
encoding, the statistics and the ViewModel (tap safety, restart persistence, settings):

```bash
# in the project folder (Mac/Linux) — or use the "test" Gradle task in Android Studio
./gradlew test
```

On Windows use `gradlew.bat test`.

The HTML report lands in `app/build/reports/tests/testDebugUnitTest/index.html`.
In Android Studio you can also right-click `app/src/test` → **Run 'Tests in …'**.

---

## 6. Build a signed APK / AAB for Google Play

Google Play wants an **.aab** (Android App Bundle). An **.apk** is handy for sending the app to a
friend or installing it yourself. Both need a **keystore** — the file that proves the app is yours.

### 6.1 Create your keystore (once, keep it forever)

```bash
keytool -genkeypair -v \
  -keystore upload-keystore.jks \
  -alias upload -keyalg RSA -keysize 2048 -validity 10000
```

`keytool` comes with Android Studio's JDK (`.../Android Studio/jbr/bin/keytool`). Answer the
questions, and **choose a password you will not forget**. Put the file somewhere safe and outside
this project — losing it means you can never update your app on Play again.

(The `.gitignore` already blocks `*.jks`, `*.keystore` and `keystore.properties`, so secrets never
end up in Git.)

### 6.2 The easy way — Android Studio (recommended for beginners)

1. **Build → Generate Signed App Bundle / APK…**
2. Choose **Android App Bundle** (for Play) or **APK** (for direct install).
3. Click **Create new…** or **Choose existing…** and select your `upload-keystore.jks`, enter the
   alias (`upload`) and the passwords.
4. Pick the **release** build variant and **Finish**.
5. Android Studio shows a link to the finished file when it is done, typically
   `app/release/app-release.aab` or `app/release/app-release.apk`.

### 6.3 The command line way

Create a file called `keystore.properties` in the project root (it is ignored by Git):

```properties
storeFile=/absolute/path/to/upload-keystore.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=upload
keyPassword=YOUR_KEY_PASSWORD
```

Then add the signing config to `app/build.gradle.kts` (inside `android { }`):

```kotlin
import java.util.Properties
import java.io.FileInputStream

val keystoreProperties = Properties().apply {
    val file = rootProject.file("keystore.properties")
    if (file.exists()) FileInputStream(file).use { load(it) }
}

android {
    // ... everything that is already there ...

    signingConfigs {
        create("release") {
            if (keystoreProperties.isNotEmpty()) {
                storeFile = file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            // isMinifyEnabled / isShrinkResources are already set in this project
        }
    }
}
```

Then build:

```bash
./gradlew bundleRelease     # -> app/build/outputs/bundle/release/app-release.aab
./gradlew assembleRelease   # -> app/build/outputs/apk/release/app-release.apk
```

### 6.4 Upload to Play Console

1. <https://play.google.com/console> → **Create app** → name "Dice Roll", type *App*, category
   *Games → Casual*.
2. Complete the **App content** checklist (privacy policy is optional here: the app collects
   nothing; you can state "No data collected" in *Data safety*).
3. **Testing → Internal testing → Create new release** → upload `app-release.aab`.
4. Add yourself as a tester, install from the Play link, and after everything looks good promote the
   release to **Production**.

Bump `versionCode` (and `versionName`) in `app/build.gradle.kts` before every future upload.

---

## 7. How the code is organised

```
Calling-app/                            ← the project root you opened in Android Studio
├── gradlew, gradlew.bat, gradle/       ← the Gradle wrapper (build tool; do not edit by hand)
├── settings.gradle.kts, build.gradle.kts, gradle.properties
├── tools/                              ← Python scripts that generated the icons and sounds
├── docs/                               ← pictures used by this README
└── app/
    ├── build.gradle.kts                ← app name, SDK versions, dependencies
    ├── proguard-rules.pro              ← release shrinking rules (almost empty on purpose)
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml     ← permissions (only VIBRATE), activity, launcher icon
        │   ├── java/com/farooq/diceroll/
        │   │   ├── MainActivity.kt            ← the only Activity: splash screen + Compose
        │   │   ├── DiceRollApplication.kt     ← app-wide startup, holds the container
        │   │   ├── di/AppContainer.kt         ← builds the objects once, hands them out
        │   │   ├── domain/                    ← pure logic, no Android code (easy to test)
        │   │   │   ├── model/                 ← DiceFaces, DiceMode, RollRecord, AppSettings
        │   │   │   ├── roll/                  ← DiceRoller (the random numbers), RollTiming
        │   │   │   ├── history/               ← HistoryCodec (save format), HistoryStats
        │   │   │   └── feedback/              ← SoundController / HapticController interfaces
        │   │   ├── data/                      ← saving to the phone with DataStore
        │   │   ├── platform/                  ← SoundPool & Vibrator implementations
        │   │   └── ui/                        ← everything you see on screen
        │   │       ├── DiceRollApp.kt         ← theme + top bar + bottom navigation + tabs
        │   │       ├── components/            ← dice drawing, buttons, cards, dialogs
        │   │       ├── screens/               ← RollScreen, HistoryScreen, SettingsScreen
        │   │       ├── roll/RollViewModel.kt  ← the brain: state, rolls, saving
        │   │       └── theme/                 ← colours, gradients, fonts, shapes
        │   └── res/
        │       ├── drawable/                  ← icons (vector XML, added by tools/fetch_icons.py)
        │       ├── mipmap-*/                  ← launcher icon in every size (tools/make_icons.py)
        │       ├── raw/                       ← dice_roll.wav and dice_land.wav (tools/make_sound.py)
        │       └── values/                    ← strings, colours, themes
        └── test/java/com/farooq/diceroll/     ← the 64 unit tests
```

The rule of thumb while reading: **`ui/` draws, `ui/roll/RollViewModel.kt` decides, `domain/`
calculates, `data/` saves.** `domain/` knows nothing about Android, which is why the tests can run
on a plain computer.

---

## 8. How it works inside

1. You tap the dice → `RollScreen` calls `RollViewModel.roll()`.
2. The ViewModel immediately ignores the tap if a roll is already running (no double rolls), picks
   the final value with `DiceRoller` (Kotlin `Random`, 1–6), and starts a coroutine.
3. For 900 ms the ViewModel emits changing "teaser" faces every 75 ms — the dice animation in
   `AnimatedDice` spins and hops while the face changes.
4. The final face is shown, the result is written to the history (DataStore), and the sound /
   vibration play if those switches are on.
5. `RollState` (an immutable data class) is all the screen needs: `faces`, `isRolling`,
   `lastRecord`. Compose redraws automatically when it changes.
6. Settings and history are exposed as `StateFlow`s, so the UI also updates when something changes
   in the background. Everything is collected lifecycle-aware, so nothing leaks when the app goes
   to the background.

Battery-friendly by design: the only continuous animation is the gentle floating of the dice, and
it stops when the app is not visible.

---

## 9. Customising the app

**Colours:** open `app/src/main/java/com/farooq/diceroll/ui/theme/Color.kt` (single colours) and
`Gradients.kt` (backgrounds, cards, button). Change `Orange500` for a blue button, for example.

**Text:** `app/src/main/res/values/strings.xml` — all the words in the app, in one place.

**Dice sound:** replace `app/src/main/res/raw/dice_roll.wav` with your own wave file (keep the same
name), or regenerate the generated ones with `python3 tools/make_sound.py`.

**Launcher icon / in-app icons:** `python3 tools/make_icons.py` draws the launcher icon (white dice
on a navy-to-purple background) for every screen density; `python3 tools/fetch_icons.py` re-downloads
the Material Symbols vector drawables. Both scripts need Python 3 and Pillow
(`pip install pillow`).

**App name / package:** the name is `app/src/main/res/values/strings.xml` → `app_name`. The package
is `com.farooq.diceroll`; changing it means renaming the folders under
`app/src/main/java/com/farooq/`, the `namespace` + `applicationId` in `app/build.gradle.kts`, and the
imports — Android Studio's *Refactor → Rename* does most of it for you.

---

## 10. Manual test checklist

The 64 automatic tests cover the logic (numbers, history, settings, tap safety). A few things can
only be checked on a real device — two minutes with this list:

- [ ] The app starts on face **1** (never 0 or 7).
- [ ] Tap the dice → it spins, faces change, and it ends on a value between 1 and 6.
- [ ] Tap **ROLL DICE** → same behaviour, and the result text matches the dice face.
- [ ] Tap very fast several times → only one roll happens.
- [ ] Switch to **2 Dice** → two dice, individual values and a total between 2 and 12.
- [ ] Roll a few times → **Recent rolls** on the Roll tab and the full list on the **History** tab
      show the same results, newest first.
- [ ] **Settings → Sound off** → no rattle. **Vibration off** → no buzz.
- [ ] **Settings → Theme → Light** → the app turns light and stays light after a restart.
- [ ] Close the app completely (swipe it away) and open it again → history, sound, vibration, theme
      and dice mode are all still there.
- [ ] **History → Clear history** → asks "are you sure?" first; Cancel keeps everything, Clear
      empties the list.
- [ ] Rotate the phone and use a small screen → nothing is cut off or overlapping.

---

## 11. Troubleshooting

| Problem | Solution |
| --- | --- |
| "Gradle sync failed" on first open | Check your internet connection — the first sync downloads Gradle and the libraries. Then *File → Sync Project with Gradle Files*. |
| Sync fails with a Java version error | *File → Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK* → pick the **jbr-21** / bundled JDK. |
| `./gradlew` says "Permission denied" (Mac/Linux) | Run `chmod +x gradlew` once. |
| `./gradlew` reports a checksum/download error | Delete the `~/.gradle/wrapper/dists/gradle-8.14.3-bin` folder and build again. |
| "Unsupported Android Gradle Plugin version" | Update Android Studio to 2025.1.3 (Narwhal 3 Feature Drop) or newer. |
| The release build failed after adding libraries | R8 sometimes needs keep rules — add them to `app/proguard-rules.pro`. |
| Sound is missing | Check the phone's media volume and *Settings → Sound* inside the app. On silent mode Android also blocks sound. |
| Vibration does nothing | The phone may have no vibrator, or *Settings → Sound & vibration → Vibration* is off system-wide. |

---

## 12. Privacy

- **No internet permission** at all — the app cannot send anything anywhere, even if it wanted to.
- **No accounts, no ads, no analytics.**
- The only permission is `VIBRATE`, which Android grants automatically without a dialog.
- Your roll history and settings never leave the phone; they live in the app's private DataStore
  file and disappear when you clear the history or uninstall the app.

---

## Appendix — how this project was verified

Everything in `domain/`, `data/`, `ui/roll/` and `src/test/` was compiled and executed with a plain
Kotlin/JVM toolchain: **64 unit tests, 64 passed, 0 failed**. The Compose UI layer
(`ui/components/`, `ui/screens/`, `ui/theme/`, `MainActivity.kt`) was checked with a static audit
(every referenced resource exists, every import resolves, format arguments match the strings,
braces/parentheses balanced, all Compose APIs used are stable ones), but it was **not** compiled —
the machine used to build this project had no Android SDK. Please run

```bash
./gradlew test assembleDebug
```

once in Android Studio to confirm the full Android build end-to-end; the whole test suite and the
first build take a few minutes on a normal laptop.

Happy rolling! 🎲
