-- 1. Specific Service Tables
CREATE TABLE IF NOT EXISTS sites (
    id uuid PRIMARY KEY DEFAULT RANDOM_UUID(),
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    address VARCHAR(255) NOT NULL, -- e.g., "221B Baker Street"
    rate numeric(10, 2)
);

CREATE TABLE IF NOT EXISTS hotels (
    id uuid PRIMARY KEY DEFAULT RANDOM_UUID(),
    name VARCHAR(255) NOT NULL,
    stars integer CHECK (stars >= 1 AND stars <= 5),
    address VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS cafes (
    id uuid PRIMARY KEY DEFAULT RANDOM_UUID(),
    name VARCHAR(255) NOT NULL,
    has_wifi boolean DEFAULT true,
    address VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS transit_stations (
      id uuid PRIMARY KEY DEFAULT RANDOM_UUID(),
      name VARCHAR(255) NOT NULL,
      transit_line VARCHAR(255),
      address VARCHAR(255) NOT NULL
);
