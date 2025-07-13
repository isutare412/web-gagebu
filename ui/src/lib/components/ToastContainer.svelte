<script lang="ts">
  import { browser } from '$app/environment';
  import { removeToast, toastState } from '$lib/stores/toast.svelte';
  import { onMount } from 'svelte';
  import { flip } from 'svelte/animate';
  import { fly } from 'svelte/transition';
  import Toast from './Toast.svelte';

  let isMobile = browser ? window.innerWidth < 768 : false;

  onMount(() => {
    const checkMobile = () => {
      isMobile = window.innerWidth < 768; // md breakpoint
    };

    checkMobile();
    window.addEventListener('resize', checkMobile);

    return () => {
      window.removeEventListener('resize', checkMobile);
    };
  });
</script>

<div
  class="fixed right-4 bottom-4 left-4 z-50 flex flex-col items-center space-y-2 md:top-4 md:right-4 md:bottom-auto md:left-auto md:w-auto md:items-end"
>
  {#each toastState.toasts as toast (toast.id)}
    <div
      animate:flip={{ duration: 300 }}
      in:fly={isMobile ? { y: 100, duration: 300 } : { x: 300, duration: 300 }}
      out:fly={isMobile ? { y: 100, duration: 200 } : { x: 300, duration: 200 }}
      class="w-full md:w-auto"
    >
      <Toast {toast} onDismiss={removeToast} />
    </div>
  {/each}
</div>
