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

<svelte:head>
	<title>Pathway - Self-Hosted SMS Gateway | Transform Android into SMS API Server</title>
	<meta name="description" content="Transform your Android device into a powerful local SMS & USSD API server. Self-hosted, privacy-focused, open source. No cloud required." />
	<meta name="keywords" content="SMS gateway, Android SMS API, self-hosted SMS, USSD API, SMS automation, local SMS server, REST API SMS, bulk SMS" />
	
	<!-- Open Graph overrides for home page -->
	<meta property="og:title" content="Pathway - Self-Hosted SMS Gateway" />
	<meta property="og:description" content="Transform your Android device into a powerful local SMS & USSD API server. Self-hosted, privacy-focused, open source." />
</svelte:head>

<!-- Hero Section -->
<section bind:this={heroSection} class="relative overflow-hidden border-b-3 border-border" aria-labelledby="hero-heading">
	<!-- Background Pattern -->
	<div class="absolute inset-0 opacity-5" aria-hidden="true">
		<div class="absolute inset-0" style="background-image: radial-gradient(circle, var(--foreground) 1px, transparent 1px); background-size: 24px 24px;"></div>
	</div>

	<div class="max-w-6xl mx-auto px-4 sm:px-6 py-16 md:py-24">
		<div class="grid md:grid-cols-2 gap-12 items-center">
			<!-- Left: Text Content -->
			<article class="space-y-8">
				<header class="hero-title opacity-0">
					<span class="inline-block px-4 py-2 bg-secondary border-3 border-border neo-shadow font-mono text-xs sm:text-sm font-bold mb-6" role="status">
						{latestVersion.toUpperCase()} NOW AVAILABLE
					</span>
					<h1 id="hero-heading" class="text-3xl sm:text-4xl md:text-5xl lg:text-6xl font-bold leading-tight tracking-tight break-words">
						Self-Hosted
						<span class="text-primary block sm:inline"> SMS Gateway</span>
					</h1>
				</header>

				<p class="hero-subtitle opacity-0 text-lg md:text-xl text-muted-foreground max-w-lg">
					Transform your Android device into a powerful local API server for SMS and USSD operations. No cloud, no subscriptions.
				</p>

				<nav class="hero-buttons opacity-0 flex flex-col sm:flex-row flex-wrap gap-3 sm:gap-4" aria-label="Primary actions">
					<a href="{base}/download" class="neo-btn px-6 sm:px-8 py-3 sm:py-4 bg-primary text-primary-foreground font-bold text-base sm:text-lg flex items-center justify-center gap-2">
						Download Now
						<ArrowRight class="w-5 h-5" aria-hidden="true" />
					</a>
					<a href="{base}/guide" class="neo-btn px-6 sm:px-8 py-3 sm:py-4 bg-background font-bold text-base sm:text-lg text-center">
						Read the Guide
					</a>
				</nav>

				<ul class="hero-subtitle opacity-0 grid grid-cols-1 sm:grid-cols-2 gap-2 sm:gap-3 pt-4" aria-label="Key benefits">
					{#each highlights as item}
						<li class="flex items-center gap-2 text-xs sm:text-sm font-medium">
							<CheckCircle class="w-4 h-4 sm:w-5 sm:h-5 text-primary shrink-0" aria-hidden="true" />
							<span class="break-words">{item}</span>
						</li>
					{/each}
				</ul>
			</article>

			<!-- Right: Visual -->
			<aside class="hero-image opacity-0 relative" aria-label="API example">
				<figure class="neo-card p-4 sm:p-6 md:p-8 overflow-hidden">
					<!-- Mock Phone/Desktop UI -->
					<div class="space-y-4">
						<div class="flex items-center gap-2 sm:gap-3 pb-4 border-b-3 border-border" aria-hidden="true">
							<div class="w-2.5 h-2.5 sm:w-3 sm:h-3 rounded-full bg-primary shrink-0"></div>
							<div class="w-2.5 h-2.5 sm:w-3 sm:h-3 rounded-full bg-secondary shrink-0"></div>
							<div class="w-2.5 h-2.5 sm:w-3 sm:h-3 rounded-full bg-accent shrink-0"></div>
							<span class="font-mono text-xs sm:text-sm ml-1 sm:ml-2 truncate">pathway-gateway</span>
						</div>

						<pre class="font-mono text-xs sm:text-sm space-y-2 overflow-x-auto" aria-label="Example API request"><code class="whitespace-pre-wrap break-all sm:break-normal sm:whitespace-pre"><span class="text-muted-foreground"># Send SMS via API</span>
<span class="text-primary">curl</span> -X POST http://192.168.1.100:8080/sms \
    -H "Authorization: Bearer YOUR_KEY" \
    -d '{`{"number": "+1234567890", "message": "Hello!"}`}'</code></pre>

						<div class="mt-4 sm:mt-6 p-3 sm:p-4 bg-muted border-3 border-border">
							<code class="font-mono text-xs sm:text-sm text-primary">{"{"}"status": "sent"{"}"}</code>
						</div>
					</div>
					<figcaption class="sr-only">Example curl command to send SMS via the Pathway API</figcaption>
				</figure>

				<!-- Decorative elements - hidden on mobile to prevent overflow -->
				<div class="absolute -top-4 -right-4 w-16 h-16 sm:w-24 sm:h-24 bg-secondary border-3 border-border -z-10 hidden sm:block" aria-hidden="true"></div>
				<div class="absolute -bottom-4 -left-4 w-12 h-12 sm:w-16 sm:h-16 bg-primary border-3 border-border -z-10 hidden sm:block" aria-hidden="true"></div>
			</aside>
		</div>
	</div>
</section>

<!-- Features Section -->
<section bind:this={featuresSection} class="py-16 md:py-24 bg-muted border-b-3 border-border" aria-labelledby="features-heading">
	<div class="max-w-6xl mx-auto px-4 sm:px-6">
		<header class="text-center mb-16">
			<h2 id="features-heading" class="text-3xl md:text-4xl font-bold mb-4">Everything You Need</h2>
			<p class="text-lg text-muted-foreground max-w-2xl mx-auto">
				A complete solution for SMS automation, from the Android gateway to the desktop dashboard.
			</p>
		</header>

		<div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6" role="list">
			{#each features as feature, i}
				<article class="feature-card opacity-0 neo-card p-6 hover:-translate-y-1 transition-transform" role="listitem">
					<div class="{feature.color} w-12 h-12 flex items-center justify-center border-3 border-border neo-shadow mb-4" aria-hidden="true">
						<feature.icon class="w-6 h-6 {feature.color === 'bg-secondary' ? 'text-secondary-foreground' : feature.color === 'bg-accent' ? 'text-accent-foreground' : 'text-primary-foreground'}" />
					</div>
					<h3 class="text-xl font-bold mb-2">{feature.title}</h3>
					<p class="text-muted-foreground">{feature.description}</p>
				</article>
			{/each}
		</div>
	</div>
</section>

<!-- CTA Section -->
<section class="py-12 sm:py-16 md:py-24" aria-labelledby="cta-heading">
	<div class="max-w-6xl mx-auto px-4 sm:px-6">
		<aside class="cta-primary p-6 sm:p-8 md:p-12 text-center border-3 border-border neo-shadow-lg">
			<h2 id="cta-heading" class="text-2xl sm:text-3xl md:text-4xl font-bold mb-4">
				Ready to Get Started?
			</h2>
			<p class="text-base sm:text-lg mb-6 sm:mb-8 max-w-2xl mx-auto">
				Download the apps, set up your gateway in minutes, and start sending SMS through your own infrastructure.
			</p>
			<nav class="flex flex-col sm:flex-row flex-wrap justify-center gap-3 sm:gap-4" aria-label="Call to action">
				<a href="{base}/download" class="neo-btn px-6 sm:px-8 py-3 sm:py-4 bg-background text-foreground font-bold text-base sm:text-lg flex items-center justify-center">
					Download Apps
					<ArrowRight class="w-5 h-5 ml-2" aria-hidden="true" />
				</a>
				<a href="https://github.com/Spit-fires/pathway" target="_blank" rel="noopener noreferrer" class="neo-btn px-6 sm:px-8 py-3 sm:py-4 bg-foreground text-background font-bold text-base sm:text-lg text-center">
					View Source
				</a>
			</nav>
		</aside>
	</div>
</section>
