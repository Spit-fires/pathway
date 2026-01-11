<script lang="ts">
    import { devices, smsCounters, incrementSmsCounter, type Device } from '$lib/stores';
    import { sendSms } from '$lib/gateway';
    import { Button } from '$lib/components/ui/button';
    import { Textarea } from '$lib/components/ui/textarea';
    import { Label } from '$lib/components/ui/label';
    import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '$lib/components/ui/card';
    import { Checkbox } from '$lib/components/ui/checkbox';
    import { Badge } from '$lib/components/ui/badge';
    import { Progress } from '$lib/components/ui/progress';
    import { Send, Smartphone, Loader2, AlertCircle, CheckCircle, Clock } from '@lucide/svelte';
    import { fly } from 'svelte/transition';

    let message = $state('');
    let numbers = $state('');
    let selectedDeviceIds = $state<string[]>([]);
    let isSending = $state(false);
    let logs = $state<Array<{ device: string, number: string, status: 'sent' | 'failed', reason?: string, time: Date }>>([]);
    let sentCount = $state(0);
    let totalCount = $state(0);
    let startTime = $state<number | null>(null);
    let estimatedTimeRemaining = $state<string>('');

    // In Svelte 5, we can use a store with $ prefix or just follow it.
    // Since devices is a store, $devices works.
    let activeDevices = $derived($devices);

    // Check if message contains non-GSM characters (Unicode like Bangla, Arabic, etc.)
    function hasUnicodeChars(text: string): boolean {
        // GSM 7-bit Basic Character Set (GSM 03.38)
        // This regex matches all characters that can be encoded in GSM 7-bit:
        // - Basic Latin letters (A-Z, a-z), digits (0-9)
        // - GSM-specific symbols: @ £ $ ¥ è é ù ì ò Ç Ø ø Å å Δ _ Φ Γ Λ Ω Π Ψ Σ Θ Ξ Æ æ ß É ¡ Ä Ö Ñ Ü § ¿ ä ö ñ ü à
        // - Common punctuation and symbols: space ! " # ¤ % & ' ( ) * + , - . / : ; < = > ?
        // - GSM extension table chars (via escape \x1B): ^ { } [ ~ ] | € \x0C
        // - Control chars: newline \n, carriage return \r
        // Any character outside this set requires UCS-2 (Unicode) encoding
        const gsmRegex = /^[@£$¥èéùìòÇ\nØø\rÅåΔ_ΦΓΛΩΠΨΣΘΞ\x1BÆæßÉ !"#¤%&'()*+,\-./0-9:;<=>?¡A-ZÄÖÑܧ¿a-zäöñüà\x0C^{}\[~\]|€]*$/;
        return !gsmRegex.test(text);
    }

    // Calculate SMS parts based on message content
    function calculateSmsParts(text: string): number {
        if (!text || text.length === 0) return 0;
        
        const isUnicode = hasUnicodeChars(text);
        
        if (isUnicode) {
            // UCS-2 encoding: 70 chars per single SMS, 67 per part in multipart
            if (text.length <= 70) return 1;
            return Math.ceil(text.length / 67);
        } else {
            // GSM 7-bit encoding: 160 chars per single SMS, 153 per part in multipart
            if (text.length <= 160) return 1;
            return Math.ceil(text.length / 153);
        }
    }

    // Get encoding type for display
    let encodingType = $derived(message && hasUnicodeChars(message) ? 'Unicode' : 'GSM');
    let smsParts = $derived(calculateSmsParts(message));
    
    // Calculate number count from the numbers field
    let numberCount = $derived.by(() => {
        if (!numbers || numbers.trim() === '') return 0;
        const numberList = numbers.split(/[,\n\s]+/).map(n => n.trim()).filter(n => n);
        return numberList.length;
    });

    function toggleDevice(id: string) {
        if (selectedDeviceIds.includes(id)) {
            selectedDeviceIds = selectedDeviceIds.filter(d => d !== id);
        } else {
            selectedDeviceIds = [...selectedDeviceIds, id];
        }
    }
    
    function selectAll() {
        if (selectedDeviceIds.length === activeDevices.length) {
            selectedDeviceIds = [];
        } else {
            selectedDeviceIds = activeDevices.map(d => d.id);
        }
    }

    async function handleSend() {
        if (!message || !numbers || selectedDeviceIds.length === 0) return;

        isSending = true;
        sentCount = 0;
        startTime = Date.now();
        estimatedTimeRemaining = 'Calculating...';
        
        // Parse numbers: support comma, newline, or space separated
        const numberList = numbers.split(/[,\n\s]+/).map(n => n.trim()).filter(n => n);
        totalCount = numberList.length;
        const targetDevices = activeDevices.filter(d => selectedDeviceIds.includes(d.id));
        
        let deviceIndex = 0;

        for (const number of numberList) {
            const device = targetDevices[deviceIndex % targetDevices.length];
            deviceIndex++;

            try {
                const result = await sendSms(device, number, message);
                logs = [{
                    device: device.name,
                    number: number,
                    status: result.status,
                    reason: result.reason,
                    time: new Date()
                }, ...logs];
                
                // Increment counter on successful send
                if (result.status === 'sent') {
                    incrementSmsCounter(device.id);
                }
            } catch (e) {
                 logs = [{
                    device: device.name,
                    number: number,
                    status: 'failed',
                    reason: 'Network Error',
                    time: new Date()
                }, ...logs];
            }
            
            // Update progress and estimate
            sentCount++;
            
            if (sentCount > 0 && startTime) {
                const elapsedMs = Date.now() - startTime;
                const avgTimePerSms = elapsedMs / sentCount;
                const remainingSms = totalCount - sentCount;
                const estimatedMs = remainingSms * avgTimePerSms;
                
                // Format estimated time
                if (estimatedMs < 60000) {
                    estimatedTimeRemaining = `${Math.ceil(estimatedMs / 1000)}s`;
                } else {
                    const minutes = Math.floor(estimatedMs / 60000);
                    const seconds = Math.ceil((estimatedMs % 60000) / 1000);
                    estimatedTimeRemaining = `${minutes}m ${seconds}s`;
                }
            }
        }
        isSending = false;
        estimatedTimeRemaining = '';
    }
</script>

<div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
    <div class="lg:col-span-2 space-y-6">
        <Card class="border-border/50 bg-background/50 backdrop-blur-xl shadow-lg">
            <CardHeader>
                 <CardTitle class="flex items-center gap-2 text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-blue-500 to-indigo-500">
                    <Send class="w-6 h-6 text-blue-500" />
                    Send SMS
                </CardTitle>
                <CardDescription>Compose and send messages to multiple recipients</CardDescription>
            </CardHeader>
            <CardContent class="space-y-6">
                <div class="space-y-2">
                    <Label for="numbers">Recipients (Phone Numbers)</Label>
                    <Textarea id="numbers" placeholder="+88017..., +88018..." bind:value={numbers} class="bg-background/50 font-mono min-h-[80px] resize-y" aria-describedby="numbers-help" />
                    <div class="flex justify-between items-center text-xs text-muted-foreground">
                        <p id="numbers-help">Enter numbers separated by commas, spaces, or new lines</p>
                        {#if numberCount > 0}
                            <Badge variant="secondary" class="font-mono">{numberCount} number{numberCount !== 1 ? 's' : ''}</Badge>
                        {/if}
                    </div>
                </div>

                <div class="space-y-2">
                    <Label for="message">Message</Label>
                    <Textarea id="message" placeholder="Type your message here..." bind:value={message} class="min-h-[120px] bg-background/50 resize-y" />
                    <div class="flex justify-between text-xs text-muted-foreground">
                        <span>{message.length} characters ({encodingType})</span>
                        <span>{smsParts} SMS part{smsParts !== 1 ? 's' : ''}</span>
                    </div>
                </div>

                <div class="space-y-3">
                    <Label>Select Gateways</Label>
                    {#if activeDevices.length === 0}
                         <div class="p-4 rounded-md border border-dashed text-center text-sm text-muted-foreground">
                            No devices configured. Please add a device in the Manager tab.
                         </div>
                    {:else}
                        <div class="grid grid-cols-1 sm:grid-cols-2 gap-2">
                            <Button variant="outline" size="sm" class="w-full justify-start text-muted-foreground mb-2 sm:col-span-2" onclick={selectAll}>
                                {selectedDeviceIds.length === activeDevices.length ? 'Deselect All' : 'Select All'}
                            </Button>
                            {#each activeDevices as device}
                                <button 
                                    type="button"
                                    class="flex items-center space-x-2 p-3 rounded-md border bg-card/50 hover:bg-card/80 transition-colors cursor-pointer text-left w-full" 
                                    onclick={() => toggleDevice(device.id)}
                                >
                                    <Checkbox id={device.id} checked={selectedDeviceIds.includes(device.id)} />
                                    <div class="grid gap-0.5 leading-none select-none">
                                        <div class="text-sm font-medium leading-none">
                                            {device.name}
                                        </div>
                                        <p class="text-xs text-muted-foreground font-mono">
                                            {device.ip}
                                        </p>
                                    </div>
                                </button>
                            {/each}
                        </div>
                    {/if}
                </div>
            </CardContent>
            <CardFooter class="flex flex-col gap-4">
                {#if isSending}
                    <div class="w-full space-y-2">
                        <div class="flex justify-between items-center text-sm">
                            <span class="text-muted-foreground flex items-center gap-1.5">
                                <Loader2 class="w-3.5 h-3.5 animate-spin" />
                                Sending {sentCount} of {totalCount}
                            </span>
                            {#if estimatedTimeRemaining}
                                <span class="text-muted-foreground flex items-center gap-1.5">
                                    <Clock class="w-3.5 h-3.5" />
                                    {estimatedTimeRemaining} remaining
                                </span>
                            {/if}
                        </div>
                        <Progress value={sentCount} max={totalCount} class="h-2" />
                    </div>
                {/if}
                <Button onclick={handleSend} disabled={isSending || !message || !numbers || selectedDeviceIds.length === 0} class="w-full font-semibold shadow-lg shadow-blue-500/20 hover:shadow-blue-500/40 transition-all bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700">
                    {#if isSending}
                        <Loader2 class="w-4 h-4 mr-2 animate-spin" />
                        Sending...
                    {:else}
                        <Send class="w-4 h-4 mr-2" />
                        Send Message
                    {/if}
                </Button>
            </CardFooter>
        </Card>
    </div>

    <div class="lg:col-span-1">
        <Card class="h-full border-border/50 bg-background/50 backdrop-blur-xl shadow-lg flex flex-col">
            <CardHeader>
                <CardTitle class="text-lg">Activity Log</CardTitle>
            </CardHeader>
             <CardContent class="flex-1 overflow-y-auto max-h-[600px] p-0">
                {#if logs.length === 0}
                    <div class="h-32 flex items-center justify-center text-sm text-muted-foreground">
                        No activity yet
                    </div>
                {:else}
                    <div class="divide-y divide-border/50">
                        {#each logs as log}
                            <div class="p-4 flex items-start gap-3 text-sm animate-in slide-in-from-top-2 duration-300">
                                {#if log.status === 'sent'}
                                    <CheckCircle class="w-4 h-4 text-green-500 mt-0.5 shrink-0" />
                                {:else}
                                    <AlertCircle class="w-4 h-4 text-red-500 mt-0.5 shrink-0" />
                                {/if}
                                <div class="grid gap-1">
                                    <p class="font-medium leading-none truncate max-w-[200px]">{log.number}</p>
                                    <p class="text-xs text-muted-foreground">
                                        via {log.device} • {log.time.toLocaleTimeString()}
                                    </p>
                                    {#if log.reason}
                                        <p class="text-xs text-destructive">{log.reason}</p>
                                    {/if}
                                </div>
                            </div>
                        {/each}
                    </div>
                {/if}
            </CardContent>
        </Card>
    </div>
</div>
