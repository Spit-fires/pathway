import type { Device } from './stores';

interface SmsResponse {
    status: 'sent' | 'failed';
    reason?: string;
}

interface UssdResponse {
    status: 'success' | 'failed';
    result?: string;
    reason?: string;
}

function cleanIp(ip: string): string {
    return ip.trim()
        .replace(/^https?:\/\//, '')
        .replace(/\/+$/, '');
}

// Check if running in Tauri environment
function isTauri(): boolean {
    return typeof window !== 'undefined' && '__TAURI__' in window;
}

// HTTP request helper that uses Tauri's HTTP plugin when available (bypasses CORS)
async function httpRequest<T>(
    url: string, 
    options: { 
        method: string; 
        headers?: Record<string, string>; 
        body?: string;
        timeout?: number;
    }
): Promise<{ ok: boolean; status: number; data?: T; error?: string }> {
    const timeout = options.timeout || 10000;
    
    if (isTauri()) {
        try {
            const { fetch } = await import('@tauri-apps/plugin-http');
            const response = await fetch(url, {
                method: options.method,
                headers: options.headers,
                body: options.body ? options.body : undefined,
                connectTimeout: timeout,
            });
            
            if (response.ok) {
                const data = await response.json() as T;
                return { ok: true, status: response.status, data };
            }
            return { ok: false, status: response.status, error: `HTTP ${response.status}` };
        } catch (error) {
            const message = error instanceof Error ? error.message : 'Unknown error';
            return { ok: false, status: 0, error: message };
        }
    } else {
        // Fallback to browser fetch for development/static builds
        try {
            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), timeout);
            
            const response = await fetch(url, {
                method: options.method,
                headers: options.headers,
                body: options.body,
                signal: controller.signal
            });
            clearTimeout(timeoutId);
            
            if (response.ok) {
                const data = await response.json() as T;
                return { ok: true, status: response.status, data };
            }
            return { ok: false, status: response.status, error: `HTTP ${response.status}` };
        } catch (error) {
            let message = error instanceof Error ? error.message : 'Unknown error';
            if (error instanceof TypeError) {
                message = 'Connection failed (CORS or Network Error). Use the Tauri desktop app for full functionality.';
            }
            return { ok: false, status: 0, error: message };
        }
    }
}

export async function sendSms(device: Device, number: string, message: string): Promise<SmsResponse> {
    const url = `http://${cleanIp(device.ip)}/sms`;
    const result = await httpRequest<SmsResponse>(url, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${device.apiKey}`,
            'Content-Type': 'application/json; charset=utf-8'
        },
        body: JSON.stringify({ number, message }),
        timeout: 10000
    });

    if (result.ok && result.data) {
        return result.data;
    }
    
    console.error(`Failed to send SMS via ${device.name}:`, result.error);
    return { status: 'failed', reason: result.error };
}

export async function runUssd(device: Device, code: string): Promise<UssdResponse> {
    const url = `http://${cleanIp(device.ip)}/ussd`;
    const result = await httpRequest<UssdResponse>(url, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${device.apiKey}`,
            'Content-Type': 'application/json; charset=utf-8'
        },
        body: JSON.stringify({ code }),
        timeout: 15000
    });

    if (result.ok && result.data) {
        return result.data;
    }
    
    console.error(`Failed to run USSD on ${device.name}:`, result.error);
    return { status: 'failed', reason: result.error };
}

export async function checkDeviceStatus(device: Device): Promise<'online' | 'offline'> {
    const url = `http://${cleanIp(device.ip)}/`;
    const result = await httpRequest<unknown>(url, {
        method: 'GET',
        timeout: 3000
    });

    return result.ok ? 'online' : 'offline';
}
