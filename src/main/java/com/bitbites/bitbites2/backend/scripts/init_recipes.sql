create type food_type as enum (
    'Appetizer',
    'Bread',
    'Dessert',
    'Drink',
    'MainCourse',
    'Salad',
    'Soup'
    );

create type plans_type as enum (
    'daily',
    'family',
    'self',
    'weekly'
    );

create table recipes (
    id SERIAL primary key,
    name varchar(255) not null,
    category_food food_type not null,
    kitchen_type varchar(255),
    kilocalories double precision,
    servings integer,
    instructions text,
    duration interval
);

create table meal_plans (
    id SERIAL primary key,
    type plans_type not null,
    members integer,
    user_id integer references users(id)
);


create table daily_schedules (
    id SERIAL PRIMARY KEY,
    meal_plan_id INTEGER REFERENCES meal_plans(id) ON DELETE CASCADE,
    breakfast_index INTEGER references recipes(id) not null,
    lunch_soup_index INTEGER references recipes(id) NOT NULL,
    lunch_main_course_index INTEGER references recipes(id) NOT NULL,
    dinner_main_course_index INTEGER references recipes(id) NOT NULL,
    dinner_dessert_index INTEGER references recipes(id) not null
);

create table self_plan_recipes (
    meal_plan_id INTEGER REFERENCES meal_plans(id) ON DELETE CASCADE,
    recipe_id INTEGER REFERENCES recipes(id) ON DELETE CASCADE,
    PRIMARY KEY (meal_plan_id, recipe_id)
);

-- Delete from child tables first to avoid FK constraint errors
DELETE FROM self_plan_recipes;
DELETE FROM daily_schedules;
DELETE FROM grocery_items;
DELETE FROM grocery_lists;
DELETE FROM meal_plans;
DELETE FROM recipes;
DELETE FROM ingredients;
DELETE FROM users;
-- Optional: Reset sequence counters
ALTER SEQUENCE grocery_items_id_seq RESTART WITH 1;
ALTER SEQUENCE grocery_lists_id_seq RESTART WITH 1;
ALTER SEQUENCE meal_plans_id_seq RESTART WITH 1;
ALTER SEQUENCE recipes_id_seq RESTART WITH 1;
ALTER SEQUENCE daily_schedules_id_seq RESTART WITH 1;
