-- DEVELOPMENT ONLY. Run manually against a disposable local Supabase database.
-- Creates one demo store (if needed) and two sample orders for existing buyer/seller profiles.
-- Never apply this file to a shared or production project.
do $$
declare
    buyer_id uuid;
    seller_id uuid;
    store_id uuid;
begin
    select id into buyer_id
    from public.profiles
    where role = 'buyer'
    order by created_at
    limit 1;

    select id into seller_id
    from public.profiles
    where role = 'seller'
    order by created_at
    limit 1;

    if buyer_id is null or seller_id is null then
        raise exception 'Create one buyer and one seller account before using the local analytics seed.';
    end if;

    insert into public.stores (owner_id, store_name, address, latitude, longitude)
    values (seller_id, 'Toko Demo Lokal', 'Alamat contoh lokal, Kota Malang', 0, 0)
    on conflict (owner_id) do nothing;

    select id into store_id from public.stores where owner_id = seller_id;

    insert into public.orders (
        id, buyer_id, store_id, status, subtotal, shipping_fee, total,
        shipping_distance_km, total_weight_kg,
        shipping_address_snapshot, store_address_snapshot, created_at
    ) values
        (
            'f5f4b51d-409e-47dc-9da2-771ce8df3991', buyer_id, store_id, 'completed',
            2450000, 0, 2450000, 0, 420,
            '{"address":"Alamat contoh lokal"}'::jsonb,
            '{"address":"Toko Demo Lokal"}'::jsonb,
            now() - interval '3 days'
        ),
        (
            'f5f4b51d-409e-47dc-9da2-771ce8df3992', buyer_id, store_id, 'pending',
            850000, 0, 850000, 0, 155,
            '{"address":"Alamat contoh lokal"}'::jsonb,
            '{"address":"Toko Demo Lokal"}'::jsonb,
            now() - interval '1 day'
        )
    on conflict (id) do nothing;
end;
$$;
