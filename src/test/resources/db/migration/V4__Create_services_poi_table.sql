-- 2. The Central Morph Table for Spatial Data
CREATE TABLE spatial_locations (
    id uuid PRIMARY KEY DEFAULT RANDOM_UUID(),
    locatable_type VARCHAR(255) NOT NULL, -- e.g., 'site', 'hotel', 'cafe', 'transit_station'
    locatable_id uuid NOT NULL,   -- The ID from the specific service table
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL
);

-- 3. Optimization Indexes
-- B-Tree index for fast exact-match lookups when joining back to the service tables
CREATE INDEX idx_spatial_morph ON spatial_locations (locatable_type, locatable_id);
