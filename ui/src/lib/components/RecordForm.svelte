<script lang="ts">
  import type { components } from '$lib/api/schema';
  import Loading from '$lib/components/Loading.svelte';
  import { formatDateISO } from '$lib/utils/date';

  type AccountBookView = components['schemas']['AccountBookView'];

  interface RecordFormData {
    categoryId: string;
    recordType: 'INCOME' | 'EXPENSE';
    amount: string;
    summary: string;
    description: string;
    date: string;
  }

  interface Props {
    accountBook: AccountBookView;
    formData: RecordFormData;
    saving: boolean;
    onSubmit: (event: Event) => void;
    onCancel: () => void;
    submitText?: string;
    showPreview?: boolean;
    isModal?: boolean;
  }

  let {
    accountBook,
    formData = $bindable(),
    saving,
    onSubmit,
    onCancel,
    submitText = 'Create Record',
    showPreview = true,
    isModal = false,
  }: Props = $props();

  let amountError = $state('');
  let summaryError = $state('');

  function validateAmount(value: string) {
    if (value === '') {
      amountError = '';
      return;
    }

    const numValue = Number(value);
    if (isNaN(numValue) || !/^\d*\.?\d*$/.test(value)) {
      amountError = 'Please enter a valid number';
    } else if (numValue < 0) {
      amountError = 'Amount cannot be negative';
    } else {
      amountError = '';
    }
  }

  function validateSummary(value: string) {
    if (value === '') {
      summaryError = '';
      return;
    }

    if (value.length > 32) {
      summaryError = 'Summary cannot exceed 32 characters';
    } else {
      summaryError = '';
    }
  }

  function handleAmountInput(event: Event) {
    const target = event.target as HTMLInputElement;
    validateAmount(target.value);
  }

  function handleSummaryInput(event: Event) {
    const target = event.target as HTMLInputElement;
    validateSummary(target.value);
  }

  function handleSubmit(event: Event) {
    event.preventDefault();
    if (amountError || summaryError) return;
    onSubmit(event);
  }

  $effect(() => {
    validateAmount(formData.amount);
    validateSummary(formData.summary);
  });
</script>

<form onsubmit={handleSubmit} class="space-y-6">
  <!-- Type and Category -->
  <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
    <fieldset class="fieldset">
      <legend class="fieldset-legend">Type *</legend>
      <select
        id="record-type"
        class="select select-bordered w-full"
        bind:value={formData.recordType}
        autocomplete="off"
        required
      >
        <option value="EXPENSE">💸 Expense</option>
        <option value="INCOME">💰 Income</option>
      </select>
    </fieldset>

    <fieldset class="fieldset">
      <legend class="fieldset-legend">Category *</legend>
      <select
        id="category"
        class="select select-bordered w-full"
        bind:value={formData.categoryId}
        autocomplete="off"
        required
      >
        <option value="">Select a category</option>
        {#each accountBook.categories || [] as category (category.id)}
          <option value={category.id}>
            {category.name}
          </option>
        {/each}
      </select>
    </fieldset>
  </div>

  <!-- Amount and Date -->
  <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
    <fieldset class="fieldset">
      <legend class="fieldset-legend">Amount *</legend>
      <label
        class="input input-bordered flex items-center gap-2 {amountError ? 'input-error' : ''}"
      >
        <span class="text-base-content/70">₩</span>
        <input
          id="amount"
          type="text"
          class="grow [appearance:textfield] [&::-webkit-inner-spin-button]:appearance-none [&::-webkit-outer-spin-button]:appearance-none"
          bind:value={formData.amount}
          oninput={handleAmountInput}
          placeholder="0"
          autocomplete="off"
          required
          min="0"
          step="1"
        />
      </label>
      {#if amountError}
        <p class="label text-error mt-1 text-sm">{amountError}</p>
      {/if}
    </fieldset>

    <fieldset class="fieldset">
      <legend class="fieldset-legend">Date *</legend>
      <input
        id="date"
        type="date"
        class="input input-bordered w-full"
        bind:value={formData.date}
        required
      />
    </fieldset>
  </div>

  <!-- Summary -->
  <fieldset class="fieldset">
    <legend class="fieldset-legend">Summary *</legend>
    <input
      id="summary"
      type="text"
      class="input input-bordered w-full {summaryError ? 'input-error' : ''}"
      bind:value={formData.summary}
      oninput={handleSummaryInput}
      placeholder="Brief description of the transaction"
      maxlength="32"
      autocomplete="off"
      required
    />
    {#if summaryError}
      <p class="label text-error mt-1 text-sm">{summaryError}</p>
    {:else}
      <p class="label">Required - A short description (max 32 characters)</p>
    {/if}
    <p class="label text-base-content/60 text-xs">
      {formData.summary.length}/32 characters
    </p>
  </fieldset>

  <!-- Description -->
  <fieldset class="fieldset">
    <legend class="fieldset-legend">Description</legend>
    <textarea
      id="description"
      class="textarea textarea-bordered h-24 w-full"
      bind:value={formData.description}
      placeholder="Additional details about this transaction..."
      maxlength="500"
      autocomplete="off"
    ></textarea>
    <p class="label">Optional - Additional details</p>
  </fieldset>

  <!-- Preview -->
  {#if showPreview}
    <div class="card bg-base-200">
      <div class="card-body">
        <h3 class="card-title text-sm">Preview</h3>
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-2">
            <span class="badge badge-sm badge-outline text-sm">
              {accountBook.categories?.find((c) => c.id === formData.categoryId)?.name ||
                'Select category'}
            </span>
          </div>
          <div
            class="text-lg font-semibold {formData.recordType === 'INCOME'
              ? 'text-success'
              : 'text-error'}"
          >
            {formData.recordType === 'INCOME' ? '+' : '-'}{formData.amount
              ? Number(formData.amount).toLocaleString()
              : '0'}
          </div>
        </div>
        {#if formData.summary}
          <p class="text-base-content/80 text-sm">{formData.summary}</p>
        {/if}
        <p class="text-base-content/60 text-xs">
          {formData.date ? formatDateISO(formData.date) : 'Select date'}
        </p>
      </div>
    </div>
  {/if}

  <!-- Actions -->
  <div class="flex gap-4 pt-4">
    <button type="button" class="btn btn-outline {isModal ? '' : 'flex-1'}" onclick={onCancel}>
      Cancel
    </button>
    <button
      type="submit"
      class="btn btn-primary {isModal ? '' : 'flex-1'}"
      disabled={saving ||
        !formData.categoryId ||
        !formData.amount ||
        !formData.date ||
        !formData.summary ||
        !!amountError ||
        !!summaryError}
    >
      {#if saving}
        <Loading size="sm" />
      {/if}
      {submitText}
    </button>
  </div>
</form>
