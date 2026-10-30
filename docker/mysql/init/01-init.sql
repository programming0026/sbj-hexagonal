-- Hexagonal Architecture Database Initialization Script

USE hexagonal_db;

-- Tabla users: debe coincidir con la entidad User (name <= 50, email <= 100 único, phone <= 15)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(15),
    created_at DATETIME(6),
    updated_at DATETIME(6),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Datos de prueba
INSERT INTO users (name, email, phone, created_at, updated_at) VALUES
('Juan Pérez', 'juan.perez@email.com', '+525511111111', NOW(6), NOW(6)),
('María González', 'maria.gonzalez@email.com', '+525522222222', NOW(6), NOW(6)),
('Carlos Rodríguez', 'carlos.rodriguez@email.com', '+525533333333', NOW(6), NOW(6)),
('Ana Martínez', 'ana.martinez@email.com', '+525544444444', NOW(6), NOW(6)),
('Luis García', 'luis.garcia@email.com', '+525555555555', NOW(6), NOW(6)),
('Carmen López', 'carmen.lopez@email.com', NULL, NOW(6), NOW(6)),
('Miguel Hernández', 'miguel.hernandez@email.com', NULL, NOW(6), NOW(6)),
('Laura Sánchez', 'laura.sanchez@email.com', NULL, NOW(6), NOW(6)),
('David Torres', 'david.torres@email.com', NULL, NOW(6), NOW(6)),
('Patricia Ruiz', 'patricia.ruiz@email.com', NULL, NOW(6), NOW(6));

-- Create additional tables for hexagonal architecture demo
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
    category VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_active (active),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert product test data
INSERT INTO products (name, description, price, stock, category) VALUES
('Laptop Dell XPS 13', 'Ultrabook with Intel i7 processor', 1299.99, 15, 'Electronics'),
('iPhone 15 Pro', 'Latest iPhone with A17 Pro chip', 999.99, 25, 'Electronics'),
('Samsung Galaxy S24', 'Android flagship smartphone', 899.99, 20, 'Electronics'),
('MacBook Air M2', 'Apple laptop with M2 chip', 1199.99, 10, 'Electronics'),
('Sony WH-1000XM5', 'Noise canceling headphones', 299.99, 30, 'Audio'),
('iPad Pro 12.9', 'Professional tablet with M2 chip', 1099.99, 12, 'Electronics'),
('Nintendo Switch OLED', 'Gaming console with OLED display', 349.99, 18, 'Gaming'),
('AirPods Pro 2', 'Wireless earbuds with ANC', 249.99, 40, 'Audio'),
('Tesla Model 3 Charger', 'Home charging solution', 599.99, 8, 'Automotive'),
('Mechanical Keyboard', 'RGB gaming keyboard', 159.99, 25, 'Accessories');

-- Create orders table for relationship demonstration
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL CHECK (total_amount >= 0),
    status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivery_address TEXT,
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_order_date (order_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert order test data
INSERT INTO orders (user_id, total_amount, status, delivery_address) VALUES
(1, 1299.99, 'DELIVERED', 'Av. Insurgentes 123, CDMX, México'),
(2, 549.98, 'SHIPPED', 'Calle Reforma 456, Guadalajara, México'),
(3, 999.99, 'CONFIRMED', 'Blvd. Venustiano Carranza 789, Monterrey, México'),
(4, 299.99, 'PENDING', 'Av. Juárez 321, Puebla, México'),
(5, 2199.98, 'DELIVERED', 'Calle Hidalgo 654, Tijuana, México'),
(1, 349.99, 'PENDING', 'Av. Insurgentes 123, CDMX, México'),
(3, 159.99, 'CONFIRMED', 'Blvd. Venustiano Carranza 789, Monterrey, México');

-- Create order_items table
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL CHECK (unit_price >= 0),
    total_price DECIMAL(12,2) GENERATED ALWAYS AS (quantity * unit_price) STORED,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert order items test data
INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES
(1, 1, 1, 1299.99),  -- Dell XPS for order 1
(2, 5, 1, 299.99),   -- Sony headphones
(2, 10, 1, 159.99),  -- Keyboard
(2, 8, 1, 249.99),   -- AirPods (total should be adjusted in orders)
(3, 2, 1, 999.99),   -- iPhone for order 3
(4, 5, 1, 299.99),   -- Sony headphones for order 4
(5, 4, 1, 1199.99),  -- MacBook
(5, 6, 1, 1099.99),  -- iPad (total should be adjusted in orders)
(6, 7, 1, 349.99),   -- Nintendo Switch for order 6
(7, 10, 1, 159.99);  -- Keyboard for order 7

-- Update order totals to match items (fixing the initial test data)
UPDATE orders SET total_amount = 709.97 WHERE id = 2; -- headphones + keyboard + airpods
UPDATE orders SET total_amount = 2299.98 WHERE id = 5; -- macbook + ipad

-- Create indexes for better performance
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_orders_total_amount ON orders(total_amount);

-- Show initialization completion
SELECT 'Database initialized successfully!' as message;
SELECT COUNT(*) as user_count FROM users;
SELECT COUNT(*) as product_count FROM products;
SELECT COUNT(*) as order_count FROM orders;
SELECT COUNT(*) as order_items_count FROM order_items;