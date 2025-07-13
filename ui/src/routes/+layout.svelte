<script lang="ts">
  import { api } from '$lib/api/client';
  import Loading from '$lib/components/Loading.svelte';
  import Navigation from '$lib/components/Navigation.svelte';
  import ToastContainer from '$lib/components/ToastContainer.svelte';
  import { showApiErrorToast } from '$lib/stores/toast.svelte';
  import { setUser, userState } from '$lib/stores/user.svelte';
  import { onMount } from 'svelte';
  import '../app.css';

  let { children } = $props();

  onMount(async () => {
    try {
      const response = await api.getCurrentUser();

      if (response.error) {
        showApiErrorToast(response.error, 'Failed to fetch user');
        setUser(null);
        return;
      }

      if (response.data?.user) {
        setUser(response.data.user);
      } else {
        setUser(null);
      }
    } catch (error) {
      showApiErrorToast(error, 'Failed to fetch user');
      setUser(null);
    }
  });
</script>

<div class="bg-base-200 min-h-screen">
  <Navigation />
  <main class="mx-auto max-w-4xl p-4">
    {#if userState.isLoading}
      <Loading size="lg" message="Loading application..." />
    {:else}
      {@render children()}
    {/if}
  </main>
  <ToastContainer />
</div>
