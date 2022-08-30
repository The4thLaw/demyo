/*
 * Ensure it is possible to insert larger number/total
 * https://github.com/The4thLaw/demyo/issues/20
 */
ALTER TABLE derivatives
	ALTER COLUMN number INT NULL;

ALTER TABLE derivatives
	ALTER COLUMN total INT NULL;

/**
 * Support readers
 * https://github.com/The4thLaw/demyo/issues/4
 */

-- Create the table

CREATE TABLE readers (
	id INT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	colour CHAR(7) DEFAULT NULL
);
CREATE INDEX ON readers(name);

-- Each reader has favourites series and albums

CREATE TABLE readers_favourite_series (
	reader_id INT NOT NULL,
	series_id INT NOT NULL,
	PRIMARY KEY(reader_id, series_id),
	CONSTRAINT fk_readers_favourite_series_reader FOREIGN KEY (reader_id) REFERENCES readers(id) ON DELETE CASCADE,
	CONSTRAINT fk_readers_favourite_series_series FOREIGN KEY (series_id) REFERENCES series(id) ON DELETE CASCADE
);
CREATE TABLE readers_favourite_albums (
	reader_id INT NOT NULL,
	album_id INT NOT NULL,
	PRIMARY KEY(reader_id, album_id),
	CONSTRAINT fk_readers_favourite_albums_reader FOREIGN KEY (reader_id) REFERENCES readers(id) ON DELETE CASCADE,
	CONSTRAINT fk_readers_favourite_albums_album FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE CASCADE
);

-- Each reader has his reading list

CREATE TABLE readers_reading_list (
	reader_id INT NOT NULL,
	album_id INT NOT NULL,
	PRIMARY KEY(reader_id, album_id),
	CONSTRAINT fk_readers_reading_list_reader FOREIGN KEY (reader_id) REFERENCES readers(id) ON DELETE CASCADE,
	CONSTRAINT fk_readers_reading_list_album FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE CASCADE
);

/**
 * Marked as first edition
 * https://github.com/The4thLaw/demyo/issues/11
 */

ALTER TABLE albums
	ADD COLUMN marked_as_first_edition BOOLEAN DEFAULT 'false';

/**
 * Per-reader configuration
 * Also clear the configuration since there was no advertised way of changing it and we want to handle this at
 * application level
 * https://github.com/The4thLaw/demyo/issues/50
 */

DELETE FROM configuration;
ALTER TABLE configuration
	ADD COLUMN reader_id INT NOT NULL;
ALTER TABLE configuration
	ADD CONSTRAINT fk_configuration_reader FOREIGN KEY (reader_id) REFERENCES readers(id) ON DELETE CASCADE;
