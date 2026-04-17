CREATE DATABASE retail_store;
USE retail_store;

CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT, 
    name VARCHAR(100) NOT NULL, city VARCHAR(50), email VARCHAR(100),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL, category VARCHAR(50), price DECIMAL(10, 2), stock_quantity INT DEFAULT 0
);


CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL, order_date DATE, total_amount DECIMAL(10, 2),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE order_items (
    item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL, product_id INT NOT NULL, quantity INT, unit_price DECIMAL(10, 2),
    FOREIGN KEY (order_id) REFERENCES orders(order_id), FOREIGN KEY (product_id) REFERENCES products(product_id)
);



INSERT INTO customers (name, city, email) VALUES
('Hency Mariam', 'Trichy', 'hency@gmail.com'),
('Jona', 'Chennai', 'Jona@email.com'),
('Lenida', 'Bangalore', 'lenida@email.com'),
('Sruthi', 'Coimbatore', 'sruthi@email.com'),
('Siva', 'Hyderabad', 'siva@email.com');

INSERT INTO products (name, category, price, stock_quantity) VALUES
('Laptop', 'Electronics', 45000, 25),
('Smartphone', 'Electronics', 25000, 50),
('Headphones', 'Electronics', 5000, 100),
('Running Shoes', 'Sports', 3500, 80),
('Yoga Mat', 'Sports', 800, 150),
('Fiction Novel', 'Books', 450, 200),
('Coffee Maker', 'Home & Kitchen', 4500, 40);


INSERT INTO orders (customer_id, order_date, total_amount) VALUES
(1, '2024-01-15', 28500),
(2, '2024-01-20', 75000),
(3, '2024-02-10', 15000),
(1, '2024-02-18', 9500),
(4, '2024-03-05', 3500),
(5, '2024-03-15', 52000),
(2, '2024-04-01', 6800),
(1, '2024-04-12', 27000),
(3, '2024-05-05', 2700),
(1, '2024-09-22', 6200),
(3, '2024-10-10', 25000),
(2, '2024-10-20', 3000);

INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES
(1, 2, 1, 25000),
(1, 3, 1, 5000),
(2, 1, 1, 45000),
(2, 7, 3, 4500),
(3, 6, 2, 450),
(4, 4, 1, 3500),
(5, 5, 4, 800),
(6, 1, 1, 45000),
(6, 7, 1, 4500),
(7, 2, 2, 25000),
(8, 2, 1, 25000),
(9, 5, 3, 800),
(10, 1, 1, 45000),
(10, 3, 1, 5000),
(11, 7, 3, 4500),
(12, 3, 4, 5000),
(13, 1, 1, 45000),
(13, 4, 1, 3500),
(14, 6, 2, 450),
(15, 6, 5, 450),
(16, 2, 1, 25000),
(16, 5, 4, 800),
(17, 2, 2, 25000),
(18, 3, 1, 5000),
(19, 1, 1, 45000),
(20, 3, 5, 5000);



SELECT 
    p.product_id, p.name, p.category,
    SUM(o.quantity) as total_quantity_sold,
    SUM(o.quantity * o.unit_price) as total_revenue,
    RANK() OVER (ORDER BY SUM(o.quantity) DESC) as sales_rank
FROM products p
JOIN order_items o ON p.product_id = o.product_id
GROUP BY p.product_id, p.name, p.category
ORDER BY total_quantity_sold DESC;

SELECT 
    c.customer_id, c.name, c.city,
    COUNT(o.order_id) as total_orders,
    SUM(o.total_amount) as total_spent,
    RANK() OVER (ORDER BY SUM(o.total_amount) DESC) as customer_rank
FROM customers c
LEFT JOIN orders o ON c.customer_id = o.customer_id
GROUP BY c.customer_id, c.name, c.city
ORDER BY total_spent DESC;

