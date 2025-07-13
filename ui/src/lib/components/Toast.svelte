<script lang="ts">
  import type { Toast } from '$lib/stores/toast.svelte';
  import { removeToast } from '$lib/stores/toast.svelte';
  import { onMount } from 'svelte';
  import { linear } from 'svelte/easing';
  import { Tween } from 'svelte/motion';

  let { toast, onDismiss } = $props<{ toast: Toast; onDismiss?: (id: string) => void }>();

  // Create initial Tween instance for smooth progress animation
  let progress = $state(
    new Tween(100, {
      duration: toast.duration,
      easing: linear,
    })
  );

  let timeoutId: number | null = null;
  let startTime: number = Date.now();
  let pausedAt: number | null = null;
  let totalPausedTime: number = 0;

  // Internal dismiss function that handles cleanup
  function dismissToast() {
    if (timeoutId) {
      clearTimeout(timeoutId);
      timeoutId = null;
    }

    if (onDismiss) {
      onDismiss(toast.id);
    } else {
      removeToast(toast.id);
    }
  }

  onMount(() => {
    startTime = Date.now();

    // Start the animation from 100% to 0%
    progress.set(0);

    // Auto-dismiss after duration
    timeoutId = setTimeout(() => {
      dismissToast();
    }, toast.duration);

    // Cleanup on component destroy
    return () => {
      if (timeoutId) {
        clearTimeout(timeoutId);
      }
    };
  });

  function handleMouseEnter() {
    if (!pausedAt && timeoutId) {
      clearTimeout(timeoutId);
      pausedAt = Date.now();
      // Stop the current animation by setting it to its current value
      progress.set(progress.current);
    }
  }

  function handleMouseLeave() {
    if (pausedAt) {
      const pauseDuration = Date.now() - pausedAt;
      totalPausedTime += pauseDuration;
      pausedAt = null;

      // Calculate remaining time
      const elapsed = Date.now() - startTime - totalPausedTime;
      const remainingTime = Math.max(0, toast.duration - elapsed);

      if (remainingTime > 0) {
        // Create a new Tween instance for the remaining duration
        progress = new Tween(progress.current, {
          duration: remainingTime,
          easing: linear,
        });

        // Resume animation from current position to 0
        progress.set(0);

        // Set new timeout for remaining time
        timeoutId = setTimeout(() => {
          dismissToast();
        }, remainingTime);
      } else {
        dismissToast();
      }
    }
  }

  function handleDismiss() {
    if (toast.dismissible) {
      dismissToast();
    }
  }

  // Get the appropriate alert class based on toast type
  const getAlertClass = (type: string) => {
    switch (type) {
      case 'success':
        return 'alert-success';
      case 'error':
        return 'alert-error';
      case 'warning':
        return 'alert-warning';
      case 'info':
        return 'alert-info';
      default:
        return 'alert-info';
    }
  };

  // Get the appropriate icon for each toast type
  const getIcon = (type: string) => {
    switch (type) {
      case 'success':
        return 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z';
      case 'error':
        return 'M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z';
      case 'warning':
        return 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4.5c-.77-.833-2.694-.833-3.464 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z';
      case 'info':
        return 'M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z';
      default:
        return 'M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z';
    }
  };
</script>

<div
  class="alert {getAlertClass(
    toast.type
  )} relative w-full transform overflow-hidden shadow-lg transition-all duration-300 ease-in-out md:w-fit md:max-w-4xl md:min-w-64"
  role="alert"
  onmouseenter={handleMouseEnter}
  onmouseleave={handleMouseLeave}
>
  <svg
    xmlns="http://www.w3.org/2000/svg"
    class="h-6 w-6 shrink-0 stroke-current"
    fill="none"
    viewBox="0 0 24 24"
  >
    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d={getIcon(toast.type)} />
  </svg>

  <div class="min-w-0 flex-grow">
    {#if toast.title}
      <div class="font-bold">{toast.title}</div>
    {/if}
    <div class={toast.title ? 'text-sm' : ''}>{toast.message}</div>
  </div>

  <div class="flex items-center gap-2">
    {#if toast.dismissible}
      <button
        class="btn btn-sm btn-ghost btn-circle"
        onclick={handleDismiss}
        aria-label="Close toast"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          class="h-4 w-4"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M6 18L18 6M6 6l12 12"
          />
        </svg>
      </button>
    {/if}
  </div>

  <!-- Progress bar - now always shown since all toasts have duration -->
  <div class="absolute bottom-0 left-0 h-1 w-full bg-black/10">
    <div class="h-full bg-current" style="width: {progress.current}%"></div>
  </div>
</div>
