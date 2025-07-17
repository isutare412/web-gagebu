<script lang="ts">
  import { goto } from '$app/navigation';
  import { page } from '$app/state';
  import { api } from '$lib/api/client';
  import type { components } from '$lib/api/schema';
  import ConfirmModal from '$lib/components/ConfirmModal.svelte';
  import Loading from '$lib/components/Loading.svelte';
  import RecordCard from '$lib/components/RecordCard.svelte';
  import { showApiErrorToast, showSuccessToast } from '$lib/stores/toast.svelte';
  import { isAuthenticated, userState } from '$lib/stores/user.svelte';
  import { formatDateTimeISO } from '$lib/utils/date';
  import { onMount, tick } from 'svelte';

  type AccountBookView = components['schemas']['AccountBookView'];
  type RecordSummaryView = components['schemas']['RecordSummaryView'];
  type InvitationView = components['schemas']['InvitationView'];

  const accountBookId = page.params.accountBookId;

  let accountBook: AccountBookView | null = $state(null);
  let records: RecordSummaryView[] = $state([]);
  let invitations: InvitationView[] = $state([]);
  let loading = $state(true);
  let recordsLoading = $state(false);

  // Pagination - reactive to URL changes
  let currentPage = $derived(parseInt(page.url.searchParams.get('p') || '1'));
  let pageSize = $derived.by(() => {
    const urlPageSize = page.url.searchParams.get('ps');
    const localPageSize = localStorage.getItem('pageSize');
    const finalPageSize = urlPageSize || localPageSize || '20';

    // Keep localStorage in sync with URL or default
    if (urlPageSize && typeof window !== 'undefined') {
      localStorage.setItem('pageSize', urlPageSize);
    }

    return parseInt(finalPageSize);
  });

  // For the select binding - synced with the derived pageSize
  let pageSizeSelect = $derived(pageSize);
  let totalPages = $state(0);
  let totalElements = $state(0);

  // Filters - reactive to URL changes
  let selectedCategory = $derived(page.url.searchParams.get('c') || undefined);
  let recordType = $derived.by(() => {
    const type = page.url.searchParams.get('rt');
    return type === 'INCOME' || type === 'EXPENSE' ? (type as 'INCOME' | 'EXPENSE') : undefined;
  });
  let startDate = $derived(page.url.searchParams.get('sd') || '');
  let endDate = $derived(page.url.searchParams.get('ed') || '');
  let sortDirection: 'ASCENDING' | 'DESCENDING' = $state('DESCENDING');

  // Form state for filter inputs (separate from URL-derived values)
  let selectedCategoryInput: string | undefined = $state(undefined);
  let recordTypeInput: 'INCOME' | 'EXPENSE' | undefined = $state(undefined);
  let startDateInput = $state('');
  let endDateInput = $state('');

  // UI State
  let showInviteModal = $state(false);
  let showEditModal = $state(false);
  let showDeleteModal = $state(false);
  let editedName = $state('');
  let saving = $state(false);
  let deleting = $state(false);
  let showFilters = $state(false);
  let initialLoad = $state(true);
  let loadRecordsTimeout: number | null = null;

  // Check if current user is owner of the account book
  let isOwner = $derived.by(() => {
    if (!accountBook || !userState.user) return false;
    const currentMember = accountBook.members?.find(
      (member) => member.userId === userState.user?.id
    );
    return currentMember?.role === 'OWNER';
  });

  async function loadAccountBook() {
    if (!accountBookId) return;

    try {
      loading = true;
      const response = await api.getAccountBook(accountBookId);

      if (response.error) {
        showApiErrorToast(response.error, 'Failed to load account book');
        return;
      }

      if (response.data) {
        accountBook = response.data;
        editedName = response.data.name || '';
      }
    } catch (err) {
      showApiErrorToast(err, 'Failed to load account book');
    } finally {
      loading = false;
    }
  }

  async function loadRecords() {
    if (!accountBookId) return;

    try {
      recordsLoading = true;
      const response = await api.listRecords(accountBookId, {
        page: currentPage,
        pageSize,
        categories: selectedCategory ? [selectedCategory] : undefined,
        recordType,
        startDate: startDate || undefined,
        endDate: endDate || undefined,
        direction: sortDirection,
      });

      if (response.error) {
        showApiErrorToast(response.error, 'Failed to load records');
        return;
      }

      if (response.data) {
        records = response.data.records || [];
        totalPages = response.data.totalPages || 0;
        totalElements = response.data.totalElements || 0;
      }
    } catch (err) {
      showApiErrorToast(err, 'Failed to load records');
    } finally {
      recordsLoading = false;
    }
  }

  async function loadInvitations() {
    if (!accountBookId || !isOwner) return;

    try {
      const response = await api.listInvitations(accountBookId);

      if (response.error) {
        showApiErrorToast(response.error, 'Failed to load invitations');
        return;
      }

      if (response.data?.invitations) {
        invitations = response.data.invitations;
      }
    } catch (err) {
      showApiErrorToast(err, 'Failed to load invitations');
    }
  }

  async function createInvitation() {
    if (!accountBookId || !isOwner) return;

    try {
      const response = await api.createInvitation(accountBookId);

      if (response.error) {
        showApiErrorToast(response.error, 'Failed to create invitation');
        return;
      }

      await loadInvitations();
      showSuccessToast('Invitation created successfully');
    } catch (err) {
      showApiErrorToast(err, 'Failed to create invitation');
    }
  }

  async function deleteInvitation(invitationId: string) {
    if (!accountBookId || !isOwner) return;

    try {
      const response = await api.deleteInvitation(accountBookId, invitationId);

      if (response.error) {
        showApiErrorToast(response.error, 'Failed to delete invitation');
        return;
      }

      await loadInvitations();
      showSuccessToast('Invitation deleted successfully');
    } catch (err) {
      showApiErrorToast(err, 'Failed to delete invitation');
    }
  }

  async function updateAccountBook() {
    if (!accountBookId || !editedName.trim() || !isOwner) return;

    try {
      saving = true;
      const response = await api.updateAccountBook(accountBookId, { name: editedName });

      if (response.error) {
        showApiErrorToast(response.error, 'Failed to update account book');
        return;
      }

      await loadAccountBook();
      showEditModal = false;
      showSuccessToast('Account book updated successfully');
    } catch (err) {
      showApiErrorToast(err, 'Failed to update account book');
    } finally {
      saving = false;
    }
  }

  async function deleteAccountBook() {
    if (!accountBookId || !isOwner) return;

    try {
      deleting = true;
      const response = await api.deleteAccountBook(accountBookId);

      if (response.error) {
        showApiErrorToast(response.error, 'Failed to delete account book');
        return;
      }

      showSuccessToast('Account book deleted successfully');
      showDeleteModal = false;
      goto('/');
    } catch (err) {
      showApiErrorToast(err, 'Failed to delete account book');
    } finally {
      deleting = false;
    }
  }

  function handleDeleteCancel() {
    showDeleteModal = false;
  }

  function updateUrlParams(
    newPage?: number,
    newPageSize?: number,
    filters?: {
      category?: string;
      recordType?: 'INCOME' | 'EXPENSE';
      startDate?: string;
      endDate?: string;
    }
  ) {
    const url = new URL(window.location.href);
    url.searchParams.set('p', (newPage || currentPage).toString());
    url.searchParams.set('ps', (newPageSize || pageSize).toString());

    // Handle filter parameters
    const filterParams = filters || {
      category: selectedCategory,
      recordType: recordType,
      startDate: startDate,
      endDate: endDate,
    };

    if (filterParams.category) {
      url.searchParams.set('c', filterParams.category);
    } else {
      url.searchParams.delete('c');
    }

    if (filterParams.recordType) {
      url.searchParams.set('rt', filterParams.recordType);
    } else {
      url.searchParams.delete('rt');
    }

    if (filterParams.startDate) {
      url.searchParams.set('sd', filterParams.startDate);
    } else {
      url.searchParams.delete('sd');
    }

    if (filterParams.endDate) {
      url.searchParams.set('ed', filterParams.endDate);
    } else {
      url.searchParams.delete('ed');
    }

    goto(url.pathname + url.search, { replaceState: true });
  }

  function navigateToPage(
    newPage: number,
    newPageSize?: number,
    filters?: {
      category?: string;
      recordType?: 'INCOME' | 'EXPENSE';
      startDate?: string;
      endDate?: string;
    }
  ) {
    const url = new URL(window.location.href);
    url.searchParams.set('p', newPage.toString());
    url.searchParams.set('ps', (newPageSize || pageSize).toString());

    // Handle filter parameters
    const filterParams = filters || {
      category: selectedCategory,
      recordType: recordType,
      startDate: startDate,
      endDate: endDate,
    };

    if (filterParams.category) {
      url.searchParams.set('c', filterParams.category);
    } else {
      url.searchParams.delete('c');
    }

    if (filterParams.recordType) {
      url.searchParams.set('rt', filterParams.recordType);
    } else {
      url.searchParams.delete('rt');
    }

    if (filterParams.startDate) {
      url.searchParams.set('sd', filterParams.startDate);
    } else {
      url.searchParams.delete('sd');
    }

    if (filterParams.endDate) {
      url.searchParams.set('ed', filterParams.endDate);
    } else {
      url.searchParams.delete('ed');
    }

    goto(url.pathname + url.search);
  }

  function resetFilters() {
    navigateToPage(1, undefined, {
      category: undefined,
      recordType: undefined,
      startDate: '',
      endDate: '',
    });
  }

  function applyFilters() {
    navigateToPage(1, undefined, {
      category: selectedCategoryInput,
      recordType: recordTypeInput,
      startDate: startDateInput,
      endDate: endDateInput,
    });
  }

  function handleFilterChange() {
    // Apply filters immediately on input change
    applyFilters();
  }

  function changePage(page: number) {
    // Validate page number
    if (page < 1 || page > totalPages || page === currentPage) {
      return;
    }

    // Prevent navigation while loading
    if (recordsLoading) {
      return;
    }

    navigateToPage(page);
  }

  function changePageSize(newPageSize: number) {
    localStorage.setItem('pageSize', newPageSize.toString());
    navigateToPage(1, newPageSize); // Reset to first page when page size changes
  }

  onMount(() => {
    if (!isAuthenticated()) {
      goto('/');
      return;
    }

    // Ensure URL params are set immediately if not present
    const hasParams = page.url.searchParams.has('p') && page.url.searchParams.has('ps');
    if (!hasParams) {
      updateUrlParams();
    }

    loadAccountBook();

    // Allow effects to run after initial setup - this will trigger the main effect
    setTimeout(() => {
      initialLoad = false;
    }, 0);
  });

  // React to all changes that require record reloading
  $effect(() => {
    // Track all the derived values that affect record loading
    const page = currentPage;
    const size = pageSize;
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const category = selectedCategory;
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const type = recordType;
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const start = startDate;
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const end = endDate;
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const direction = sortDirection;

    // Only reload if we have valid pagination values and the component is fully mounted
    if (page && size && !loading && !initialLoad) {
      // Clear any existing timeout
      if (loadRecordsTimeout) {
        clearTimeout(loadRecordsTimeout);
      }

      // Use tick to ensure all state changes have settled before loading records
      loadRecordsTimeout = setTimeout(async () => {
        await tick();
        loadRecords();
        loadRecordsTimeout = null;
      }, 0);
    }
  });

  // Sync form inputs with URL parameters (for back/forward navigation)
  $effect(() => {
    selectedCategoryInput = selectedCategory;
    recordTypeInput = recordType;
    startDateInput = startDate;
    endDateInput = endDate;
  });

  // Keep pageSizeSelect in sync with derived pageSize
  $effect(() => {
    pageSizeSelect = pageSize;
  });

  // Load invitations when account book is loaded and user is owner
  $effect(() => {
    if (accountBook && isOwner) {
      loadInvitations();
    }
  });

  // Keyboard navigation for pagination
  $effect(() => {
    function handleKeyDown(event: KeyboardEvent) {
      // Only handle pagination keys when not in an input field
      if (
        event.target instanceof HTMLInputElement ||
        event.target instanceof HTMLTextAreaElement ||
        event.target instanceof HTMLSelectElement
      ) {
        return;
      }

      switch (event.key) {
        case 'ArrowLeft':
          if (event.shiftKey && currentPage > 1) {
            event.preventDefault();
            changePage(currentPage - 1);
          }
          break;
        case 'ArrowRight':
          if (event.shiftKey && currentPage < totalPages) {
            event.preventDefault();
            changePage(currentPage + 1);
          }
          break;
        case 'Home':
          if (event.shiftKey && currentPage > 1) {
            event.preventDefault();
            changePage(1);
          }
          break;
        case 'End':
          if (event.shiftKey && currentPage < totalPages) {
            event.preventDefault();
            changePage(totalPages);
          }
          break;
      }
    }

    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  });
