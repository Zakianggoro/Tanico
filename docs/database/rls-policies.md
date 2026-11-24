# Supabase RLS Policy Requirements

This document defines access requirements, not final SQL policies. Policies must be reviewed and tested before deployment.

## General

- Enable RLS on every application table exposed through Supabase client APIs.
- Use `auth.uid()` to identify the authenticated user.
- Do not trust user IDs or roles supplied by Android.
- Use database-side ownership checks.
- Keep service-role credentials on trusted server infrastructure only.

## Access matrix

| Data | Buyer | Seller |
|---|---|---|
| Own profile | Read/update | Read/update |
| Own addresses | CRUD | As product requirements permit |
| Published products | Read | Read |
| Own store/products | No management | CRUD own store only |
| Own cart | CRUD | Not applicable |
| Own buyer orders | Read | Not applicable |
| Orders for own store | Not applicable | Read/update allowed statuses |
| Own notifications | Read/update read state | Read/update read state |
| Published articles | Read | Read |
| Own article likes | CRUD own likes | CRUD own likes |

## Important safeguards

- Prevent users from changing their own role through a normal profile update.
- Seller ownership must be verified through `stores.owner_id`.
- Buyers must not update order totals, store IDs, buyer IDs, or order status directly.
- Sellers must not change buyer identity or financial snapshots.
- Order creation and stock mutation must use trusted database functions.
- Test policies using separate buyer and seller test accounts.
