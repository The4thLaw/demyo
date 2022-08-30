/** Allow reader-less configuration entries */
ALTER TABLE configuration
	ALTER COLUMN reader_id INT NULL;
