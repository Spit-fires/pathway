<script lang="ts">
	import { onMount } from 'svelte';
	import { animate, stagger } from 'motion';
	import { Smartphone, Monitor, Wifi, Settings, Play, CheckCircle, ArrowRight, Terminal } from '@lucide/svelte';
	import { base } from '$app/paths';

	onMount(() => {
		animate(
			'.guide-step' as any,
			{ opacity: [0, 1], x: [-20, 0] },
			{ duration: 0.5, delay: stagger(0.1), ease: 'easeOut' }
		);
	});
</script>

<svelte:head>
	<title>User Guide - How to Set Up Pathway SMS Gateway</title>
	<meta name="description" content="Learn how to set up and use Pathway SMS gateway. Step-by-step instructions for installing the Android app, configuring the gateway, and using the API." />
	<meta name="keywords" content="SMS gateway setup, Pathway tutorial, Android SMS API guide, how to send SMS via API, SMS gateway installation" />
	<meta property="og:title" content="User Guide - Pathway SMS Gateway Setup" />
	<meta property="og:description" content="Step-by-step guide to set up Pathway SMS gateway on Android and connect with the desktop dashboard." />
</svelte:head>

<!-- Hero -->
<section class="py-16 border-b-3 border-border" aria-labelledby="guide-heading">
	<header class="max-w-4xl mx-auto px-4 sm:px-6 text-center">
		<span class="inline-block px-4 py-2 bg-accent border-3 border-border neo-shadow font-mono text-sm font-bold text-accent-foreground mb-6">
			GETTING STARTED
		</span>
		<h1 id="guide-heading" class="text-4xl md:text-5xl font-bold mb-4">User Guide</h1>
		<p class="text-lg text-muted-foreground max-w-2xl mx-auto">
			Learn how to set up and use Pathway to send SMS through your Android device.
		</p>
	</header>
</section>

<!-- Installation Steps -->
<section class="py-16">
	<div class="max-w-4xl mx-auto px-4 sm:px-6">
		<h2 class="text-2xl font-bold mb-8 flex items-center gap-3">
			<span class="w-10 h-10 bg-primary flex items-center justify-center border-3 border-border neo-shadow text-primary-foreground font-bold">1</span>
			Install the Android Gateway
		</h2>

		<div class="space-y-4 ml-4 border-l-3 border-border pl-8 pb-8">
			<div class="guide-step opacity-0 neo-card p-6">
				<div class="flex items-start gap-4">
					<div class="shrink-0 w-8 h-8 bg-secondary flex items-center justify-center border-3 border-border font-bold">A</div>
					<div>
						<h3 class="font-bold mb-2">Download the APK</h3>
						<p class="text-muted-foreground">Go to the <a href="{base}/download" class="neo-link text-primary">download page</a> and get the latest <code class="bg-muted px-2 py-1 border border-border font-mono text-sm">app-debug.apk</code> file.</p>
					</div>
				</div>
			</div>

			<div class="guide-step opacity-0 neo-card p-6">
				<div class="flex items-start gap-4">
					<div class="shrink-0 w-8 h-8 bg-secondary flex items-center justify-center border-3 border-border font-bold">B</div>
					<div>
						<h3 class="font-bold mb-2">Install on Android</h3>
						<p class="text-muted-foreground">Transfer the APK to your Android device and install it. You may need to enable "Install from unknown sources" in your settings.</p>
					</div>
				</div>
			</div>

			<div class="guide-step opacity-0 neo-card p-6">
				<div class="flex items-start gap-4">
					<div class="shrink-0 w-8 h-8 bg-secondary flex items-center justify-center border-3 border-border font-bold">C</div>
					<div>
						<h3 class="font-bold mb-2">Grant Permissions</h3>
						<p class="text-muted-foreground">Open the app and grant the required permissions: SMS, Phone, and Notifications. These are needed to send SMS and run USSD codes.</p>
					</div>
				</div>
			</div>
		</div>

		<h2 class="text-2xl font-bold mb-8 flex items-center gap-3">
			<span class="w-10 h-10 bg-primary flex items-center justify-center border-3 border-border neo-shadow text-primary-foreground font-bold">2</span>
			Configure the Gateway
		</h2>

		<div class="space-y-4 ml-4 border-l-3 border-border pl-8 pb-8">
			<div class="guide-step opacity-0 neo-card p-6">
				<div class="flex items-start gap-4">
					<div class="shrink-0">
						<Settings class="w-6 h-6 text-primary" />
					</div>
					<div>
						<h3 class="font-bold mb-2">Set your API Key</h3>
						<p class="text-muted-foreground">Enter a secure API key that will be used to authenticate requests. This should be kept secret.</p>
					</div>
				</div>
			</div>

			<div class="guide-step opacity-0 neo-card p-6">
				<div class="flex items-start gap-4">
					<div class="shrink-0">
						<Wifi class="w-6 h-6 text-primary" />
					</div>
					<div>
						<h3 class="font-bold mb-2">Note the IP Address</h3>
						<p class="text-muted-foreground">The app will display your device's local IP address (e.g., <code class="bg-muted px-2 py-1 border border-border font-mono text-sm">192.168.1.100</code>). You'll need this to connect from the desktop.</p>
					</div>
				</div>
			</div>

			<div class="guide-step opacity-0 neo-card p-6">
				<div class="flex items-start gap-4">
					<div class="shrink-0">
						<Play class="w-6 h-6 text-primary" />
					</div>
					<div>
						<h3 class="font-bold mb-2">Start the Server</h3>
						<p class="text-muted-foreground">Tap "Start Server" to begin accepting API requests. The server runs on port 8080 by default.</p>
					</div>
				</div>
			</div>
		</div>

		<h2 class="text-2xl font-bold mb-8 flex items-center gap-3">
			<span class="w-10 h-10 bg-primary flex items-center justify-center border-3 border-border neo-shadow text-primary-foreground font-bold">3</span>
			Connect the Desktop Dashboard
		</h2>

		<div class="space-y-4 ml-4 border-l-3 border-border pl-8 pb-8">
			<div class="guide-step opacity-0 neo-card p-6">
				<div class="flex items-start gap-4">
					<div class="shrink-0">
						<Monitor class="w-6 h-6 text-primary" />
					</div>
					<div>
						<h3 class="font-bold mb-2">Install Desktop App</h3>
						<p class="text-muted-foreground">Download and install the desktop app for your operating system from the <a href="{base}/download" class="neo-link text-primary">download page</a>.</p>
					</div>
				</div>
			</div>

			<div class="guide-step opacity-0 neo-card p-6">
				<div class="flex items-start gap-4">
					<div class="shrink-0">
						<Smartphone class="w-6 h-6 text-primary" />
					</div>
					<div>
						<h3 class="font-bold mb-2">Add Your Device</h3>
						<p class="text-muted-foreground">In the desktop app, add a new device with the IP address and API key from your Android gateway.</p>
					</div>
				</div>
			</div>

			<div class="guide-step opacity-0 neo-card p-6">
				<div class="flex items-start gap-4">
					<div class="shrink-0">
						<CheckCircle class="w-6 h-6 text-primary" />
					</div>
					<div>
						<h3 class="font-bold mb-2">Start Sending SMS</h3>
						<p class="text-muted-foreground">Once connected, you can send individual messages or bulk campaigns through the dashboard!</p>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>

