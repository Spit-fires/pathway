<script lang="ts">
	import { onMount } from 'svelte';
	import { animate, stagger } from 'motion';
	import { Smartphone, Monitor, Zap, Shield, Globe, MessageSquare, ArrowRight, CheckCircle } from '@lucide/svelte';
	import { base } from '$app/paths';

	let heroSection: HTMLElement;
	let featuresSection: HTMLElement;
	let mounted = $state(false);
	let latestVersion = $state('v0.5.0');

	onMount(async () => {
		mounted = true;
		
		// Fetch latest version from GitHub
		try {
			const res = await fetch('https://api.github.com/repos/Spit-fires/pathway/releases/latest');
			if (res.ok) {
				const data = await res.json();
				latestVersion = data.tag_name || 'v0.5.0';
			}
		} catch (e) {
			// Keep default version on error
		}
		
		// Animate hero elements
		animate(
			'.hero-title' as any,
			{ opacity: [0, 1], y: [30, 0] },
			{ duration: 0.6, ease: 'easeOut' }
		);
		
		animate(
			'.hero-subtitle' as any,
			{ opacity: [0, 1], y: [20, 0] },
			{ duration: 0.6, delay: 0.2, ease: 'easeOut' }
		);
		
		animate(
			'.hero-buttons' as any,
			{ opacity: [0, 1], y: [20, 0] },
			{ duration: 0.6, delay: 0.4, ease: 'easeOut' }
		);
		
		animate(
			'.hero-image' as any,
			{ opacity: [0, 1], scale: [0.95, 1] },
			{ duration: 0.8, delay: 0.3, ease: 'easeOut' }
		);

		// Animate features on scroll
		const observer = new IntersectionObserver((entries) => {
			entries.forEach(entry => {
				if (entry.isIntersecting) {
					animate(
						'.feature-card' as any,
						{ opacity: [0, 1], y: [40, 0] },
						{ duration: 0.5, delay: stagger(0.1), ease: 'easeOut' }
					);
					observer.disconnect();
				}
			});
		}, { threshold: 0.2 });

		if (featuresSection) observer.observe(featuresSection);
	});

	const features = [
		{
			icon: MessageSquare,
			title: 'SMS Gateway',
			description: 'Send and manage SMS messages through a simple REST API. Supports Unicode and multipart messages.',
			color: 'bg-primary'
		},
		{
			icon: Smartphone,
			title: 'Android App',
			description: 'Turn any Android phone into an SMS gateway. Easy setup, runs 24/7 in the background.',
			color: 'bg-secondary'
		},
		{
			icon: Monitor,
			title: 'Desktop Dashboard',
			description: 'Manage multiple devices, send campaigns, and view logs from a beautiful desktop app.',
			color: 'bg-accent'
		},
		{
			icon: Shield,
			title: 'Self-Hosted',
			description: 'Your data stays on your network. No cloud required, complete privacy and control.',
			color: 'bg-primary'
		},
		{
			icon: Zap,
			title: 'Dual SIM Support',
			description: 'Intelligent SIM selection with support for dual SIM devices and load balancing.',
			color: 'bg-secondary'
		},
		{
			icon: Globe,
			title: 'Open Source',
			description: 'Fully open source under MIT license. Audit, modify, and contribute freely.',
			color: 'bg-accent'
		}
	];

	const highlights = [
		'No cloud subscription required',
		'Works on local network',
		'Cross-platform desktop apps',
		'Modern API design'
	];
</script>

