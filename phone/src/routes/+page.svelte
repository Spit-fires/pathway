<script lang="ts">
    import { onMount, onDestroy } from 'svelte';
    import { fly } from 'svelte/transition';
    import { config } from '$lib/config';
    import Gateway from '$lib/GatewayPlugin';
    import { Button } from '$lib/components/ui/button';
    import { Input } from '$lib/components/ui/input';
    import { Label } from '$lib/components/ui/label';
    import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '$lib/components/ui/card';
    import { Eye, EyeOff, Play, Square, Trash2, Copy, Server, Radio, Activity, Globe, Settings, Power, Zap, Battery } from '@lucide/svelte';

    let logs: string[] = [];
    let isRunning = false;
    let apiKey = '';
    let port = 8080;
    let ipAddress = '0.0.0.0';
    let url = '';
    let isVisible = false;
    let listenerParams: any = null;
    let deviceManufacturer = '';

    function addLog(msg: string) {
        logs = [`[${new Date().toLocaleTimeString()}] ${msg}`, ...logs].slice(0, 50);
    }

    // onMount moved to bottom


    onDestroy(() => {
        if (listenerParams) {
            listenerParams.remove();
        }
    });

    function updateUrl() {
        url = `http://${ipAddress}:${port}`;
    }

    async function toggleServer() {
        if (isRunning) {
            try {
                await Gateway.setKeepScreenOn({ on: false });
                const res = await Gateway.stopServer();
                addLog(res.message);
                isRunning = false;
            } catch (e: any) {
                addLog(`Error stop: ${e.message}`);
            }
        } else {
            try {
                // Save config first
                await config.setApiKey(apiKey);
                await config.setPort(port);
                
                await Gateway.setKeepScreenOn({ on: true });
                const res = await Gateway.startServer({ port, apiKey });
                addLog(res.message);
                if (res.success) {
                    isRunning = true;
                    updateUrl();
                }
            } catch (e: any) {
                addLog(`Error start: ${e.message}`);
            }
        }
    }

    function toggleVisibility() {
        isVisible = !isVisible;
    }

    // Test Console State
    let testTab: 'sms' | 'ussd' = 'sms';
    // selectedSim removed
    let testPhone = '';
    let testMsg = '';
    let testCode = '';
    let testLoading = false;
    let testResult = '';

    async function callApi(endpoint: string, body: any) {
        testLoading = true;
        testResult = '';
        try {
             // Use 127.0.0.1 for local loopback on device
            const res = await fetch(`http://127.0.0.1:${port}${endpoint}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json; charset=utf-8',
                    'Authorization': `Bearer ${apiKey}`
                },
                body: JSON.stringify({ ...body })
            });
            const data = await res.json();
            testResult = JSON.stringify(data, null, 2);
            if (data.status === 'failed') {
                 addLog(`Test Failed: ${data.reason}`);
            } else {
                 addLog(`Test Success: ${endpoint}`);
            }
        } catch (e: any) {
             testResult = `Error: ${e.message}`;
             addLog(`Test Error: ${e.message}`);
        } finally {
            testLoading = false;
        }
    }

    function sendTestSms() {
        if (!testPhone || !testMsg) return;
        callApi('/sms', { number: testPhone, message: testMsg });
    }

    function sendTestUssd() {
        if (!testCode) return;
        callApi('/ussd', { code: testCode });
    }

    function copyUrl() {
        navigator.clipboard.writeText(url);
        addLog('URL copied to clipboard');
    }

    // SIM State
    let sims: { slot: number; subscriptionId: number; carrierName: string; number: string; displayName: string }[] = [];
    let preferredSimSlot = -1;

    onMount(async () => {
        // ... previous onMount logic ...
        apiKey = await config.getApiKey();
        port = await config.getPort();
        
        try {
            const ipRes = await Gateway.getLocalIpAddress();
            ipAddress = ipRes.ip;
        } catch (e) {
            console.error(e);
        }
        updateUrl();
        
        // Get device info
        try {
            const deviceInfo = await Gateway.getDeviceInfo();
            deviceManufacturer = deviceInfo.manufacturer;
            addLog(`Device: ${deviceInfo.manufacturer} ${deviceInfo.model}`);
        } catch (e) {
            console.error("Failed to get device info", e);
        }

        // Load SIMs
        try {
            const simRes = await Gateway.getSims();
            sims = simRes.sims;
            const prefRes = await Gateway.getPreferredSim();
            preferredSimSlot = prefRes.slot;
        } catch (e) {
            console.error("Failed to load SIMs", e);
            // Permission might be needed first
            await Gateway.requestPermissions();
             try {
                const simRes = await Gateway.getSims();
                sims = simRes.sims;
            } catch (ignore) {}
        }

        // Listen for native logs
        listenerParams = await Gateway.addListener('log', (data: { message: string }) => {
            addLog(data.message);
        });
    });

    async function setPreference(slot: number) {
        preferredSimSlot = slot;
        await Gateway.setPreferredSim({ slot });
        addLog(`Preference saved: ${slot === -1 ? 'Auto' : `SIM ${slot + 1}`}`);
    }

    async function exitApp() {
        await Gateway.exitApp();
    }

    async function requestBattery() {
        await Gateway.requestBatteryOpt();
    }
    
    async function openOemBattery() {
        try {
            const result = await Gateway.openOemBatterySettings();
            addLog(`Opened ${result.manufacturer} battery settings${result.fallback ? ' (fallback)' : ''}`);
        } catch (e: any) {
            addLog(`Failed to open OEM settings: ${e.message}`);
        }
    }
</script>

<div class="space-y-6 max-w-2xl mx-auto">
    <Card class="border-border bg-card text-card-foreground shadow-lg backdrop-blur-sm">
        <CardHeader class="pb-2">
            <div class="flex items-center justify-between">
                <div>
                    <CardTitle class="text-xl font-bold flex items-center gap-2">
                        <Server class="w-5 h-5 text-primary" />
                        Gateway Control
                    </CardTitle>
                    <CardDescription>Manage your local SMS/USSD API Server.</CardDescription>
                </div>
                <div class="flex items-center gap-2">
                    <Button variant="ghost" size="icon" class="w-8 h-8 rounded-full hover:bg-yellow-500/10 hover:text-yellow-600" onclick={requestBattery} title="Ignore Battery Optimizations">
                        <Zap class="w-4 h-4" />
                    </Button>
                    <Button variant="ghost" size="icon" class="w-8 h-8 rounded-full hover:bg-green-500/10 hover:text-green-600" onclick={openOemBattery} title="OEM Battery Settings (Realme, Xiaomi, etc.)">
                        <Battery class="w-4 h-4" />
                    </Button>
                    <Button variant="ghost" size="icon" class="w-8 h-8 rounded-full hover:bg-destructive/10 hover:text-destructive" onclick={exitApp} title="Exit App">
                        <Power class="w-4 h-4" />
                    </Button>
                    
                     <!-- Status Badge -->
                    <div class={`ml-1 px-3 py-1 rounded-full text-[10px] font-bold tracking-wider uppercase border flex items-center gap-1.5 ${isRunning ? 'bg-emerald-500/10 text-emerald-500 border-emerald-500/20' : 'bg-destructive/10 text-destructive border-destructive/20'}`}>
                        <span class={`w-1.5 h-1.5 rounded-full ${isRunning ? 'bg-emerald-500 animate-pulse' : 'bg-destructive'}`}></span>
                        {isRunning ? 'Online' : 'Offline'}
                    </div>
                </div>
            </div>
        </CardHeader>
        
        <CardContent class="space-y-6 pt-4">
            {#if isRunning}
                <div in:fly={{ y: -10, duration: 200 }} class="p-4 bg-primary/5 rounded-lg border border-primary/10 space-y-3">
                    <Label class="text-[10px] font-bold uppercase tracking-wider text-primary flex items-center gap-1.5">
                        <Globe class="w-3 h-3" /> Public Endpoint
                    </Label>
                    <div class="flex gap-2">
                        <code class="flex-1 bg-background/50 p-3 rounded-md border border-border font-mono text-sm text-foreground select-all flex items-center shadow-inner">
                            {url}
                        </code>
                        <Button variant="outline" size="icon" onclick={copyUrl} class="border-border hover:bg-muted">
                            <Copy class="w-4 h-4 text-muted-foreground" />
                        </Button>
                    </div>
                    <p class="text-xs text-muted-foreground leading-relaxed">
                        API requires <code class="bg-muted px-1 py-0.5 rounded text-foreground font-mono">Authorization: Bearer {apiKey}</code>
                    </p>
                </div>
            {:else}
                <div class="grid gap-5">
                    <div class="grid gap-2">
                        <Label for="port">Local Port</Label>
                        <Input id="port" type="number" bind:value={port} class="font-mono bg-background/50 border-input focus-visible:ring-primary" />
                    </div>
                    <div class="grid gap-2">
                        <Label for="key">API Key</Label>
                        <div class="relative">
                            <Input id="key" type={isVisible ? "text" : "password"} bind:value={apiKey} class="font-mono pr-10 bg-background/50 border-input focus-visible:ring-primary" placeholder="Enter secure key" />
                            <Button variant="ghost" size="icon" class="absolute right-0 top-0 h-full px-3 text-muted-foreground hover:text-foreground" onclick={toggleVisibility}>
                                {#if isVisible}
                                    <EyeOff class="w-4 h-4" />
                                {:else}
                                    <Eye class="w-4 h-4" />
                                {/if}
                            </Button>
                        </div>
                    </div>
                </div>
            {/if}
        </CardContent>

        <CardFooter class="pt-2">
            <Button 
                class={`w-full font-bold transition-all h-11 ${isRunning ? 'bg-destructive hover:bg-destructive/90 text-destructive-foreground' : 'bg-primary hover:bg-primary/90 text-primary-foreground'}`}
                onclick={toggleServer}
            >
                {#if isRunning}
                    <Square class="w-4 h-4 mr-2 fill-current opacity-50" /> Stop Server
                {:else}
                    <Play class="w-4 h-4 mr-2 fill-current opacity-50" /> Start Server
                {/if}
            </Button>
        </CardFooter>
    </Card>

    <!-- SIM Configuration -->
    <Card class="border-border bg-card text-card-foreground shadow-lg backdrop-blur-sm">
        <CardHeader class="pb-2">
             <CardTitle class="text-xl font-bold flex items-center gap-2">
                <Settings class="w-5 h-5 text-primary" />
                SIM Configuration
            </CardTitle>
            <CardDescription>Select the default SIM for outgoing requests.</CardDescription>
        </CardHeader>
        <CardContent>
            {#if sims.length === 0}
                <div class="text-sm text-muted-foreground italic">No SIM cards detected or permission missing.</div>
            {:else}
                <div class="grid gap-3">
                    <button
                        class={`relative flex items-center justify-between p-3 rounded-lg border transition-all ${preferredSimSlot === -1 ? 'bg-primary/10 border-primary ring-1 ring-primary' : 'bg-card border-border hover:bg-muted/50'}`}
                        onclick={() => setPreference(-1)}
                    >
                        <div class="flex items-center gap-3">
                             <div class="p-2 rounded-full bg-background border">
                                <Activity class="w-4 h-4 text-muted-foreground" />
                            </div>
                            <div class="text-left">
                                <div class="text-sm font-bold">Auto / System Default</div>
                                <div class="text-xs text-muted-foreground">Let Android decide</div>
                            </div>
                        </div>
                        {#if preferredSimSlot === -1}
                            <div class="w-2.5 h-2.5 rounded-full bg-primary mx-2"></div>
                        {/if}
                    </button>

                    {#each sims as sim}
                        <button
                            class={`relative flex items-center justify-between p-3 rounded-lg border transition-all ${preferredSimSlot === sim.slot ? 'bg-primary/10 border-primary ring-1 ring-primary' : 'bg-card border-border hover:bg-muted/50'}`}
                            onclick={() => setPreference(sim.slot)}
                        >
                             <div class="flex items-center gap-3">
                                <div class="p-2 rounded-full bg-background border">
                                    <Globe class="w-4 h-4 text-muted-foreground" />
                                </div>
                                <div class="text-left">
                                    <div class="text-sm font-bold">{sim.displayName} {sim.slot + 1}</div>
                                    <div class="text-xs text-muted-foreground">
                                        {sim.carrierName} {sim.number ? `• ${sim.number}` : ''}
                                    </div>
                                </div>
                            </div>
                             {#if preferredSimSlot === sim.slot}
                                <div class="w-2.5 h-2.5 rounded-full bg-primary mx-2"></div>
                            {/if}
                        </button>
                    {/each}
                </div>
            {/if}
        </CardContent>
    </Card>

    <!-- Test Console -->
     {#if isRunning}
        <div in:fly={{ y: 20, duration: 300, delay: 100 }}>
            <Card class="border-border bg-card text-card-foreground shadow-lg backdrop-blur-sm">
                <CardHeader class="pb-2">
                <CardTitle class="text-xl font-bold flex items-center gap-2">
                    <Activity class="w-5 h-5 text-accent" />
                    Test Console
                </CardTitle>
                <CardDescription>Send test commands directly from device.</CardDescription>
            </CardHeader>
            <CardContent class="space-y-4">
                <!-- Tabs -->
                <div class="flex p-1 bg-muted/50 rounded-lg border border-border/50">
                    <button 
                        class={`flex-1 py-1.5 text-sm font-medium rounded-md transition-all ${testTab === 'sms' ? 'bg-background shadow-sm text-foreground' : 'text-muted-foreground hover:text-foreground'}`}
                        onclick={() => testTab = 'sms'}
                    >
                        SMS
                    </button>
                    <button 
                         class={`flex-1 py-1.5 text-sm font-medium rounded-md transition-all ${testTab === 'ussd' ? 'bg-background shadow-sm text-foreground' : 'text-muted-foreground hover:text-foreground'}`}
                        onclick={() => testTab = 'ussd'}
                    >
                        USSD
                    </button>
                </div>

                <div class="grid gap-4">
                    {#if testTab === 'sms'}
                        <div class="grid gap-2" in:fly={{ x: -10, duration: 200 }}>
                            <Label for="t_phone">Phone Number</Label>
                             <Input id="t_phone" placeholder="+880..." bind:value={testPhone} class="bg-background/50" />
                            
                            <Label for="t_msg">Message</Label>
                            <Input id="t_msg" placeholder="Hello World" bind:value={testMsg} class="bg-background/50" />
                            
                            <Button onclick={sendTestSms} disabled={testLoading} class="w-full">
                                {#if testLoading}Sending...{:else}Send SMS{/if}
                            </Button>
                        </div>
                    {:else}
                         <div class="grid gap-2" in:fly={{ x: 10, duration: 200 }}>
                            <Label for="t_code">USSD Code</Label>
                            <Input id="t_code" placeholder="*123#" bind:value={testCode} class="bg-background/50" />
                            
                            <Button onclick={sendTestUssd} disabled={testLoading} class="w-full">
                                {#if testLoading}Running...{:else}Run USSD{/if}
                            </Button>
                        </div>
                    {/if}

                    {#if testResult}
                        <div class="rounded-md bg-muted/50 p-3 border border-border mt-2">
                             <div class="text-[10px] uppercase font-bold text-muted-foreground mb-1">Result</div>
                             <pre class="whitespace-pre-wrap font-mono text-xs text-foreground overflow-x-auto">{testResult}</pre>
                        </div>
                    {/if}
                </div>
            </CardContent>
        </Card>
    </div>
    {/if}

    <!-- Logs -->
    <Card class="border-border bg-card shadow-sm backdrop-blur-sm">
        <CardHeader class="py-3 px-4 border-b border-border bg-muted/20">
            <div class="flex justify-between items-center">
                <div class="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-muted-foreground">
                    <Activity class="w-3.5 h-3.5" /> Recent Activity
                </div>
                <Button variant="ghost" size="sm" class="h-6 text-xs text-muted-foreground hover:text-foreground" onclick={() => logs = []}>
                    <Trash2 class="w-3 h-3 mr-1" /> Clear
                </Button>
            </div>
        </CardHeader>
        <div class="p-4 h-[300px] overflow-y-auto font-mono text-xs space-y-2 bg-background/30 scrollbar-thin scrollbar-thumb-border scrollbar-track-transparent">
            {#if logs.length === 0}
                <div class="h-full flex flex-col items-center justify-center text-muted-foreground/40 italic gap-3">
                    <Radio class="w-10 h-10" />
                    <span>Server logs will appear here...</span>
                </div>
            {/if}
            {#each logs as log, i (i)}
                <div in:fly={{ x: -10, duration: 150 }} class="text-foreground/90 border-l-2 border-primary/30 pl-3 py-1 bg-background/40 rounded-r-md">
                    {@html log.replace(/\[(.*?)\]/, '<span class="text-muted-foreground/50 text-[10px] uppercase mr-2 tracking-wide">[$1]</span>')}
                </div>
            {/each}
        </div>
    </Card>
</div>
