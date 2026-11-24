# Coding Conventions

## Kotlin
- Use clear, descriptive names.
- Classes and composables: PascalCase.
- Functions and properties: camelCase.
- Constants: UPPER_SNAKE_CASE where appropriate.
- Keep functions focused and avoid oversized ViewModels.
- Prefer immutable UI state models.
- Use coroutine/Flow patterns consistently.

## Compose
- Keep Composables focused on rendering and event forwarding.
- Hoist state when it improves reuse/testability.
- Do not call Supabase directly from Composables.
- Preview reusable UI components where practical.
- Use Material 3 theme tokens rather than hardcoded colors in screens.

## Data
- Separate DTOs from domain models where mapping is useful.
- Centralize error mapping.
- Do not expose raw backend exceptions directly to users.
- Keep serialization consistent.

## Security
- No secrets in source control.
- No service-role key in Android.
- Do not trust client-supplied role, owner ID, price, stock, or total.
