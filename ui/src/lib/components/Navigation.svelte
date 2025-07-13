<script lang="ts">
  import { page } from '$app/state';
  import { api } from '$lib/api/client';
  import { showApiErrorToast } from '$lib/stores/toast.svelte';
  import { isAuthenticated, userState } from '$lib/stores/user.svelte';
  import ThemeToggle from './ThemeToggle.svelte';

  async function handleLogout() {
    try {
      await api.logout();
      window.location.href = '/';
    } catch (error) {
      showApiErrorToast(error, 'Logout failed');
    }
  }

  const currentPath = $derived(page.url.pathname);

  function closeDrawer() {
    const drawer = document.getElementById('mobile-drawer') as HTMLInputElement;
    if (drawer) {
      drawer.checked = false;
    }
  }
</script>

<!-- Mobile Drawer -->
<div class="drawer sm:hidden">
  <input id="mobile-drawer" type="checkbox" class="drawer-toggle" />
  <div class="drawer-content">
    <!-- Mobile Navbar -->
    <div class="navbar bg-base-100 shadow-lg">
      <div class="mx-auto flex w-full max-w-4xl justify-between px-4">
        <div class="navbar-start flex-none">
          <label for="mobile-drawer" class="btn btn-ghost">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M4 6h16M4 12h8m-8 6h16"
              />
            </svg>
          </label>
          <a href="/" class="btn btn-ghost text-xl">💰 GageBu</a>
        </div>
        <div class="navbar-end flex-none">
          <div class="flex items-center gap-2">
            <ThemeToggle />
            {#if isAuthenticated() && userState.user}
              <div class="dropdown dropdown-end">
                <div tabindex="0" role="button" class="btn btn-ghost btn-circle avatar">
                  <div class="w-10 rounded-full">
                    {#if userState.user.pictureUrl}
                      <img alt="Profile" src={userState.user.pictureUrl} />
                    {:else}
                      <div
                        class="bg-neutral text-neutral-content flex h-10 w-10 items-center justify-center rounded-full"
                      >
                        {userState.user.nickname?.charAt(0) ||
                          userState.user.email?.charAt(0) ||
                          '?'}
                      </div>
                    {/if}
                  </div>
                </div>
                <!-- svelte-ignore a11y_no_noninteractive_tabindex -->
                <ul
                  tabindex="0"
                  class="menu menu-sm dropdown-content bg-base-100 rounded-box z-[1] mt-3 w-52 p-2 shadow"
                >
                  <li>
                    <a href="/profile" class="justify-between"> Profile </a>
                  </li>
                  <li><button onclick={handleLogout}>Logout</button></li>
                </ul>
              </div>
            {:else}
              <a href="/api/v1/oauth2/authorization/google" class="btn btn-primary">
                Login with Google
              </a>
            {/if}
          </div>
        </div>
      </div>
    </div>
  </div>

  <div class="drawer-side">
    <label for="mobile-drawer" aria-label="close sidebar" class="drawer-overlay"></label>
    <div class="menu bg-base-100 text-base-content min-h-full w-80 p-4">
      <div class="mb-6 flex items-center justify-between">
        <span class="text-xl font-bold">💰 GageBu</span>
        <div class="flex items-center gap-2">
          <ThemeToggle />
          <label for="mobile-drawer" class="btn btn-sm btn-ghost">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>
          </label>
        </div>
      </div>

      <ul class="menu-compact">
        <li>
          <a href="/" class:font-bold={currentPath === '/'} onclick={closeDrawer}>
            Account Books
          </a>
        </li>
        {#if isAuthenticated()}
          <li>
            <a href="/profile" class:font-bold={currentPath === '/profile'} onclick={closeDrawer}>
              Profile
            </a>
          </li>
        {/if}
      </ul>

      {#if isAuthenticated() && userState.user}
        <div class="divider"></div>
        <div class="flex items-center gap-3 p-2">
          <div class="avatar">
            <div class="w-10 rounded-full">
              {#if userState.user.pictureUrl}
                <img alt="Profile" src={userState.user.pictureUrl} />
              {:else}
                <div
                  class="bg-neutral text-neutral-content flex h-10 w-10 items-center justify-center rounded-full"
                >
                  {userState.user.nickname?.charAt(0) || userState.user.email?.charAt(0) || '?'}
                </div>
              {/if}
            </div>
          </div>
          <div class="flex-1">
            <div class="font-medium">{userState.user.nickname || userState.user.email}</div>
            <button class="btn btn-ghost btn-sm mt-1" onclick={handleLogout}>Logout</button>
          </div>
        </div>
      {:else}
        <div class="divider"></div>
        <a href="/api/v1/oauth2/authorization/google" class="btn btn-primary" onclick={closeDrawer}>
          Login with Google
        </a>
      {/if}
    </div>
  </div>
</div>

<!-- Desktop Navbar -->
<div class="hidden sm:block">
  <div class="navbar bg-base-100 shadow-lg">
    <div class="mx-auto flex w-full max-w-4xl justify-between px-4">
      <div class="navbar-start flex-none">
        <a href="/" class="btn btn-ghost text-xl">💰 GageBu</a>
        <div class="ml-4 hidden sm:block">
          <div class="flex items-center space-x-1">
            <a href="/" class="btn btn-ghost btn-sm" class:font-bold={currentPath === '/'}>
              Account Books
            </a>
            {#if isAuthenticated()}
              <a
                href="/profile"
                class="btn btn-ghost btn-sm"
                class:font-bold={currentPath === '/profile'}
              >
                Profile
              </a>
            {/if}
          </div>
        </div>
      </div>

      <div class="navbar-end flex-none">
        <div class="flex items-center gap-2">
          <ThemeToggle />
          {#if isAuthenticated() && userState.user}
            <div class="dropdown dropdown-end">
              <div tabindex="0" role="button" class="btn btn-ghost btn-circle avatar">
                <div class="w-10 rounded-full">
                  {#if userState.user.pictureUrl}
                    <img alt="Profile" src={userState.user.pictureUrl} />
                  {:else}
                    <div
                      class="bg-neutral text-neutral-content flex h-10 w-10 items-center justify-center rounded-full"
                    >
                      {userState.user.nickname?.charAt(0) || userState.user.email?.charAt(0) || '?'}
                    </div>
                  {/if}
                </div>
              </div>
              <!-- svelte-ignore a11y_no_noninteractive_tabindex -->
              <ul
                tabindex="0"
                class="menu menu-sm dropdown-content bg-base-100 rounded-box z-[1] mt-3 w-52 p-2 shadow"
              >
                <li>
                  <a href="/profile" class="justify-between"> Profile </a>
                </li>
                <li><button onclick={handleLogout}>Logout</button></li>
              </ul>
            </div>
          {:else}
            <a href="/api/v1/oauth2/authorization/google" class="btn btn-primary">
              Login with Google
            </a>
          {/if}
        </div>
      </div>
    </div>
  </div>
</div>
