# Feature Specification — Authentication and Profile

## Scope

Registration, login, logout, session restoration, role-aware navigation, and buyer/seller profile management.

## Authentication provider

Supabase Auth.

## User roles

- `buyer`
- `seller`

The selected role is stored in `profiles.role` and must be validated server-side. Do not allow users to self-promote or change roles through an unrestricted profile update.

## Main flows

### Registration
1. User selects buyer or seller.
2. User enters required registration data.
3. Supabase Auth creates the authentication identity.
4. A profile record is created with the selected permitted role.
5. If seller onboarding requires store data, create/complete the store profile.
6. Handle email verification if enabled in Supabase project settings.

### Login
1. User submits credentials.
2. Supabase Auth authenticates.
3. App loads profile and role.
4. App routes buyer to buyer navigation or seller to seller navigation.
5. Missing profile or unsupported role must be handled explicitly.

### Logout
1. Clear Supabase session.
2. Clear in-memory user-specific state.
3. Navigate to authentication entry screen.

## Profile

Buyer profile may show personal information and saved addresses.

Seller profile may show personal information, store information, and seller-related summaries. Sales summary calculations belong to the seller module.

## Security

- Session state must be observed/restored from Supabase Auth.
- Never store passwords in application tables.
- Never expose service-role credentials.
- RLS must protect profile and store records.
