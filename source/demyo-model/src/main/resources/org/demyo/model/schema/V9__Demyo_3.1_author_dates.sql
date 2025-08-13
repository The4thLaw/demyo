-- Issue #13: New fields for Authors: date of birth / death
ALTER TABLE authors
	ADD COLUMN birth DATE NULL;

ALTER TABLE authors
	ADD COLUMN death DATE NULL;
