-- Table schema for "pickups"
CREATE TABLE pickups (
    id VARCHAR(255) PRIMARY KEY, -- Unique identifier for the pickup
    zone_id VARCHAR(255) NOT NULL, -- Zone ID identifying the area of the pickup
    time_slot_start TIMESTAMP NOT NULL, -- Beginning of the time slot for the pickup
    time_slot_end TIMESTAMP NOT NULL, -- End of the time slot for the pickup
    frequency VARCHAR(50) NOT NULL, -- Frequency of the pickup (e.g., DAILY, WEEKLY, etc.)
    location_name VARCHAR(255) NOT NULL, -- Name of the location for the pickup
    vehicle_id VARCHAR(255), -- ID of the assigned vehicle
    worker1id VARCHAR(255), -- ID of the first assigned worker
    worker2id VARCHAR(255), -- ID of the second assigned worker
    status VARCHAR(50) NOT NULL -- Status of the pickup (e.g., PENDING, COMPLETED, etc.)
);
--