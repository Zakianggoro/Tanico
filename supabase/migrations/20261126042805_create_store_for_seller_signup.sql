create or replace function public.create_profile_for_new_auth_user()
returns trigger
language plpgsql
security definer
set search_path = ''
as $$
declare
    selected_role text := new.raw_user_meta_data ->> 'role';
    selected_name text := new.raw_user_meta_data ->> 'full_name';
    selected_store_name text := new.raw_user_meta_data ->> 'store_name';
    selected_store_address text := new.raw_user_meta_data ->> 'store_address';
    selected_latitude_text text := new.raw_user_meta_data ->> 'store_latitude';
    selected_longitude_text text := new.raw_user_meta_data ->> 'store_longitude';
    selected_latitude numeric;
    selected_longitude numeric;
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

    if selected_role = 'seller' then
        if nullif(trim(selected_store_name), '') is null
            or nullif(trim(selected_store_address), '') is null then
            raise exception 'Seller store name and address are required';
        end if;

        if selected_latitude_text is null
            or selected_latitude_text !~ '^-?[0-9]+([.][0-9]+)?$'
            or selected_longitude_text is null
            or selected_longitude_text !~ '^-?[0-9]+([.][0-9]+)?$' then
            raise exception 'Valid seller store coordinates are required';
        end if;

        selected_latitude := selected_latitude_text::numeric;
        selected_longitude := selected_longitude_text::numeric;

        if selected_latitude not between -90 and 90
            or selected_longitude not between -180 and 180 then
            raise exception 'Seller store coordinates are outside valid ranges';
        end if;

        insert into public.stores (
            owner_id,
            store_name,
            address,
            latitude,
            longitude
        )
        values (
            new.id,
            trim(selected_store_name),
            trim(selected_store_address),
            selected_latitude,
            selected_longitude
        );
    end if;

    return new;
end;
$$;
