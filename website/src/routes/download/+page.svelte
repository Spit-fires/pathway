<script lang="ts">
	import { onMount } from 'svelte';
	import { animate, stagger } from 'motion';
	import { Smartphone, Monitor, Apple, Globe, Download, ExternalLink } from '@lucide/svelte';

	const GITHUB_RELEASE_URL = 'https://github.com/Spit-fires/pathway/releases/latest';
	const GITHUB_DOWNLOAD_BASE = 'https://github.com/Spit-fires/pathway/releases/latest/download';
	
	let latestVersion = $state('v0.5.0');

	onMount(async () => {
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

		animate(
			'.download-card' as any,
			{ opacity: [0, 1], y: [30, 0] },
			{ duration: 0.5, delay: stagger(0.15), ease: 'easeOut' }
		);
	});

	const downloads = [
		{
			title: 'Android Gateway',
			description: 'The core SMS gateway app that runs on your Android device.',
			icon: Smartphone,
			color: 'bg-primary',
			files: [
				{ name: 'pathway-android.apk', label: 'Android APK', size: '~4 MB' }
			]
		},
		{
			title: 'Windows Desktop',
			description: 'Management dashboard for Windows 10/11.',
			icon: Monitor,
			color: 'bg-secondary',
			files: [
				{ name: 'pathway-windows-portable.exe', label: 'Portable EXE', size: '~15 MB' },
				{ name: 'pathway-windows-setup.exe', label: 'Installer', size: '~4 MB' }
			]
		},
		{
			title: 'macOS Desktop',
			description: 'Management dashboard for macOS (Apple Silicon).',
			icon: Apple,
			color: 'bg-accent',
			files: [
				{ name: 'pathway-macos.dmg', label: 'DMG Image', size: '~6 MB' }
			]
		},
		{
			title: 'Linux Desktop',
			description: 'Management dashboard for Linux distributions.',
			icon: Monitor,
			color: 'bg-primary',
			files: [
				{ name: 'pathway-linux.AppImage', label: 'AppImage', size: '~83 MB' },
				{ name: 'pathway-linux.deb', label: 'Debian Package', size: '~6 MB' }
			]
		},
		{
			title: 'Web Dashboard',
			description: 'Self-host the dashboard as a static website.',
			icon: Globe,
			color: 'bg-secondary',
			files: [
				{ name: 'pathway-web.zip', label: 'Static Build', size: '~90 KB' }
			]
		}
	];

	function getDownloadUrl(filename: string): string {
		return `${GITHUB_DOWNLOAD_BASE}/${filename}`;
	}
</script>

<svelte:head>
	<title>Download - Pathway</title>
</svelte:head>

<section class="py-16 md:py-24">
	<div class="max-w-6xl mx-auto px-4 sm:px-6">
		<!-- Header -->
		<div class="text-center mb-16">
			<span class="inline-block px-4 py-2 bg-secondary border-3 border-border neo-shadow font-mono text-sm font-bold mb-6">
				{latestVersion.toUpperCase()} • FREE & OPEN SOURCE
			</span>
			<h1 class="text-4xl md:text-5xl font-bold mb-4">Download Pathway</h1>
			<p class="text-lg text-muted-foreground max-w-2xl mx-auto">
				Get the Android gateway app and desktop dashboard for your platform. All apps are free and open source.
			</p>
		</div>

		<!-- Download Cards -->
		<div class="grid md:grid-cols-2 lg:grid-cols-3 gap-6 mb-12">
			{#each downloads as item}
				<div class="download-card opacity-0 neo-card p-6 flex flex-col">
					<div class="{item.color} w-12 h-12 flex items-center justify-center border-3 border-border neo-shadow mb-4">
						<item.icon class="w-6 h-6 {item.color === 'bg-secondary' ? 'text-black' : 'text-white'}" />
					</div>
					<h3 class="text-xl font-bold mb-2">{item.title}</h3>
					<p class="text-muted-foreground mb-6 flex-1">{item.description}</p>
					
					<div class="space-y-2">
						{#each item.files as file}
							<a 
								href={getDownloadUrl(file.name)}
								class="flex items-center justify-between p-3 bg-muted border-3 border-border hover:bg-secondary/50 transition-colors group"
							>
								<div class="flex items-center gap-2">
									<Download class="w-4 h-4 text-primary" />
									<span class="font-medium">{file.label}</span>
								</div>
								<span class="text-sm text-muted-foreground font-mono">{file.size}</span>
							</a>
						{/each}
					</div>
				</div>
			{/each}
		</div>

		<!-- GitHub Release Link -->
		<div class="text-center">
			<a 
				href={GITHUB_RELEASE_URL}
				target="_blank"
				rel="noopener noreferrer"
				class="neo-btn inline-flex items-center gap-2 px-8 py-4 bg-foreground text-background font-bold text-lg"
			>
				View All Releases on GitHub
				<ExternalLink class="w-5 h-5" />
			</a>
		</div>
	</div>
</section>

<!-- Requirements Section -->
<section class="py-16 bg-muted border-t-3 border-b-3 border-border">
	<div class="max-w-6xl mx-auto px-4 sm:px-6">
		<h2 class="text-2xl font-bold mb-8 text-center">System Requirements</h2>
		
		<div class="grid md:grid-cols-2 gap-8">
			<div class="neo-card p-6">
				<h3 class="text-xl font-bold mb-4 flex items-center gap-2">
					<Smartphone class="w-5 h-5 text-primary" />
					Android Gateway
				</h3>
				<ul class="space-y-2 text-muted-foreground">
					<li>• Android 6.0 (API 23) or higher</li>
					<li>• SMS and Phone permissions</li>
					<li>• WiFi connection to local network</li>
					<li>• Battery optimization disabled (recommended)</li>
				</ul>
			</div>
			
			<div class="neo-card p-6">
				<h3 class="text-xl font-bold mb-4 flex items-center gap-2">
					<Monitor class="w-5 h-5 text-primary" />
					Desktop Dashboard
				</h3>
				<ul class="space-y-2 text-muted-foreground">
					<li>• Windows 10/11, macOS 11+, or Linux</li>
					<li>• Same local network as Android device</li>
					<li>• 100 MB free disk space</li>
					<li>• Modern web browser (for web version)</li>
				</ul>
			</div>
		</div>
	</div>
</section>