<!-- API Usage -->
<section class="py-16 bg-muted border-t-3 border-b-3 border-border">
	<div class="max-w-4xl mx-auto px-4 sm:px-6">
		<h2 class="text-2xl font-bold mb-8 flex items-center gap-3">
			<Terminal class="w-6 h-6" />
			API Usage
		</h2>

		<div class="space-y-6">
			<div class="neo-card p-6">
				<h3 class="font-bold mb-4">Send SMS</h3>
				<div class="bg-foreground text-background p-4 font-mono text-sm overflow-x-auto">
					<p class="text-muted">POST /sms</p>
					<p class="mt-2">curl -X POST http://192.168.1.100:8080/sms \</p>
					<p class="pl-4">-H "Content-Type: application/json" \</p>
					<p class="pl-4">-H "Authorization: Bearer YOUR_API_KEY" \</p>
					<p class="pl-4">-d '{`{"number": "+1234567890", "message": "Hello!"}`}'</p>
				</div>
			</div>

			<div class="neo-card p-6">
				<h3 class="font-bold mb-4">Run USSD Code</h3>
				<div class="bg-foreground text-background p-4 font-mono text-sm overflow-x-auto">
					<p class="text-muted">POST /ussd</p>
					<p class="mt-2">curl -X POST http://192.168.1.100:8080/ussd \</p>
					<p class="pl-4">-H "Content-Type: application/json" \</p>
					<p class="pl-4">-H "Authorization: Bearer YOUR_API_KEY" \</p>
					<p class="pl-4">-d '{`{"code": "*123#"}`}'</p>
				</div>
			</div>

			<div class="neo-card p-6">
				<h3 class="font-bold mb-4">Health Check</h3>
				<div class="bg-foreground text-background p-4 font-mono text-sm overflow-x-auto">
					<p class="text-muted">GET / (no auth required)</p>
					<p class="mt-2">curl http://192.168.1.100:8080/</p>
					<p class="mt-2 text-muted"># Response: {`{"status": "online"}`}</p>
				</div>
			</div>
		</div>
	</div>
</section>

<!-- Tips Section -->
<section class="py-16">
	<div class="max-w-4xl mx-auto px-4 sm:px-6">
		<h2 class="text-2xl font-bold mb-8">Tips for Best Performance</h2>
		
		<div class="grid md:grid-cols-2 gap-6">
			<div class="neo-card p-6">
				<h3 class="font-bold mb-2">🔋 Battery Optimization</h3>
				<p class="text-muted-foreground">Disable battery optimization for the Pathway app to ensure it keeps running in the background.</p>
			</div>
			
			<div class="neo-card p-6">
				<h3 class="font-bold mb-2">🌐 Static IP</h3>
				<p class="text-muted-foreground">Consider assigning a static IP to your Android device in your router settings for consistent connectivity.</p>
			</div>
			
			<div class="neo-card p-6">
				<h3 class="font-bold mb-2">🔌 Keep Plugged In</h3>
				<p class="text-muted-foreground">For 24/7 operation, keep your device connected to power and enable "Keep screen on" in the app.</p>
			</div>
			
			<div class="neo-card p-6">
				<h3 class="font-bold mb-2">📱 Multiple Devices</h3>
				<p class="text-muted-foreground">Use multiple Android gateways for load balancing and to avoid carrier rate limits.</p>
			</div>
		</div>
	</div>
</section>
