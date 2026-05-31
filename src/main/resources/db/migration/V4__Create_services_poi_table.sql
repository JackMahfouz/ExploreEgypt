CREATE EXTENSION IF NOT EXISTS postgis;

-- 2. The Central Morph Table for Spatial Data
CREATE TABLE spatial_locations (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    locatable_type text NOT NULL, -- e.g., 'site', 'hotel', 'cafe', 'transit_station'
    locatable_id uuid NOT NULL,   -- The ID from the specific service table
    geom geography(Point, 4326) NOT NULL
);

-- 3. Optimization Indexes
-- B-Tree index for fast exact-match lookups when joining back to the service tables
CREATE INDEX CONCURRENTLY idx_spatial_morph ON spatial_locations (locatable_type, locatable_id);

-- GiST index on the geometry column for fast proximity calculations,
-- including the type so the database can filter categories quickly before calculating distance.
CREATE INDEX CONCURRENTLY idx_spatial_geom_type ON spatial_locations USING GiST (geom) INCLUDE (locatable_type);