-- Create the universe table
CREATE TABLE universes (
	id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name VARCHAR(127) NOT NULL UNIQUE,
	description CLOB NULL,
	website VARCHAR(255) NULL,
	logo_id INT NULL,
	CONSTRAINT fk_universes_logo FOREIGN KEY (logo_id) REFERENCES images(id) ON DELETE SET NULL
);
CREATE INDEX ON universes(name);

-- Many-to-many from universes to images
CREATE TABLE universes_images (
	universe_id INT NOT NULL,
	image_id INT NOT NULL,
	PRIMARY KEY(universe_id, image_id),
	CONSTRAINT fk_universes_images_universe FOREIGN KEY (universe_id) REFERENCES universes(id) ON DELETE CASCADE,
	CONSTRAINT fk_universes_images_image FOREIGN KEY (image_id) REFERENCES images(id) ON DELETE CASCADE
);

-- Create the new references
ALTER TABLE series
	ADD COLUMN universe_id INT NULL;
ALTER TABLE series
	ADD CONSTRAINT fk_series_universe FOREIGN KEY (universe_id) REFERENCES universes(id) ON DELETE SET NULL;
ALTER TABLE albums
	ADD COLUMN universe_id INT NULL;
ALTER TABLE albums
	ADD CONSTRAINT fk_albums_universe FOREIGN KEY (universe_id) REFERENCES universes(id) ON DELETE SET NULL;
