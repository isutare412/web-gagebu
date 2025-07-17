<script lang="ts">
  import { goto } from '$app/navigation';
  import { formatDateISO } from '$lib/utils/date';

  interface RecordData {
    id?: string;
    summary?: string;
    date?: string;
    recordType?: 'INCOME' | 'EXPENSE';
    amount?: number | string;
    category?: string;
    userNickname?: string;
    userPictureUrl?: string;
  }

  interface Props {
    record: RecordData;
    accountBookId?: string;
    showActions?: boolean;
    isPreview?: boolean;
  }

  let { record, accountBookId, showActions = true, isPreview = false }: Props = $props();

  // Convert amount to number for display - make it reactive
  const displayAmount = $derived(
    typeof record.amount === 'string' ? Number(record.amount) : record.amount
  );

  // Determine if the card should be clickable
  const isClickable = $derived(showActions && accountBookId && record.id && !isPreview);

  // Handle card click
  function handleCardClick() {
    if (isClickable) {
      goto(`/account-books/${accountBookId}/records/${record.id}`);
    }
  }

  // Determine which element to use
  const elementType = $derived(isClickable ? 'button' : 'div');
  const elementProps = $derived(
    isClickable
      ? {
          type: 'button',
          onclick: handleCardClick,
          'aria-label': `View record details for ${record.summary || 'No summary'}`,
        }
      : {}
  );
</script>

<svelte:element
  this={elementType}
  {...elementProps}
  class="card bg-base-100 border-base-300 border shadow-sm {isPreview
    ? ''
    : 'transition-shadow hover:shadow-md'} {isClickable
    ? 'hover:border-primary/40 hover:bg-base-200/50 group w-full cursor-pointer rounded-lg p-0 text-left transition-all duration-200 hover:shadow-lg'
    : ''}"
>
  <div class="card-body p-3 sm:p-4">
    <!-- Mobile Layout (< sm) -->
    <div class="sm:hidden">
      <!-- Summary and date at top -->
      <div class="mb-2">
        <h3
          class="group-hover:text-primary mb-1 text-base font-semibold transition-colors duration-200"
        >
          {record.summary || 'No summary'}
        </h3>
        <div class="text-base-content/70 text-xs">
          {record.date ? formatDateISO(record.date) : 'No date'}
        </div>
      </div>

      <!-- User info and category -->
      <div class="mb-2 flex items-center gap-2">
        {#if record.userPictureUrl}
          <div class="avatar flex-shrink-0">
            <div class="h-8 w-8 rounded-full">
              <img src={record.userPictureUrl} alt={record.userNickname || 'User'} />
            </div>
          </div>
        {:else}
          <div
            class="bg-neutral text-neutral-content flex h-8 w-8 flex-shrink-0 items-center justify-center rounded-full text-sm font-medium"
          >
            {record.userNickname?.charAt(0) || '?'}
          </div>
        {/if}
        <div class="text-base-content/70 flex min-w-0 flex-1 items-center gap-2 text-sm">
          <span class="min-w-0 truncate">{record.userNickname || 'User'}</span>
          <span class="flex-shrink-0">•</span>
          <span class="badge badge-outline badge-sm flex-shrink-0">{record.category || 'No category'}</span>
        </div>
      </div>

      <!-- Amount -->
      <div class="flex justify-end">
        <div
          class="{record.recordType === 'INCOME'
            ? 'text-success'
            : 'text-error'} text-lg font-bold transition-transform duration-200 group-hover:scale-105"
        >
          {record.recordType === 'INCOME' ? '+' : '-'}{displayAmount?.toLocaleString() || '0'}
        </div>
      </div>
    </div>

    <!-- Desktop Layout (≥ sm) -->
    <div class="hidden items-center justify-between gap-4 sm:flex">
      <div class="flex min-w-0 flex-1 items-center gap-3">
        <!-- User Avatar -->
        <div class="flex-shrink-0">
          {#if record.userPictureUrl}
            <div class="avatar">
              <div class="h-8 w-8 rounded-full">
                <img src={record.userPictureUrl} alt={record.userNickname || 'User'} />
              </div>
            </div>
          {:else}
            <div
              class="bg-neutral text-neutral-content flex h-8 w-8 items-center justify-center rounded-full text-sm font-medium"
            >
              {record.userNickname?.charAt(0) || '?'}
            </div>
          {/if}
        </div>

        <!-- Record Details -->
        <div class="min-w-0 flex-1">
          <div class="mb-1 flex items-center gap-2">
            <h3
              class="group-hover:text-primary truncate text-base font-semibold transition-colors duration-200"
            >
              {record.summary || 'No summary'}
            </h3>
          </div>
          <div class="text-base-content/70 flex min-w-0 items-center gap-2 text-sm">
            <span class="min-w-0 truncate">{record.userNickname || 'User'}</span>
            <span class="flex-shrink-0">•</span>
            <span class="badge badge-outline badge-sm flex-shrink-0 whitespace-nowrap">{record.category || 'No category'}</span>
            <span class="flex-shrink-0">•</span>
            <span class="text-xs flex-shrink-0">
              {record.date ? formatDateISO(record.date) : 'No date'}
            </span>
          </div>
        </div>
      </div>

      <!-- Amount and Actions -->
      <div class="flex flex-shrink-0 items-center gap-3">
        <div
          class="{record.recordType === 'INCOME'
            ? 'text-success'
            : 'text-error'} text-lg font-bold transition-transform duration-200 group-hover:scale-105"
        >
          {record.recordType === 'INCOME' ? '+' : '-'}{displayAmount?.toLocaleString() || '0'}
        </div>
      </div>
    </div>
  </div>
</svelte:element>
