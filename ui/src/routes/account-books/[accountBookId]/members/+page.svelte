<script lang="ts">
  import { goto } from '$app/navigation';
  import { page } from '$app/state';
  import type { MemberUpdateRequest } from '$lib/api/client';
  import { api } from '$lib/api/client';
  import type { components } from '$lib/api/schema';
  import ConfirmModal from '$lib/components/ConfirmModal.svelte';
  import Loading from '$lib/components/Loading.svelte';
  import { showApiErrorToast, showSuccessToast } from '$lib/stores/toast.svelte';
  import { isAuthenticated, userState } from '$lib/stores/user.svelte';
  import { onMount } from 'svelte';

  type AccountBookView = components['schemas']['AccountBookView'];
  type MemberView = components['schemas']['MemberView'];

  const accountBookId = page.params.accountBookId;

  let accountBook: AccountBookView | null = $state(null);
  let loading = $state(true);
  let updatingMember = $state<string | null>(null);
  let showConfirmModal = $state(false);
  let confirmAction = $state<{ member: MemberView; newRole: 'OWNER' | 'PARTICIPANT' } | null>(null);

  // Check if current user is the owner (can manage roles)
  function isCurrentUserOwner(): boolean {
    if (!accountBook?.members || !userState.user) return false;
    const currentUserMember = accountBook.members.find((m) => m.userId === userState.user!.id);
    return currentUserMember?.role === 'OWNER';
  }

  // Check if a member is the creator (creator cannot have role changed)
  function isMemberCreator(member: MemberView): boolean {
    // Use the createdBy field from AccountBookView to identify the creator
    if (!accountBook?.createdBy || !member.userId) return false;
    return accountBook.createdBy === member.userId;
  }

  function showRoleUpdateConfirm(member: MemberView, newRole: 'OWNER' | 'PARTICIPANT') {
    confirmAction = { member, newRole };
    showConfirmModal = true;
  }

  function cancelRoleUpdate() {
    showConfirmModal = false;
    confirmAction = null;
  }

  async function confirmRoleUpdate() {
    if (!confirmAction) return;

    await updateMemberRole(confirmAction.member, confirmAction.newRole);
    showConfirmModal = false;
    confirmAction = null;
  }

  async function updateMemberRole(member: MemberView, newRole: 'OWNER' | 'PARTICIPANT') {
    if (!accountBookId || !member.id) return;

    try {
      updatingMember = member.id;
      const request: MemberUpdateRequest = { role: newRole };
      const response = await api.updateMember(accountBookId, member.id, request);

      if (response.error) {
        showApiErrorToast(response.error, 'Failed to update member role');
        return;
      }

      if (response.data) {
        // Update the member in the local state
        if (accountBook?.members) {
          const memberIndex = accountBook.members.findIndex((m) => m.id === member.id);
          if (memberIndex !== -1) {
            accountBook.members[memberIndex] = response.data;
          }
        }
        showSuccessToast(`Updated ${member.nickname}'s role to ${newRole.toLowerCase()}`);
      }
    } catch (err) {
      showApiErrorToast(err, 'Failed to update member role');
    } finally {
      updatingMember = null;
    }
  }

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
        <div class="card-body p-4 sm:p-6">
          <h2 class="card-title">Account Book Members</h2>
          <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
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
                    {#if isMemberCreator(member)}
                      <div class="badge badge-accent badge-sm">Creator</div>
                    {/if}
                  </div>
                  <div class="mt-1 flex items-center gap-2">
                    {#if member.role === 'OWNER'}
                      <div class="badge badge-outline badge-sm">Owner</div>
                    {:else}
                      <div class="badge badge-outline badge-sm">Member</div>
                    {/if}

                    <!-- Role Update Controls -->
                    {#if isCurrentUserOwner() && !isMemberCreator(member) && member.userId !== userState.user?.id}
                      <div class="flex gap-1">
                        {#if member.role === 'PARTICIPANT'}
                          <button
                            class="btn btn-xs btn-ghost"
                            onclick={() => showRoleUpdateConfirm(member, 'OWNER')}
                            disabled={updatingMember === member.id}
                          >
                            {#if updatingMember === member.id}
                              <span class="loading loading-spinner loading-xs"></span>
                            {:else}
                              ↑ Promote
                            {/if}
                          </button>
                        {:else}
                          <button
                            class="btn btn-xs btn-ghost"
                            onclick={() => showRoleUpdateConfirm(member, 'PARTICIPANT')}
                            disabled={updatingMember === member.id}
                          >
                            {#if updatingMember === member.id}
                              <span class="loading loading-spinner loading-xs"></span>
                            {:else}
                              ↓ Demote
                            {/if}
                          </button>
                        {/if}
                      </div>
                    {/if}
                  </div>
                </div>
              </div>
            {/each}
          </div>
        </div>
      </div>
    {:else}
      <div class="card bg-base-100 shadow-lg">
        <div class="card-body p-4 sm:p-6">
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

<ConfirmModal
  show={showConfirmModal}
  title="Confirm Role Change"
  message={confirmAction
    ? `Are you sure you want to ${confirmAction.newRole === 'OWNER' ? 'promote' : 'demote'} ${confirmAction.member.nickname} ${confirmAction.newRole === 'OWNER' ? 'to Owner' : 'to Member'}?`
    : ''}
  confirmText={confirmAction?.newRole === 'OWNER' ? 'Promote' : 'Demote'}
  cancelText="Cancel"
  confirmButtonClass="btn-primary"
  loading={confirmAction ? updatingMember === confirmAction.member.id : false}
  onConfirm={confirmRoleUpdate}
  onCancel={cancelRoleUpdate}
/>
