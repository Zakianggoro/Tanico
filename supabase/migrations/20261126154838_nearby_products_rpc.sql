create function public.get_nearby_active_products(
    p_latitude double precision,
    p_longitude double precision,
    p_radius_km double precision default 25
)
returns table (
    id uuid,
    category_id uuid,
    name text,
    description text,
    price numeric,
    stock integer,
    unit text,
    weight_per_unit_kg numeric,
    distance_km double precision
)
language plpgsql
stable
security definer
set search_path = ''
as $$
begin
    if auth.uid() is null
        or not exists (
            select 1
            from public.profiles as buyer_profile
            where buyer_profile.id = auth.uid()
                and buyer_profile.role = 'buyer'
        )
    then
        raise exception 'Only authenticated buyers can view nearby products'
            using errcode = '42501';
    end if;

    if p_latitude is null
        or p_latitude < -90
        or p_latitude > 90
        or p_longitude is null
        or p_longitude < -180
        or p_longitude > 180
        or p_radius_km is null
        or p_radius_km <= 0
        or p_radius_km > 100
    then
        raise exception 'Invalid coordinates or search radius'
            using errcode = '22023';
    end if;

    return query
    with products_with_distance as (
        select
            product.id,
            product.category_id,
            product.name,
            product.description,
            product.price,
            product.stock,
            product.unit,
            product.weight_per_unit_kg,
            6371::double precision * 2::double precision * asin(
                sqrt(
                    least(
                        1::double precision,
                        greatest(
                            0::double precision,
                            power(
                                sin(radians(store.latitude::double precision - p_latitude) / 2.0),
                                2
                            )
                            + cos(radians(p_latitude))
                            * cos(radians(store.latitude::double precision))
                            * power(
                                sin(radians(store.longitude::double precision - p_longitude) / 2.0),
                                2
                            )
                        )
                    )
                )
            ) as distance_km
        from public.products as product
        join public.stores as store on store.id = product.store_id
        where product.status = 'active'
    )
    select
        nearby.id,
        nearby.category_id,
        nearby.name,
        nearby.description,
        nearby.price,
        nearby.stock,
        nearby.unit,
        nearby.weight_per_unit_kg,
        nearby.distance_km
    from products_with_distance as nearby
    where nearby.distance_km <= p_radius_km
    order by nearby.distance_km, nearby.id;
end;
$$;

revoke all on function public.get_nearby_active_products(double precision, double precision, double precision)
    from public, anon;
grant execute on function public.get_nearby_active_products(double precision, double precision, double precision)
    to authenticated;
