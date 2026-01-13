<script lang="ts">
	import './styles.css';
	import { Radio, Github, Download, BookOpen, Home, Shield, Menu, X } from '@lucide/svelte';
	import { base } from '$app/paths';

	let { children } = $props();
	let mobileMenuOpen = $state(false);

	const navLinks = [
		{ href: `${base}/`, label: 'Home', icon: Home },
		{ href: `${base}/download`, label: 'Download', icon: Download },
		{ href: `${base}/guide`, label: 'Guide', icon: BookOpen },
		{ href: `${base}/privacy`, label: 'Privacy', icon: Shield }
	];
</script>

<svelte:head>
	<title>Pathway - Self-Hosted SMS Gateway</title>
	<meta name="description" content="Transform your Android device into a powerful local SMS & USSD API server. Self-hosted, privacy-focused, and easy to use." />
</svelte:head>

<div class="min-h-screen bg-background text-foreground flex flex-col font-sans">
	<!-- Header -->
	<header class="sticky top-0 z-50 bg-background/95 backdrop-blur-sm border-b-3 border-border">
		<div class="max-w-6xl mx-auto px-4 sm:px-6 py-4">
			<div class="flex items-center justify-between">
				<!-- Logo -->
				<a href="{base}/" class="flex items-center gap-3 group">
					<div class="w-10 h-10 bg-primary flex items-center justify-center border-3 border-border neo-shadow group-hover:neo-shadow-lg transition-all">
						<Radio class="w-5 h-5 text-primary-foreground" />
					</div>
					<span class="text-xl font-bold tracking-tight">Pathway</span>
				</a>

				<!-- Desktop Navigation -->
				<nav class="hidden md:flex items-center gap-2">
					{#each navLinks as link}
						<a href={link.href} class="px-4 py-2 font-medium hover:bg-secondary transition-colors">
							{link.label}
						</a>
					{/each}
					<a href="https://github.com/Spit-fires/pathway" target="_blank" rel="noopener noreferrer" class="ml-2 neo-btn px-4 py-2 bg-foreground text-background font-bold flex items-center gap-2">
						<Github class="w-4 h-4" />
						GitHub
					</a>
				</nav>

				<!-- Mobile Menu Button -->
				<button onclick={() => mobileMenuOpen = !mobileMenuOpen} class="md:hidden p-2 border-3 border-border neo-shadow">
					{#if mobileMenuOpen}
						<X class="w-5 h-5" />
					{:else}
						<Menu class="w-5 h-5" />
					{/if}
				</button>
			</div>

			<!-- Mobile Navigation -->
			{#if mobileMenuOpen}
				<nav class="md:hidden mt-4 pt-4 border-t-3 border-border space-y-2">
					{#each navLinks as link}
						<a href={link.href} onclick={() => mobileMenuOpen = false} class="flex items-center gap-3 px-4 py-3 font-medium hover:bg-secondary border-3 border-border neo-shadow">
							<link.icon class="w-5 h-5" />
							{link.label}
						</a>
					{/each}
					<a href="https://github.com/Spit-fires/pathway" target="_blank" rel="noopener noreferrer" class="flex items-center gap-3 px-4 py-3 font-bold bg-foreground text-background border-3 border-border neo-shadow">
						<Github class="w-5 h-5" />
						GitHub
					</a>
				</nav>
			{/if}
		</div>
	</header>

	<!-- Main Content -->
	<main class="flex-1">
		{@render children()}
	</main>

	<!-- Footer -->
	<footer class="border-t-3 border-border bg-muted">
		<div class="max-w-6xl mx-auto px-4 sm:px-6 py-12">
			<div class="grid grid-cols-1 md:grid-cols-4 gap-8">
				<!-- Brand -->
				<div class="md:col-span-2">
					<div class="flex items-center gap-3 mb-4">
						<div class="w-10 h-10 bg-primary flex items-center justify-center border-3 border-border neo-shadow">
							<Radio class="w-5 h-5 text-primary-foreground" />
						</div>
						<span class="text-xl font-bold">Pathway</span>
					</div>
					<p class="text-muted-foreground max-w-md">
						Transform your Android device into a powerful local SMS & USSD API server. Self-hosted, privacy-focused, and completely open source.
					</p>
				</div>

				<!-- Links -->
				<div>
					<h3 class="font-bold mb-4 text-lg">Product</h3>
					<ul class="space-y-2">
						<li><a href="{base}/download" class="neo-link text-muted-foreground">Download</a></li>
						<li><a href="{base}/guide" class="neo-link text-muted-foreground">User Guide</a></li>
						<li><a href="https://github.com/Spit-fires/pathway" target="_blank" rel="noopener noreferrer" class="neo-link text-muted-foreground">Source Code</a></li>
					</ul>
				</div>

				<!-- Legal -->
				<div>
					<h3 class="font-bold mb-4 text-lg">Legal</h3>
					<ul class="space-y-2">
						<li><a href="{base}/privacy" class="neo-link text-muted-foreground">Privacy Policy</a></li>
						<li><a href="{base}/terms" class="neo-link text-muted-foreground">Terms of Service</a></li>
					</ul>
				</div>
			</div>

			<div class="mt-12 pt-8 border-t-3 border-border flex flex-col md:flex-row justify-between items-center gap-4">
				<p class="text-muted-foreground text-sm font-mono">
					© {new Date().getFullYear()} Pathway. MIT License.
				</p>
				<p class="text-muted-foreground text-sm">
					Made with ❤️ by <a href="https://fh.js.cool" target="_blank" rel="noopener noreferrer" class="neo-link text-primary">fh.js.cool</a>
				</p>
			</div>
		</div>
	</footer>
</div>
