<script lang="ts">
	import './styles.css';
	import { Radio, Github, Download, BookOpen, Home, Shield, Menu, X } from '@lucide/svelte';
	import { base } from '$app/paths';
	import { page } from '$app/stores';

	let { children } = $props();
	let mobileMenuOpen = $state(false);

	const navLinks = [
		{ href: `${base}/`, label: 'Home', icon: Home },
		{ href: `${base}/download`, label: 'Download', icon: Download },
		{ href: `${base}/guide`, label: 'Guide', icon: BookOpen },
		{ href: `${base}/privacy`, label: 'Privacy', icon: Shield }
	];
	
	// Site-wide SEO constants
	const siteName = 'Pathway';
	const siteDescription = 'Transform your Android device into a powerful local SMS & USSD API server. Self-hosted, privacy-focused, and easy to use.';
	const siteUrl = 'https://spit-fires.github.io/pathway';
	const siteImage = `${siteUrl}/og-image.png`;
	const siteKeywords = 'SMS gateway, Android SMS API, self-hosted SMS, USSD API, SMS automation, local SMS server, open source SMS';
</script>

<svelte:head>
	<title>Pathway - Self-Hosted SMS Gateway</title>
	<meta name="description" content={siteDescription} />
	<meta name="keywords" content={siteKeywords} />
	
	<!-- Canonical URL -->
	<link rel="canonical" href={siteUrl} />
	
	<!-- Open Graph -->
	<meta property="og:title" content="Pathway - Self-Hosted SMS Gateway" />
	<meta property="og:description" content={siteDescription} />
	<meta property="og:url" content={siteUrl} />
	<meta property="og:image" content={siteImage} />
	
	<!-- Twitter Card -->
	<meta name="twitter:title" content="Pathway - Self-Hosted SMS Gateway" />
	<meta name="twitter:description" content={siteDescription} />
	<meta name="twitter:image" content={siteImage} />
	
	<!-- JSON-LD Structured Data -->
	{@html `<script type="application/ld+json">
	{
		"@context": "https://schema.org",
		"@type": "SoftwareApplication",
		"name": "Pathway",
		"description": "${siteDescription}",
		"applicationCategory": "CommunicationApplication",
		"operatingSystem": "Android, Windows, macOS, Linux",
		"offers": {
			"@type": "Offer",
			"price": "0",
			"priceCurrency": "USD"
		},
		"license": "https://opensource.org/licenses/MIT",
		"author": {
			"@type": "Organization",
			"name": "Pathway Team",
			"url": "https://github.com/Spit-fires/pathway"
		}
	}
	</script>`}
</svelte:head>

<!-- Skip to main content link for accessibility -->
<a href="#main-content" class="skip-link">Skip to main content</a>

