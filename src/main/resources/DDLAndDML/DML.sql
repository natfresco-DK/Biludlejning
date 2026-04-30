USE biludlejning;

INSERT INTO users (first_name, last_name, username, password, email, phone, role, active)
VALUES ('Simon', 'Admin', 'sadmin', 'QoJ+o69Avow5/WwH8Hz1UIjRb8DfiwdVK10MHKT0v78=', 'admin@biludlejning.dk', '40112233', 'ADMIN', TRUE),
       ('Maria', 'Jensen', 'mjensen', 'QoJ+o69Avow5/WwH8Hz1UIjRb8DfiwdVK10MHKT0v78=', 'maria@biludlejning.dk', '22113344', 'DATAREGISTRERING', TRUE),
       ('Thomas', 'Hansen', 'thansen', 'QoJ+o69Avow5/WwH8Hz1UIjRb8DfiwdVK10MHKT0v78=', 'thomas@biludlejning.dk', '33114455', 'SKADE/UDBEDRING', TRUE),
       ('Simon', 'Fresco', 'sfresco', 'QoJ+o69Avow5/WwH8Hz1UIjRb8DfiwdVK10MHKT0v78=', 'simon@biludlejning.dk', '66666666', 'FORETNINGSUDVIKLING', TRUE),
       ('test', 'test', 'test', 'QoJ+o69Avow5/WwH8Hz1UIjRb8DfiwdVK10MHKT0v78=', 'test@test.dk', '12345678', 'DATAREGISTRERING', TRUE);

/*
 all passwords: password_placeholder
 */;


INSERT INTO customers (first_name, last_name, email, phone, licence_no, street_address, zip_code, city)
VALUES ('Anders', 'Nielsen', 'anders@email.dk', '50112233', 'DK12345678', 'Nørrebrogade 10', 2200, 'København'),
       ('Louise', 'Madsen', 'louise@email.dk', '60113344', 'DK87654321', 'Vestergade 5', 8000, 'Aarhus'),
       ('Mikkel', 'Pedersen', 'mikkel@email.dk', '70114455', 'DK45671234', 'Algade 22', 9000, 'Aalborg'),
       ('Emma', 'Christensen', 'emma@email.dk', '80115566', 'DK99887766', 'Stationsvej 8', 5000, 'Odense');

INSERT INTO cars (reg_nr, vin, brand, model, location, odometer, car_description, status, active)
VALUES ('AB12345', 'VIN00000000000001', 'Toyota', 'Yaris', 'København', 45000, 'Compact city car', 'RENTED', TRUE),
       ('CD67890', 'VIN00000000000002', 'Volkswagen', 'Golf', 'Aarhus', 72000, 'Popular family hatchback', 'RENTED', TRUE),
       ('EF11223', 'VIN00000000000003', 'Tesla', 'Model 3', 'København', 28000, 'Electric sedan', 'RENTED', TRUE),
       ('GH44556', 'VIN00000000000004', 'Ford', 'Focus', 'Odense', 91000, 'Reliable station car', 'MAINTENANCE', TRUE),
       ('IJ77889', 'VIN00000000000005', 'BMW', '320i', 'Aalborg', 65000, 'Luxury sedan', 'AVAILABLE', TRUE),
       ('IJ77890', 'VIN00000000000006', 'BMW-1', '320i', 'Aalborg-Øst', 650300, 'Luxury sedan3', 'RETURNED', TRUE),
       ('IJ77830', 'VIN00000000000007', 'BWM', '3203i', 'Aalborg-S', 65030, 'Luxury sedan3', 'RETURNED', TRUE);

INSERT INTO rental_agreements
(start_date, end_date, downpayment, monthly_payment, max_km, created_by, car_id, customer_id)
VALUES ('2026-01-01', '2026-06-30', 5000.00, 3200.00, 15000, 1, 2, 1),
       ('2026-02-15', '2026-08-15', 7000.00, 4500.00, 18000, 2, 3, 2),
       ('2026-03-01', '2026-09-01', 4000.00, 2900.00, 12000, 3, 1, 3),
       ('2026-04-01', '2026-10-01', 5000.00, 3200.00, 15000, 1, 6, 1),
       ('2026-01-01', '2026-06-30', 5000.00, 3200.00, 15000, 1, 7, 1);

INSERT INTO damage_reports
    (return_date, report_date, cost, odometer, agreement_id, registered_by)
VALUES ('2026-06-30', '2026-07-01', 2500.00, 86500, 1, 2),
       ('2026-08-15', '2026-08-16', 1200.00, 39000, 2, 3);

INSERT INTO damage_items (damage_report_id, description, price)
VALUES (1, 'Scratch on rear bumper', 1500.00),
       (1, 'Broken side mirror', 1000.00),
       (2, 'Stone chip in windshield', 1200.00);
