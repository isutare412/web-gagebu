<script lang="ts">
  import { goto } from '$app/navigation';
  import { api } from '$lib/api/client';
  import Loading from '$lib/components/Loading.svelte';
  import { showApiErrorToast, showSuccessToast } from '$lib/stores/toast.svelte';
  import { isAuthenticated, setUser, userState } from '$lib/stores/user.svelte';
  import { formatDateISO } from '$lib/utils/date';
  import { onMount } from 'svelte';

  let editing = $state(false);
  let editedNickname = $state('');
  let saving = $state(false);

  function startEdit() {
    editedNickname = userState.user?.nickname || '';
    editing = true;
  }

  function cancelEdit() {
    editing = false;
    editedNickname = '';
  }

  async function saveProfile() {
    if (!userState.user?.id || !editedNickname.trim()) return;

    try {
      saving = true;
      const response = await api.updateUser(userState.user.id, { nickname: editedNickname });

      if (response.error) {
        showApiErrorToast(response.error, 'Failed to update profile');
        return;
      }

      if (response.data) {
        // Update the user store with new data
        setUser(response.data);
        editing = false;
        showSuccessToast('Profile updated successfully');
      }
    } catch (err) {
      showApiErrorToast(err, 'Failed to update profile');
    } finally {
      saving = false;
    }
  }

  onMount(() => {
    if (!isAuthenticated()) {
      goto('/');
    }
  });
</script>

{#if !isAuthenticated()}
  <div class="hero min-h-96">
    <div class="hero-content text-center">
      <div class="max-w-md">
        <h1 class="text-2xl font-bold">Access Denied</h1>
        <p class="py-6">Please log in to view your profile.</p>
        <a href="/" class="btn btn-primary">Go Home</a>
      </div>
    </div>
  </div>
{:else if userState.user}
  <div class="mx-auto max-w-4xl">
    <div class="card bg-base-100 shadow-xl">
      <div class="card-body p-4 sm:p-6">
        <div class="mb-6 flex items-center gap-4">
          <div class="avatar">
            <div class="w-24 rounded-full">
              {#if userState.user.pictureUrl}
                <img src={userState.user.pictureUrl} alt="Profile" />
              {:else}
                <div
                  class="bg-neutral text-neutral-content flex h-24 w-24 items-center justify-center rounded-full text-3xl"
                >
                  {userState.user.nickname?.charAt(0) || userState.user.email?.charAt(0) || '?'}
                </div>
              {/if}
            </div>
          </div>
          <div>
            <h1 class="text-3xl font-bold">Profile</h1>
            <p class="text-base-content/70">Manage your account information</p>
          </div>
        </div>

        <div class="space-y-4">
          <!-- Nickname -->
          <div class="form-control">
            <label class="label" for="nickname-input">
              <span class="label-text font-semibold">Nickname</span>
            </label>
            {#if editing}
              <div class="flex items-center gap-2">
                <input
                  id="nickname-input"
                  type="text"
                  class="input input-bordered max-w-[160px] flex-1"
                  bind:value={editedNickname}
                  placeholder="Enter nickname"
                />
                <button
                  class="btn btn-success btn-sm"
                  onclick={saveProfile}
                  disabled={saving || !editedNickname.trim()}
                >
                  {#if saving}
                    <Loading size="xs" />
                  {/if}
                  Save
                </button>
                <button class="btn btn-ghost btn-sm" onclick={cancelEdit}> Cancel </button>
              </div>
            {:else}
              <div class="flex items-center gap-2">
                <span class="text-lg">{userState.user.nickname || 'Not set'}</span>
                <button class="btn btn-sm" onclick={startEdit}> Edit </button>
              </div>
            {/if}
          </div>

          <!-- Email -->
          <div class="form-control">
            <div class="label">
              <span class="label-text font-semibold">Email</span>
            </div>
            <div class="text-lg">{userState.user.email}</div>
          </div>

          <!-- Identity Provider -->
          <div class="form-control">
            <div class="label">
              <span class="label-text font-semibold">Sign-in Method</span>
            </div>
            <div class="flex items-center gap-2">
              <div class="badge badge-outline">
                {userState.user.idpType || 'Unknown'}
              </div>
            </div>
          </div>

          <!-- Roles -->
          {#if userState.user.roles && userState.user.roles.length > 0}
            <div class="form-control">
              <div class="label">
                <span class="label-text font-semibold">Roles</span>
              </div>
              <div class="flex gap-2">
                {#each userState.user.roles as role (role)}
                  <div class="badge badge-primary">{role}</div>
                {/each}
              </div>
            </div>
          {/if}

          <!-- Account Information -->
          <div class="divider">Account Information</div>

          <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
            <div class="form-control">
              <div class="label">
                <span class="label-text font-semibold">Member Since</span>
              </div>
              <div class="text-sm">
                {formatDateISO(userState.user.createdAt || '')}
              </div>
            </div>

            <div class="form-control">
              <div class="label">
                <span class="label-text font-semibold">Last Updated</span>
              </div>
              <div class="text-sm">
                {formatDateISO(userState.user.updatedAt || '')}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
{/if}
