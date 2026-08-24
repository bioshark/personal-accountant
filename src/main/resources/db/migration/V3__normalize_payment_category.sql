-- payment_entity.category was still a native ENUM on databases created before the VARCHAR
-- drift fix (e.g. the 1.1.6 production database). V2 only normalised pending_payment_entity and
-- recurring_payment_template; bring payment_entity.category in line here so the String-typed
-- entity mapping validates and its stored values match the `category` lookup table.
--
-- On databases where the column is already VARCHAR (dev, or a fresh V1 schema) the ALTER is a
-- harmless no-op and the UPDATE is idempotent.
alter table payment_entity
    alter column category set data type varchar(255);

update payment_entity
set category = concat(upper(substring(replace(category, '_', ' '), 1, 1)),
                      lower(substring(replace(category, '_', ' '), 2)));
