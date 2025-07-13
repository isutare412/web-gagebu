import type { components } from '$lib/api/schema';

type UserView = components['schemas']['UserView'];

// User state using Svelte 5 runes - object approach for cross-module sharing
export const userState = $state({
  user: null as UserView | null,
  isLoading: true,
});

// Derived state - wrapped in a function to expose current value
export function isAuthenticated() {
  return !!userState.user;
}

export function setUser(userData: UserView | null) {
  userState.user = userData;
  userState.isLoading = false;
}
