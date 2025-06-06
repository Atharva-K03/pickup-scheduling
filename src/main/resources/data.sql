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
    worker1id,
    worker2id,
    status
)
VALUES (
    'P001',
    'Z001',
    '2023-10-01 08:00:00',
    '2023-10-01 10:00:00',
    'DAILY',
    'Central Park',
    'PT001',
    'W001',
    'W002',
    'IN_PROGRESS'
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
    worker1id,
    worker2id,
    status
)
VALUES (
    'P002',
    'Z002',
    '2023-10-02 14:00:00',
    '2023-10-02 16:00:00',
    'WEEKLY',
    'Riverside Blvd',
    'vehicle_002',
    'W003',
    'W004',
    'NOT_STARTED'
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
    worker1id,
    worker2id,
    status
)

--
VALUES (
    'P003',
    'Z003',
    '2023-10-05 10:00:00',
    '2023-10-05 12:00:00',
    'MONTHLY',
    'Elm Street',
    'PT003',
    'W005',
    'W006',
    'COMPLETED'
);