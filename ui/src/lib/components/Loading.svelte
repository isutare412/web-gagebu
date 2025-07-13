<script lang="ts">
  let { size = 'lg', message = 'Loading...' } = $props<{
    size?: 'xs' | 'sm' | 'md' | 'lg';
    message?: string;
  }>();
</script>

<div class="flex flex-col items-center justify-center py-8">
  <span class="loading loading-spinner loading-{size}"></span>
  {#if message}
    <p class="text-base-content/70 mt-2">{message}</p>
  {/if}
</div>
