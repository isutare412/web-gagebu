<script lang="ts">
  import { goto } from '$app/navigation';
  import { page } from '$app/state';
  import type { CategoryListUpdateRequest, CategoryUpdateRequest } from '$lib/api/client';
  import { api } from '$lib/api/client';
  import type { components } from '$lib/api/schema';
  import ConfirmModal from '$lib/components/ConfirmModal.svelte';
  import Loading from '$lib/components/Loading.svelte';
  import { showApiErrorToast, showSuccessToast } from '$lib/stores/toast.svelte';
  import { isAuthenticated, userState } from '$lib/stores/user.svelte';
  import { onMount } from 'svelte';

  type AccountBookView = components['schemas']['AccountBookView'];
  type CategoryView = components['schemas']['CategoryView'];

  const accountBookId = page.params.accountBookId;

  let accountBook: AccountBookView | null = $state(null);
  let categories: CategoryView[] = $state([]);
  let loading = $state(true);
  let saving = $state(false);

  // Edit state
  let editingCategories: CategoryUpdateRequest[] = $state([]);
  let showDeleteConfirm = $state(false);
  let categoryToDelete: CategoryView | null = $state(null);

  // Check if current user can manage categories (is owner)
  let canManageCategories = $derived.by(() => {
    if (!accountBook || !userState.user) return false;

    const currentMember = accountBook.members?.find(
      (member) => member.userId === userState.user?.id
    );
    return currentMember?.role === 'OWNER';
  });

  async function loadData() {
    if (!accountBookId) return;

    try {
      loading = true;
      const [accountBookResponse, categoriesResponse] = await Promise.all([
        api.getAccountBook(accountBookId),
        api.getCategories(accountBookId),
      ]);

      if (accountBookResponse.error) {
        showApiErrorToast(accountBookResponse.error, 'Failed to load account book');
        return;
      }

      if (categoriesResponse.error) {
        showApiErrorToast(categoriesResponse.error, 'Failed to load categories');
        return;
      }

      if (accountBookResponse.data) {
        accountBook = accountBookResponse.data;
      }

      if (categoriesResponse.data?.categories) {
        categories = categoriesResponse.data.categories;
        // Initialize editing categories with current data
        editingCategories = categories.map((cat) => ({
          id: cat.id,
          name: cat.name || '',
        }));
      }
    } catch (err) {
      showApiErrorToast(err, 'Failed to load data');
    } finally {
      loading = false;
    }
  }

  function addNewCategory() {
    editingCategories = [
      ...editingCategories,
      {
        id: undefined, // null for new categories
        name: '',
      },
    ];
  }

  function removeCategory(index: number) {
    const category = editingCategories[index];
    if (!category) return;

    const originalCategory = categories.find((c) => c.id === category.id);

    // If it's a new category (no id), just remove from editing list
    if (!category.id) {
      editingCategories = editingCategories.filter((_, i) => i !== index);
      return;
    }

    // If it's an existing category, show confirmation for basic/fallback categories
    if (originalCategory && (originalCategory.isBasic || originalCategory.isFallback)) {
      const categoryType = originalCategory.isFallback ? 'fallback' : 'basic';
      showApiErrorToast(
        {
          status: 400,
          statusText: 'Bad Request',
          errorCode: 'BAD_REQUEST',
          message: `Cannot delete ${categoryType} category`,
        },
        `Cannot delete ${categoryType} category`
      );
      return;
    }

    categoryToDelete = originalCategory || null;
    showDeleteConfirm = true;
  }

  function confirmDeleteCategory() {
    if (!categoryToDelete) return;

    const categoryIndex = editingCategories.findIndex((c) => c.id === categoryToDelete?.id);
    if (categoryIndex !== -1) {
      editingCategories = editingCategories.filter((_, i) => i !== categoryIndex);
    }

    categoryToDelete = null;
    showDeleteConfirm = false;
  }

  function updateCategoryName(index: number, name: string) {
    editingCategories[index] = {
      ...editingCategories[index],
      name,
    };
  }

  function validateCategories(): string | null {
    // Check for empty names
    for (let i = 0; i < editingCategories.length; i++) {
      const category = editingCategories[i];
      if (!category || !category.name || !category.name.trim()) {
        return `Category ${i + 1} name cannot be empty`;
      }
      if (category.name.length > 16) {
        return `Category ${i + 1} name must not exceed 16 characters`;
      }
    }

    // Check for duplicate names
    const names = editingCategories
      .map((c) => c?.name?.trim().toLowerCase() || '')
      .filter((name) => name);
    const uniqueNames = new Set(names);
    if (names.length !== uniqueNames.size) {
      return 'Category names must be unique';
    }

    return null;
  }

  async function saveCategories() {
    if (!accountBookId) return;

    const validationError = validateCategories();
    if (validationError) {
      showApiErrorToast(
        {
          status: 400,
          statusText: 'Bad Request',
          errorCode: 'BAD_REQUEST',
          message: validationError,
        },
        validationError
      );
      return;
    }

    try {
      saving = true;
      const updateRequest: CategoryListUpdateRequest = {
        categories: editingCategories,
      };

      const response = await api.updateCategories(accountBookId, updateRequest);

      if (response.error) {
        showApiErrorToast(response.error, 'Failed to save categories');
        return;
      }

      if (response.data?.categories) {
        categories = response.data.categories;
        editingCategories = categories.map((cat) => ({
          id: cat.id,
          name: cat.name || '',
        }));
      }

      showSuccessToast('Categories updated successfully');
    } catch (err) {
      showApiErrorToast(err, 'Failed to save categories');
    } finally {
      saving = false;
    }
  }

  function resetChanges() {
    editingCategories = categories.map((cat) => ({
      id: cat.id,
      name: cat.name || '',
    }));
  }

  // Check if there are unsaved changes
  let hasUnsavedChanges = $derived.by(() => {
    if (editingCategories.length !== categories.length) return true;

    return editingCategories.some((editing, index) => {
      const original = categories.find((c) => c.id === editing.id);
      if (!original && editing.name) return true; // New category with name
      if (original && original.name !== editing.name) return true; // Changed name
      return false;
    });
  });

  // Check if any category has blank/empty name
  let hasBlankCategories = $derived.by(() => {
    return editingCategories.some((category) => !category.name || !category.name.trim());
  });

  onMount(() => {
    if (!isAuthenticated()) {
      goto('/');
      return;
    }

    loadData();
  });
