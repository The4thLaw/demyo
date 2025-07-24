-- Issue #14: Add support for genres
ALTER TABLE tags
	ADD COLUMN taxon_type VARCHAR(63) NOT NULL DEFAULT 'TAG';

CREATE TABLE series_tags (
	series_id INT NOT NULL,
	tag_id INT NOT NULL,
	PRIMARY KEY(series_id, tag_id),
	CONSTRAINT fk_series_tags_series FOREIGN KEY (series_id) REFERENCES series(id) ON DELETE CASCADE,
	CONSTRAINT fk_series_tags_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);
