# Pathway

Pathway is a robust, self-hosted SMS and USSD Gateway solution that turns your Android device into a local API server. It consists of a mobile gateway app and a desktop dashboard for management.

## Project Structure

- **`/phone`**: The Android Gateway application. Built with **SvelteKit** and **Capacitor**. It runs a local HTTP server on the device (`http://IP:8080`) to expose SMS sending and USSD execution capabilities to your network.
- **`/desktop`**: The Management Dashboard. Built with **SvelteKit** and **Capacitor**. Available as both a static web application and native Android app. Connects to the gateway to send campaigns, view logs, and manage the system.

## Key Features

### Android Gateway (`/phone`)

- **Rest API**: Simple endpoints for `/sms` and `/ussd`.
- **Dual SIM Support**: Intelligent SIM selection and global default settings.
- **Unicode Support**: Native support for Bangla and other Unicode languages (Multipart SMS).
- **24/7 Persistence**:
  - **Keep Screen On**: Prevents device sleep while the server is running.
  - **Battery Optimization**: Built-in request to bypass Android's aggressive background killing.
- **Test Console**: In-app console to verify API functionality immediately.

### Desktop Dashboard (`/desktop`)

- **Multi-Device Support**: Connect and manage multiple Android phone gateways simultaneously.
- **Real-time Status**: Automatic background polling shows you exactly which devices are `Online` or `Offline`.
- **Smart SMS Distribution**:
  - When selecting multiple devices for a message, the client uses **Round-Robin Load Balancing**.
  - If you have 100 numbers and 2 devices, it sends 50 through Device A and 50 through Device B.
  - This speeds up bulk sending and prevents carrier rate-limiting on a single SIM.
- **Activity Logs**: detailed per-device delivery logs.
- **Modern Aesthetics**: Premium dark-mode UI built with Svelte 5 and Shadcn.

## Getting Started

### Prerequisites

- Node.js 22+
- pnpm
- Java 17+ (for Android builds)

### 1. Android Gateway (`/phone`)

```bash
cd phone
pnpm install

# Run on Android Device (requires USB debugging)
pnpm run dev:android
```

Once running on the phone:

1. Tap **Start Server**.
2. Note the IP address and Port (default `8080`).
3. Configure your Default SIM in the "SIM Configuration" card.
4. Use the "Power" and "Zap" icons to ensure the app stays running in the background.

### 2. Desktop Client (`/desktop`)

#### Web Version (Static)

```bash
cd desktop
pnpm install

# Start Development Server
pnpm run dev

# Build for production (static site)
pnpm run build
```

#### Android Version

```bash
cd desktop
pnpm install

# Run on Android Device (requires USB debugging)
pnpm run dev:android
```

The desktop client can now run as a native Android app using Capacitor, in addition to the traditional static web build.

## API Usage

The Gateway exposes a standardized JSON API.

### Send SMS

**POST** `/sms`

```json
{
  "number": "01700000000",
  "message": "Hello from Pathway!"
}
```

### Run USSD

**POST** `/ussd`

```json
{
  "code": "*124#"
}
```

_Note: The API uses the app's globally configured "Preferred SIM"._

## Automated Builds (CI/CD)

GitHub Actions are configured to automatically build artifacts on push:

- **Phone Android App**: Pushing to `/phone` builds a Debug APK (`phone-app-debug.apk`).
- **Desktop Static Site**: Pushing to `/desktop` builds a static site zip (`desktop-build.zip`).
- **Desktop Android App**: Pushing to `/desktop` also builds an Android APK (`desktop-app-debug.apk`).

## Creating Releases

Releases are automatically created when you push a version tag. To create a new release:

1. **Create a version tag** following semantic versioning (e.g., `v1.0.0`, `v1.2.3`):

   ```bash
   git tag v1.0.0
   ```

2. **Push the tag** to GitHub:

   ```bash
   git push origin v1.0.0
   ```

3. The release workflow will automatically:
   - Build the Phone Android APK (`phone-app-debug.apk`)
   - Build the Desktop Android APK (`desktop-app-debug.apk`)
   - Build the Desktop static site (`desktop-build.zip`)
   - Create a GitHub Release with auto-generated release notes from commits
   - Attach all artifacts to the release for download

### Release Artifacts

Each release includes:

| Artifact                  | Description                                                     |
| ------------------------- | --------------------------------------------------------------- |
| `phone-app-debug.apk`     | Android Gateway application (install directly on phone)         |
| `desktop-app-debug.apk`   | Desktop Dashboard Android app (install directly on phone)       |
| `desktop-build.zip`       | Desktop Dashboard static site (extract and host)                |

### Quick Release Commands

```bash
# Create and push a new release
git tag v1.0.0 && git push origin v1.0.0

# Create a pre-release version
git tag v1.0.0-beta.1 && git push origin v1.0.0-beta.1

# List all existing tags
git tag -l

# Delete a tag locally and remotely (if needed)
git tag -d v1.0.0
git push origin --delete v1.0.0
```
