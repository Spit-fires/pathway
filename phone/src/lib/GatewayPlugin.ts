import { registerPlugin, type PluginListenerHandle, type PermissionState } from '@capacitor/core';

export interface PermissionStatus {
  [pluginPermission: string]: PermissionState;
}

export interface DeviceInfo {
  manufacturer: string;
  model: string;
  brand: string;
  device: string;
  sdkVersion: number;
}

export interface GatewayPlugin {
  startServer(options: { port: number; apiKey: string }): Promise<{ success: boolean; message: string }>;
  stopServer(): Promise<{ success: boolean; message: string }>;
  getLocalIpAddress(): Promise<{ ip: string }>;
  sendSms(options: { number: string; message: string }): Promise<{ value: string }>;
  runUssd(options: { code: string }): Promise<{ value: string }>;
  
  // SIM Management
  getSims(): Promise<{ sims: { slot: number; subscriptionId: number; carrierName: string; number: string; displayName: string }[] }>;
  setPreferredSim(options: { slot: number }): Promise<void>;
  getPreferredSim(): Promise<{ slot: number }>;
  
  // Persistence & Lifecycle
  setKeepScreenOn(options: { on: boolean }): Promise<void>;
  exitApp(): Promise<void>;
  requestBatteryOpt(): Promise<{ status?: string } | void>;
  
  // OEM-specific battery settings (Realme, Xiaomi, OPPO, Samsung, etc.)
  openOemBatterySettings(): Promise<{ manufacturer: string; opened: boolean; fallback?: boolean }>;
  
  // Device info
  getDeviceInfo(): Promise<DeviceInfo>;
  
  // Events
  addListener(eventName: 'log', listenerFunc: (data: { message: string }) => void): Promise<PluginListenerHandle> & PluginListenerHandle;
  
  // Standard Capacitor Permissions
  checkPermissions(): Promise<PermissionStatus>;
  requestPermissions(): Promise<PermissionStatus>;
}

const Gateway = registerPlugin<GatewayPlugin>('Gateway');

export default Gateway;
