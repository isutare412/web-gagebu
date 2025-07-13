<script lang="ts">
  import { goto } from '$app/navigation';
  import { page } from '$app/state';
  import { api } from '$lib/api/client';
  import type { components } from '$lib/api/schema';
  import ConfirmModal from '$lib/components/ConfirmModal.svelte';
  import Loading from '$lib/components/Loading.svelte';
  import RecordForm from '$lib/components/RecordForm.svelte';
  import { showApiErrorToast, showSuccessToast } from '$lib/stores/toast.svelte';
  import { isAuthenticated, userState } from '$lib/stores/user.svelte';
  import { formatDateTimeISO, formatDateWithDay } from '$lib/utils/date';
  import { onMount } from 'svelte';

  type RecordView = components['schemas']['RecordView'];
  type AccountBookView = components['schemas']['AccountBookView'];

  const accountBookId = page.params.accountBookId;
  const recordId = page.params.recordId;

  let record: RecordView | null = $state(null);
  let accountBook: AccountBookView | null = $state(null);
  let loading = $state(true);
  let showEditModal = $state(false);
  let showDeleteModal = $state(false);
  let saving = $state(false);
  let deleting = $state(false);

  // Edit form data
  let editData = $state({
    categoryId: '',
    recordType: 'EXPENSE' as 'INCOME' | 'EXPENSE',
    amount: '',
    summary: '',
    description: '',
    date: '',
  });

  async function loadRecord() {
    if (!accountBookId || !recordId) return;

    try {
      loading = true;
      const [recordResponse, accountBookResponse] = await Promise.all([
        api.getRecord(accountBookId, recordId),
        api.getAccountBook(accountBookId),
      ]);

      if (recordResponse.error) {
        showApiErrorToast(recordResponse.error, 'Failed to load record');
        return;
      }

      if (accountBookResponse.error) {
        showApiErrorToast(accountBookResponse.error, 'Failed to load account book');
        return;
      }

      if (recordResponse.data) {
        record = recordResponse.data;
        // Initialize edit form with current data
        editData = {
          categoryId: getCategoryIdByName(recordResponse.data.category || '') || '',
          recordType: recordResponse.data.recordType || 'EXPENSE',
          amount: recordResponse.data.amount?.toString() || '',
          summary: recordResponse.data.summary || '',
          description: recordResponse.data.description || '',
          date: recordResponse.data.date
            ? (() => {
                const date = new Date(recordResponse.data.date);
                const year = date.getFullYear();
                const month = String(date.getMonth() + 1).padStart(2, '0');
                const day = String(date.getDate()).padStart(2, '0');
                return `${year}-${month}-${day}`;
              })()
            : '',
        };
      }

      if (accountBookResponse.data) {
        accountBook = accountBookResponse.data;
      }
    } catch (err) {
      showApiErrorToast(err, 'Failed to load record');
    } finally {
      loading = false;
    }
  }

  function getCategoryIdByName(categoryName: string): string | null {
    return accountBook?.categories?.find((cat) => cat.name === categoryName)?.id || null;
  }

  // Check if current user can edit this record
  let canEditRecord = $derived.by(() => {
    if (!record || !accountBook || !userState.user) return false;

    // Find current user's role in this account book
    const currentMember = accountBook.members?.find(
      (member) => member.userId === userState.user?.id
    );
    if (!currentMember) return false;

    // Owners can edit any record
    if (currentMember.role === 'OWNER') return true;

    // Participants can only edit their own records
    if (currentMember.role === 'PARTICIPANT') {
      return record.userId === userState.user.id;
    }

    return false;
  });

  async function updateRecord() {
    if (!accountBookId || !recordId) return;

    try {
      saving = true;
      const response = await api.updateRecord(accountBookId, recordId, {
        ...editData,
        amount: Number(editData.amount),
      });

      if (response.error) {
        showApiErrorToast(response.error, 'Failed to update record');
        return;
      }

      await loadRecord();
      showSuccessToast('Record updated successfully');
      showEditModal = false;
    } catch (err) {
      showApiErrorToast(err, 'Failed to update record');
    } finally {
      saving = false;
    }
  }

  async function deleteRecord() {
    if (!accountBookId || !recordId) return;

    try {
      deleting = true;
      const response = await api.deleteRecord(accountBookId, recordId);

      if (response.error) {
        showApiErrorToast(response.error, 'Failed to delete record');
        return;
      }

      showSuccessToast('Record deleted successfully');
      showDeleteModal = false;
      goto(`/account-books/${accountBookId}`);
    } catch (err) {
      showApiErrorToast(err, 'Failed to delete record');
    } finally {
      deleting = false;
    }
  }

  function showDeleteConfirmation() {
    showDeleteModal = true;
  }

  function handleDeleteCancel() {
    showDeleteModal = false;
  }

  function startEdit() {
    if (record && accountBook) {
      editData = {
        categoryId: getCategoryIdByName(record.category || '') || '',
        recordType: record.recordType || 'EXPENSE',
        amount: record.amount?.toString() || '',
        summary: record.summary || '',
        description: record.description || '',
        date: record.date
          ? (() => {
              const date = new Date(record.date);
              const year = date.getFullYear();
              const month = String(date.getMonth() + 1).padStart(2, '0');
              const day = String(date.getDate()).padStart(2, '0');
              return `${year}-${month}-${day}`;
            })()
          : '',
      };
      showEditModal = true;
    }
  }

  function handleEditSubmit(event: Event) {
    event.preventDefault();
    updateRecord();
  }

  function handleEditCancel() {
    showEditModal = false;
  }

  onMount(() => {
    if (!isAuthenticated()) {
      goto('/');
      return;
    }

    loadRecord();
  });
