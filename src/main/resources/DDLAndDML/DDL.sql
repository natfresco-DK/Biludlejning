CREATE DATABASE IF NOT EXISTS biludlejning;

USE biludlejning;

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
    last_name      varchar(50),
    email          VARCHAR(100),
    phone          VARCHAR(30),
    licence_no     VARCHAR(50),
    street_address VARCHAR(150),
    zip_code       INT,
    city           VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS cars
(
    car_id          INT AUTO_INCREMENT PRIMARY KEY,
    reg_nr          VARCHAR(20) NOT NULL UNIQUE,
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
    from_date       DATE           NOT NULL,
    to_date         DATE           NOT NULL,
    downpayment     DECIMAL(10, 2) NOT NULL,
    monthly_payment DECIMAL(10, 2) NOT NULL,
    max_km          INT            NOT NULL,
    created_by      INT,
    car_id          INT            NOT NULL,
    customer_id     INT            NOT NULL,

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
    registered_by    INT,

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