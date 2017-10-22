/*
 * Ensure it is possible to insert larger number/total
 * https://github.com/The4thLaw/demyo/issues/20
 */
ALTER TABLE derivatives
	MODIFY COLUMN number INT UNSIGNED NULL;

ALTER TABLE derivatives
	MODIFY COLUMN total INT UNSIGNED NULL;

/**
 * Support readers
 * https://github.com/The4thLaw/demyo/issues/4
 */

-- Create the table

CREATE TABLE readers (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	colour CHAR(7) DEFAULT NULL
);
CREATE INDEX ON publishers(name);

-- Create a default reader

INSERT INTO readers(name) VALUES ('Demyo');

-- Each reader has favourites series and albums

CREATE TABLE readers_favourite_series (
	reader_id INT UNSIGNED NOT NULL,
	series_id INT UNSIGNED NOT NULL,
	PRIMARY KEY(reader_id, series_id),
	CONSTRAINT fk_readers_favourite_series_reader FOREIGN KEY (reader_id) REFERENCES readers(id) ON DELETE CASCADE,
	CONSTRAINT fk_readers_favourite_series_series FOREIGN KEY (series_id) REFERENCES series(id) ON DELETE CASCADE
);
CREATE TABLE readers_favourite_albums (
	reader_id INT UNSIGNED NOT NULL,
	album_id INT UNSIGNED NOT NULL,
	PRIMARY KEY(reader_id, album_id),
	CONSTRAINT fk_readers_favourite_albums_reader FOREIGN KEY (reader_id) REFERENCES readers(id) ON DELETE CASCADE,
	CONSTRAINT fk_readers_favourite_albums_album FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE CASCADE
);

-- Each reader has his reading list

CREATE TABLE readers_reading_list (
	reader_id INT UNSIGNED NOT NULL,
	album_id INT UNSIGNED NOT NULL,
	PRIMARY KEY(reader_id, album_id),
	CONSTRAINT fk_readers_reading_list_reader FOREIGN KEY (reader_id) REFERENCES readers(id) ON DELETE CASCADE,
	CONSTRAINT fk_readers_reading_list_album FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE CASCADE
);

