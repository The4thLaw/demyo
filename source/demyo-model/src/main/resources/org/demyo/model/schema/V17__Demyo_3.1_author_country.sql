-- Issue #26: Add a field for author nationality
ALTER TABLE authors
	ADD COLUMN country VARCHAR(31) NULL;
