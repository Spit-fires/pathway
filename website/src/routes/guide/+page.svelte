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
<section class="py-12 sm:py-16 border-b-3 border-border" aria-labelledby="guide-heading">
	<header class="max-w-4xl mx-auto px-4 sm:px-6 text-center">
		<span class="inline-block px-3 sm:px-4 py-2 bg-accent border-3 border-border neo-shadow font-mono text-xs sm:text-sm font-bold text-accent-foreground mb-6">
			GETTING STARTED
		</span>
		<h1 id="guide-heading" class="text-3xl sm:text-4xl md:text-5xl font-bold mb-4">User Guide</h1>
		<p class="text-base sm:text-lg text-muted-foreground max-w-2xl mx-auto">
			Learn how to set up and use Pathway to send SMS through your Android device.
		</p>
	</header>
</section>

<!-- Installation Steps -->
<section class="py-12 sm:py-16">
	<div class="max-w-4xl mx-auto px-4 sm:px-6">
		<h2 class="text-xl sm:text-2xl font-bold mb-6 sm:mb-8 flex items-center gap-3">
			<span class="w-8 h-8 sm:w-10 sm:h-10 bg-primary flex items-center justify-center border-3 border-border neo-shadow text-primary-foreground font-bold text-sm sm:text-base shrink-0">1</span>
			<span class="break-words">Install the Android Gateway</span>
		</h2>

		<div class="space-y-4 ml-2 sm:ml-4 border-l-3 border-border pl-4 sm:pl-8 pb-8">
			<div class="guide-step opacity-0 neo-card p-4 sm:p-6">
				<div class="flex items-start gap-3 sm:gap-4">
					<div class="shrink-0 w-7 h-7 sm:w-8 sm:h-8 bg-secondary flex items-center justify-center border-3 border-border font-bold text-sm">A</div>
					<div class="min-w-0">
						<h3 class="font-bold mb-2 text-sm sm:text-base">Download the APK</h3>
						<p class="text-muted-foreground text-sm sm:text-base">Go to the <a href="{base}/download" class="neo-link text-primary">download page</a> and get the latest <code class="bg-muted px-1.5 sm:px-2 py-0.5 sm:py-1 border border-border font-mono text-xs sm:text-sm break-all">app-debug.apk</code> file.</p>
					</div>
				</div>
			</div>

			<div class="guide-step opacity-0 neo-card p-4 sm:p-6">
				<div class="flex items-start gap-3 sm:gap-4">
					<div class="shrink-0 w-7 h-7 sm:w-8 sm:h-8 bg-secondary flex items-center justify-center border-3 border-border font-bold text-sm">B</div>
					<div class="min-w-0">
						<h3 class="font-bold mb-2 text-sm sm:text-base">Install on Android</h3>
						<p class="text-muted-foreground text-sm sm:text-base">Transfer the APK to your Android device and install it. You may need to enable "Install from unknown sources" in your settings.</p>
					</div>
				</div>
			</div>

			<div class="guide-step opacity-0 neo-card p-4 sm:p-6">
				<div class="flex items-start gap-3 sm:gap-4">
					<div class="shrink-0 w-7 h-7 sm:w-8 sm:h-8 bg-secondary flex items-center justify-center border-3 border-border font-bold text-sm">C</div>
					<div class="min-w-0">
						<h3 class="font-bold mb-2 text-sm sm:text-base">Grant Permissions</h3>
						<p class="text-muted-foreground text-sm sm:text-base">Open the app and grant the required permissions: SMS, Phone, and Notifications. These are needed to send SMS and run USSD codes.</p>
					</div>
				</div>
			</div>
		</div>

		<h2 class="text-xl sm:text-2xl font-bold mb-6 sm:mb-8 flex items-center gap-3">
			<span class="w-8 h-8 sm:w-10 sm:h-10 bg-primary flex items-center justify-center border-3 border-border neo-shadow text-primary-foreground font-bold text-sm sm:text-base shrink-0">2</span>
			<span class="break-words">Configure the Gateway</span>
		</h2>

		<div class="space-y-4 ml-2 sm:ml-4 border-l-3 border-border pl-4 sm:pl-8 pb-8">
			<div class="guide-step opacity-0 neo-card p-4 sm:p-6">
				<div class="flex items-start gap-3 sm:gap-4">
					<div class="shrink-0">
						<Settings class="w-5 h-5 sm:w-6 sm:h-6 text-primary" />
					</div>
					<div class="min-w-0">
						<h3 class="font-bold mb-2 text-sm sm:text-base">Set your API Key</h3>
						<p class="text-muted-foreground text-sm sm:text-base">Enter a secure API key that will be used to authenticate requests. This should be kept secret.</p>
					</div>
				</div>
			</div>

			<div class="guide-step opacity-0 neo-card p-4 sm:p-6">
				<div class="flex items-start gap-3 sm:gap-4">
					<div class="shrink-0">
						<Wifi class="w-5 h-5 sm:w-6 sm:h-6 text-primary" />
					</div>
					<div class="min-w-0">
						<h3 class="font-bold mb-2 text-sm sm:text-base">Note the IP Address</h3>
						<p class="text-muted-foreground text-sm sm:text-base">The app will display your device's local IP address (e.g., <code class="bg-muted px-1.5 sm:px-2 py-0.5 sm:py-1 border border-border font-mono text-xs sm:text-sm">192.168.1.100</code>). You'll need this to connect from the desktop.</p>
					</div>
				</div>
			</div>

			<div class="guide-step opacity-0 neo-card p-4 sm:p-6">
				<div class="flex items-start gap-3 sm:gap-4">
					<div class="shrink-0">
						<Play class="w-5 h-5 sm:w-6 sm:h-6 text-primary" />
					</div>
					<div class="min-w-0">
						<h3 class="font-bold mb-2 text-sm sm:text-base">Start the Server</h3>
						<p class="text-muted-foreground text-sm sm:text-base">Tap "Start Server" to begin accepting API requests. The server runs on port 8080 by default.</p>
					</div>
				</div>
			</div>
		</div>

		<h2 class="text-xl sm:text-2xl font-bold mb-6 sm:mb-8 flex items-center gap-3">
			<span class="w-8 h-8 sm:w-10 sm:h-10 bg-primary flex items-center justify-center border-3 border-border neo-shadow text-primary-foreground font-bold text-sm sm:text-base shrink-0">3</span>
			<span class="break-words">Connect the Desktop Dashboard</span>
		</h2>

		<div class="space-y-4 ml-2 sm:ml-4 border-l-3 border-border pl-4 sm:pl-8 pb-8">
			<div class="guide-step opacity-0 neo-card p-4 sm:p-6">
				<div class="flex items-start gap-3 sm:gap-4">
					<div class="shrink-0">
						<Monitor class="w-5 h-5 sm:w-6 sm:h-6 text-primary" />
					</div>
					<div class="min-w-0">
						<h3 class="font-bold mb-2 text-sm sm:text-base">Install Desktop App</h3>
						<p class="text-muted-foreground text-sm sm:text-base">Download and install the desktop app for your operating system from the <a href="{base}/download" class="neo-link text-primary">download page</a>.</p>
					</div>
				</div>
			</div>

			<div class="guide-step opacity-0 neo-card p-4 sm:p-6">
				<div class="flex items-start gap-3 sm:gap-4">
					<div class="shrink-0">
						<Smartphone class="w-5 h-5 sm:w-6 sm:h-6 text-primary" />
					</div>
					<div class="min-w-0">
						<h3 class="font-bold mb-2 text-sm sm:text-base">Add Your Device</h3>
						<p class="text-muted-foreground text-sm sm:text-base">In the desktop app, add a new device with the IP address and API key from your Android gateway.</p>
					</div>
				</div>
			</div>

			<div class="guide-step opacity-0 neo-card p-4 sm:p-6">
				<div class="flex items-start gap-3 sm:gap-4">
					<div class="shrink-0">
						<CheckCircle class="w-5 h-5 sm:w-6 sm:h-6 text-primary" />
					</div>
					<div class="min-w-0">
						<h3 class="font-bold mb-2 text-sm sm:text-base">Start Sending SMS</h3>
						<p class="text-muted-foreground text-sm sm:text-base">Once connected, you can send individual messages or bulk campaigns through the dashboard!</p>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>

