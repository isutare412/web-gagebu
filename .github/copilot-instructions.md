# Web-Gagebu Coding Assistant Instructions

## Project Overview
Web-Gagebu is a full-stack account book application with:
- **Backend**: Spring Boot 3.5 API with Java 21, PostgreSQL, Redis
- **Frontend**: SvelteKit 2.x with TypeScript, TailwindCSS 4.x, DaisyUI
- **Architecture**: Feature-based modular design with strict separation of concerns

## Key Development Patterns

### Backend Architecture (api/)
- **Feature modules**: Each feature lives in `feature/[name]/` with standardized structure:
  - `controller/` - REST endpoints with OpenAPI docs
  - `service/` - Business logic
  - `repository/` - Data access with QueryDSL 
  - `domain/` - JPA entities extending `BaseEntity`
  - `dto/` - Request/response objects
  - `mapping/` - MapStruct mappers
  - `validation/` - Custom validators

- **Security**: OAuth2 + JWT hybrid auth with cookie-based tokens
  - Controllers use `@AuthenticationPrincipal JwtUserPayload` for user context
  - Dual auth scheme: Bearer token + Cookie token (see `SecurityConfig`)

- **Error Handling**: Centralized via `GlobalExceptionHandler` with `ErrorCode` enum
  - Use `AppException` for business logic errors with specific error codes

- **Entity Design**: 
  - All entities extend `BaseEntity` (audit fields) or `BaseTimeEntity`
  - Use `UUID` for all primary keys
  - JPA auditing enabled for created/modified tracking

### Frontend Architecture (ui/)
- **SvelteKit**: SSR/SPA hybrid with file-based routing (see [svelte5.md](./svelte5.md) for Svelte 5 reference)
  - Avoid Svelte 4 syntax like `$:`, stores.
  - **Event Handling**: Use Svelte 5 syntax (`onclick`, `onmouseenter`, etc.) instead of old syntax (`on:click`, `on:mouseenter`)
    - Correct: `<button onclick={handleClick}>Click me</button>`
    - Incorrect: `<button on:click={handleClick}>Click me</button>`
    - Mixing syntaxes will cause compilation errors
  - **Dynamic Elements**: Use `svelte:element` to avoid partial tag issues in conditional blocks
    - Problem: `{#if condition}<button>...{:else}<div>...{/if}` causes "partial tag" compilation errors
    - Solution: Use `svelte:element` with dynamic `this` attribute:
    ```svelte
    <script>
      const elementType = $derived(isClickable ? 'button' : 'div');
      const elementProps = $derived(isClickable ? { type: 'button', onclick: handler } : {});
    </script>
    <svelte:element this={elementType} {...elementProps} class="shared-styles">
      <!-- content -->
    </svelte:element>
    ```
    - Allows conditional element types without splitting template blocks
    - Works with any HTML element or Svelte component
    - Enables clean conditional styling and event handling
  - **Reactive State**: Use `$derived.by()` for complex derived values that require function bodies
    - Simple: `let count = $derived(items.length)`
    - Complex: `let isValid = $derived.by(() => { /* complex logic */ return result; })`
    - Note: `$derived.by()` returns the computed value directly, not a function
  - **Reactive Timing**: Use `tick()` to handle multiple rapid state changes in `$effect`
    - Import: `import { tick } from 'svelte'`
    - Use with flag pattern to prevent duplicate API calls:
      ```typescript
      let isLoading = $state(false);
      
      $effect(() => {
        if (conditions && !isLoading) {
          isLoading = true;
          tick().then(() => {
            apiCall().finally(() => {
              isLoading = false;
            });
          });
        }
      });
      ```
    - `tick()` waits for all pending state changes to be applied before proceeding
    - Eliminates need for arbitrary timeouts and provides more predictable timing
    - Prevents race conditions when multiple derived values change simultaneously
- **Styling**: TailwindCSS 4.x + DaisyUI 5.x components (see [daisyui.md](./daisyui.md) for component reference)
- **Build**: Vite with TypeScript, uses PNPM for package management
- **Node Version**: Managed via Volta (22.17.0)
- **Package Management**: Always use `pnpm` instead of `npm` for all package operations
  - Install packages: `pnpm add <package>` or `pnpm add -D <package>` for dev dependencies
  - Run scripts: `pnpm run <script>` or `pnpm <script>` for common scripts
  - Install dependencies: `pnpm install` (never `npm install`)
- **Import Strategy**: Use direct imports instead of barrel exports (index.ts files)
  - Prefer: `import Component from '$lib/components/Component.svelte'`
  - Avoid: `import { Component } from '$lib/components'` with index.ts barrel exports
  - Direct imports provide better IDE support, clearer dependency tracking, and simpler refactoring

- **Error Handling**: Toast-first approach for user feedback
  - Use `showApiErrorToast()` for all API failures (4xx/5xx responses)
  - **Important**: Check `response.error` first, as `openapi-fetch` doesn't throw on HTTP errors
  - Pattern: `if (response.error) { showApiErrorToast(response.error, 'Fallback message'); return; }`
  - Utilizes structured `ErrorResponse` schema with `errorCode` and `message` fields
  - Transforms error codes to human-readable titles (e.g., "BAD_REQUEST" → "Bad Request")
  - Avoid inline error banners/alerts - use toast notifications instead
  - Toast system provides consistent positioning, auto-dismissal, and progress indicators
  - Do NOT duplicate error messages (both toast + banner)

### Development Workflow

#### Running the Application
```bash
# Start infrastructure (PostgreSQL + Redis)
docker compose up postgres redis -d

# Backend development
cd api
./gradlew bootRun --args='--spring.profiles.active=local'

# Frontend development  
cd ui
pnpm dev
```

#### Testing
```bash
# Backend tests (uses H2 in-memory DB)
cd api
./gradlew test

# Frontend linting/formatting
cd ui
pnpm lint
pnpm format
```

#### Building
```bash
# Backend JAR
cd api
./gradlew bootJar

# Frontend production build
cd ui
pnpm build
```

## Configuration Management
- **Environment-specific configs**: `application-{profile}.yaml`
- **Required environment variables**: Check `compose.yaml` for database/Redis settings
- **JWT Keys**: Generate via `api/scripts/generate_rsa256_keypair.sh`
- **Local development**: Use `application-local.yaml` profile

## Code Generation & Tools
- **QueryDSL**: Q-classes generated automatically via annotation processor
- **MapStruct**: Mappers generated at compile time (see `mapping/` packages)
- **Lombok**: Reduces boilerplate (getters, builders, etc.)

## Key Integration Points
- **Database**: PostgreSQL with JPA/Hibernate and QueryDSL for complex queries
- **Authentication**: Google OAuth2 → JWT tokens stored in HTTP-only cookies
- **API Documentation**: OpenAPI 3 with Swagger UI at `/swagger-ui.html`
- **Logging**: Log4j2 with JSON formatting for structured logs

## When Adding New Features
1. Create feature package in `api/src/main/java/.../feature/[name]/`
2. Follow the controller → service → repository → entity pattern
3. Add proper OpenAPI annotations to controllers
4. Write tests following nested describe/context pattern (see `JwtProviderTest`)
5. Update error codes in `ErrorCode` enum if needed
6. Create corresponding Svelte routes/components in `ui/src/routes/`

## Common Gotchas
- JWT tokens require both private/public key configuration
- Database schema auto-updates in development (`ddl-auto: update`)
- Virtual threads enabled for better concurrency
- Security filters run with specific order (see `@EnableTransactionManagement(order = 100)`)
- Frontend uses TailwindCSS 4.x (different from v3 syntax)
