-- =====================================================================
-- DEV ONLY. Run in the Supabase SQL Editor. Never run on production.
-- 1) Categories (the product form needs at least one).
-- 2) One sample pending order, so the seller "Pesanan" tab has something to show.
--    Needs: 1 seller store, 1 buyer profile, 1 product in that store.
--    Adjust column names/values if your orders/order_items schema differs.
-- =====================================================================

insert into public.categories (name, description) values
    ('Jerami', 'Sisa batang padi'),
    ('Sekam', 'Kulit gabah hasil penggilingan'),
    ('Batang jagung', 'Sisa panen jagung'),
    ('Kulit kopi', 'Limbah pengolahan kopi')
on conflict (name) do nothing;

do $$
declare
    v_store   public.stores;
    v_buyer   uuid;
    v_product public.products;
    v_order   uuid := gen_random_uuid();
begin
    select * into v_store from public.stores order by created_at limit 1;
    select id into v_buyer from public.profiles where role = 'buyer' limit 1;
    select * into v_product from public.products where store_id = v_store.id limit 1;

    if v_store.id is null or v_buyer is null or v_product.id is null then
        raise notice 'Seed pesanan dilewati: butuh 1 toko, 1 pembeli, dan 1 produk.';
        return;
    end if;

    insert into public.orders (
        id, buyer_id, store_id, status, subtotal, shipping_fee, total,
        shipping_distance_km, total_weight_kg, shipping_address_snapshot, store_address_snapshot
    ) values (
        v_order, v_buyer, v_store.id, 'pending',
        v_product.price * 2, 10000, v_product.price * 2 + 10000,
        5, v_product.weight_per_unit_kg * 2,
        '{"recipient_name":"Pembeli Uji","address":"Alamat uji"}'::jsonb,
        jsonb_build_object('address', v_store.address)
    );

    insert into public.order_items (
        id, order_id, product_id, product_name, unit_price, quantity, unit, weight_per_unit_kg, subtotal
    ) values (
        gen_random_uuid(), v_order, v_product.id, v_product.name, v_product.price, 2,
        v_product.unit, v_product.weight_per_unit_kg, v_product.price * 2
    );

    update public.products set stock = greatest(stock - 2, 0) where id = v_product.id;
end $$;
