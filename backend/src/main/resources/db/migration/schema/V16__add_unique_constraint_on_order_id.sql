ALTER TABLE deliveries
    ADD CONSTRAINT uk_deliveries_order_id UNIQUE (order_id);
