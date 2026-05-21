DELETE FROM rental_agreements;
DELETE FROM users;
DELETE FROM customers;
DELETE FROM cars;
DELETE FROM damage_reports;
DELETE FROM damage_items;

CREATE TABLE IF NOT EXISTS users
(
    user_id    INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(100),
    phone      VARCHAR(30),
    role       VARCHAR(50)  NOT NULL,
    active     BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS customers
(
    customer_id    INT AUTO_INCREMENT PRIMARY KEY,
    first_name     VARCHAR(50),
    last_name      VARCHAR(50),
    email          VARCHAR(100),
    phone          VARCHAR(30),
    licence_no     VARCHAR(50),
    street_address VARCHAR(150),
    zip_code       INT,
    city           VARCHAR(100),
    active         BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS cars
(
    car_id          INT AUTO_INCREMENT PRIMARY KEY,
    reg_nr          VARCHAR(20) UNIQUE,
    vin             VARCHAR(50) NOT NULL UNIQUE,
    brand           VARCHAR(50) NOT NULL,
    model           VARCHAR(50) NOT NULL,
    location        VARCHAR(100),
    odometer        INT         NOT NULL DEFAULT 0,
    car_description VARCHAR(255),
    status          VARCHAR(30) NOT NULL,
    active          BOOLEAN              DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS rental_agreements
(
    agreement_id    INT AUTO_INCREMENT PRIMARY KEY,
    start_date      DATE           NOT NULL,
    end_date        DATE           NOT NULL,
    downpayment     DECIMAL(10, 2) NOT NULL,
    monthly_payment DECIMAL(10, 2) NOT NULL,
    max_km          INT            NOT NULL,
    created_by      INT            NOT NULL,
    car_id          INT            NOT NULL,
    customer_id     INT            NOT NULL,
    active          BOOLEAN        NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_agreement_user
        FOREIGN KEY (created_by)
            REFERENCES users (user_id)
            ON DELETE RESTRICT,

    CONSTRAINT fk_agreement_car
        FOREIGN KEY (car_id)
            REFERENCES cars (car_id)
            ON DELETE RESTRICT,

    CONSTRAINT fk_agreement_customer
        FOREIGN KEY (customer_id)
            REFERENCES customers (customer_id)
            ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS damage_reports
(
    damage_report_id INT AUTO_INCREMENT PRIMARY KEY,
    return_date      DATE NOT NULL,
    report_date      DATE NOT NULL,
    cost             DECIMAL(10, 2) DEFAULT 0,
    odometer         INT  NOT NULL,

    agreement_id     INT  NOT NULL,
    registered_by    INT  NOT NULL,

    CONSTRAINT fk_report_agreement
        FOREIGN KEY (agreement_id)
            REFERENCES rental_agreements (agreement_id)
            ON DELETE RESTRICT,

    CONSTRAINT fk_report_user
        FOREIGN KEY (registered_by)
            REFERENCES users (user_id)
            ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS damage_items
(
    damage_id        INT AUTO_INCREMENT PRIMARY KEY,
    damage_report_id INT            NOT NULL,
    description      VARCHAR(255)   NOT NULL,
    price            DECIMAL(10, 2) NOT NULL,

    CONSTRAINT fk_damageitem_report
        FOREIGN KEY (damage_report_id)
            REFERENCES damage_reports (damage_report_id)
            ON DELETE CASCADE
);

INSERT INTO users (first_name, last_name, username, password, email, phone, role, active)
VALUES ('Simon', 'Admin', 'sadmin', 'QoJ+o69Avow5/WwH8Hz1UIjRb8DfiwdVK10MHKT0v78=', 'admin@biludlejning.dk', '40112233',
        'ADMIN', TRUE),
       ('Maria', 'Jensen', 'mjensen', 'QoJ+o69Avow5/WwH8Hz1UIjRb8DfiwdVK10MHKT0v78=', 'maria@biludlejning.dk',
        '22113344', 'DATAREGISTRERING', TRUE),
       ('Thomas', 'Hansen', 'thansen', 'QoJ+o69Avow5/WwH8Hz1UIjRb8DfiwdVK10MHKT0v78=', 'thomas@biludlejning.dk',
        '33114455', 'SKADE/UDBEDRING', TRUE);

INSERT INTO customers (first_name, last_name, email, phone, licence_no, street_address, zip_code, city)
VALUES ('Anders', 'Nielsen', 'anders@email.dk', '50112233', 'DK12345678', 'Nørrebrogade 10', 2200, 'København'),
       ('Louise', 'Madsen', 'louise@email.dk', '60113344', 'DK87654321', 'Vestergade 5', 8000, 'Aarhus');

INSERT INTO cars (reg_nr, vin, brand, model, location, odometer, car_description, status, active)
VALUES ('AB12345', 'VIN00000000000001', 'Toyota', 'Yaris', 'København', 45000, 'Compact city car', 'AVAILABLE', TRUE),
       ('CD67890', 'VIN00000000000002', 'Volkswagen', 'Golf', 'Aarhus', 72000, 'Popular family hatchback', 'AVAILABLE',
        1),
       ('EF11223', 'VIN00000000000003', 'Tesla', 'Model 3', 'København', 28000, 'Electric sedan', 'RENTED', TRUE);

INSERT INTO rental_agreements
(start_date, end_date, downpayment, monthly_payment, max_km, created_by, car_id, customer_id, active)
VALUES ('2026-05-01', '2026-06-01', 5000.00, 3200.00, 15000, 2, 3, 1, TRUE);
