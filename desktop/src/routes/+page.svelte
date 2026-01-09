<script lang="ts">
    import DeviceManager from '$lib/components/DeviceManager.svelte';
    import SmsSender from '$lib/components/SmsSender.svelte';
    import { devices, type Device } from '$lib/stores';
    import { Tabs, TabsContent, TabsList, TabsTrigger } from '$lib/components/ui/tabs';
    import { Toaster } from 'svelte-sonner';
    import { MessageSquare, Settings, Radio } from '@lucide/svelte';
    import { fly } from 'svelte/transition';
    import { onMount } from 'svelte';
    import { checkDeviceStatus } from '$lib/gateway';

    onMount(() => {
        const interval = setInterval(async () => {
            let currentDevices: Device[] = [];
            devices.subscribe(d => currentDevices = d)(); 
            
            if (currentDevices && currentDevices.length > 0) {
                for (const device of currentDevices) {
                    const status = await checkDeviceStatus(device);
                    if (device.status !== status) {
                        devices.update(list => list.map(d => 
                            d.id === device.id ? { ...d, status } : d
                        ));
                    }
                }
            }
        }, 5000); // Poll every 5 seconds

        return () => clearInterval(interval);
    });
</script>

<div class="min-h-screen bg-gradient-to-br from-background via-background/95 to-secondary/10 dark:from-background dark:via-background/90 dark:to-primary/5 p-6 md:p-8 font-sans selection:bg-primary/20">
    <div class="max-w-6xl mx-auto space-y-8">
        <header class="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-6 border-b border-border/40" in:fly={{ y: -20, duration: 500 }}>
            <div class="space-y-1">
                <h1 class="text-3xl md:text-4xl font-bold tracking-tight bg-clip-text text-transparent bg-gradient-to-r from-primary to-primary/60">
                    Pathway Gateway
                </h1>
                <p class="text-muted-foreground font-medium text-lg">
                    Desktop Client for Android SMS Gateway
                </p>
            </div>
             <div class="flex items-center gap-2 px-4 py-2 rounded-full bg-secondary/50 backdrop-blur-sm border border-secondary">
                <div class="w-2 h-2 rounded-full bg-green-500 animate-pulse"></div>
                <span class="text-sm font-medium text-secondary-foreground">System Online</span>
            </div>
        </header>

        <main in:fly={{ y: 20, duration: 500, delay: 200 }}>
            <Tabs value="send" class="space-y-6">
                <TabsList class="grid w-full grid-cols-2 max-w-[400px] bg-secondary/50 p-1 backdrop-blur-sm">
                    <TabsTrigger value="send" class="data-[state=active]:bg-background data-[state=active]:text-foreground data-[state=active]:shadow-sm transition-all duration-300">
                        <MessageSquare class="w-4 h-4 mr-2" />
                        Send SMS
                    </TabsTrigger>
                    <TabsTrigger value="devices" class="data-[state=active]:bg-background data-[state=active]:text-foreground data-[state=active]:shadow-sm transition-all duration-300">
                        <Settings class="w-4 h-4 mr-2" />
                        Manage Devices
                    </TabsTrigger>
                </TabsList>
                
                <TabsContent value="send" class="space-y-4 focus-visible:outline-none focus-visible:ring-0">
                    <SmsSender />
                </TabsContent>
                
                <TabsContent value="devices" class="space-y-4 focus-visible:outline-none focus-visible:ring-0">
                    <DeviceManager />
                </TabsContent>
            </Tabs>
        </main>
        
        <footer class="pt-12 text-center text-sm text-muted-foreground">
            <p>© 2026 Pathway Android Gateway. All rights reserved.</p>
        </footer>
    </div>
    <Toaster />
</div>

<style>
    @reference "tailwindcss";

    :global(body) {
        @apply antialiased;
    }
</style>
