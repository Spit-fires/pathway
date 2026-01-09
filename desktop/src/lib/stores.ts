import { writable } from 'svelte/store';
import { browser } from '$app/environment';

export interface Device {
    id: string;
    name: string;
    ip: string;
    apiKey: string;
    status?: 'online' | 'offline' | 'unknown';
}

const storedDevices = browser ? JSON.parse(localStorage.getItem('devices') || '[]') : [];

export const devices = writable<Device[]>(storedDevices);

if (browser) {
    devices.subscribe((value) => {
        localStorage.setItem('devices', JSON.stringify(value));
    });
}
