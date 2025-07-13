<script lang="ts">
  import { goto } from '$app/navigation';
  import { page } from '$app/state';
  import { api } from '$lib/api/client';
  import Loading from '$lib/components/Loading.svelte';
  import { showApiErrorToast, showSuccessToast } from '$lib/stores/toast.svelte';
  import { isAuthenticated } from '$lib/stores/user.svelte';
  import { onMount } from 'svelte';

  const invitationId = page.params.invitationId;

  let loading = $state(true);
  let joining = $state(false);
  let success = $state(false);

  async function joinInvitation() {
    if (!invitationId) return;

    try {
      joining = true;
      const response = await api.joinInvitation(invitationId);

      if (response.error) {
        showApiErrorToast(
          response.error,
          'Failed to join account book. The invitation may have expired or is invalid.'
        );
        return;
      }

      success = true;
      showSuccessToast('Successfully joined account book!');

      // Redirect to account books after a delay
      setTimeout(() => {
        goto('/');
      }, 2000);
    } catch (err) {
      showApiErrorToast(
        err,
        'Failed to join account book. The invitation may have expired or is invalid.'
      );
    } finally {
      joining = false;
      loading = false;
    }
  }

  onMount(() => {
    if (!isAuthenticated()) {
      // Store the invitation ID and redirect to login
      sessionStorage.setItem('pendingInvitation', invitationId || '');
      goto('/api/v1/oauth2/authorization/google');
      return;
    }

    loading = false;
  });
</script>

<svelte:head>
  <title>Join Account Book - GageBu</title>
</svelte:head>

<div class="mx-auto max-w-4xl">
  {#if loading}
    <Loading size="lg" message="Loading invitation..." />
  {:else if !isAuthenticated()}
    <div class="hero bg-base-100 rounded-box min-h-96">
      <div class="hero-content text-center">
        <div class="max-w-md">
          <h1 class="text-3xl font-bold">Join Account Book</h1>
          <p class="py-6">
            You've been invited to join an account book! Please log in to accept the invitation.
          </p>
          <a href="/api/v1/oauth2/authorization/google" class="btn btn-primary">
            Login with Google
          </a>
        </div>
      </div>
    </div>
  {:else if success}
    <div class="hero bg-base-100 rounded-box min-h-96">
      <div class="hero-content text-center">
        <div class="max-w-md">
          <div class="mb-4 text-6xl">🎉</div>
          <h1 class="text-success text-3xl font-bold">Successfully Joined!</h1>
          <p class="py-6">
            You have successfully joined the account book. Redirecting you to your account books...
          </p>
          <Loading size="lg" message="Redirecting..." />
        </div>
      </div>
    </div>
  {:else}
    <div class="hero bg-base-100 rounded-box min-h-96">
      <div class="hero-content text-center">
        <div class="max-w-md">
          <div class="mb-4 text-6xl">📧</div>
          <h1 class="text-3xl font-bold">Account Book Invitation</h1>
          <p class="py-6">
            You've been invited to join an account book. Click the button below to accept the
            invitation and start collaborating!
          </p>
          <button class="btn btn-primary btn-lg" onclick={joinInvitation} disabled={joining}>
            {#if joining}
              <Loading size="sm" />
            {/if}
            Join Account Book
          </button>
          <div class="mt-4">
            <a href="/" class="link text-sm">Cancel and go to my account books</a>
          </div>
        </div>
      </div>
    </div>
  {/if}
</div>
