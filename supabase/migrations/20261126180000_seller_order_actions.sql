-- =====================================================================
-- Seller order actions: accept / reject (with idempotent stock restoration).
--
-- !! READ BEFORE APPLYING !!
-- The order status values below ('pending', 'accepted', 'rejected') are a PROPOSAL.
-- The shared docs do not list them yet. First check what orders.status allows:
--   select pg_get_constraintdef(oid) from pg_constraint
--   where conrelid = 'public.orders'::regclass and contype = 'c';
-- If the real values differ, edit the three constants in BOTH functions.
-- Never edit this file after it is applied in a shared environment; add a new migration.
-- =====================================================================

create or replace function public.seller_accept_order(p_order_id uuid)
returns void
language plpgsql
security definer
set search_path = ''
as $$
declare
    c_pending  constant text := 'pending';
    c_accepted constant text := 'accepted';
    v_uid   uuid := (select auth.uid());
    v_order public.orders;
begin
    if v_uid is null then
        raise exception 'not_authenticated' using errcode = '28000';
    end if;

    -- Same error for "missing" and "not your store" so existence is not leaked.
    select o.* into v_order
    from public.orders o
    join public.stores s on s.id = o.store_id
    where o.id = p_order_id and s.owner_id = v_uid
    for update of o;

    if not found then
        raise exception 'order_not_found' using errcode = 'P0002';
    end if;

    if v_order.status <> c_pending then
        raise exception 'invalid_transition'
            using errcode = 'P0001', detail = format('%s -> %s', v_order.status, c_accepted);
    end if;

    update public.orders set status = c_accepted, updated_at = now() where id = p_order_id;

    insert into public.order_status_history (id, order_id, changed_by, old_status, new_status, note, created_at)
    values (gen_random_uuid(), p_order_id, v_uid, v_order.status, c_accepted, null, now());

    insert into public.notifications (id, user_id, order_id, title, message, type, is_read, created_at)
    values (gen_random_uuid(), v_order.buyer_id, p_order_id,
            'Status pesanan diperbarui', 'Pesanan Anda diterima oleh penjual.', 'order_status', false, now());
end;
$$;

create or replace function public.seller_reject_order(p_order_id uuid, p_reason text default null)
returns void
language plpgsql
security definer
set search_path = ''
as $$
declare
    c_pending  constant text := 'pending';
    c_rejected constant text := 'rejected';
    v_uid    uuid := (select auth.uid());
    v_order  public.orders;
    v_reason text := nullif(btrim(p_reason), '');
begin
    if v_uid is null then
        raise exception 'not_authenticated' using errcode = '28000';
    end if;

    select o.* into v_order
    from public.orders o
    join public.stores s on s.id = o.store_id
    where o.id = p_order_id and s.owner_id = v_uid
    for update of o;

    if not found then
        raise exception 'order_not_found' using errcode = 'P0002';
    end if;

    if v_order.status <> c_pending then
        raise exception 'invalid_transition'
            using errcode = 'P0001', detail = format('%s -> %s', v_order.status, c_rejected);
    end if;

    -- Idempotent stock restoration: only once, guarded by stock_restored_at.
    if v_order.stock_restored_at is null then
        perform 1
        from public.products p
        where p.id in (
            select oi.product_id from public.order_items oi
            where oi.order_id = p_order_id and oi.product_id is not null
        )
        order by p.id
        for update;

        update public.products p
        set stock = p.stock + q.quantity
        from (
            select oi.product_id, sum(oi.quantity)::integer as quantity
            from public.order_items oi
            where oi.order_id = p_order_id and oi.product_id is not null
            group by oi.product_id
        ) q
        where p.id = q.product_id;
    end if;

    update public.orders
    set status = c_rejected,
        stock_restored_at = coalesce(v_order.stock_restored_at, now()),
        updated_at = now()
    where id = p_order_id;

    insert into public.order_status_history (id, order_id, changed_by, old_status, new_status, note, created_at)
    values (gen_random_uuid(), p_order_id, v_uid, v_order.status, c_rejected, v_reason, now());

    insert into public.notifications (id, user_id, order_id, title, message, type, is_read, created_at)
    values (gen_random_uuid(), v_order.buyer_id, p_order_id,
            'Status pesanan diperbarui',
            'Pesanan Anda ditolak oleh penjual.' || coalesce(' Alasan: ' || v_reason, ''),
            'order_status', false, now());
end;
$$;

revoke all on function public.seller_accept_order(uuid) from public, anon;
revoke all on function public.seller_reject_order(uuid, text) from public, anon;
grant execute on function public.seller_accept_order(uuid) to authenticated;
grant execute on function public.seller_reject_order(uuid, text) to authenticated;
