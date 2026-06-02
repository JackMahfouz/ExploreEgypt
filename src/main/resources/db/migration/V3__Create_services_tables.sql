-- 1. Specific Service Tables
CREATE TABLE IF NOT EXISTS sites (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name text NOT NULL,
    description text,
    address text NOT NULL, -- e.g., "221B Baker Street"
    rate numeric(10, 2)
);

CREATE TABLE IF NOT EXISTS hotels (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name text NOT NULL,
    stars integer CHECK (stars >= 1 AND stars <= 5),
    address text NOT NULL
);

CREATE TABLE IF NOT EXISTS cafes (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name text NOT NULL,
    has_wifi boolean DEFAULT true,
    address text NOT NULL
);

CREATE TABLE IF NOT EXISTS transit_stations (
      id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
      name text NOT NULL,
      transit_line text,
      address text NOT NULL
);

-- 3. Optimization Indexes
-- B-Tree index for fast exact-match lookups when joining back to the service tables
CREATE INDEX CONCURRENTLY idx_spatial_morph ON spatial_locations (locatable_type, locatable_id);