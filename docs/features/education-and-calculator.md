# Feature Specification — Education and Waste Value Calculator

## Education scope

- Article list.
- Article search.
- Article categories.
- Article detail.
- Like/unlike article.

## Data sources

- `article_categories`
- `articles`
- `article_likes`

## Rules

- Only published articles are visible to normal users.
- A user can have at most one like per article.
- Article content and images are managed through approved data administration workflow.

## Waste value calculator

Data sources:
- `waste_types`
- `waste_price_estimates`

Conceptual calculation:

`estimated_value = entered_quantity * estimated_unit_price`

The UI must state that the result is an estimate and may differ from actual local selling prices. Store the estimate reference/version or effective timestamp used when appropriate.
