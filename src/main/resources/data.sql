-- Sample data for "pickups" table

-- Pickup 1
INSERT INTO pickups (
    id,
    zone_id,
    time_slot_start,
    time_slot_end,
    frequency,
    location_name,
    vehicle_id,
    worker1_id,
    worker2_id,
    status
)
VALUES (
    'pickup_001',
    'zone_001',
    '2023-10-01 08:00:00',
    '2023-10-01 10:00:00',
    'DAILY',
    'Central Park',
    'vehicle_001',
    'worker_001',
    'worker_002',
    'PENDING'
);

-- Pickup 2
INSERT INTO pickups (
    id,
    zone_id,
    time_slot_start,
    time_slot_end,
    frequency,
    location_name,
    vehicle_id,
    worker1_id,
    worker2_id,
    status
)
VALUES (
    'pickup_002',
    'zone_002',
    '2023-10-02 14:00:00',
    '2023-10-02 16:00:00',
    'WEEKLY',
    'Riverside Blvd',
    'vehicle_002',
    'worker_003',
    'worker_004',
    'COMPLETED'
);

-- Pickup 3
INSERT INTO pickups (
    id,
    zone_id,
    time_slot_start,
    time_slot_end,
    frequency,
    location_name,
    vehicle_id,
    worker1_id,
    worker2_id,
    status
)

--
VALUES (
    'pickup_003',
    'zone_003',
    '2023-10-05 10:00:00',
    '2023-10-05 12:00:00',
    'MONTHLY',
    'Elm Street',
    'vehicle_003',
    'worker_005',
    'worker_006',
    'PENDING'
);