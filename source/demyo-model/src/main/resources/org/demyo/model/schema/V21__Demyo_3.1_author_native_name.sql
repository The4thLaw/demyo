-- Issue #261: Add a field for the author's name in their native language
ALTER TABLE authors
	ADD COLUMN native_lang_name VARCHAR(511) NULL;
