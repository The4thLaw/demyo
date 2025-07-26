-- Issue #14: Add support for genres
ALTER TABLE tags
	ADD COLUMN taxon_type VARCHAR(63) NOT NULL DEFAULT 'TAG';

-- Rename all tables and constraints
ALTER TABLE albums_tags ALTER COLUMN tag_id RENAME TO taxon_id;
ALTER TABLE albums_tags RENAME CONSTRAINT fk_albums_tags_album TO fk_albums_taxons_album;
ALTER TABLE albums_tags RENAME CONSTRAINT fk_albums_tags_tag TO fk_albums_taxons_taxon;
ALTER TABLE albums_tags RENAME TO albums_taxons;
ALTER TABLE tags RENAME TO taxons;

CREATE TABLE series_taxons (
	series_id INT NOT NULL,
	taxon_id INT NOT NULL,
	PRIMARY KEY(series_id, taxon_id),
	CONSTRAINT fk_series_taxons_series FOREIGN KEY (series_id) REFERENCES series(id) ON DELETE CASCADE,
	CONSTRAINT fk_series_taxons_taxon FOREIGN KEY (taxon_id) REFERENCES taxons(id) ON DELETE CASCADE
);
