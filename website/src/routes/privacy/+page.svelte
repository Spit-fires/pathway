<script lang="ts">
	import { base } from '$app/paths';
</script>

<svelte:head>
	<title>Privacy Policy - Pathway</title>
</svelte:head>

<section class="py-16 md:py-24">
	<div class="max-w-4xl mx-auto px-4 sm:px-6">
		<div class="mb-12">
			<span class="inline-block px-4 py-2 bg-accent border-3 border-border neo-shadow font-mono text-sm font-bold text-accent-foreground mb-6">
				LEGAL
			</span>
			<h1 class="text-4xl md:text-5xl font-bold mb-4">Privacy Policy</h1>
			<p class="text-muted-foreground">Last updated: January 2026</p>
		</div>

		<div class="prose prose-lg max-w-none space-y-8">
			<div class="neo-card p-6 md:p-8">
				<h2 class="text-2xl font-bold mb-4">Overview</h2>
				<p class="text-muted-foreground">
					Pathway is a self-hosted SMS gateway solution. We are committed to protecting your privacy. This policy explains how the Pathway applications handle your data.
				</p>
			</div>

			<div class="neo-card p-6 md:p-8">
				<h2 class="text-2xl font-bold mb-4">Data Collection</h2>
				<p class="text-muted-foreground mb-4">
					<strong class="text-foreground">Pathway does not collect, store, or transmit any personal data to external servers.</strong>
				</p>
				<p class="text-muted-foreground">
					All data processed by Pathway, including:
				</p>
				<ul class="list-disc list-inside text-muted-foreground mt-2 space-y-1">
					<li>Phone numbers</li>
					<li>SMS message content</li>
					<li>USSD codes and responses</li>
					<li>Device information</li>
					<li>API keys and configuration</li>
				</ul>
				<p class="text-muted-foreground mt-4">
					...remains entirely on your local devices and network. We have no access to this data.
				</p>
			</div>

			<div class="neo-card p-6 md:p-8">
				<h2 class="text-2xl font-bold mb-4">Local Data Storage</h2>
				<p class="text-muted-foreground">
					The Android gateway app stores configuration locally on your device, including:
				</p>
				<ul class="list-disc list-inside text-muted-foreground mt-2 space-y-1">
					<li>Your chosen API key (stored in app preferences)</li>
					<li>Port configuration</li>
					<li>SIM preferences</li>
				</ul>
				<p class="text-muted-foreground mt-4">
					The desktop dashboard stores device configurations in your operating system's local storage. This data never leaves your computer.
				</p>
			</div>

			<div class="neo-card p-6 md:p-8">
				<h2 class="text-2xl font-bold mb-4">Network Communication</h2>
				<p class="text-muted-foreground">
					Communication between the desktop dashboard and Android gateway occurs only on your local network. The applications do not:
				</p>
				<ul class="list-disc list-inside text-muted-foreground mt-2 space-y-1">
					<li>Connect to any cloud services</li>
					<li>Send analytics or telemetry data</li>
					<li>Phone home to any servers</li>
					<li>Require internet access for core functionality</li>
				</ul>
			</div>

			<div class="neo-card p-6 md:p-8">
				<h2 class="text-2xl font-bold mb-4">Third-Party Services</h2>
				<p class="text-muted-foreground">
					Pathway does not integrate with or send data to any third-party services. The only external communication is:
				</p>
				<ul class="list-disc list-inside text-muted-foreground mt-2 space-y-1">
					<li>SMS messages sent through your mobile carrier</li>
					<li>USSD requests processed by your mobile carrier</li>
				</ul>
				<p class="text-muted-foreground mt-4">
					These communications are between your device and your carrier, not Pathway.
				</p>
			</div>

			<div class="neo-card p-6 md:p-8">
				<h2 class="text-2xl font-bold mb-4">Android Permissions</h2>
				<p class="text-muted-foreground">
					The Android app requires the following permissions:
				</p>
				<ul class="list-disc list-inside text-muted-foreground mt-2 space-y-1">
					<li><strong class="text-foreground">SEND_SMS:</strong> To send SMS messages via the API</li>
					<li><strong class="text-foreground">READ_PHONE_STATE:</strong> To detect SIM cards and carrier info</li>
					<li><strong class="text-foreground">CALL_PHONE:</strong> To run USSD codes</li>
					<li><strong class="text-foreground">ACCESS_WIFI_STATE:</strong> To display your local IP address</li>
				</ul>
				<p class="text-muted-foreground mt-4">
					These permissions are used solely for the stated functionality and never to collect or transmit personal data.
				</p>
			</div>

			<div class="neo-card p-6 md:p-8">
				<h2 class="text-2xl font-bold mb-4">Open Source</h2>
				<p class="text-muted-foreground">
					Pathway is fully open source. You can audit the source code at any time to verify our privacy claims:
				</p>
				<p class="mt-4">
					<a href="https://github.com/Spit-fires/pathway" target="_blank" rel="noopener noreferrer" class="neo-link text-primary font-bold">
						github.com/Spit-fires/pathway
					</a>
				</p>
			</div>

			<div class="neo-card p-6 md:p-8">
				<h2 class="text-2xl font-bold mb-4">Contact</h2>
				<p class="text-muted-foreground">
					If you have any questions about this privacy policy, please open an issue on our GitHub repository or contact the maintainers.
				</p>
			</div>
		</div>
	</div>
</section>
