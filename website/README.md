# Pathway Website

Product showcase website for [Pathway](https://github.com/Spit-fires/pathway) - a self-hosted SMS & USSD Gateway solution.

## Overview

This is the public-facing website for the Pathway project, built with SvelteKit and featuring a neo brutalism design theme with Motion scroll animations.

### Pages

- **Home** - Hero section with features overview
- **Download** - Platform-specific download links to GitHub releases
- **Guide** - Installation and usage instructions
- **Privacy** - Privacy policy
- **Terms** - Terms of service

## Development

### Prerequisites

- Node.js 20+
- pnpm

### Setup

```sh
# Install dependencies
pnpm install

# Start development server
pnpm run dev

# Type check
pnpm run check

# Build for production
pnpm run build

# Preview production build
pnpm run preview
```

## Deployment

The website is automatically deployed to GitHub Pages when changes are pushed to the `main` branch (in the `/website` directory).

### GitHub Pages Configuration

The workflow supports custom base paths via the `WEBSITE_BASE_PATH` repository variable:

- **Default**: Uses `/{repository-name}` (e.g., `/pathway`)
- **Custom domain**: Set `WEBSITE_BASE_PATH` to empty string `""` in repository variables

### Manual Deployment

To trigger a manual deployment, use the "Run workflow" button in the GitHub Actions tab.

## Tech Stack

- [SvelteKit](https://kit.svelte.dev/) - Web framework
- [Tailwind CSS](https://tailwindcss.com/) - Styling
- [Motion](https://motion.dev/) - Animations
- [Lucide Icons](https://lucide.dev/) - Icons
- [@sveltejs/adapter-static](https://kit.svelte.dev/docs/adapter-static) - Static site generation
