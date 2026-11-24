# Testing Guidelines

## Unit tests
- ViewModel state transitions.
- Input validation.
- Cart quantity calculations.
- Order total calculations.
- Weight calculation.
- Role-aware navigation decisions.
- Calculator estimated value.

## Integration tests
- Supabase authentication and profile loading.
- Product listing and filtering.
- Cart persistence.
- Checkout RPC success and failure.
- Order status transitions.
- Notification creation and read state.

## Database/security tests
- Buyer cannot read another buyer's private records.
- Seller cannot modify another seller's products.
- Buyer cannot alter order totals/status directly.
- Concurrent checkout cannot oversell stock.
- Repeated cancellation/rejection cannot restore stock twice.
- Payment remains unconfirmed unless a real approved payment workflow confirms it.

## Manual test scenarios
Maintain test accounts for buyer and seller roles. Test empty states, network failures, expired sessions, unavailable products, insufficient stock, and unauthorized access.
