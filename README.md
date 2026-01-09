# Pathway

Pathway is a robust, self-hosted SMS and USSD Gateway solution that turns your Android device into a local API server. It consists of a mobile gateway app and a desktop dashboard for management.

## Project Structure

- **`/phone`**: The Android Gateway application. Built with **SvelteKit** and **Capacitor**. It runs a local HTTP server on the device (`http://IP:8080`) to expose SMS sending and USSD execution capabilities to your network.
- **`/desktop`**: The Management Dashboard. Built with **SvelteKit**. Connects to the gateway to send campaigns, view logs, and manage the system.

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

- Node.js 20+
- pnpm
- Java 17 (for Android builds)

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

```bash
cd desktop
pnpm install

# Start Development Server
pnpm run dev
```

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

- **Android App**: Pushing to `/phone` builds a Debug APK (`app-debug.apk`).
- **Desktop App**: Pushing to `/desktop` builds a static site zip (`desktop-build.zip`).
