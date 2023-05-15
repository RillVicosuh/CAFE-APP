/*COPY MENU
FROM 'menu.csv'
WITH DELIMITER ';';

COPY USERS
FROM 'users.csv'
WITH DELIMITER ';';

COPY ORDERS
FROM 'orders.csv'
WITH DELIMITER ';';
ALTER SEQUENCE orders_orderid_seq RESTART 87257;

COPY ITEMSTATUS
FROM 'itemStatus.csv'
WITH DELIMITER ';';*/

COPY MENU
FROM '/extra/rvill095/project/data/menu.csv'
WITH DELIMITER ';';

COPY USERS
FROM '/extra/rvill095/project/data/users.csv'
WITH DELIMITER ';';

COPY ORDERS
FROM '/extra/rvill095/project/data/orders.csv'
WITH DELIMITER ';';
ALTER SEQUENCE orders_orderid_seq RESTART 86257;

COPY ITEMSTATUS
FROM '/extra/rvill095/project/data/itemStatus.csv'
WITH DELIMITER ';';
