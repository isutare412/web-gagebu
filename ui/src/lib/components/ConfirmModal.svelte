<script lang="ts">
  import Loading from './Loading.svelte';

  let {
    show,
    title,
    message,
    confirmText = 'Delete',
    cancelText = 'Cancel',
    confirmButtonClass = 'btn-error',
    loading = false,
    onConfirm,
    onCancel,
  }: {
    show: boolean;
    title: string;
    message: string;
    confirmText?: string;
    cancelText?: string;
    confirmButtonClass?: string;
    loading?: boolean;
    onConfirm: () => void;
    onCancel: () => void;
  } = $props();

  function handleConfirm() {
    if (loading) return;
    onConfirm();
  }

  function handleCancel() {
    if (loading) return;
    onCancel();
  }
</script>

{#if show}
  <div class="modal modal-open">
    <div class="modal-box">
      <h3 class="text-lg font-bold">{title}</h3>
      <p class="py-4">{message}</p>
      <div class="modal-action">
        <button class="btn" onclick={handleCancel} disabled={loading}>
          {cancelText}
        </button>
        <button class="btn {confirmButtonClass}" onclick={handleConfirm} disabled={loading}>
          {#if loading}
            <Loading size="sm" />
          {/if}
          {confirmText}
        </button>
      </div>
    </div>
  </div>
{/if}
