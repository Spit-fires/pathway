import { Preferences } from '@capacitor/preferences';

const KEY_API_KEY = 'gateway_api_key';
const KEY_PORT = 'gateway_port';

export const config = {
    getApiKey: async (): Promise<string> => {
        const { value } = await Preferences.get({ key: KEY_API_KEY });
        return value || 'pathway-secret-key'; // Default
    },

    setApiKey: async (key: string) => {
        await Preferences.set({ key: KEY_API_KEY, value: key });
    },

    getPort: async (): Promise<number> => {
        const { value } = await Preferences.get({ key: KEY_PORT });
        return value ? parseInt(value) : 8080;
    },

    setPort: async (port: number) => {
        await Preferences.set({ key: KEY_PORT, value: port.toString() });
    }
};