</script>

<svelte:head>
  <title>Categories - {accountBook?.name || 'Account Book'} - GageBu</title>
</svelte:head>

{#if loading}
  <Loading size="lg" message="Loading categories..." />
{:else if accountBook}
  <div class="mx-auto max-w-4xl">
    <!-- Breadcrumb -->
    <div class="breadcrumbs mb-6 text-sm">
      <ul>
        <li><a href="/">Account Books</a></li>
        <li><a href="/account-books/{accountBookId}">{accountBook.name}</a></li>
        <li>Categories</li>
      </ul>
    </div>

    <!-- Header -->
    <div class="mb-6 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
      <h1 class="text-3xl font-bold">Manage Categories</h1>
      {#if canManageCategories}
        <div class="flex gap-2 self-end">
          <button
            class="btn btn-outline"
            onclick={resetChanges}
            disabled={!hasUnsavedChanges || saving}
          >
            Reset
          </button>
          <button
            class="btn btn-primary"
            onclick={saveCategories}
            disabled={!hasUnsavedChanges || saving || hasBlankCategories}
          >
            {#if saving}
              <span class="loading loading-spinner loading-sm"></span>
            {/if}
            Save Changes
          </button>
        </div>
      {/if}
    </div>

    {#if !canManageCategories}
      <div class="alert alert-info mb-6">
        <svg
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          class="h-6 w-6 shrink-0 stroke-current"
        >
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
          ></path>
        </svg>
        <span>Only account book owners can manage categories.</span>
      </div>
    {/if}

    <!-- Categories List -->
    <div class="card bg-base-100 shadow-xl">
      <div class="card-body p-4 sm:p-6">
        <div class="mb-4 flex items-center justify-between">
          <h2 class="card-title">Categories</h2>
          {#if canManageCategories}
            <button class="btn btn-sm btn-primary" onclick={addNewCategory} disabled={saving}>
              + Add Category
            </button>
          {/if}
        </div>

        <div class="grid grid-cols-1 gap-3 md:grid-cols-2">
          {#each editingCategories as editingCategory, index (editingCategory.id || `new-${index}`)}
            {@const originalCategory = categories.find((c) => c.id === editingCategory.id)}
            {@const isNew = !editingCategory.id}
            {@const isBasic = originalCategory?.isBasic || false}
            {@const isFallback = originalCategory?.isFallback || false}
            {@const isCustom = !isBasic && !isFallback && !isNew}
            {@const canDelete = canManageCategories && !isBasic && !isFallback}

            <div
              class="rounded-lg border p-3 {isNew
                ? 'border-primary border-dashed'
                : 'border-base-300'}"
            >
              <!-- Category Name Input Row -->
              <div class="flex items-center gap-3">
                <div class="flex-1">
                  <input
                    type="text"
                    class="input input-bordered w-full {!canManageCategories
                      ? 'input-disabled'
                      : ''}"
                    placeholder="Category name"
                    value={editingCategory.name}
                    oninput={(e) => {
                      const target = e.target as HTMLInputElement;
                      if (target) updateCategoryName(index, target.value);
                    }}
                    disabled={!canManageCategories || saving}
                    maxlength="16"
                  />
                </div>

                <!-- Delete Button -->
                {#if canDelete}
                  <button
                    class="btn btn-sm btn-ghost btn-square text-error"
                    onclick={() => removeCategory(index)}
                    disabled={saving}
                    title="Delete category"
                    aria-label="Delete category"
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
                        d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                      />
                    </svg>
                  </button>
                {:else if (isBasic || isFallback) && canManageCategories}
                  <div class="flex w-8 justify-center">
                    <div
                      class="tooltip tooltip-left"
                      data-tip="{isFallback ? 'Fallback' : 'Basic'} categories cannot be deleted"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="text-base-content/50 h-4 w-4"
                        fill="none"
                        viewBox="0 0 24 24"
                        stroke="currentColor"
                      >
                        <path
                          stroke-linecap="round"
                          stroke-linejoin="round"
                          stroke-width="2"
                          d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                        />
                      </svg>
                    </div>
                  </div>
                {/if}
              </div>

              <!-- Badges Row -->
              {#if isFallback || isBasic || isNew || isCustom}
                <div class="mt-2 flex gap-1">
                  {#if isFallback}
                    <div class="badge badge-error badge-sm">Fallback</div>
                  {/if}
                  {#if isBasic}
                    <div class="badge badge-info badge-sm">Basic</div>
                  {/if}
                  {#if isNew}
                    <div class="badge badge-success badge-sm">New</div>
                  {/if}
                  {#if isCustom}
                    <div class="badge badge-secondary badge-sm">Custom</div>
                  {/if}
                </div>
              {/if}
            </div>
          {/each}

          {#if editingCategories.length === 0}
            <div class="text-base-content/70 col-span-full py-8 text-center">
              No categories available. Add a category to get started.
            </div>
          {/if}
        </div>

        {#if hasUnsavedChanges}
          <div class="alert alert-warning mt-4">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-6 w-6 shrink-0 stroke-current"
              fill="none"
              viewBox="0 0 24 24"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L4.082 17.5c-.77.833.192 2.5 1.732 2.5z"
              />
            </svg>
            <span>You have unsaved changes. Don't forget to save your changes!</span>
          </div>
        {/if}
      </div>
    </div>

    <!-- Help Section -->
    <div class="prose mt-6 max-w-none">
      <div class="bg-base-200 rounded-lg p-4">
        <h3 class="mb-2 text-lg font-semibold">About Categories</h3>
        <ul class="space-y-1 text-sm">
          <li>
            <strong>Basic categories:</strong> Default categories that come with new account books. They
            cannot be deleted but can be renamed.
          </li>
          <li>
            <strong>Fallback category:</strong> A special category used when other categories are deleted.
            Records will automatically be moved to this category. Cannot be deleted.
          </li>
          <li>
            <strong>Custom categories:</strong> Categories you create. Can be edited or deleted freely.
          </li>
          <li>
            <strong>Category limits:</strong> Names must be 1-16 characters and unique within the account
            book.
          </li>
        </ul>
      </div>
    </div>
  </div>

  <!-- Delete Confirmation Modal -->
  <ConfirmModal
    show={showDeleteConfirm}
    title="Delete Category"
    message="Are you sure you want to delete this category? All records using this category will be moved to the fallback category."
    confirmText="Delete"
    onConfirm={confirmDeleteCategory}
    onCancel={() => {
      showDeleteConfirm = false;
      categoryToDelete = null;
    }}
  />
{:else}
  <div class="mx-auto max-w-4xl">
    <div class="alert alert-error">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="h-6 w-6 shrink-0 stroke-current"
        fill="none"
        viewBox="0 0 24 24"
      >
        <path
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z"
        />
      </svg>
      <span>Account book not found.</span>
    </div>
  </div>
{/if}
