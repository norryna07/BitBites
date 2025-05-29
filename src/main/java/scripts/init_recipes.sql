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