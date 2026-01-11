<script lang="ts">
    import { devices, smsCounters, resetSmsCounter, type Device } from '$lib/stores';
    import { Button } from '$lib/components/ui/button';
    import { Input } from '$lib/components/ui/input';
    import { Label } from '$lib/components/ui/label';
    import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '$lib/components/ui/card';
    import { Badge } from '$lib/components/ui/badge';
    import { Trash2, Plus, Smartphone, CheckCircle2, XCircle, Wifi, RotateCcw, MessageSquare } from '@lucide/svelte';
    import { fly, scale } from 'svelte/transition';

    import { toast } from 'svelte-sonner';

    let newName = $state('');
    let newIp = $state('');
    let newKey = $state('');

    function addDevice() {
        if (!newName || !newIp || !newKey) {
            toast.error('Please fill in all fields');
            return;
        }

        // Sanitize IP: remove http://, https:// and trailing slashes
        let sanitizedIp = newIp.trim()
            .replace(/^https?:\/\//, '')
            .replace(/\/+$/, '');

        devices.update(d => [
            ...d,
            {
                id: crypto.randomUUID ? crypto.randomUUID() : Date.now().toString(),
                name: newName.trim(),
                ip: sanitizedIp,
                apiKey: newKey.trim(),
                status: 'unknown'
            }
        ]);

        toast.success(`Device "${newName}" added successfully`);

        newName = '';
        newIp = '';
        newKey = '';
    }

    function removeDevice(id: string) {
        devices.update(d => d.filter(dev => dev.id !== id));
    }
</script>

<div class="space-y-6">
    <Card class="border-border/50 bg-background/50 backdrop-blur-xl shadow-lg transition-all duration-300 hover:shadow-xl hover:border-primary/20">
        <CardHeader>
            <CardTitle class="flex items-center gap-2 text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-primary to-primary/60">
                <Smartphone class="w-6 h-6 text-primary" />
                Add New Device
            </CardTitle>
            <CardDescription>Connect a new Pathway Android Gateway device</CardDescription>
        </CardHeader>
        <CardContent class="grid gap-4">
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div class="space-y-2">
                    <Label for="name" class="text-sm font-medium">Device Name</Label>
                    <Input id="name" placeholder="Currently Office Phone" bind:value={newName} class="bg-background/50 border-input/50 focus:ring-primary/20 transition-all font-sans" />
                </div>
                <div class="space-y-2">
                    <Label for="ip" class="text-sm font-medium">IP Address / Host</Label>
                    <Input id="ip" placeholder="192.168.1.100:8080" bind:value={newIp} class="bg-background/50 border-input/50 focus:ring-primary/20 transition-all font-mono text-sm" />
                </div>
                <div class="space-y-2">
                    <Label for="key" class="text-sm font-medium">API Key</Label>
                    <Input id="key" type="password" placeholder="••••••••" bind:value={newKey} class="bg-background/50 border-input/50 focus:ring-primary/20 transition-all font-mono text-sm" />
                </div>
            </div>
        </CardContent>
        <CardFooter class="flex justify-end pt-2">
            <Button onclick={addDevice} disabled={!newName || !newIp || !newKey} class="w-full md:w-auto font-semibold shadow-lg shadow-primary/20 hover:shadow-primary/40 transition-all">
                <Plus class="w-4 h-4 mr-2" />
                Add Device
            </Button>
        </CardFooter>
    </Card>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {#each $devices as device (device.id)}
            <div in:scale={{ duration: 200, start: 0.95 }} out:fly={{ y: 20, duration: 200 }}>
                <Card class="h-full border-border/50 bg-card/30 hover:bg-card/50 backdrop-blur-sm transition-all duration-300 hover:shadow-md group relative overflow-hidden">
                     <div class="absolute inset-0 bg-gradient-to-br from-primary/5 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500 pointer-events-none"></div>
                    
                    <CardHeader class="pb-2">
                        <div class="flex justify-between items-start">
                           <div class="space-y-1">
                                <CardTitle class="text-lg font-semibold tracking-tight">{device.name}</CardTitle>
                                <div class="flex items-center gap-1.5 text-xs text-muted-foreground font-mono">
                                    <Wifi class="w-3 h-3" />
                                    {device.ip}
                                </div>
                           </div>
                           <Button variant="ghost" size="icon" class="text-muted-foreground hover:text-destructive hover:bg-destructive/10 -mt-1 -mr-2 transition-colors" onclick={() => removeDevice(device.id)}>
                                <Trash2 class="w-4 h-4" />
                            </Button>
                        </div>
                    </CardHeader>
                    <CardContent>
                        <div class="flex items-center justify-between gap-2 mt-2">
                            <Badge variant={device.status === 'online' ? 'default' : device.status === 'offline' ? 'destructive' : 'secondary'} class="transition-colors">
                                {#if device.status === 'online'}
                                    <CheckCircle2 class="w-3 h-3 mr-1" />
                                {:else if device.status === 'offline'}
                                    <XCircle class="w-3 h-3 mr-1" />
                                {:else}
                                    <div class="w-3 h-3 mr-1 rounded-full border-2 border-current border-t-transparent animate-spin"></div>
                                {/if}
                                {device.status || 'Unknown'}
                            </Badge>
                        </div>
                        
                        <!-- SMS Counter -->
                        <div class="flex items-center justify-between mt-4 pt-3 border-t border-border/50">
                            <div class="flex items-center gap-2">
                                <MessageSquare class="w-4 h-4 text-muted-foreground" />
                                <span class="text-sm text-muted-foreground">SMS Sent:</span>
                                <span class="font-bold text-foreground">{$smsCounters[device.id] || 0}</span>
                            </div>
                            <Button 
                                variant="ghost" 
                                size="sm" 
                                class="h-7 px-2 text-xs text-muted-foreground hover:text-foreground"
                                onclick={() => {
                                    resetSmsCounter(device.id);
                                    toast.success(`Counter reset for ${device.name}`);
                                }}
                            >
                                <RotateCcw class="w-3 h-3 mr-1" />
                                Reset
                            </Button>
                        </div>
                    </CardContent>
                </Card>
            </div>
        {/each}
    </div>
</div>
