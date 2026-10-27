alter table monthly_expense_entity
    add column if not exists month_done boolean default false not null;
