CREATE TABLE IF NOT EXISTS products (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(255) DEFAULT NULL,
  description varchar(255) DEFAULT NULL,
  price double DEFAULT NULL,
  category varchar(255) DEFAULT NULL,
  image varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ;

CREATE TABLE IF NOT EXISTS carts (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  userUuid varchar(255) DEFAULT NULL,
  name varchar(255) DEFAULT NULL,
  quantity int(11) DEFAULT NULL,
  PRIMARY KEY (id)
) ;

CREATE TABLE IF NOT EXISTS carts_products (
  cart_id bigint(20) NOT NULL,
  product_id bigint(20) NOT NULL,
  PRIMARY KEY (cart_id,product_id),
  FOREIGN KEY (cart_id) REFERENCES carts (id),
  FOREIGN KEY (product_id) REFERENCES products (id)
) ;