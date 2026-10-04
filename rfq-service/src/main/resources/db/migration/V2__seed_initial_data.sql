-- Flyway Migration V2: Initial Seed Data for RFQ to Quote Automation POC

-- Seed Customers
INSERT INTO customer (id, name, email, company_code) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'ABC Manufacturing Inc.', 'procurement@abc-mfg.com', 'CUST-ABC-001'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'Apex Industrial Solutions', 'rfq@apex-industrial.com', 'CUST-APX-002');

-- Seed Products
INSERT INTO product (id, sku, name, description, unit, category) VALUES
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380001', 'IND-SENS-100', 'Industrial Vibration Sensor', 'High precision tri-axial vibration sensor for heavy machinery monitoring', 'EA', 'Sensors'),
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380002', 'CTRL-MOD-020', 'Programmable Logic Control Module', '24V DC Digital I/O extension module for industrial automation', 'EA', 'Controllers'),
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380003', 'PWR-SUPP-050', '24V 10A Industrial Power Supply', 'DIN-rail mount 240W regulated power supply', 'EA', 'Power Systems');

-- Seed Standard Price List
INSERT INTO price_list (id, name, currency, effective_from, effective_to) VALUES
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380100', 'Standard B2B Catalog Price List 2026', 'USD', '2026-01-01', '2026-12-31');

-- Seed Price List Items
INSERT INTO price_list_item (id, price_list_id, product_id, unit_price) VALUES
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380101', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380100', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380001', 150.0000), -- Industrial Sensor: $150.00
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380102', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380100', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380002', 450.0000), -- Control Module: $450.00
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380103', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380100', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380003', 120.0000); -- Power Supply: $120.00
