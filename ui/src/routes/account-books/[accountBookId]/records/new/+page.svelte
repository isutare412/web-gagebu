<script lang="ts">
  import { goto } from '$app/navigation';
  import { page } from '$app/state';
  import { api } from '$lib/api/client';
  import type { components } from '$lib/api/schema';
  import Loading from '$lib/components/Loading.svelte';
  import RecordForm from '$lib/components/RecordForm.svelte';
  import { showApiErrorToast, showSuccessToast } from '$lib/stores/toast.svelte';
  import { isAuthenticated } from '$lib/stores/user.svelte';
  import { onMount } from 'svelte';

  type AccountBookView = components['schemas']['AccountBookView'];
  type RecordCreateRequest = components['schemas']['RecordCreateRequest'];

  const accountBookId = page.params.accountBookId;

  let accountBook: AccountBookView | null = $state(null);
  let loading = $state(true);
  let saving = $state(false);

  // Form data
  let formData = $state({
    categoryId: '',
    recordType: 'EXPENSE' as 'INCOME' | 'EXPENSE',
    amount: '',
    summary: '',
    description: '',
    date: (() => {
      // Use local date to avoid timezone issues
      const today = new Date();
      const year = today.getFullYear();
      const month = String(today.getMonth() + 1).padStart(2, '0');
      const day = String(today.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    })(),
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
        // Set default category if available
        if (response.data.categories && response.data.categories.length > 0) {
          const firstCategory = response.data.categories[0];
          if (firstCategory?.id) {
            formData.categoryId = firstCategory.id;
          }
        }
      }
    } catch (err) {
      showApiErrorToast(err, 'Failed to load account book');
    } finally {
      loading = false;
    }
  }

  async function createRecord() {
    if (
      !accountBookId ||
      !formData.categoryId ||
      !formData.amount ||
      !formData.date ||
      !formData.summary
    )
      return;

    try {
      saving = true;
      const recordData: RecordCreateRequest = {
        categoryId: formData.categoryId,
        recordType: formData.recordType,
        amount: Number(formData.amount),
        summary: formData.summary,
        description: formData.description || '',
        date: formData.date,
      };

      const response = await api.createRecord(accountBookId, recordData);

      if (response.error) {
        showApiErrorToast(response.error, 'Failed to create record');
        return;
      }

      if (response.data?.id) {
        showSuccessToast('Record created successfully');
        goto(`/account-books/${accountBookId}/records/${response.data.id}`);
      } else if (response.data) {
        // Record created but no ID returned, still a success
        showSuccessToast('Record created successfully');
        goto(`/account-books/${accountBookId}`);
      } else {
        // No data returned, treat as error
        showApiErrorToast(
          new Error('Failed to create record - no data returned'),
          'Failed to create record'
        );
      }
    } catch (err) {
      showApiErrorToast(err, 'Failed to create record');
    } finally {
      saving = false;
    }
  }

  function handleSubmit(event: Event) {
    event.preventDefault();
    createRecord();
  }

  function handleCancel() {
    goto(`/account-books/${accountBookId}`);
  }

  onMount(() => {
    if (!isAuthenticated()) {
      goto('/');
      return;
    }

    loadAccountBook();
  });
</script>

<svelte:head>
  <title>New Record - {accountBook?.name || 'Account Book'} - GageBu</title>
</svelte:head>

{#if loading}
  <Loading size="lg" message="Loading account book..." />
{:else if !accountBook}
  <div class="hero min-h-96">
    <div class="hero-content text-center">
      <div class="max-w-md">
        <h1 class="text-2xl font-bold">Account Book Not Found</h1>
        <p class="py-6">
          The account book you're looking for doesn't exist or you don't have access to it.
        </p>
        <a href="/" class="btn btn-primary">Go Home</a>
      </div>
    </div>
  </div>
{:else}
  <div class="mx-auto max-w-4xl">
    <!-- Breadcrumb -->
    <div class="breadcrumbs mb-6 text-sm">
      <ul>
        <li><a href="/">Account Books</a></li>
        <li><a href="/account-books/{accountBookId}">{accountBook.name}</a></li>
        <li>New Record</li>
      </ul>
    </div>

    <!-- Header -->
    <div class="mb-6">
      <h1 class="text-3xl font-bold">Add New Record</h1>
      <p class="text-base-content/70">
        Create a new income or expense record for {accountBook.name}
      </p>
    </div>

    <!-- Form -->
    <div class="card bg-base-100 shadow-lg">
      <div class="card-body">
        <RecordForm
          {accountBook}
          bind:formData
          {saving}
          onSubmit={handleSubmit}
          onCancel={handleCancel}
          submitText="Create Record"
        />
      </div>
    </div>
  </div>
{/if}