SELECT 
    YEAR(o.order_date) as year,
    MONTH(o.order_date) as month,
    DATE_FORMAT(o.order_date, '%Y-%m-01') as month_date,
    COUNT(o.order_id) as total_orders,
    SUM(o.total_amount) as monthly_revenue,
    ROUND(AVG(o.total_amount), 2) as avg_order_value
FROM orders o
GROUP BY YEAR(o.order_date), MONTH(o.order_date)
ORDER BY year DESC, month DESC;

SELECT 
    p.category,
    COUNT(DISTINCT oi.order_id) as order_count,
    SUM(oi.quantity) as total_items_sold,
    SUM(oi.quantity * oi.unit_price) as category_revenue,
    ROUND(100 * SUM(oi.quantity * oi.unit_price) / 
        (SELECT SUM(oi2.quantity * oi2.unit_price) FROM order_items oi2), 2) as percentage_of_total
FROM products p
JOIN order_items oi ON p.product_id = oi.product_id
GROUP BY p.category
ORDER BY category_revenue DESC;


SELECT 
    c.customer_id, c.name, c.city, c.registration_date,
    MAX(o.order_date) as last_order_date,
    DATEDIFF(CURDATE(), MAX(o.order_date)) as days_since_last_order,
    COUNT(o.order_id) as total_orders,
    SUM(o.total_amount) as lifetime_value
FROM customers c
LEFT JOIN orders o ON c.customer_id = o.customer_id
GROUP BY c.customer_id, c.name, c.city, c.registration_date
HAVING MAX(o.order_date) IS NULL OR DATEDIFF(CURDATE(), MAX(o.order_date)) > 180
ORDER BY days_since_last_order DESC;

SELECT 
    c.customer_id, c.name, c.city,
    COUNT(DISTINCT oi.product_id) as product_categories_purchased,
    SUM(oi.quantity) as total_items,
    SUM(oi.quantity * oi.unit_price) as total_spent,
    ROUND(AVG(oi.quantity * oi.unit_price), 2) as avg_item_value
FROM customers c
JOIN orders o ON c.customer_id = o.customer_id
JOIN order_items oi ON o.order_id = oi.order_id
GROUP BY c.customer_id, c.name, c.city
HAVING COUNT(DISTINCT oi.product_id) > 2
ORDER BY total_spent DESC;

SELECT 
    p.product_id, p.name, p.category, p.price, p.stock_quantity,
    COALESCE(SUM(oi.quantity), 0) as total_sold,
    CASE 
        WHEN COALESCE(SUM(oi.quantity), 0) >= 10 THEN 'High Demand'
        WHEN COALESCE(SUM(oi.quantity), 0) >= 5 THEN 'Moderate Demand'
        ELSE 'Low Demand'
    END as demand_level
FROM products p
LEFT JOIN order_items oi ON p.product_id = oi.product_id
GROUP BY p.product_id, p.name, p.category, p.price, p.stock_quantity
ORDER BY demand_level, total_sold DESC;

SELECT 
    MONTH(o.order_date) as month,
    YEAR(o.order_date) as year,
    p.category,
    SUM(oi.quantity * oi.unit_price) as revenue
FROM orders o
JOIN order_items oi ON o.order_id = oi.order_id
JOIN products p ON oi.product_id = p.product_id
GROUP BY MONTH(o.order_date), YEAR(o.order_date), p.category
ORDER BY year DESC, month DESC;

SELECT 
    c.customer_id, c.name, c.city,
    COUNT(DISTINCT DATE(o.order_date)) as purchase_days,
    COUNT(DISTINCT MONTH(o.order_date)) as purchase_months,
    SUM(o.total_amount) as total_revenue,
    ROUND(SUM(o.total_amount) / COUNT(DISTINCT MONTH(o.order_date)), 2) as avg_monthly_value
FROM customers c
JOIN orders o ON c.customer_id = o.customer_id
GROUP BY c.customer_id, c.name, c.city
ORDER BY total_revenue DESC
LIMIT 10;