</script>

<svelte:head>
  <title>{accountBook?.name || 'Account Book'} - GageBu</title>
</svelte:head>

{#if loading}
  <Loading size="lg" message="Loading account book..." />
{:else if accountBook}
  <!-- Breadcrumb -->
  <div class="breadcrumbs mb-6 text-sm">
    <ul>
      <li><a href="/">Account Books</a></li>
      <li>{accountBook.name}</li>
    </ul>
  </div>

  <!-- Header -->
  <div class="mb-6 flex flex-col items-start justify-between gap-4 md:flex-row md:items-center">
    <div class="min-w-0 flex-1 md:flex-shrink">
      <h1 class="text-3xl font-bold break-words">{accountBook.name}</h1>
      <p class="text-base-content/70">
        {accountBook.members?.length || 0} members • {totalElements} records
      </p>
    </div>

    <div class="flex gap-2 self-end md:flex-shrink-0">
      <div class="dropdown sm:dropdown-end">
        <div tabindex="0" role="button" class="btn btn-sm sm:btn-md">⚙️ Options</div>
        <!-- svelte-ignore a11y_no_noninteractive_tabindex -->
        <ul
          tabindex="0"
          class="dropdown-content menu bg-base-100 rounded-box z-[1] w-52 p-2 shadow"
        >
          {#if isOwner}
            <li><button onclick={() => (showEditModal = true)}>Edit Name</button></li>
          {/if}
          <li><a href="/account-books/{accountBookId}/members">View Members</a></li>
          {#if isOwner}
            <li><a href="/account-books/{accountBookId}/categories">Manage Categories</a></li>
          {/if}
          {#if isOwner}
            <li><button onclick={() => (showInviteModal = true)}>Manage Invitations</button></li>
          {/if}
          {#if isOwner}
            <li>
              <button onclick={() => (showDeleteModal = true)} class="text-error">Delete</button>
            </li>
          {/if}
        </ul>
      </div>
      <a href="/account-books/{accountBookId}/records/new" class="btn btn-primary btn-sm sm:btn-md">
        + Add Record
      </a>
    </div>
  </div>

  <!-- Filters -->
  <div class="card bg-base-100 mb-6 shadow-lg">
    <div class="card-body p-4 sm:p-6">
      <div class="flex items-center justify-between">
        <h2 class="card-title">Filters</h2>
        <button class="btn btn-ghost btn-sm" onclick={() => (showFilters = !showFilters)}>
          {showFilters ? '▼' : '▶'}
          {showFilters ? 'Hide' : 'Show'} Filters
        </button>
      </div>

      {#if showFilters}
        <div class="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-4">
          <!-- Record Type -->
          <div class="form-control">
            <label class="label" for="record-type">
              <span class="label-text">Type</span>
            </label>
            <select
              id="record-type"
              class="select select-bordered"
              bind:value={recordTypeInput}
              onchange={handleFilterChange}
            >
              <option value={undefined}>All Types</option>
              <option value="INCOME">Income</option>
              <option value="EXPENSE">Expense</option>
            </select>
          </div>

          <!-- Categories -->
          {#if accountBook.categories && accountBook.categories.length > 0}
            <div class="form-control">
              <label class="label" for="categories">
                <span class="label-text">Category</span>
              </label>
              <select
                id="categories"
                class="select select-bordered"
                bind:value={selectedCategoryInput}
                onchange={handleFilterChange}
              >
                <option value={undefined}>All Categories</option>
                {#each accountBook.categories as category (category.id)}
                  <option value={category.name}>{category.name}</option>
                {/each}
              </select>
            </div>
          {/if}

          <!-- Date Range -->
          <div class="form-control">
            <label class="label" for="start-date">
              <span class="label-text">Start Date</span>
            </label>
            <input
              id="start-date"
              type="date"
              class="input input-bordered"
              bind:value={startDateInput}
              onchange={handleFilterChange}
            />
          </div>

          <div class="form-control">
            <label class="label" for="end-date">
              <span class="label-text">End Date</span>
            </label>
            <input
              id="end-date"
              type="date"
              class="input input-bordered"
              bind:value={endDateInput}
              onchange={handleFilterChange}
            />
          </div>
        </div>

        <div class="mt-4 flex flex-col gap-4 sm:flex-row sm:items-center">
          <div class="flex flex-col gap-2 sm:flex-row">
            <button class="btn btn-primary" onclick={applyFilters}>Apply Filters</button>
            <button class="btn" onclick={resetFilters}>Reset</button>
          </div>
          <div class="flex flex-col gap-2 sm:ml-auto sm:flex-row sm:items-center">
            <span class="text-sm">Show:</span>
            <select
              class="select select-bordered select-sm"
              bind:value={pageSizeSelect}
              onchange={() => changePageSize(pageSizeSelect)}
            >
              <option value={10}>10 per page</option>
              <option value={20}>20 per page</option>
              <option value={50}>50 per page</option>
              <option value={100}>100 per page</option>
            </select>
            <select class="select select-bordered select-sm" bind:value={sortDirection}>
              <option value="DESCENDING">Newest First</option>
              <option value="ASCENDING">Oldest First</option>
            </select>
          </div>
        </div>
      {/if}
    </div>
  </div>

  <!-- Records -->
  <div class="card bg-base-100 shadow-lg">
    <div class="card-body p-4 sm:p-6">
      <div class="flex items-center justify-between">
        <h2 class="card-title">Records</h2>
        <button
          class="btn btn-ghost btn-circle btn-sm"
          onclick={loadRecords}
          disabled={recordsLoading}
          title="Refresh records"
          aria-label="Refresh records"
        >
          <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
            />
          </svg>
        </button>
      </div>

      {#if recordsLoading}
        <Loading size="lg" message="Loading records..." />
      {:else if records.length === 0}
        <div class="py-12 text-center">
          <div class="mb-4 text-6xl">📝</div>
          <h3 class="mb-2 text-xl font-semibold">No records found</h3>
          <p class="text-base-content/70 mb-4">Start adding some income and expense records!</p>
          <a href="/account-books/{accountBookId}/records/new" class="btn btn-primary">
            Add First Record
          </a>
        </div>
      {:else}
        <div class="space-y-2">
          {#each records as record (record.id)}
            <RecordCard
              record={{
                id: record.id,
                summary: record.summary,
                date: record.date,
                recordType: record.recordType,
                amount: record.amount || 0,
                category: record.category,
                userNickname: record.userNickname,
                userPictureUrl: record.userPictureUrl,
              }}
              {accountBookId}
              showActions={true}
              isPreview={false}
            />
          {/each}
        </div>

        <!-- Pagination -->
        {#if totalPages > 1}
          <div class="mt-6 flex flex-col items-center gap-4 sm:flex-row sm:justify-between">
            <div class="text-base-content/70 text-sm">
              Showing {Math.min((currentPage - 1) * pageSize + 1, totalElements)} to {Math.min(
                currentPage * pageSize,
                totalElements
              )} of {totalElements} records
            </div>
            <div class="flex items-center gap-2">
              <!-- Previous Page Button -->
              <button
                class="btn btn-sm"
                onclick={() => changePage(currentPage - 1)}
                disabled={currentPage <= 1 || recordsLoading}
                title="Previous Page"
              >
                {#if recordsLoading}
                  <Loading size="sm" />
                {:else}
                  <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M15 19l-7-7 7-7"
                    />
                  </svg>
                {/if}
              </button>

              <!-- Page Input -->
              <div class="flex items-center gap-1">
                <input
                  type="number"
                  class="input input-bordered w-10 py-0 text-center text-sm [appearance:textfield] [&::-webkit-inner-spin-button]:appearance-none [&::-webkit-outer-spin-button]:appearance-none"
                  min="1"
                  max={totalPages}
                  value={currentPage}
                  disabled={recordsLoading}
                  onchange={(e) => {
                    const target = e.target as HTMLInputElement;
                    const page = parseInt(target.value);
                    if (page >= 1 && page <= totalPages && !recordsLoading) {
                      changePage(page);
                    }
                  }}
                />
                <span class="text-base-content/70 text-sm">of {totalPages}</span>
              </div>

              <!-- Next Page Button -->
              <button
                class="btn btn-sm"
                onclick={() => changePage(currentPage + 1)}
                disabled={currentPage >= totalPages || recordsLoading}
                title="Next Page"
              >
                {#if recordsLoading}
                  <Loading size="sm" />
                {:else}
                  <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M9 5l7 7-7 7"
                    />
                  </svg>
                {/if}
              </button>
            </div>
          </div>

          <!-- Keyboard Navigation Hint -->
          <div class="mt-2 text-center">
            <span class="text-base-content/50 text-xs">
              💡 Tip: Use Shift + ← → to navigate pages, Shift + Home/End for first/last page
            </span>
          </div>
        {:else if totalElements > 0}
          <div class="text-base-content/70 mt-6 text-center text-sm">
            Showing all {totalElements} records
          </div>
        {/if}
      {/if}
    </div>
  </div>
{/if}

<!-- Edit Account Book Modal -->
{#if showEditModal}
  <div class="modal modal-open">
    <div class="modal-box">
      <h3 class="text-lg font-bold">Edit Account Book</h3>
      <div class="py-4">
        <div class="form-control w-full">
          <label class="label" for="edit-name">
            <span class="label-text">Name</span>
          </label>
          <input
            id="edit-name"
            type="text"
            class="input input-bordered w-full"
            bind:value={editedName}
          />
        </div>
      </div>
      <div class="modal-action">
        <button class="btn" onclick={() => (showEditModal = false)}>Cancel</button>
        <button
          class="btn btn-primary"
          onclick={updateAccountBook}
          disabled={saving || !editedName.trim()}
        >
          {#if saving}
            <Loading size="sm" />
          {/if}
          Save
        </button>
      </div>
    </div>
  </div>
{/if}

<!-- Invite Modal -->
{#if showInviteModal && isOwner}
  <div class="modal modal-open">
    <div class="modal-box">
      <h3 class="text-lg font-bold">Manage Invitations</h3>
      <div class="py-4">
        <div class="mb-4 flex items-center justify-between">
          <p>Active invitations:</p>
          <button class="btn btn-primary btn-sm" onclick={createInvitation}>
            Create Invitation
          </button>
        </div>

        {#if invitations.length === 0}
          <p class="text-base-content/70">No active invitations</p>
        {:else}
          <div class="space-y-2">
            {#each invitations as invitation (invitation.id)}
              <div class="bg-base-200 flex items-center justify-between rounded p-3">
                <div>
                  <p class="font-mono text-sm">{invitation.id}</p>
                  <p class="text-base-content/70 text-xs">
                    Expires: {formatDateTimeISO(invitation.expiration || '')}
                  </p>
                </div>
                <div class="flex gap-2">
                  <button
                    class="btn btn-sm btn-square"
                    onclick={() =>
                      navigator.clipboard.writeText(
                        window.location.origin + '/invitations/' + invitation.id
                      )}
                    title="Copy Link"
                    aria-label="Copy Link"
                  >
                    <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"
                      />
                    </svg>
                  </button>
                  <button
                    class="btn btn-sm btn-square btn-error"
                    onclick={() => deleteInvitation(invitation.id!)}
                    title="Delete Invitation"
                    aria-label="Delete Invitation"
                  >
                    <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                      />
                    </svg>
                  </button>
                </div>
              </div>
            {/each}
          </div>
        {/if}
      </div>
      <div class="modal-action">
        <button class="btn" onclick={() => (showInviteModal = false)}>Close</button>
      </div>
    </div>
  </div>
{/if}

<!-- Delete Confirmation Modal -->
<ConfirmModal
  show={showDeleteModal}
  title="Delete Account Book"
  message="Are you sure you want to delete this account book? This action cannot be undone."
  loading={deleting}
  onConfirm={deleteAccountBook}
  onCancel={handleDeleteCancel}
/>
