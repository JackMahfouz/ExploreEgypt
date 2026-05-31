-- 1. Specific Service Tables
CREATE TABLE sites (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name text NOT NULL,
    description text,
    address text NOT NULL, -- e.g., "221B Baker Street"
    rate numeric(10, 2)
);

CREATE TABLE hotels (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name text NOT NULL,
    stars integer CHECK (stars >= 1 AND stars <= 5),
    address text NOT NULL
);

CREATE TABLE cafes (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name text NOT NULL,
    has_wifi boolean DEFAULT true,
    address text NOT NULL
);

CREATE TABLE transit_stations (
      id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
      name text NOT NULL,
      transit_line text,
      address text NOT NULL
);