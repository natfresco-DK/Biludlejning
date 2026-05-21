INSERT INTO "users" (first_name, last_name, username, password, email, phone, role, active)
VALUES ('Simon', 'Admin', 'sadmin', 'QoJ+o69Avow5/WwH8Hz1UIjRb8DfiwdVK10MHKT0v78=', 'admin@biludlejning.dk', '40112233',
        'ADMIN', TRUE),
       ('Maria', 'Jensen', 'mjensen', 'QoJ+o69Avow5/WwH8Hz1UIjRb8DfiwdVK10MHKT0v78=', 'maria@biludlejning.dk',
        '22113344', 'DATAREGISTRERING', TRUE),
       ('Thomas', 'Hansen', 'thansen', 'QoJ+o69Avow5/WwH8Hz1UIjRb8DfiwdVK10MHKT0v78=', 'thomas@biludlejning.dk',
        '33114455', 'SKADE/UDBEDRING', TRUE);

INSERT INTO "customers" (first_name, last_name, email, phone, licence_no, street_address, zip_code, city)
VALUES ('Anders', 'Nielsen', 'anders@email.dk', '50112233', 'DK12345678', 'Nørrebrogade 10', 2200, 'København'),
       ('Louise', 'Madsen', 'louise@email.dk', '60113344', 'DK87654321', 'Vestergade 5', 8000, 'Aarhus');

INSERT INTO "cars" (reg_nr, vin, brand, model, location, odometer, car_description, status, active)
VALUES ('AB12345', 'VIN00000000000001', 'Toyota', 'Yaris', 'København', 45000, 'Compact city car', 'AVAILABLE', TRUE),
       ('CD67890', 'VIN00000000000002', 'Volkswagen', 'Golf', 'Aarhus', 72000, 'Popular family hatchback', 'AVAILABLE',
        TRUE),
       ('EF11223', 'VIN00000000000003', 'Tesla', 'Model 3', 'København', 28000, 'Electric sedan', 'RENTED', TRUE);

INSERT INTO "rental_agreements"
(start_date, end_date, downpayment, monthly_payment, max_km, created_by, car_id, customer_id, active)
VALUES ('2026-05-01', '2026-06-01', 5000.00, 3200.00, 15000, 2, 3, 1, TRUE);