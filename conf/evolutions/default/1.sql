# --- !Ups
DROP TABLE IF EXISTS recipes;

CREATE TABLE recipes (
  id SERIAL PRIMARY KEY,
  -- name of recipe
  title VARCHAR(100) NOT NULL,
  -- time required to cook/bake the recipe
  making_time VARCHAR(100) NOT NULL,
  -- number of people the recipe will feed
  serves VARCHAR(100) NOT NULL,
  -- food items necessary to prepare the recipe
  ingredients VARCHAR(300) NOT NULL,
  -- price of recipe
  cost INTEGER NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Seed data
INSERT INTO recipes (title, making_time, serves, ingredients, cost, created_at, updated_at)
VALUES
  ('Chicken Curry', '45 min', '4 people', 'onion, chicken, seasoning', 1000, '2016-01-10 12:10:12', '2016-01-10 12:10:12'),
  ('Rice Omelette', '30 min', '2 people', 'onion, egg, seasoning, soy sauce', 700, '2016-01-11 13:10:12', '2016-01-11 13:10:12');

# --- !Downs
DROP TABLE IF EXISTS recipes;