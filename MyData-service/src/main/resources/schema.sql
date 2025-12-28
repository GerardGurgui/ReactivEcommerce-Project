CREATE TABLE IF NOT EXISTS carts (
  id SERIAL PRIMARY KEY,
  userUuid varchar(255) DEFAULT NULL,
  name varchar(255) DEFAULT NULL,
  totalProducts int DEFAULT NULL,
  totalPrice double precision DEFAULT NULL,
  status varchar(255) DEFAULT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart_products (
  id SERIAL PRIMARY KEY,
  cart_id int NOT NULL,
  product_id int NOT NULL,
  productName varchar(255) DEFAULT NULL,
  quantity int NOT NULL,
  productPrice double precision NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  FOREIGN KEY (cart_id) REFERENCES carts (id),
  UNIQUE (cart_id, product_id)
);