</script>

<svelte:head>
  <title>{record?.summary || 'Record'} - GageBu</title>
</svelte:head>

{#if loading}
  <Loading size="lg" message="Loading record..." />
{:else if record && accountBook}
  <div class="mx-auto max-w-4xl">
    <!-- Breadcrumb -->
    <div class="breadcrumbs mb-6 text-sm">
      <ul>
        <li><a href="/">Account Books</a></li>
        <li><a href="/account-books/{accountBookId}">{accountBook.name}</a></li>
        <li>Record Details</li>
      </ul>
    </div>

    <!-- Header -->
    <div
      class="mb-6 flex flex-col items-start justify-between gap-4 sm:flex-row sm:items-start sm:gap-0"
    >
      <div>
        <h1 class="text-3xl font-bold">{record.summary}</h1>
      </div>

      {#if canEditRecord}
        <div class="flex gap-2 self-end">
          <button class="btn" onclick={startEdit}> ✏️ Edit </button>
          <button class="btn btn-error" onclick={showDeleteConfirmation} disabled={deleting}>
            {#if deleting}
              <Loading size="sm" />
            {/if}
            🗑️ Delete
          </button>
        </div>
      {/if}
    </div>

    <!-- Record Details -->
    <div class="card bg-base-100 shadow-lg">
      <div class="card-body">
        <div class="grid grid-cols-1 gap-6 md:grid-cols-2">
          <!-- Amount -->
          <div>
            <h3 class="mb-2 text-lg font-semibold">Amount</h3>
            <div
              class="text-3xl font-bold {record.recordType === 'INCOME'
                ? 'text-success'
                : 'text-error'}"
            >
              {record.recordType === 'INCOME' ? '+' : '-'}{record.amount?.toLocaleString()}
            </div>
          </div>

          <!-- Date -->
          <div>
            <h3 class="mb-2 text-lg font-semibold">Date</h3>
            <div class="text-xl">
              {formatDateWithDay(record.date || '')}
            </div>
          </div>

          <!-- User -->
          <div>
            <h3 class="mb-2 text-lg font-semibold">Created by</h3>
            <div class="flex items-center gap-3">
              {#if record.userPictureUrl}
                <div class="avatar">
                  <div class="w-10 rounded-full">
                    <img src={record.userPictureUrl} alt={record.userNickname} />
                  </div>
                </div>
              {:else}
                <div
                  class="bg-neutral text-neutral-content flex h-10 w-10 items-center justify-center rounded-full"
                >
                  {record.userNickname?.charAt(0) || '?'}
                </div>
              {/if}
              <span class="text-lg">{record.userNickname}</span>
            </div>
          </div>

          <!-- Category -->
          <div>
            <h3 class="mb-2 text-lg font-semibold">Category</h3>
            <div class="badge badge-outline badge-lg">{record.category}</div>
          </div>
        </div>

        {#if record.description}
          <div class="mt-6">
            <h3 class="mb-2 text-lg font-semibold">Description</h3>
            <p class="text-base-content/80 whitespace-pre-wrap">{record.description}</p>
          </div>
        {/if}

        <!-- Metadata -->
        <div class="divider">Record Information</div>
        <div class="grid grid-cols-1 gap-4 text-sm md:grid-cols-2">
          <div>
            <span class="font-semibold">Created:</span>
            <span class="ml-2">{formatDateTimeISO(record.createdAt || '')}</span>
          </div>
          <div>
            <span class="font-semibold">Last Updated:</span>
            <span class="ml-2">{formatDateTimeISO(record.updatedAt || '')}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
{/if}

<!-- Edit Record Modal -->
{#if showEditModal && accountBook}
  <div class="modal modal-open z-40">
    <div class="modal-box max-w-2xl">
      <h3 class="text-lg font-bold">Edit Record</h3>

      <div class="py-4">
        <RecordForm
          {accountBook}
          bind:formData={editData}
          {saving}
          onSubmit={handleEditSubmit}
          onCancel={handleEditCancel}
          submitText="Save Changes"
          showPreview={true}
          isModal={true}
        />
      </div>
    </div>
  </div>
{/if}

<!-- Delete Confirmation Modal -->
<ConfirmModal
  show={showDeleteModal}
  title="Delete Record"
  message="Are you sure you want to delete this record? This action cannot be undone."
  loading={deleting}
  onConfirm={deleteRecord}
  onCancel={handleDeleteCancel}
/>
