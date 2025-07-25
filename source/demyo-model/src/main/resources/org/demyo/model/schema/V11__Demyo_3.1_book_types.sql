-- Create the type table
CREATE TABLE book_types (
	id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name VARCHAR(127) NOT NULL UNIQUE,
	description CLOB NULL,
	label_type VARCHAR(127) NOT NULL,
	field_config VARCHAR(2048) NULL
);
CREATE INDEX ON book_types(name);

-- Add the new type column
ALTER TABLE albums
	ADD COLUMN book_type_id INT NULL;

-- Create the type
INSERT INTO book_types(name, label_type) VALUES
	('__DEFAULT__', 'GRAPHIC_NOVEL');

-- Set the albums to the new type
UPDATE albums SET book_type_id = (SELECT id FROM book_types WHERE name = '__DEFAULT__') WHERE 1=1;

-- Make the column non-nullable
ALTER TABLE albums
	ALTER COLUMN book_type_id INT NOT NULL;

-- Add the constraint
ALTER TABLE albums
	ADD CONSTRAINT fk_album_type FOREIGN KEY (book_type_id) REFERENCES book_types(id) ON DELETE CASCADE;
