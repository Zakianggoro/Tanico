create table public.profiles (
    id uuid primary key references auth.users (id) on delete cascade,
    full_name text not null check (length(trim(full_name)) > 0),
    email text,
    phone text,
    role text not null check (role in ('buyer', 'seller')),
    avatar_path text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create function public.create_profile_for_new_auth_user()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
declare
    selected_role text := new.raw_user_meta_data ->> 'role';
    selected_name text := new.raw_user_meta_data ->> 'full_name';
begin
    if selected_role is null or selected_role not in ('buyer', 'seller') then
        raise exception 'Unsupported TANICO profile role';
    end if;

    insert into public.profiles (id, full_name, email, phone, role)
    values (
        new.id,
        coalesce(nullif(trim(selected_name), ''), 'Pengguna TANICO'),
        coalesce(new.email, new.raw_user_meta_data ->> 'email'),
        coalesce(new.phone, new.raw_user_meta_data ->> 'phone'),
        selected_role
    );
    return new;
end;
$$;

create trigger on_auth_user_created_create_profile
after insert on auth.users
for each row execute function public.create_profile_for_new_auth_user();

create function public.prevent_profile_role_change()
returns trigger
language plpgsql
set search_path = ''
as $$
begin
    if new.role is distinct from old.role and current_user not in ('postgres', 'service_role') then
        raise exception 'Profile role cannot be changed by the account owner';
    end if;
    return new;
end;
$$;

create trigger prevent_profile_role_change_before_update
before update on public.profiles
for each row execute function public.prevent_profile_role_change();

create function public.set_updated_at()
returns trigger
language plpgsql
set search_path = ''
as $$
begin
    new.updated_at = now();
    return new;
end;
$$;

create trigger profiles_set_updated_at
before update on public.profiles
for each row execute function public.set_updated_at();

create table public.stores (
    id uuid primary key default gen_random_uuid(),
    owner_id uuid not null unique references public.profiles (id) on delete cascade,
    store_name text not null check (length(trim(store_name)) > 0),
    description text,
    address text not null check (length(trim(address)) > 0),
    latitude numeric(9, 6) not null,
    longitude numeric(9, 6) not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint stores_latitude_range check (latitude between -90 and 90),
    constraint stores_longitude_range check (longitude between -180 and 180)
);

create trigger stores_set_updated_at
before update on public.stores
for each row execute function public.set_updated_at();

create table public.categories (
    id uuid primary key default gen_random_uuid(),
    name text not null unique check (length(trim(name)) > 0),
    description text,
    created_at timestamptz not null default now()
);

create table public.products (
    id uuid primary key default gen_random_uuid(),
    store_id uuid not null references public.stores (id) on delete cascade,
    category_id uuid not null references public.categories (id),
    name text not null check (length(trim(name)) > 0),
    description text,
    price numeric(14, 2) not null check (price >= 0),
    stock integer not null check (stock >= 0),
    unit text not null,
    weight_per_unit_kg numeric(12, 3) not null check (weight_per_unit_kg >= 0),
    status text not null check (status in ('draft', 'active', 'inactive', 'out_of_stock')),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index products_store_status_idx on public.products (store_id, status);
create index products_category_status_idx on public.products (category_id, status);
revoke update on public.products from authenticated;
grant update (category_id, name, description, price, unit, weight_per_unit_kg, status)
on public.products to authenticated;

create trigger products_set_updated_at
before update on public.products
for each row execute function public.set_updated_at();

create table public.product_images (
    id uuid primary key default gen_random_uuid(),
    product_id uuid not null references public.products (id) on delete cascade,
    storage_path text not null,
    is_primary boolean not null default false,
    created_at timestamptz not null default now()
);

create table public.user_addresses (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references public.profiles (id) on delete cascade,
    label text,
    recipient_name text not null check (length(trim(recipient_name)) > 0),
    phone text not null,
    address text not null check (length(trim(address)) > 0),
    city text not null check (length(trim(city)) > 0),
    postal_code text,
    latitude numeric(9, 6) not null,
    longitude numeric(9, 6) not null,
    is_default boolean not null default false,
    created_at timestamptz not null default now(),
    constraint user_addresses_latitude_range check (latitude between -90 and 90),
    constraint user_addresses_longitude_range check (longitude between -180 and 180)
);

create index user_addresses_user_id_idx on public.user_addresses (user_id);

create table public.orders (
    id uuid primary key default gen_random_uuid(),
    buyer_id uuid not null references public.profiles (id),
    store_id uuid not null references public.stores (id),
    address_id uuid references public.user_addresses (id) on delete set null,
    status text not null check (status in (
        'pending', 'accepted', 'rejected', 'processing', 'shipped', 'completed', 'cancelled'
    )),
    subtotal numeric(14, 2) not null check (subtotal >= 0),
    shipping_fee numeric(14, 2) not null check (shipping_fee >= 0),
    total numeric(14, 2) not null check (total >= 0),
    shipping_distance_km numeric(12, 3) not null check (shipping_distance_km >= 0),
    total_weight_kg numeric(12, 3) not null check (total_weight_kg >= 0),
    shipping_address_snapshot jsonb not null,
    store_address_snapshot jsonb not null,
    shipping_calculation_details jsonb,
    payment_method text,
    payment_status text,
    stock_restored_at timestamptz,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint orders_total_matches_components check (total = subtotal + shipping_fee)
);

create index orders_store_created_at_idx on public.orders (store_id, created_at desc);
create index orders_buyer_created_at_idx on public.orders (buyer_id, created_at desc);

create trigger orders_set_updated_at
before update on public.orders
for each row execute function public.set_updated_at();

create table public.order_items (
    id uuid primary key default gen_random_uuid(),
    order_id uuid not null references public.orders (id) on delete cascade,
    product_id uuid references public.products (id) on delete set null,
    product_name text not null,
    unit_price numeric(14, 2) not null check (unit_price >= 0),
    quantity integer not null check (quantity > 0),
    unit text not null,
    weight_per_unit_kg numeric(12, 3) not null check (weight_per_unit_kg >= 0),
    subtotal numeric(14, 2) not null check (subtotal >= 0)
);

create index order_items_order_id_idx on public.order_items (order_id);

create table public.order_status_history (
    id uuid primary key default gen_random_uuid(),
    order_id uuid not null references public.orders (id) on delete cascade,
    changed_by uuid references public.profiles (id),
    old_status text,
    new_status text not null,
    note text,
    created_at timestamptz not null default now()
);

alter table public.profiles enable row level security;
alter table public.stores enable row level security;
alter table public.user_addresses enable row level security;
alter table public.categories enable row level security;
alter table public.products enable row level security;
alter table public.product_images enable row level security;
alter table public.orders enable row level security;
alter table public.order_items enable row level security;
alter table public.order_status_history enable row level security;

create policy profiles_select_own on public.profiles
for select to authenticated
using (id = (select auth.uid()));

create policy profiles_update_own on public.profiles
for update to authenticated
using (id = (select auth.uid()))
with check (id = (select auth.uid()));

create policy stores_select_own on public.stores
for select to authenticated
using (owner_id = (select auth.uid()));

create policy stores_insert_own_seller on public.stores
for insert to authenticated
with check (
    owner_id = (select auth.uid())
    and exists (
        select 1 from public.profiles p
        where p.id = (select auth.uid()) and p.role = 'seller'
    )
);

create policy stores_update_own_seller on public.stores
for update to authenticated
using (
    owner_id = (select auth.uid())
    and exists (
        select 1 from public.profiles p
        where p.id = (select auth.uid()) and p.role = 'seller'
    )
)
with check (
    owner_id = (select auth.uid())
    and exists (
        select 1 from public.profiles p
        where p.id = (select auth.uid()) and p.role = 'seller'
    )
);

create policy stores_delete_own_seller on public.stores
for delete to authenticated
using (
    owner_id = (select auth.uid())
    and exists (
        select 1 from public.profiles p
        where p.id = (select auth.uid()) and p.role = 'seller'
    )
);

create policy categories_select_public on public.categories
for select to anon, authenticated
using (true);

create policy products_select_published_or_own on public.products
for select to anon, authenticated
using (
    status = 'active'
    or exists (
        select 1 from public.stores s
        where s.id = products.store_id and s.owner_id = (select auth.uid())
    )
);

create policy products_insert_own_store on public.products
for insert to authenticated
with check (
    exists (
        select 1 from public.stores s
        where s.id = products.store_id and s.owner_id = (select auth.uid())
    )
);

create policy products_update_own_store on public.products
for update to authenticated
using (
    exists (
        select 1 from public.stores s
        where s.id = products.store_id and s.owner_id = (select auth.uid())
    )
)
with check (
    exists (
        select 1 from public.stores s
        where s.id = products.store_id and s.owner_id = (select auth.uid())
    )
);

create policy products_delete_own_store on public.products
for delete to authenticated
using (
    exists (
        select 1 from public.stores s
        where s.id = products.store_id and s.owner_id = (select auth.uid())
    )
);

create policy product_images_select_published_or_own on public.product_images
for select to anon, authenticated
using (
    exists (
        select 1 from public.products p
        where p.id = product_images.product_id
          and (p.status = 'active' or exists (
              select 1 from public.stores s
              where s.id = p.store_id and s.owner_id = (select auth.uid())
          ))
    )
);

create policy product_images_insert_own_product on public.product_images
for insert to authenticated
with check (
    exists (
        select 1 from public.products p
        join public.stores s on s.id = p.store_id
        where p.id = product_images.product_id and s.owner_id = (select auth.uid())
    )
);

create policy product_images_update_own_product on public.product_images
for update to authenticated
using (
    exists (
        select 1 from public.products p
        join public.stores s on s.id = p.store_id
        where p.id = product_images.product_id and s.owner_id = (select auth.uid())
    )
)
with check (
    exists (
        select 1 from public.products p
        join public.stores s on s.id = p.store_id
        where p.id = product_images.product_id and s.owner_id = (select auth.uid())
    )
);

create policy product_images_delete_own_product on public.product_images
for delete to authenticated
using (
    exists (
        select 1 from public.products p
        join public.stores s on s.id = p.store_id
        where p.id = product_images.product_id and s.owner_id = (select auth.uid())
    )
);

create policy user_addresses_manage_own_buyer on public.user_addresses
for all to authenticated
using (
    user_id = (select auth.uid())
    and exists (
        select 1 from public.profiles p
        where p.id = (select auth.uid()) and p.role = 'buyer'
    )
)
with check (
    user_id = (select auth.uid())
    and exists (
        select 1 from public.profiles p
        where p.id = (select auth.uid()) and p.role = 'buyer'
    )
);

create policy orders_select_buyer_or_store_owner on public.orders
for select to authenticated
using (
    buyer_id = (select auth.uid())
    or exists (
        select 1 from public.stores s
        where s.id = orders.store_id and s.owner_id = (select auth.uid())
    )
);

create policy order_items_select_related_order on public.order_items
for select to authenticated
using (
    exists (
        select 1 from public.orders o
        where o.id = order_items.order_id
          and (
              o.buyer_id = (select auth.uid())
              or exists (
                  select 1 from public.stores s
                  where s.id = o.store_id and s.owner_id = (select auth.uid())
              )
          )
    )
);

create policy order_status_history_select_related_order on public.order_status_history
for select to authenticated
using (
    exists (
        select 1 from public.orders o
        where o.id = order_status_history.order_id
          and (
              o.buyer_id = (select auth.uid())
              or exists (
                  select 1 from public.stores s
                  where s.id = o.store_id and s.owner_id = (select auth.uid())
              )
          )
    )
);
