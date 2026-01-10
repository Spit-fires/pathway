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

// Check if running in Tauri environment (Tauri 2.x compatible)
function isTauri(): boolean {
    if (typeof window === 'undefined') return false;
    // Check for Tauri 2.x internals - this is the primary check
    // @ts-expect-error - Tauri injects these globals
    return !!(window.__TAURI_INTERNALS__ || window.__TAURI__);
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
            console.log(`[Tauri] Loading HTTP plugin...`);
            const { fetch: tauriFetch } = await import('@tauri-apps/plugin-http');
            console.log(`[Tauri] Making request to: ${url}`);
            
            // Build fetch options compatible with Tauri HTTP plugin
            const fetchOptions: RequestInit = {
                method: options.method,
                headers: options.headers,
            };
            
            // Only add body for non-GET requests
            if (options.method !== 'GET' && options.body) {
                fetchOptions.body = options.body;
            }
            
            const response = await tauriFetch(url, fetchOptions);
            
            console.log(`[Tauri] Response status: ${response.status}`);
            
            if (response.ok) {
                const text = await response.text();
                console.log(`[Tauri] Response body: ${text}`);
                try {
                    const data = JSON.parse(text) as T;
                    return { ok: true, status: response.status, data };
                } catch {
                    // Response is not JSON, return as-is for status check
                    return { ok: true, status: response.status };
                }
            }
            return { ok: false, status: response.status, error: `HTTP ${response.status}` };
        } catch (error: unknown) {
            // Enhanced error logging
            console.error('[Tauri] HTTP request failed:', error);
            let message = 'Unknown error';
            if (error instanceof Error) {
                message = error.message;
                console.error('[Tauri] Error name:', error.name);
                console.error('[Tauri] Error stack:', error.stack);
            } else if (typeof error === 'string') {
                message = error;
            } else if (error && typeof error === 'object') {
                message = JSON.stringify(error);
            }
            
            // Try browser fetch as fallback
            console.log('[Tauri] Falling back to browser fetch...');
            return browserFetch<T>(url, options, timeout);
        }
    } else {
        console.log(`[Browser] Making request to: ${url}`);
        return browserFetch<T>(url, options, timeout);
    }
}

// Browser fetch fallback
async function browserFetch<T>(
    url: string,
    options: { 
        method: string; 
        headers?: Record<string, string>; 
        body?: string;
    },
    timeout: number
): Promise<{ ok: boolean; status: number; data?: T; error?: string }> {
    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), timeout);
        
        const response = await fetch(url, {
            method: options.method,
            headers: options.headers,
            body: options.body,
            signal: controller.signal,
            mode: 'cors'
        });
        clearTimeout(timeoutId);
        
        if (response.ok) {
            const text = await response.text();
            try {
                const data = JSON.parse(text) as T;
                return { ok: true, status: response.status, data };
            } catch {
                return { ok: true, status: response.status };
            }
        }
        return { ok: false, status: response.status, error: `HTTP ${response.status}` };
    } catch (error) {
        let message = error instanceof Error ? error.message : 'Unknown error';
        if (error instanceof TypeError) {
            message = 'Connection failed (CORS or Network Error). Ensure the gateway device is reachable and running.';
        }
        console.error('[Browser] Fetch failed:', error);
        return { ok: false, status: 0, error: message };
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
