CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(30),
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE taskers (
    tasker_id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(30),
    password_hash VARCHAR(255) NOT NULL,
    rating_avg NUMERIC(3,2),
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE admins (
    admin_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE services (
    service_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE tasker_services (
    tasker_id BIGINT NOT NULL REFERENCES taskers(tasker_id) ON DELETE CASCADE,
    service_id BIGINT NOT NULL REFERENCES services(service_id) ON DELETE CASCADE,
    price NUMERIC(10,2),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (service_id, tasker_id)
);

CREATE TABLE requests (
    request_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    tasker_id BIGINT REFERENCES taskers(tasker_id) ON DELETE SET NULL,
    service_id BIGINT NOT NULL REFERENCES services(service_id) ON DELETE RESTRICT,
    city VARCHAR(100),
    street VARCHAR(150),
    block VARCHAR(50),
    apartment VARCHAR(50),
    scheduled_at TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_request_status CHECK (status IN ('PENDING', 'ACCEPTED', 'IN_PROGRESS', 'DONE', 'CANCELED'))
);

CREATE TABLE payments (
    request_id BIGINT PRIMARY KEY REFERENCES requests(request_id) ON DELETE CASCADE,
    amount NUMERIC(10,2) NOT NULL CHECK (amount >= 0),
    method VARCHAR(20) NOT NULL,
    paid_at TIMESTAMP,
    transaction_ref VARCHAR(120),
    CONSTRAINT chk_payment_method CHECK (method IN ('CARD', 'PAYPAL', 'EWALLET'))
);

CREATE TABLE reviews (
    request_id BIGINT PRIMARY KEY REFERENCES requests(request_id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    tasker_id BIGINT NOT NULL REFERENCES taskers(tasker_id) ON DELETE CASCADE,
    rating SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE timetables (
    timetable_id BIGSERIAL PRIMARY KEY,
    tasker_id BIGINT NOT NULL REFERENCES taskers(tasker_id) ON DELETE CASCADE,
    work_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_booked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_timetable_time CHECK (end_time > start_time)
);

CREATE INDEX idx_requests_user_id ON requests(user_id);
CREATE INDEX idx_requests_tasker_id ON requests(tasker_id);
CREATE INDEX idx_requests_service_id ON requests(service_id);
CREATE INDEX idx_timetables_tasker_date ON timetables(tasker_id, work_date);

INSERT INTO admins(email, password_hash)
VALUES ('admin@san3a.com', '$2a$10$7M6R6gBnQ4MzuWgWQ6rAxe0u5O0dWfWQ4f8fKqzKbD4T8jefSE6Zm');
