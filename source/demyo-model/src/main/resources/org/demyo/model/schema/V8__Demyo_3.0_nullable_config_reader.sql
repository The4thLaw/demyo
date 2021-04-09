/** Allow reader-less configuration entries */
ALTER TABLE configuration
	MODIFY COLUMN reader_id INT UNSIGNED NULL;
