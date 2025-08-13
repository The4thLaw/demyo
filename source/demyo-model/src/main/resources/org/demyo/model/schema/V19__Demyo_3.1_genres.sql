-- Issue #14: Add genre to Series, Albums
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

-- Create a view to keep track of album authors regardless of their role
CREATE VIEW albums_authors(album_id, author_id) as
	SELECT * FROM albums_artists
	UNION
	SELECT * FROM albums_writers
	UNION
	SELECT * FROM albums_colorists
	UNION
	SELECT * FROM albums_inkers
	UNION
	SELECT * FROM albums_cover_artists
	UNION
	SELECT * FROM albums_translators;

-- Create a view to aggregate taxons that are somehow part of an album, possibly through their series
CREATE VIEW albums_aggregated_taxons AS
	SELECT a.id AS album_id, st.taxon_id FROM series_taxons st INNER JOIN albums a ON st.series_id = a.series_id
	UNION
	SELECT album_id, taxon_id FROM albums_taxons;
