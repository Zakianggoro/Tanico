# Supabase migrations

No executable migration is included yet intentionally.

Before writing migrations, the team must review:
1. `docs/database/database-design.md`
2. `docs/database/table-definitions.md`
3. `docs/database/rls-policies.md`
4. `docs/database/transaction-workflow.md`

Open decisions affecting implementation:
- payment workflow,
- routing/distance provider,
- shipping tariff formula,
- cancellation rules,
- exact product units and mandatory weight requirements.

Once reviewed, create ordered SQL migration files in this directory. Never apply unreviewed generated SQL to a shared Supabase project.
