create table public.carts (
    id uuid primary key default gen_random_uuid(),
    buyer_id uuid not null unique references public.profiles (id) on delete cascade,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create trigger carts_set_updated_at
before update on public.carts
for each row execute function public.set_updated_at();

create table public.cart_items (
    id uuid primary key default gen_random_uuid(),
    cart_id uuid not null references public.carts (id) on delete cascade,
    product_id uuid not null references public.products (id) on delete cascade,
    quantity integer not null check (quantity > 0),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    unique (cart_id, product_id)
);

create index cart_items_cart_id_idx on public.cart_items (cart_id);

create trigger cart_items_set_updated_at
before update on public.cart_items
for each row execute function public.set_updated_at();

create table public.notifications (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references public.profiles (id) on delete cascade,
    order_id uuid references public.orders (id) on delete set null,
    title text not null,
    message text not null,
    type text not null,
    is_read boolean not null default false,
    created_at timestamptz not null default now()
);

create index notifications_user_read_created_idx
on public.notifications (user_id, is_read, created_at desc);

create table public.article_categories (
    id uuid primary key default gen_random_uuid(),
    name text not null unique check (length(trim(name)) > 0),
    description text
);

create table public.articles (
    id uuid primary key default gen_random_uuid(),
    category_id uuid references public.article_categories (id) on delete set null,
    title text not null check (length(trim(title)) > 0),
    content text not null,
    image_path text,
    status text not null check (status in ('draft', 'published', 'archived')),
    published_at timestamptz,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index articles_status_published_at_idx
on public.articles (status, published_at desc);

create trigger articles_set_updated_at
before update on public.articles
for each row execute function public.set_updated_at();

create table public.article_likes (
    id uuid primary key default gen_random_uuid(),
    article_id uuid not null references public.articles (id) on delete cascade,
    user_id uuid not null references public.profiles (id) on delete cascade,
    created_at timestamptz not null default now(),
    unique (article_id, user_id)
);

create table public.waste_types (
    id uuid primary key default gen_random_uuid(),
    name text not null unique check (length(trim(name)) > 0),
    unit text not null,
    description text,
    is_active boolean not null default true
);

create table public.waste_price_estimates (
    id uuid primary key default gen_random_uuid(),
    waste_type_id uuid not null references public.waste_types (id) on delete cascade,
    estimated_price numeric(14, 2) not null check (estimated_price >= 0),
    effective_at timestamptz not null,
    source_note text,
    created_at timestamptz not null default now()
);

create index waste_price_estimates_type_effective_idx
on public.waste_price_estimates (waste_type_id, effective_at desc);

alter table public.carts enable row level security;
alter table public.cart_items enable row level security;
alter table public.notifications enable row level security;
alter table public.article_categories enable row level security;
alter table public.articles enable row level security;
alter table public.article_likes enable row level security;
alter table public.waste_types enable row level security;
alter table public.waste_price_estimates enable row level security;

create policy carts_manage_own_buyer on public.carts
for all to authenticated
using (
    buyer_id = (select auth.uid())
    and exists (
        select 1 from public.profiles p
        where p.id = (select auth.uid()) and p.role = 'buyer'
    )
)
with check (
    buyer_id = (select auth.uid())
    and exists (
        select 1 from public.profiles p
        where p.id = (select auth.uid()) and p.role = 'buyer'
    )
);

create policy cart_items_select_own_buyer on public.cart_items
for select to authenticated
using (
    exists (
        select 1 from public.carts c
        join public.profiles p on p.id = c.buyer_id
        where c.id = cart_items.cart_id
          and c.buyer_id = (select auth.uid())
          and p.role = 'buyer'
    )
);

create policy cart_items_insert_own_buyer on public.cart_items
for insert to authenticated
with check (
    exists (
        select 1 from public.carts c
        join public.profiles p on p.id = c.buyer_id
        where c.id = cart_items.cart_id
          and c.buyer_id = (select auth.uid())
          and p.role = 'buyer'
    )
    and exists (
        select 1 from public.products product
        where product.id = cart_items.product_id and product.status = 'active'
    )
);

create policy cart_items_update_own_buyer on public.cart_items
for update to authenticated
using (
    exists (
        select 1 from public.carts c
        join public.profiles p on p.id = c.buyer_id
        where c.id = cart_items.cart_id
          and c.buyer_id = (select auth.uid())
          and p.role = 'buyer'
    )
)
with check (
    exists (
        select 1 from public.carts c
        join public.profiles p on p.id = c.buyer_id
        where c.id = cart_items.cart_id
          and c.buyer_id = (select auth.uid())
          and p.role = 'buyer'
    )
    and exists (
        select 1 from public.products product
        where product.id = cart_items.product_id and product.status = 'active'
    )
);

create policy cart_items_delete_own_buyer on public.cart_items
for delete to authenticated
using (
    exists (
        select 1 from public.carts c
        join public.profiles p on p.id = c.buyer_id
        where c.id = cart_items.cart_id
          and c.buyer_id = (select auth.uid())
          and p.role = 'buyer'
    )
);

create policy notifications_select_own on public.notifications
for select to authenticated
using (user_id = (select auth.uid()));

create policy notifications_update_read_state on public.notifications
for update to authenticated
using (user_id = (select auth.uid()))
with check (user_id = (select auth.uid()));

revoke update on public.notifications from authenticated;
grant update (is_read) on public.notifications to authenticated;

create policy article_categories_select_public on public.article_categories
for select to anon, authenticated
using (true);

create policy articles_select_published on public.articles
for select to anon, authenticated
using (status = 'published' and published_at is not null and published_at <= now());

create policy article_likes_select_own on public.article_likes
for select to authenticated
using (user_id = (select auth.uid()));

create policy article_likes_insert_own on public.article_likes
for insert to authenticated
with check (
    user_id = (select auth.uid())
    and exists (
        select 1 from public.articles article
        where article.id = article_likes.article_id
          and article.status = 'published'
          and article.published_at is not null
          and article.published_at <= now()
    )
);

create policy article_likes_delete_own on public.article_likes
for delete to authenticated
using (user_id = (select auth.uid()));

create policy waste_types_select_active on public.waste_types
for select to anon, authenticated
using (is_active);

create policy waste_price_estimates_select_public on public.waste_price_estimates
for select to anon, authenticated
using (
    exists (
        select 1 from public.waste_types waste_type
        where waste_type.id = waste_price_estimates.waste_type_id
          and waste_type.is_active
    )
);
