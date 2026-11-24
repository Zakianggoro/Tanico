# Feature Specification — Notifications

## Scope

In-app notifications for buyers and sellers.

## Data source

`notifications`

## Event examples

- New order created: notify the seller.
- Order accepted/rejected: notify the buyer.
- Order processing/shipped/completed: notify the buyer.
- Other events only after product owner approval.

## Rules

- Each notification belongs to one recipient.
- Users can read only their own notifications.
- Mark-as-read updates only the recipient's notification.
- Notification creation should be tied to trusted server-side order events where possible.
- Push notifications are not part of the baseline; in-app notifications are sufficient for initial implementation.
