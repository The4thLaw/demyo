ALTER TABLE series
	ADD COLUMN original_name VARCHAR(255) NULL;

ALTER TABLE albums
	ADD COLUMN original_title VARCHAR(255) NULL;