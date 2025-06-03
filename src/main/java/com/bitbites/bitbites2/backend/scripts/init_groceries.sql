create type unit_type as enum (
    'gram',
    'kilogram',
    'liter',
    'milliliter',
    'spoon',
    'cup',
    'piece'
    );

create table ingredients (
    name varchar(100) primary key,
    category varchar(100) not null
);

create table grocery_items (
    id SERIAL primary key ,
    ingredient varchar(100) references ingredients(name) not null,
    quantity double precision not null,
    unit unit_type not null,
    grocery_list_id integer references grocery_lists(id)
);

create table grocery_lists (
    id SERIAL primary key,
    recipe_id integer references recipes(id),
    mealplan_id integer references meal_plans(id)
);


