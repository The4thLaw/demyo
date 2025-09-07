-- Issue #56: Add fields for the original title of Albums and Series
ALTER TABLE series
	ADD COLUMN original_name VARCHAR(255) NULL;

ALTER TABLE albums
	ADD COLUMN original_title VARCHAR(255) NULL;