<!-- API Usage -->
<section class="py-12 sm:py-16 bg-muted border-t-3 border-b-3 border-border">
	<div class="max-w-4xl mx-auto px-4 sm:px-6">
		<h2 class="text-xl sm:text-2xl font-bold mb-6 sm:mb-8 flex items-center gap-3">
			<Terminal class="w-5 h-5 sm:w-6 sm:h-6" />
			API Usage
		</h2>

		<div class="space-y-4 sm:space-y-6">
			<div class="neo-card p-4 sm:p-6">
				<h3 class="font-bold mb-4 text-sm sm:text-base">Send SMS</h3>
				<div class="bg-foreground text-background p-3 sm:p-4 font-mono text-xs sm:text-sm overflow-x-auto">
					<p class="text-muted">POST /sms</p>
					<p class="mt-2 break-all">curl -X POST http://192.168.1.100:8080/sms \</p>
					<p class="pl-2 sm:pl-4 break-all">-H "Content-Type: application/json" \</p>
					<p class="pl-2 sm:pl-4 break-all">-H "Authorization: Bearer YOUR_API_KEY" \</p>
					<p class="pl-2 sm:pl-4 break-all">-d '{`{"number": "+1234567890", "message": "Hello!"}`}'</p>
				</div>
			</div>

			<div class="neo-card p-4 sm:p-6">
				<h3 class="font-bold mb-4 text-sm sm:text-base">Run USSD Code</h3>
				<div class="bg-foreground text-background p-3 sm:p-4 font-mono text-xs sm:text-sm overflow-x-auto">
					<p class="text-muted">POST /ussd</p>
					<p class="mt-2 break-all">curl -X POST http://192.168.1.100:8080/ussd \</p>
					<p class="pl-2 sm:pl-4 break-all">-H "Content-Type: application/json" \</p>
					<p class="pl-2 sm:pl-4 break-all">-H "Authorization: Bearer YOUR_API_KEY" \</p>
					<p class="pl-2 sm:pl-4 break-all">-d '{`{"code": "*123#"}`}'</p>
				</div>
			</div>

			<div class="neo-card p-4 sm:p-6">
				<h3 class="font-bold mb-4 text-sm sm:text-base">Health Check</h3>
				<div class="bg-foreground text-background p-3 sm:p-4 font-mono text-xs sm:text-sm overflow-x-auto">
					<p class="text-muted">GET / (no auth required)</p>
					<p class="mt-2 break-all">curl http://192.168.1.100:8080/</p>
					<p class="mt-2 text-muted"># Response: {`{"status": "online"}`}</p>
				</div>
			</div>
		</div>
	</div>
