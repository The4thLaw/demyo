-- Issue #243: Support author pseudonyms
ALTER TABLE authors
	ADD COLUMN pseudonym_of_id INT NULL;
ALTER TABLE authors
	ADD CONSTRAINT fk_author_pseudonym FOREIGN KEY (pseudonym_of_id) REFERENCES authors(id) ON DELETE SET NULL;
