# TrackerConnect
A modern Android application for tracking shipments and parcels built with Jetpack Compose, following Clean Architecture principles and Material Design 3 guidelines.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Android Studio**: Ladybug | 2024.2.1 or newer
- **JDK**: Version 17
- **Android SDK**: API 24+ (Android 7.0 Nougat or higher)
- **Git**: For cloning the repository

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/xolani-xd95/tracker-app.git
cd TrackerConnect
```

### 2. Open in Android Studio

1. Launch Android Studio
2. Select **"Open an Existing Project"**
3. Navigate to the cloned `TrackerConnect` directory
4. Click **"OK"**

### 3. Sync Gradle

Android Studio will automatically start syncing Gradle when you open the project. If it doesn't:

1. Click on **File → Sync Project with Gradle Files**
2. Wait for all dependencies to download (this may take a few minutes on first run)

### 4. Build the Project

```bash
# From terminal in project root
./gradlew build

# Or use Android Studio
# Click Build → Make Project
```

### 5. Run the Application

#### Using Android Studio

1. Connect an Android device via USB (with USB debugging enabled) **OR** start an Android emulator (API 24+)
2. Click the **"Run"** button (green play icon) in the toolbar
3. Select your target device
4. Click **"OK"**

#### Using Command Line

```bash
# Install on connected device/emulator
./gradlew installDebug

# Run the app
adb shell am start -n co.za.xdcodes.trackerconnect/.MainActivity
```

## Project Structure

```
TrackerConnect/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── assets/                 # Mock API data (JSON files)
│   │   │   ├── java/
│   │   │   │   └── co/za/xdcodes/trackerconnect/
│   │   │   │       ├── core/           # Shared functionality
│   │   │   │       │   ├── data/       # Data layer
│   │   │   │       │   ├── domain/     # Domain layer
│   │   │   │       │   ├── model/      # Domain models
│   │   │   │       │   ├── ui/         # Shared UI components
│   │   │   │       │   └── util/       # Utilities
│   │   │   │       ├── di/             # Dependency injection
│   │   │   │       ├── feature/        # Feature modules
│   │   │   │       │   ├── shipments/  # Shipments list screen
│   │   │   │       │   └── details/    # Shipment details screen
│   │   │   │       ├── navigation/     # Navigation setup
│   │   │   │       └── MainActivity.kt
│   │   │   ├── AndroidManifest.xml
│   │   │   └── res/                    # Resources
│   │   └── test/                       # Unit tests
│   └── build.gradle.kts                # App module build file
├── build.gradle.kts                    # Project build file
├── settings.gradle.kts                 # Gradle settings
├── gradle/libs.versions.toml           # Version catalog
├── README.md
└── SOLUTION.md                         # Technical documentation
```

## Configuration

### Gradle Configuration

The project uses Gradle Version Catalogs for dependency management. All versions are defined in `gradle/libs.versions.toml`.

#### Key Dependencies

- **Compose BOM**: 2024.12.01 (manages all Compose library versions)
- **Kotlin**: 2.1.0
- **Hilt**: 2.51.1
- **Room**: 2.6.1
- **Navigation Compose**: 2.8.5
- **Kotlin Serialization**: 1.7.3
- **Coroutines**: 1.9.0

### Build Variants

- **debug**: Development build with debugging enabled
- **release**: Production build (minification disabled by default)

To create a release build:

```bash
./gradlew assembleRelease
```

The APK will be located at: `app/build/outputs/apk/release/app-release.apk`

## Using the Application

### Adding a Tracking Number

1. Launch the app
2. Tap the **"+"** floating action button
3. Enter a tracking number from the list below
4. (Optional) Customize the carrier and title
5. Tap **"Add Tracking"**

The app will search the mock API (JSON file) for the tracking number and add it to your tracked shipments if found.

### Sample Tracking Numbers

Use these tracking numbers to test the application:

| Tracking Number        | Carrier           | Status            |
|------------------------|-------------------|-------------------|
| 1Z999AA10123456784     | Acme Express      | In Transit        |
| 9400111899223197428499 | Universal Post    | Delivered         |
| EV123456789CN          | SkyNet Logistics  | Out for Delivery  |
| JJ000123456US          | ParcelGo          | In Transit        |
| LV987654321US          | Acme Express      | Exception         |
| GM555555555US          | Universal Post    | Created           |

### Features

- **Track Multiple Shipments**: Add and monitor unlimited shipments
- **Search**: Filter shipments by tracking number, title, or carrier
- **Favorites**: Mark important shipments for quick access
- **Refresh**: Sync with mock API to get latest status updates
- **Details**: View comprehensive tracking information including checkpoints
- **Offline Support**: All tracked shipments are cached locally

## Troubleshooting

### Gradle Sync Failed

**Solution**:
1. Ensure you have a stable internet connection
2. Click **File → Invalidate Caches / Restart**
3. Delete `.gradle` and `.idea` folders, then reopen the project

### Build Failed - SDK Not Found

**Solution**:
1. Open **Tools → SDK Manager**
2. Ensure Android SDK API 36 is installed
3. Set ANDROID_HOME environment variable:
   ```bash
   export ANDROID_HOME=$HOME/Library/Android/sdk  # macOS/Linux
   set ANDROID_HOME=C:\Users\<YourUsername>\AppData\Local\Android\Sdk  # Windows
   ```

### App Crashes on Launch

**Solution**:
1. Check minimum SDK version: Device must be API 24+ (Android 7.0)
2. Clear app data: Settings → Apps → TrackerConnect → Clear Data
3. Reinstall the app

### Database Issues

**Solution**:
```bash
# Uninstall and reinstall to reset database
adb uninstall co.za.xdcodes.trackerconnect
./gradlew installDebug
```

## Tech Stack

### Core Technologies

- **Kotlin** - 100% Kotlin codebase
- **Jetpack Compose** - Declarative UI framework
- **Material Design 3** - Modern Material Design components
- **Coroutines & Flow** - Asynchronous programming

### Architecture

- **Clean Architecture** - Separation of concerns across layers
- **MVVM Pattern** - ViewModel-driven UI state management
- **Repository Pattern** - Single source of truth for data

### Key Libraries

- **Hilt** - Dependency injection
- **Room** - Local database persistence
- **Navigation Compose** - Type-safe navigation with @Serializable routes
- **Kotlin Serialization** - JSON parsing
- **ViewModel** - State management with lifecycle awareness

## Additional Resources

- **Technical Documentation**: See [SOLUTION.md](SOLUTION.md) for detailed architecture and design decisions
- **Jetpack Compose**: https://developer.android.com/jetpack/compose
- **Material Design 3**: https://m3.material.io/

## License

This project is part of a coding assessment and is intended for educational purposes.

## Contact

**Author**: Xolani Dlamini

For questions or issues, please open an issue in the repository.

---

**Note**: This application uses mock JSON data stored in the `assets` folder to simulate an API. In a production environment, you would integrate with a real shipment tracking API.
