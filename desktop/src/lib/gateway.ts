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

export async function sendSms(device: Device, number: string, message: string): Promise<SmsResponse> {
    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 10000); // 10s timeout

        const response = await fetch(`http://${cleanIp(device.ip)}/sms`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${device.apiKey}`,
                'Content-Type': 'application/json; charset=utf-8'
            },
            body: JSON.stringify({ number, message }),
            signal: controller.signal
        });
        clearTimeout(timeoutId);

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error(`Failed to send SMS via ${device.name}:`, error);
        let reason = error instanceof Error ? error.message : 'Unknown error';
        if (error instanceof TypeError) {
            reason = 'Connection failed (CORS or Network Error). Ensure the app is running and CORS is enabled.';
        }
        return { status: 'failed', reason };
    }
}

export async function runUssd(device: Device, code: string): Promise<UssdResponse> {
    try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 15000); // 15s timeout for USSD

        const response = await fetch(`http://${cleanIp(device.ip)}/ussd`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${device.apiKey}`,
                'Content-Type': 'application/json; charset=utf-8'
            },
            body: JSON.stringify({ code }),
            signal: controller.signal
        });
        clearTimeout(timeoutId);

        if (!response.ok) {
           throw new Error(`HTTP ${response.status}`);
        }

        return await response.json();
    } catch (error) {
         console.error(`Failed to run USSD on ${device.name}:`, error);
         let reason = error instanceof Error ? error.message : 'Unknown error';
         if (error instanceof TypeError) {
             reason = 'Connection failed (CORS or Network Error). Ensure the app is running and CORS is enabled.';
         }
        return { status: 'failed', reason };
    }
}

export async function checkDeviceStatus(device: Device): Promise<'online' | 'offline'> {
     try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 3000); // 3s timeout for ping

        const response = await fetch(`http://${cleanIp(device.ip)}/`, {
            method: 'GET',
            signal: controller.signal
        });
        clearTimeout(timeoutId);

        if (response.ok) {
            return 'online';
        }
        return 'offline';
    } catch (error) {
        return 'offline';
    }
}
