<script lang="ts">
  import { api } from '$lib/api/client';
  import type { components } from '$lib/api/schema';
  import Loading from '$lib/components/Loading.svelte';
  import { showApiErrorToast, showSuccessToast } from '$lib/stores/toast.svelte';
  import { isAuthenticated, userState } from '$lib/stores/user.svelte';
  import { formatDateISO } from '$lib/utils/date';

  type AccountBookSummary = components['schemas']['AccountBookSummaryView'];

  let accountBooks: AccountBookSummary[] = $state([]);
  let loading = $state(true);
  let showCreateForm = $state(false);
  let newAccountBookName = $state('');
  let creating = $state(false);
  let nameError = $state('');

  async function loadAccountBooks() {
    if (!isAuthenticated()) return;

    try {
      loading = true;
      const response = await api.listAccountBooks();

      if (response.error) {
        showApiErrorToast(response.error, 'Failed to load account books');
        return;
      }

      if (response.data?.accountBooks) {
        accountBooks = response.data.accountBooks;
      }
    } catch (err) {
      showApiErrorToast(err, 'Failed to load account books');
    } finally {
      loading = false;
    }
  }

  function validateAccountBookName(name: string): string {
    if (name.length > 64) {
      return 'Account book name must not exceed 64 characters';
    }
    return '';
  }

  function handleNameInput(e: Event) {
    const target = e.target as HTMLInputElement;
    newAccountBookName = target.value;
    nameError = validateAccountBookName(newAccountBookName);
  }

  async function createAccountBook() {
    if (!newAccountBookName.trim()) return;

    // Validate name length before creating
    const error = validateAccountBookName(newAccountBookName);
    if (error) {
      nameError = error;
      return;
    }

    try {
      creating = true;
      const response = await api.createAccountBook({ name: newAccountBookName });

      if (response.error) {
        showApiErrorToast(response.error, 'Failed to create account book');
        return;
      }

      showSuccessToast('Account book created successfully');
      newAccountBookName = '';
      nameError = '';
      showCreateForm = false;
      await loadAccountBooks();
    } catch (err) {
      showApiErrorToast(err, 'Failed to create account book');
    } finally {
      creating = false;
    }
  }

  $effect(() => {
    if (isAuthenticated() && !userState.isLoading) {
      loadAccountBooks();
    }
  });
</script>

<div class="mx-auto max-w-4xl">
  <div class="mb-6 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
    <h1 class="text-3xl font-bold">My Account Books</h1>
    {#if isAuthenticated()}
      <button class="btn btn-primary" onclick={() => (showCreateForm = true)}>
        + New Account Book
      </button>
    {/if}
  </div>

  {#if !isAuthenticated() && !userState.isLoading}
    <div class="hero bg-base-100 rounded-box min-h-96">
      <div class="hero-content text-center">
        <div class="max-w-md">
          <h1 class="text-5xl font-bold">Welcome to GageBu!</h1>
          <p class="py-6">
            Manage your finances with ease. Track expenses, create budgets, and collaborate with
            others.
          </p>
          <a href="/api/v1/oauth2/authorization/google" class="btn btn-primary">
            Get Started with Google
          </a>
        </div>
      </div>
    </div>
  {:else if loading}
    <Loading size="lg" message="Loading account books..." />
  {:else}
    <div class="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
      {#each accountBooks as accountBook (accountBook.id)}
        <div class="card bg-base-100 shadow-xl transition-shadow hover:shadow-2xl">
          <div class="card-body p-3 sm:p-6">
            <h2 class="card-title">{accountBook.name}</h2>
            <p class="text-base-content/70 text-sm">
              Created: {formatDateISO(accountBook.createdAt || '')}
            </p>
            <div class="card-actions justify-end">
              <a href="/account-books/{accountBook.id}" class="btn btn-primary"> Open </a>
            </div>
          </div>
        </div>
      {/each}

      {#if accountBooks.length === 0}
        <div class="col-span-full py-12 text-center">
          <div class="mb-4 text-6xl">📖</div>
          <h3 class="mb-2 text-xl font-semibold">No account books yet</h3>
          <p class="text-base-content/70 mb-4">Create your first account book to get started!</p>
          <button class="btn btn-primary" onclick={() => (showCreateForm = true)}>
            Create Account Book
          </button>
        </div>
      {/if}
    </div>
  {/if}
</div>

<!-- Create Account Book Modal -->
{#if showCreateForm}
  <div class="modal modal-open">
    <div class="modal-box">
      <h3 class="text-lg font-bold">Create New Account Book</h3>
      <div class="py-4">
        <div class="form-control w-full">
          <label class="label" for="account-book-name">
            <span class="label-text">Account Book Name</span>
          </label>
          <input
            id="account-book-name"
            type="text"
            placeholder="My Account Book"
            class="input input-bordered w-full {nameError ? 'input-error' : ''}"
            bind:value={newAccountBookName}
            oninput={handleNameInput}
            onkeydown={(e) => e.key === 'Enter' && !nameError && createAccountBook()}
          />
          {#if nameError}
            <div class="label">
              <span class="label-text-alt text-error">{nameError}</span>
            </div>
          {/if}
        </div>
      </div>
      <div class="modal-action">
        <button
          class="btn"
          onclick={() => {
            showCreateForm = false;
            newAccountBookName = '';
            nameError = '';
          }}
        >
          Cancel
        </button>
        <button
          class="btn btn-primary"
          onclick={createAccountBook}
          disabled={creating || !newAccountBookName.trim() || nameError !== ''}
        >
          {#if creating}
            <Loading size="sm" />
          {/if}
          Create
        </button>
      </div>
    </div>
  </div>
{/if}