</section>

<!-- Tips Section -->
<section class="py-12 sm:py-16">
	<div class="max-w-4xl mx-auto px-4 sm:px-6">
		<h2 class="text-xl sm:text-2xl font-bold mb-6 sm:mb-8">Tips for Best Performance</h2>
		
		<div class="grid sm:grid-cols-2 gap-4 sm:gap-6">
			<div class="neo-card p-4 sm:p-6">
				<h3 class="font-bold mb-2 text-sm sm:text-base">🔋 Battery Optimization</h3>
				<p class="text-muted-foreground text-sm sm:text-base">Disable battery optimization for the Pathway app to ensure it keeps running in the background.</p>
			</div>
			
			<div class="neo-card p-4 sm:p-6">
				<h3 class="font-bold mb-2 text-sm sm:text-base">🌐 Static IP</h3>
				<p class="text-muted-foreground text-sm sm:text-base">Consider assigning a static IP to your Android device in your router settings for consistent connectivity.</p>
			</div>
			
			<div class="neo-card p-4 sm:p-6">
				<h3 class="font-bold mb-2 text-sm sm:text-base">🔌 Keep Plugged In</h3>
				<p class="text-muted-foreground text-sm sm:text-base">For 24/7 operation, keep your device connected to power and enable "Keep screen on" in the app.</p>
			</div>
			
			<div class="neo-card p-4 sm:p-6">
				<h3 class="font-bold mb-2 text-sm sm:text-base">📱 Multiple Devices</h3>
				<p class="text-muted-foreground text-sm sm:text-base">Use multiple Android gateways for load balancing and to avoid carrier rate limits.</p>
			</div>
		</div>
	</div>
</section>
