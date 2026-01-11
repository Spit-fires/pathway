# Pathway Architecture

## System Overview

```
┌─────────────────────────────────────────────────────────────┐
│                     Desktop Dashboard                        │
│                   (SvelteKit + Tauri)                       │
│                                                              │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐           │
│  │  Campaign  │  │   Device   │  │  Activity  │           │
│  │ Management │  │ Management │  │    Logs    │           │
│  └────────────┘  └────────────┘  └────────────┘           │
│                                                              │
│         Round-Robin Load Balancing & Device Polling         │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       │ HTTP API Calls
                       │ (Local Network)
                       │
        ┌──────────────┼──────────────┐
        │              │              │
        ▼              ▼              ▼
┌───────────┐  ┌───────────┐  ┌───────────┐
│  Device 1 │  │  Device 2 │  │  Device N │
│           │  │           │  │           │
│  ┌─────┐  │  │  ┌─────┐  │  │  ┌─────┐  │
│  │ SIM1│  │  │  │ SIM1│  │  │  │ SIM1│  │
│  │ SIM2│  │  │  │ SIM2│  │  │  │ SIM2│  │
│  └─────┘  │  │  └─────┘  │  │  └─────┘  │
│           │  │           │  │           │
│  HTTP     │  │  HTTP     │  │  HTTP     │
│  Server   │  │  Server   │  │  Server   │
│  :8080    │  │  :8080    │  │  :8080    │
└───────────┘  └───────────┘  └───────────┘
Android Gateway  Android Gateway  Android Gateway
(SvelteKit +    (SvelteKit +     (SvelteKit +
 Capacitor)      Capacitor)       Capacitor)
     │               │                │
     │               │                │
     ▼               ▼                ▼
┌─────────┐    ┌─────────┐     ┌─────────┐
│ Carrier │    │ Carrier │     │ Carrier │
│ Network │    │ Network │     │ Network │
└─────────┘    └─────────┘     └─────────┘
```

## Communication Flow

### 1. SMS Sending Workflow

```
Desktop Dashboard
       │
       │ 1. User selects recipients and message
       │
       ▼
   Load Balancer
       │
       │ 2. Distributes messages across devices
       │    (Round-robin algorithm)
       │
       ├────────────┬────────────┐
       │            │            │
       ▼            ▼            ▼
   Device 1     Device 2     Device 3
   (33 msgs)    (33 msgs)    (34 msgs)
       │            │            │
       │ 3. HTTP POST /sms
       │
       ▼            ▼            ▼
   SIM Card     SIM Card     SIM Card
       │            │            │
       │ 4. Send via carrier
       │
       ▼            ▼            ▼
  Recipients   Recipients   Recipients
```

### 2. Device Status Monitoring

```
Desktop Dashboard
       │
       │ Background polling every X seconds
       │
       ├─────────┬─────────┬─────────┐
       │         │         │         │
       ▼         ▼         ▼         ▼
   GET /     GET /     GET /     GET /
   Device 1  Device 2  Device 3  Device 4
       │         │         │         │
       │ Response with status
       │
       ▼         ▼         ▼         ▼
   Update UI Status:
   🟢 Online  🟢 Online  🔴 Offline  🟢 Online
```

## Technology Stack

### Android Gateway (Phone)
- **Frontend Framework**: SvelteKit 2.x
- **Mobile Runtime**: Capacitor 8.x
- **HTTP Server**: Built-in Capacitor HTTP server
- **UI Components**: Tailwind CSS 4.x, Shadcn-Svelte
- **Build Tool**: Vite 7.x

### Desktop Dashboard
- **Frontend Framework**: SvelteKit 2.x
- **Desktop Runtime**: Tauri 2.x (Rust)
- **HTTP Client**: Tauri HTTP Plugin
- **UI Components**: Tailwind CSS 4.x, Shadcn-Svelte, Mode Watcher
- **Build Tool**: Vite 7.x

## Data Flow

### Device Registration
1. User starts server on Android gateway
2. Gateway displays IP address and port
3. User manually adds device to desktop dashboard
4. Desktop stores device configuration locally

### Message Sending
1. User composes message in desktop dashboard
2. User selects target numbers and devices
3. Desktop splits recipients across selected devices
4. Desktop sends HTTP POST requests to each device
5. Each device processes requests and sends SMS via SIM
6. Responses logged in desktop activity log

### Device Health Check
1. Desktop polls each registered device periodically
2. Timeout/error marks device as offline
3. Successful response marks device as online
4. UI updates status indicators in real-time

## Security Considerations

- **Local Network Only**: All communication happens on local network by default
- **No Authentication**: Current version has no built-in authentication
- **Unencrypted**: HTTP communication is not encrypted
- **Permissions**: Android app requires SMS and Phone permissions

## Performance Characteristics

- **Concurrent Devices**: Supports multiple devices simultaneously
- **Load Distribution**: Round-robin ensures balanced load
- **Network Latency**: Depends on local network speed
- **SMS Throughput**: Limited by carrier rate limits per SIM
- **Background Operation**: Requires battery optimization bypass on Android
