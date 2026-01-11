import { writable } from 'svelte/store';
import { browser } from '$app/environment';

export interface Device {
    id: string;
    name: string;
    ip: string;
    apiKey: string;
    status?: 'online' | 'offline' | 'unknown';
}

export interface SmsCounter {
    [deviceId: string]: number;
}

const storedDevices = browser ? JSON.parse(localStorage.getItem('devices') || '[]') : [];
const storedSmsCounters = browser ? JSON.parse(localStorage.getItem('smsCounters') || '{}') : {};

export const devices = writable<Device[]>(storedDevices);
export const smsCounters = writable<SmsCounter>(storedSmsCounters);

if (browser) {
    devices.subscribe((value) => {
        localStorage.setItem('devices', JSON.stringify(value));
    });
    
    smsCounters.subscribe((value) => {
        localStorage.setItem('smsCounters', JSON.stringify(value));
    });
}

export function incrementSmsCounter(deviceId: string) {
    smsCounters.update(counters => ({
        ...counters,
        [deviceId]: (counters[deviceId] || 0) + 1
    }));
}

export function resetSmsCounter(deviceId: string) {
    smsCounters.update(counters => ({
        ...counters,
        [deviceId]: 0
    }));
}

export function resetAllSmsCounters() {
    smsCounters.set({});
}