<div class="min-h-screen bg-background text-foreground flex flex-col font-sans overflow-x-hidden">
	<!-- Header -->
	<header class="sticky top-0 z-50 bg-background/95 backdrop-blur-sm border-b-3 border-border">
		<div class="max-w-6xl mx-auto px-4 sm:px-6 py-4">
			<nav class="flex items-center justify-between" aria-label="Main navigation">
				<!-- Logo -->
				<a href="{base}/" class="flex items-center gap-3 group" aria-label="Pathway Home">
					<div class="w-10 h-10 bg-primary flex items-center justify-center border-3 border-border neo-shadow group-hover:neo-shadow-lg transition-all" aria-hidden="true">
						<Radio class="w-5 h-5 text-primary-foreground" />
					</div>
					<span class="text-xl font-bold tracking-tight">Pathway</span>
				</a>

				<!-- Desktop Navigation -->
				<div class="hidden md:flex items-center gap-2">
					{#each navLinks as link}
						<a href={link.href} class="px-4 py-2 font-medium hover:bg-secondary transition-colors">
							{link.label}
						</a>
					{/each}
					<a href="https://github.com/Spit-fires/pathway" target="_blank" rel="noopener noreferrer" class="ml-2 neo-btn px-4 py-2 bg-foreground text-background font-bold flex items-center gap-2" aria-label="View Pathway on GitHub">
						<Github class="w-4 h-4" aria-hidden="true" />
						GitHub
					</a>
				</div>

				<!-- Mobile Menu Button -->
				<button 
					onclick={() => mobileMenuOpen = !mobileMenuOpen} 
					class="md:hidden p-2 border-3 border-border neo-shadow"
					aria-expanded={mobileMenuOpen}
					aria-controls="mobile-menu"
					aria-label={mobileMenuOpen ? 'Close menu' : 'Open menu'}
				>
					{#if mobileMenuOpen}
						<X class="w-5 h-5" aria-hidden="true" />
					{:else}
						<Menu class="w-5 h-5" aria-hidden="true" />
					{/if}
				</button>
			</nav>

			<!-- Mobile Navigation -->
			{#if mobileMenuOpen}
				<nav id="mobile-menu" class="md:hidden mt-4 pt-4 border-t-3 border-border space-y-2" aria-label="Mobile navigation">
					{#each navLinks as link}
						<a href={link.href} onclick={() => mobileMenuOpen = false} class="flex items-center gap-3 px-4 py-3 font-medium hover:bg-secondary border-3 border-border neo-shadow">
							<link.icon class="w-5 h-5" aria-hidden="true" />
							{link.label}
						</a>
					{/each}
					<a href="https://github.com/Spit-fires/pathway" target="_blank" rel="noopener noreferrer" class="flex items-center gap-3 px-4 py-3 font-bold bg-foreground text-background border-3 border-border neo-shadow">
						<Github class="w-5 h-5" aria-hidden="true" />
						GitHub
					</a>
				</nav>
			{/if}
		</div>
	</header>

	<!-- Main Content -->
	<main id="main-content" class="flex-1">
		{@render children()}
	</main>

	<!-- Footer -->
	<footer class="border-t-3 border-border bg-muted">
		<div class="max-w-6xl mx-auto px-4 sm:px-6 py-12">
			<div class="grid grid-cols-1 md:grid-cols-4 gap-8">
				<!-- Brand -->
				<div class="md:col-span-2">
					<div class="flex items-center gap-3 mb-4">
						<div class="w-10 h-10 bg-primary flex items-center justify-center border-3 border-border neo-shadow" aria-hidden="true">
							<Radio class="w-5 h-5 text-primary-foreground" />
						</div>
						<span class="text-xl font-bold">Pathway</span>
					</div>
					<p class="text-muted-foreground max-w-md">
						Transform your Android device into a powerful local SMS & USSD API server. Self-hosted, privacy-focused, and completely open source.
					</p>
				</div>

				<!-- Links -->
				<nav aria-label="Product links">
					<h2 class="font-bold mb-4 text-lg">Product</h2>
					<ul class="space-y-2" role="list">
						<li><a href="{base}/download" class="neo-link text-muted-foreground">Download</a></li>
						<li><a href="{base}/guide" class="neo-link text-muted-foreground">User Guide</a></li>
						<li><a href="https://github.com/Spit-fires/pathway" target="_blank" rel="noopener noreferrer" class="neo-link text-muted-foreground">Source Code</a></li>
					</ul>
				</nav>

				<!-- Legal -->
				<nav aria-label="Legal links">
					<h2 class="font-bold mb-4 text-lg">Legal</h2>
					<ul class="space-y-2" role="list">
						<li><a href="{base}/privacy" class="neo-link text-muted-foreground">Privacy Policy</a></li>
						<li><a href="{base}/terms" class="neo-link text-muted-foreground">Terms of Service</a></li>
					</ul>
				</nav>
			</div>

			<div class="mt-12 pt-8 border-t-3 border-border flex flex-col md:flex-row justify-between items-center gap-4">
				<p class="text-muted-foreground text-sm font-mono">
					<small>© {new Date().getFullYear()} Pathway. MIT License.</small>
				</p>
				<p class="text-muted-foreground text-sm">
					<small>Made with ❤️ by <a href="https://fh.js.cool" target="_blank" rel="noopener noreferrer" class="neo-link text-primary">fh.js.cool</a></small>
				</p>
			</div>
		</div>
	</footer>
</div>
