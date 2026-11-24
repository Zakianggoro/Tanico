# Application Architecture

## 1. Architecture baseline

Use MVVM with Repository Pattern. Keep the initial Android Studio project as a single Gradle application module, while organizing source code by feature and responsibility. Multi-module Gradle architecture can be considered later if team size or build complexity requires it.

## 2. Logical layers

```text
Compose UI
   |
   v
ViewModel (UI state and UI events)
   |
   v
Use Cases / Domain rules
   |
   v
Repository interfaces
   |
   v
Repository implementations
   |
   v
Supabase data sources
   |------ Supabase Auth
   |------ PostgreSQL / PostgREST / RPC
   |------ Supabase Storage
```

## 3. Responsibilities

### Presentation
- Compose screens and reusable UI components.
- ViewModels and UI state.
- Navigation and role-aware destination selection.
- No direct database queries from Composables.

### Domain
- Business rules and use cases.
- Domain models and repository contracts.
- Independent of Compose UI implementation.

### Data
- Supabase integration.
- DTOs/data models and mapping.
- Repository implementations.
- Error mapping and serialization.

### Core
- Theme and design system.
- Navigation primitives.
- Session abstraction.
- Shared utilities and common UI components.

## 4. Suggested package structure

```text
com.tanico.app/
├── core/
│   ├── common/
│   ├── navigation/
│   ├── session/
│   ├── theme/
│   └── ui/
├── data/
│   ├── remote/
│   ├── model/
│   ├── mapper/
│   └── repository/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── feature/
│   ├── auth/
│   ├── profile/
│   ├── marketplace/
│   ├── cart/
│   ├── checkout/
│   ├── orders/
│   ├── seller/
│   ├── notifications/
│   └── education/
└── MainActivity.kt
```

The exact package names may be adjusted to the package identifier created in Android Studio. Keep the responsibility boundaries intact.

## 5. Data flow

1. User interacts with a Composable.
2. Composable sends an event to its ViewModel.
3. ViewModel invokes a use case or repository operation.
4. Repository obtains or updates data through Supabase data sources.
5. Result is mapped to domain/UI state.
6. ViewModel exposes updated state to Compose.

## 6. Session and navigation

- Supabase Auth is the identity provider.
- On successful authentication, load the user's profile and role.
- Route `buyer` to buyer navigation and `seller` to seller navigation.
- Handle loading, unauthenticated, profile-missing, and unauthorized states.
- A role-specific UI route does not replace RLS authorization.
