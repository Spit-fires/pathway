<script lang="ts">
    import './layout.css';
    import { onMount } from 'svelte';
    import Gateway from '$lib/GatewayPlugin';
    import { Capacitor } from '@capacitor/core';
    import { Radio } from '@lucide/svelte';
    import { version } from '../../package.json';

    let ipAddress = 'Detecting...';

    onMount(async () => {
        // 1. Permissions
        if (Capacitor.isNativePlatform()) {
            try {
                // Request critical permissions (SMS, Phone, Storage/Wifi)
                await Gateway.requestPermissions();
            } catch (e) {
                console.error('Native init error', e);
            }
        }

        try {
            const res = await Gateway.getLocalIpAddress();
            ipAddress = res.ip;
        } catch (e) {
            console.error('Failed to get IP', e);
            ipAddress = 'Unknown';
        }
    });
</script>

<div class="min-h-screen bg-background text-foreground flex flex-col font-sans selection:bg-primary/30 overflow-x-hidden">
    <!-- Header -->
    <header class="bg-card/50 backdrop-blur-md border-b border-border sticky top-0 z-50 px-4 py-3 flex justify-between items-center shadow-sm">
        <div class="flex items-center gap-2 min-w-0">
            <div class="w-8 h-8 rounded-lg bg-primary/10 border border-primary/20 flex items-center justify-center text-primary shadow-sm shrink-0">
                <Radio class="w-4 h-4" />
            </div>
            <div class="min-w-0">
                <h1 class="font-bold text-base tracking-tight text-foreground truncate">Pathway <span class="text-primary font-light italic opacity-80">gateway</span></h1>
            </div>
        </div>
        <div class="text-[10px] font-bold text-muted-foreground font-mono px-2 py-1 rounded-md bg-secondary border border-secondary shrink-0">
           {ipAddress}
        </div>
    </header>

    <!-- Main Content -->
    <main class="flex-1 overflow-x-hidden overflow-y-auto p-4 w-full max-w-3xl mx-auto flex flex-col justify-center">
        <slot />
    </main>

    <!-- Footer -->
    <footer class="p-4 text-center text-xs text-muted-foreground/60 border-t border-border mt-auto">
        <p class="font-mono">v{version} • Local Node • <a href="https://fh.js.cool" target="_blank" rel="noopener noreferrer" class="text-primary hover:underline">fh.js.cool</a></p>
    </footer>
</div>
