<script lang="ts">
  import { goto } from '$app/navigation';
  import { page } from '$app/state';
  import { api } from '$lib/api/client';
  import type { components } from '$lib/api/schema';
  import Loading from '$lib/components/Loading.svelte';
  import { showApiErrorToast } from '$lib/stores/toast.svelte';
  import { isAuthenticated, userState } from '$lib/stores/user.svelte';
  import { onMount } from 'svelte';

  type AccountBookView = components['schemas']['AccountBookView'];

  const accountBookId = page.params.accountBookId;

  let accountBook: AccountBookView | null = $state(null);
  let loading = $state(true);

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
      }
    } catch (err) {
      showApiErrorToast(err, 'Failed to load account book');
    } finally {
      loading = false;
    }
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
  <title>Members - {accountBook?.name || 'Account Book'} - GageBu</title>
</svelte:head>

{#if loading}
  <Loading size="lg" message="Loading members..." />
{:else if accountBook}
  <div class="mx-auto max-w-4xl">
    <!-- Breadcrumb -->
    <div class="breadcrumbs mb-6 text-sm">
      <ul>
        <li><a href="/">Account Books</a></li>
        <li><a href="/account-books/{accountBookId}">{accountBook.name}</a></li>
        <li>Members</li>
      </ul>
    </div>

    <!-- Header -->
    <div class="mb-6">
      <h1 class="text-3xl font-bold">Members</h1>
      <p class="text-base-content/70">
        {accountBook.name} • {accountBook.members?.length || 0} members
      </p>
    </div>

    <!-- Members -->
    {#if accountBook.members && accountBook.members.length > 0}
      <div class="card bg-base-100 shadow-lg">
        <div class="card-body">
          <h2 class="card-title">Account Book Members</h2>
          <div class="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
            {#each accountBook.members as member (member.id)}
              <div class="bg-base-200 flex items-center gap-4 rounded-lg p-4">
                {#if member.pictureUrl}
                  <div class="avatar">
                    <div class="w-12 rounded-full">
                      <img src={member.pictureUrl} alt={member.nickname} />
                    </div>
                  </div>
                {:else}
                  <div
                    class="bg-neutral text-neutral-content flex h-12 w-12 items-center justify-center rounded-full text-lg"
                  >
                    {member.nickname?.charAt(0) || '?'}
                  </div>
                {/if}
                <div class="flex-1">
                  <div class="flex items-center gap-2">
                    <p class="font-semibold">{member.nickname}</p>
                    {#if member.userId === userState.user?.id}
                      <div class="badge badge-primary badge-sm">Me</div>
                    {/if}
                  </div>
                  {#if member.role === 'OWNER'}
                    <div class="badge badge-outline badge-sm">Owner</div>
                  {:else}
                    <div class="badge badge-outline badge-sm">Member</div>
                  {/if}
                </div>
              </div>
            {/each}
          </div>
        </div>
      </div>
    {:else}
      <div class="card bg-base-100 shadow-lg">
        <div class="card-body">
          <div class="py-12 text-center">
            <div class="mb-4 text-6xl">👥</div>
            <h3 class="mb-2 text-xl font-semibold">No members found</h3>
            <p class="text-base-content/70 mb-4">This account book has no members yet.</p>
          </div>
        </div>
      </div>
    {/if}
  </div>
{/if}
