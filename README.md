<div align="center">

# 📱 Pathway

### Self-Hosted SMS & USSD Gateway

*Transform your Android device into a powerful local API server*

[![Version](https://img.shields.io/github/v/release/Spit-fires/pathway?label=version)](https://github.com/Spit-fires/pathway/releases)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Build Status](https://img.shields.io/github/actions/workflow/status/Spit-fires/pathway/release.yml?branch=main)](https://github.com/Spit-fires/pathway/actions)

</div>

---

## 🌟 Overview

**Pathway** is a robust, self-hosted SMS and USSD Gateway solution that turns your Android device into a local API server. It consists of two main components:

- 🔧 **Android Gateway App** - Runs directly on your phone
- 💻 **Desktop Dashboard** - Manage everything from your computer

Perfect for businesses, developers, and enthusiasts who need reliable SMS automation without depending on cloud services.

> 📖 **[View Architecture Documentation](docs/ARCHITECTURE.md)** for detailed system design and data flow diagrams.

## 📑 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
- [API Usage](#-api-usage)
- [Automated Builds](#-automated-builds-cicd)
- [Creating Releases](#-creating-releases)
- [Limitations](#-limitations)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🏗️ Project Structure

```
pathway/
├── 📱 phone/          # Android Gateway Application
│   └── Built with SvelteKit + Capacitor
│       Runs local HTTP server (http://IP:8080)
│
└── 💻 desktop/        # Management Dashboard
    └── Built with SvelteKit + Tauri
        Available as native desktop apps & web build
```

### Component Details

| Component | Technology | Description |
|-----------|-----------|-------------|
| **📱 `/phone`** | SvelteKit + Capacitor | Android Gateway app that runs a local HTTP server on your device to expose SMS and USSD capabilities |
| **💻 `/desktop`** | SvelteKit + Tauri | Management Dashboard for sending campaigns, viewing logs, and managing the system. Available for Windows, macOS, Linux |

---

## ✨ Key Features

### 📱 Android Gateway (`/phone`)

<table>
<tr>
<td width="50%">

#### 🔌 Core Functionality
- **REST API**: Simple endpoints for `/sms` and `/ussd`
- **Dual SIM Support**: Intelligent SIM selection with global defaults
- **Unicode Support**: Native support for Bangla and other Unicode languages (Multipart SMS)

</td>
<td width="50%">

#### ⚡ Reliability Features
- **24/7 Persistence**: Keep screen on to prevent device sleep
- **Battery Optimization**: Bypass Android's background process killing
- **Test Console**: In-app API verification tool

</td>
</tr>
</table>

### 💻 Desktop Dashboard (`/desktop`)

<table>
<tr>
<td width="50%">

#### 🖥️ Platform Support
- **Native Desktop Apps**: Built with Tauri for Windows, macOS, and Linux
- **No CORS Restrictions**: Direct communication with gateways
- **Multi-Device Support**: Connect and manage multiple Android gateways simultaneously

</td>
<td width="50%">

#### 🚀 Advanced Features
- **Real-time Status**: Automatic polling shows device Online/Offline status
- **Smart SMS Distribution**: Round-robin load balancing across devices
- **Activity Logs**: Detailed per-device delivery logs
- **Modern UI**: Premium dark-mode interface built with Svelte 5 and Shadcn

</td>
</tr>
</table>

#### 📊 Smart Distribution Example

When sending 100 messages with 2 connected devices:
```
Device A: 50 messages (Round-robin distribution)
Device B: 50 messages (Round-robin distribution)
```
✅ **Benefits**: Faster bulk sending & prevents carrier rate-limiting on single SIM

---

## 🚀 Getting Started

### 📋 Prerequisites

Before you begin, ensure you have the following installed:

| Requirement | Version | Purpose |
|------------|---------|---------|
| **Node.js** | 20+ | JavaScript runtime |
| **pnpm** | Latest | Package manager |
| **Java** | 17+ | Android builds |
| **Rust** | Latest | Tauri desktop builds |

### 📱 Step 1: Android Gateway Setup

```bash
# Navigate to phone directory
cd phone

# Install dependencies
pnpm install

# Run on Android Device (requires USB debugging enabled)
pnpm run dev:android
```

#### 📝 Configuration Steps

Once the app is running on your phone:

1. **🚀 Start Server**: Tap the "Start Server" button
2. **📡 Note Connection Details**: Check the IP address and Port (default: `8080`)
3. **📞 Configure SIM**: Set your default SIM in the "SIM Configuration" card
4. **🔋 Enable Background Running**: Use the "Power" and "Zap" icons to keep the app active

> **💡 Tip**: Make sure your phone and computer are on the same network for local API access.

### 💻 Step 2: Desktop Dashboard Setup

```bash
# Navigate to desktop directory
cd desktop

# Install dependencies
pnpm install

# Option 1: Start Web Development Server
pnpm run dev

# Option 2: Start Tauri Development (native desktop app)
pnpm run tauri:dev

# Option 3: Build Tauri Desktop App for production
pnpm run tauri:build
```

---

## 🔌 API Usage

The Gateway exposes a standardized JSON API for easy integration.

### 📤 Send SMS

**Endpoint**: `POST /sms`

**Request Body**:
```json
{
  "number": "01700000000",
  "message": "Hello from Pathway!"
}
```

**Example with cURL**:
```bash
curl -X POST http://192.168.1.100:8080/sms \
  -H "Content-Type: application/json" \
  -d '{
    "number": "01700000000",
    "message": "Hello from Pathway!"
  }'
```

### 📞 Run USSD

**Endpoint**: `POST /ussd`

**Request Body**:
```json
{
  "code": "*124#"
}
```

**Example with cURL**:
```bash
curl -X POST http://192.168.1.100:8080/ussd \
  -H "Content-Type: application/json" \
  -d '{
    "code": "*124#"
  }'
```

> **📝 Note**: The API uses the app's globally configured "Preferred SIM" setting.

---

## 🤖 Automated Builds (CI/CD)

GitHub Actions automatically build artifacts on push:

| Component | Trigger | Output |
|-----------|---------|--------|
| **📱 Android App** | Push to `/phone` | `app-debug.apk` (Debug APK via Capacitor) |
| **💻 Desktop App** | Push to `/desktop` | Static site zip + Native executables (Windows, macOS, Linux) |

---

## 📦 Creating Releases

Releases are automatically created when you push a version tag.

### Quick Start

1. **Create a version tag** (following semantic versioning):
   ```bash
   git tag v1.0.0
   ```

2. **Push the tag** to GitHub:
   ```bash
   git push origin v1.0.0
   ```

3. **Automatic Build** - The release workflow will:
   - ✅ Build Android APK (`app-debug.apk`)
   - ✅ Build Desktop static site (`desktop-build.zip`)
   - ✅ Build native apps for Windows, macOS, Linux
   - ✅ Create GitHub Release with auto-generated notes
   - ✅ Attach all artifacts for download

### 📥 Release Artifacts

Each release includes the following downloadable files:

| Artifact | Platform | Description |
|----------|----------|-------------|
| `app-debug.apk` | 🤖 Android | Gateway application (install directly) |
| `desktop-build.zip` | 🌐 Web | Static site (extract and host) |
| `pathway-desktop_*.AppImage` | 🐧 Linux | Portable executable (no installation) |
| `pathway-desktop_*.deb` | 🐧 Linux | Debian package |
| `Pathway Desktop.exe` | 🪟 Windows | Portable executable (no installation) |
| `Pathway Desktop_*-setup.exe` | 🪟 Windows | NSIS installer |
| `Pathway Desktop_*.dmg` | 🍎 macOS | Disk image |

### 🛠️ Quick Release Commands

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

---

## ⚠️ Limitations

### 📱 Android Gateway Limitations

| Limitation | Description | Workaround |
|------------|-------------|------------|
| **🔋 Battery & Power Management** | Android may kill the app in the background despite optimization settings | Keep device plugged in and disable battery optimization for the app |
| **📶 Network Requirements** | Both phone and desktop must be on the same local network | Use VPN or port forwarding for remote access |
| **🔌 Phone Must Stay Powered** | The device needs to remain on and connected to power for 24/7 operation | Use a dedicated device or ensure continuous power supply |
| **📱 Android Version** | Requires Android 6.0 (API level 23) or higher | Use a supported device |
| **🔐 Permissions** | Requires SMS and Phone permissions at runtime | Grant all requested permissions during setup |

### 💻 Desktop Dashboard Limitations

| Limitation | Description | Workaround |
|------------|-------------|------------|
| **🌐 Local Network Only** | Default setup works only on local network | Configure network routing or VPN for remote access |
| **📊 No SMS Reception** | Currently only supports sending SMS, not receiving | Future feature consideration |
| **🔄 Manual Device Management** | Devices must be manually added to the dashboard | Keep track of device IPs (consider static IP assignment) |
| **📱 No Cloud Sync** | All data is stored locally on the desktop app | Backup desktop app data regularly |

### 🔧 Technical Limitations

| Limitation | Description | Impact |
|------------|-------------|--------|
| **📤 Carrier Rate Limits** | Mobile carriers may throttle or block bulk SMS sending | Use multiple devices or spread sending over time |
| **💬 SMS Character Limits** | Standard SMS: 160 characters (Unicode: 70 characters) | Long messages automatically split into multiple parts |
| **📞 USSD Session Timeout** | USSD sessions may timeout based on carrier settings | Execute USSD codes promptly |
| **🔒 No End-to-End Encryption** | API communication is not encrypted by default | Use on trusted networks or implement HTTPS |
| **📊 No Built-in Analytics** | Limited analytics and reporting features | Export logs for external analysis |

### 🚧 Known Issues

- **Android 12+**: Some aggressive battery optimization requires manual intervention in device settings
- **Dual SIM**: SIM switching may have a slight delay depending on the device
- **Background Restrictions**: Some Android manufacturers (Xiaomi, Huawei, etc.) have additional restrictions that need to be disabled manually

> **💡 Tip**: For production use, we recommend using a dedicated Android device that stays powered and connected to ensure maximum reliability.

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

### Development Setup

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Built with [SvelteKit](https://kit.svelte.dev/)
- Mobile app powered by [Capacitor](https://capacitorjs.com/)
- Desktop app powered by [Tauri](https://tauri.app/)
- UI components from [Shadcn-Svelte](https://www.shadcn-svelte.com/)

---

<div align="center">

**Made with ❤️ by the Pathway Team**

[⭐ Star us on GitHub](https://github.com/Spit-fires/pathway) | [🐛 Report Bug](https://github.com/Spit-fires/pathway/issues) | [💡 Request Feature](https://github.com/Spit-fires/pathway/issues)

</div>

---
