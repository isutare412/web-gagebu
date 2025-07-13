import { browser } from '$app/environment';

type Theme = 'light' | 'dark';

function getSystemTheme(): Theme {
  if (!browser) return 'light';
  return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
}

function getStoredTheme(): Theme | null {
  if (!browser) return null;
  const stored = localStorage.getItem('theme');
  return stored === 'light' || stored === 'dark' ? stored : null;
}

// Initialize theme: stored preference > system preference > light
const initialTheme = getStoredTheme() || getSystemTheme();

// Theme state using Svelte 5 runes - global state approach
export const themeState = $state({
  theme: initialTheme,
  isSystemDefault: getStoredTheme() === null,
});

// Apply theme to document
function applyTheme(newTheme: Theme) {
  if (!browser) return;
  document.documentElement.setAttribute('data-theme', newTheme);
}

// Apply initial theme immediately if in browser
if (browser) {
  applyTheme(themeState.theme);
}

// Listen for system theme changes only if using system default
if (browser && themeState.isSystemDefault) {
  const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
  const handleSystemThemeChange = (e: MediaQueryListEvent) => {
    if (themeState.isSystemDefault) {
      themeState.theme = e.matches ? 'dark' : 'light';
    }
  };
  mediaQuery.addEventListener('change', handleSystemThemeChange);
}

// Theme actions
export function toggleTheme() {
  const newTheme = themeState.theme === 'light' ? 'dark' : 'light';
  setTheme(newTheme);
}

export function setTheme(newTheme: Theme) {
  themeState.theme = newTheme;
  themeState.isSystemDefault = false;

  if (browser) {
    localStorage.setItem('theme', newTheme);
    applyTheme(newTheme);
  }
}

export function resetToSystem() {
  if (browser) {
    localStorage.removeItem('theme');
    themeState.theme = getSystemTheme();
    themeState.isSystemDefault = true;
    applyTheme(themeState.theme);
  }
}

// Convenience getter functions
export function getCurrentTheme(): Theme {
  return themeState.theme;
}

export function isSystemDefault(): boolean {
  return themeState.isSystemDefault;
}
