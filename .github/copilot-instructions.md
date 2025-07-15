# Copilot Instructions for Web GageBu

## UI/UX Guidelines

### Card Body Padding
When using `card-body` elements, follow this responsive padding pattern for consistent mobile experience:

- **Default cards**: Use `card-body p-4 sm:p-6` for most cards (categories, records, account books, members, profile, etc.)
- **Small cards**: Use `card-body p-3 sm:p-4` for compact cards like RecordCard components
- **Large cards**: Use `card-body p-3 sm:p-6` for home page/landing cards

**Pattern**: Always use smaller padding on mobile (`p-3` or `p-4`) and larger padding on desktop (`sm:p-4` or `sm:p-6`).

**Example**:
```svelte
<!-- Standard card -->
<div class="card bg-base-100 shadow-xl">
  <div class="card-body p-4 sm:p-6">
    <!-- content -->
  </div>
</div>

<!-- Compact card -->
<div class="card bg-base-100 shadow-sm">
  <div class="card-body p-3 sm:p-4">
    <!-- content -->
  </div>
</div>
```

This ensures optimal mobile experience with appropriate spacing across all screen sizes.