<!-- Hero Section -->
<section bind:this={heroSection} class="relative overflow-hidden border-b-3 border-border">
	<!-- Background Pattern -->
	<div class="absolute inset-0 opacity-5">
		<div class="absolute inset-0" style="background-image: radial-gradient(circle, var(--foreground) 1px, transparent 1px); background-size: 24px 24px;"></div>
	</div>

	<div class="max-w-6xl mx-auto px-4 sm:px-6 py-16 md:py-24">
		<div class="grid md:grid-cols-2 gap-12 items-center">
			<!-- Left: Text Content -->
			<div class="space-y-8">
				<div class="hero-title opacity-0">
					<span class="inline-block px-4 py-2 bg-secondary border-3 border-border neo-shadow font-mono text-sm font-bold mb-6">
						{latestVersion.toUpperCase()} NOW AVAILABLE
					</span>
					<h1 class="text-4xl md:text-5xl lg:text-6xl font-bold leading-tight tracking-tight">
						Self-Hosted
						<span class="text-primary"> SMS Gateway</span>
					</h1>
				</div>

				<p class="hero-subtitle opacity-0 text-lg md:text-xl text-muted-foreground max-w-lg">
					Transform your Android device into a powerful local API server for SMS and USSD operations. No cloud, no subscriptions.
				</p>

				<div class="hero-buttons opacity-0 flex flex-wrap gap-4">
					<a href="{base}/download" class="neo-btn px-8 py-4 bg-primary text-primary-foreground font-bold text-lg flex items-center gap-2">
						Download Now
						<ArrowRight class="w-5 h-5" />
					</a>
					<a href="{base}/guide" class="neo-btn px-8 py-4 bg-background font-bold text-lg">
						Read the Guide
					</a>
				</div>

				<ul class="hero-subtitle opacity-0 grid grid-cols-2 gap-3 pt-4">
					{#each highlights as item}
						<li class="flex items-center gap-2 text-sm font-medium">
							<CheckCircle class="w-5 h-5 text-primary shrink-0" />
							{item}
						</li>
					{/each}
				</ul>
			</div>

			<!-- Right: Visual -->
			<div class="hero-image opacity-0 relative">
				<div class="neo-card p-6 md:p-8">
					<!-- Mock Phone/Desktop UI -->
					<div class="space-y-4">
						<div class="flex items-center gap-3 pb-4 border-b-3 border-border">
							<div class="w-3 h-3 rounded-full bg-primary"></div>
							<div class="w-3 h-3 rounded-full bg-secondary"></div>
							<div class="w-3 h-3 rounded-full bg-accent"></div>
							<span class="font-mono text-sm ml-2">pathway-gateway</span>
						</div>

						<div class="font-mono text-sm space-y-2">
							<p class="text-muted-foreground"># Send SMS via API</p>
							<p><span class="text-primary">curl</span> -X POST http://192.168.1.100:8080/sms \</p>
							<p class="pl-4">-H "Authorization: Bearer YOUR_KEY" \</p>
							<p class="pl-4">-d '{`{"number": "+1234567890", "message": "Hello!"}`}'</p>
						</div>

						<div class="mt-6 p-4 bg-muted border-3 border-border">
							<p class="font-mono text-sm text-primary">{"{"}"status": "sent"{"}"}</p>
						</div>
					</div>
				</div>

				<!-- Decorative elements -->
				<div class="absolute -top-4 -right-4 w-24 h-24 bg-secondary border-3 border-border -z-10"></div>
				<div class="absolute -bottom-4 -left-4 w-16 h-16 bg-primary border-3 border-border -z-10"></div>
			</div>
		</div>
	</div>
</section>

<!-- Features Section -->
<section bind:this={featuresSection} class="py-16 md:py-24 bg-muted border-b-3 border-border">
	<div class="max-w-6xl mx-auto px-4 sm:px-6">
		<div class="text-center mb-16">
			<h2 class="text-3xl md:text-4xl font-bold mb-4">Everything You Need</h2>
			<p class="text-lg text-muted-foreground max-w-2xl mx-auto">
				A complete solution for SMS automation, from the Android gateway to the desktop dashboard.
			</p>
		</div>

		<div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
			{#each features as feature, i}
				<div class="feature-card opacity-0 neo-card p-6 hover:-translate-y-1 transition-transform">
					<div class="{feature.color} w-12 h-12 flex items-center justify-center border-3 border-border neo-shadow mb-4">
						<feature.icon class="w-6 h-6 {feature.color === 'bg-secondary' ? 'text-secondary-foreground' : 'text-primary-foreground'}" />
					</div>
					<h3 class="text-xl font-bold mb-2">{feature.title}</h3>
					<p class="text-muted-foreground">{feature.description}</p>
				</div>
			{/each}
		</div>
	</div>
</section>

<!-- CTA Section -->
<section class="py-16 md:py-24">
	<div class="max-w-6xl mx-auto px-4 sm:px-6">
		<div class="neo-card p-8 md:p-12 text-center bg-primary">
			<h2 class="text-3xl md:text-4xl font-bold text-primary-foreground mb-4">
				Ready to Get Started?
			</h2>
			<p class="text-lg text-white/90 mb-8 max-w-2xl mx-auto">
				Download the apps, set up your gateway in minutes, and start sending SMS through your own infrastructure.
			</p>
			<div class="flex flex-wrap justify-center gap-4">
				<a href="{base}/download" class="inline-block px-8 py-4 bg-background text-foreground font-bold text-lg border-3 border-border neo-shadow hover:neo-shadow-lg hover:-translate-y-0.5 transition-all">
					Download Apps
					<ArrowRight class="w-5 h-5 inline ml-2" />
				</a>
				<a href="https://github.com/Spit-fires/pathway" target="_blank" rel="noopener noreferrer" class="inline-block px-8 py-4 bg-white text-primary font-bold text-lg border-3 border-border neo-shadow hover:neo-shadow-lg hover:-translate-y-0.5 transition-all">
					View Source
				</a>
			</div>
		</div>
	</div>
</section>
