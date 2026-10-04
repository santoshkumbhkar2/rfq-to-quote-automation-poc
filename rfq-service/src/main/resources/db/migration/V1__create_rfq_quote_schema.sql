-- Flyway Migration V1: Core Database Schema for RFQ to Quote Automation POC

-- Enable UUID generation extension if available
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Table: customer
CREATE TABLE customer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    company_code VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: product
CREATE TABLE product (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sku VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    unit VARCHAR(50) NOT NULL DEFAULT 'EA',
    category VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: price_list
CREATE TABLE price_list (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    effective_from DATE NOT NULL,
    effective_to DATE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: price_list_item
CREATE TABLE price_list_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    price_list_id UUID NOT NULL REFERENCES price_list(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    unit_price NUMERIC(19, 4) NOT NULL CHECK (unit_price >= 0),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_price_list_product UNIQUE (price_list_id, product_id)
);

-- Table: rfq
CREATE TABLE rfq (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rfq_number VARCHAR(100) NOT NULL UNIQUE,
    customer_id UUID NOT NULL REFERENCES customer(id),
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: rfq_item
CREATE TABLE rfq_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    rfq_id UUID NOT NULL REFERENCES rfq(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES product(id),
    quantity INT NOT NULL CHECK (quantity > 0),
    requested_specs TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: quote
CREATE TABLE quote (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quote_number VARCHAR(100) NOT NULL UNIQUE,
    rfq_id UUID NOT NULL REFERENCES rfq(id),
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    subtotal NUMERIC(19, 4) NOT NULL DEFAULT 0.0000,
    tax_rate NUMERIC(5, 4) NOT NULL DEFAULT 0.1800,
    tax_amount NUMERIC(19, 4) NOT NULL DEFAULT 0.0000,
    total_amount NUMERIC(19, 4) NOT NULL DEFAULT 0.0000,
    valid_until TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: quote_item
CREATE TABLE quote_item (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quote_id UUID NOT NULL REFERENCES quote(id) ON DELETE CASCADE,
    rfq_item_id UUID REFERENCES rfq_item(id),
    product_id UUID NOT NULL REFERENCES product(id),
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(19, 4) NOT NULL CHECK (unit_price >= 0),
    total_price NUMERIC(19, 4) NOT NULL CHECK (total_price >= 0),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Table: workflow_event
CREATE TABLE workflow_event (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for Query Optimization
CREATE INDEX idx_rfq_customer ON rfq(customer_id);
CREATE INDEX idx_rfq_status ON rfq(status);
CREATE INDEX idx_rfq_item_rfq ON rfq_item(rfq_id);
CREATE INDEX idx_price_list_item_lookup ON price_list_item(price_list_id, product_id);
CREATE INDEX idx_quote_rfq ON quote(rfq_id);
CREATE INDEX idx_quote_item_quote ON quote_item(quote_id);
CREATE INDEX idx_workflow_event_entity ON workflow_event(entity_type, entity_id);
