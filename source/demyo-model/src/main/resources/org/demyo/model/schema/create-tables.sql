CREATE TABLE images (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	url VARCHAR(255) NOT NULL,
	description VARCHAR(255) NULL
);
CREATE INDEX ON images(description);

CREATE TABLE publishers (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	website VARCHAR(255) NULL,
	feed VARCHAR(255) NULL,
	history CLOB NULL,
	logo_id INT UNSIGNED NULL,
	CONSTRAINT fk_publishers_images FOREIGN KEY (logo_id) REFERENCES images(id) ON DELETE SET NULL
);
CREATE INDEX ON publishers(name);

CREATE TABLE collections (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	website VARCHAR(255) NULL,
	feed VARCHAR(255) NULL,
	history CLOB NULL,
	logo_id INT UNSIGNED NULL,
	publisher_id INT UNSIGNED NOT NULL,
	CONSTRAINT fk_collections_images FOREIGN KEY (logo_id) REFERENCES images(id) ON DELETE SET NULL,
	CONSTRAINT fk_collections_publishers FOREIGN KEY (publisher_id) REFERENCES publishers(id) ON DELETE RESTRICT
);
CREATE INDEX ON collections(name);

CREATE TABLE authors (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(127) NOT NULL,
	fname VARCHAR(127) NULL,
	nickname VARCHAR(127) NULL,
	biography CLOB NULL,
	portrait_id INT UNSIGNED NULL,
	website VARCHAR(255) NULL,
	CONSTRAINT fk_authors_images FOREIGN KEY (portrait_id) REFERENCES images(id) ON DELETE SET NULL
);
CREATE INDEX ON authors(name);

CREATE TABLE series (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	summary CLOB NULL,
	completed BOOLEAN DEFAULT 'false',
	comment CLOB NULL,
	website VARCHAR(255) NULL
);
CREATE INDEX ON series(name);

CREATE TABLE series_relations (
	main INT UNSIGNED,
	sub INT UNSIGNED,
	PRIMARY KEY(main, sub),
	CONSTRAINT fk_series_relations_main FOREIGN KEY (main) REFERENCES series(id) ON DELETE CASCADE,
	CONSTRAINT fk_series_relations_sub FOREIGN KEY (sub) REFERENCES series(id) ON DELETE CASCADE
);
CREATE INDEX ON series_relations(main);

CREATE TABLE bindings (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255) NOT NULL
);

CREATE TABLE albums (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	series_id INT UNSIGNED NOT NULL,
	cycle SMALLINT UNSIGNED NULL,
	number FLOAT UNSIGNED NULL,
	number_suffix VARCHAR(5) NULL,
	title VARCHAR(255) NULL, /* One shots sometimes have no title */
	publisher_id INT UNSIGNED NOT NULL,
	collection_id INT UNSIGNED NULL,
	first_edition DATE NULL,
	this_edition DATE NULL,
	isbn VARCHAR(18) NULL, /* ISBN-10 or ISBN-13 with at most 5 separators (spaces, dashes, ...) */
	purchase_date DATE NULL,
	purchase_price FLOAT NULL,
	wishlist BOOLEAN DEFAULT 'false',
	binding_id INT UNSIGNED NULL,
	cover_id INT UNSIGNED NULL,
	summary CLOB NULL,
	comment CLOB NULL,
	height FLOAT UNSIGNED NULL,
	width FLOAT UNSIGNED NULL,
	pages INT UNSIGNED NULL,
	CONSTRAINT fk_albums_series FOREIGN KEY (series_id) REFERENCES series(id) ON DELETE RESTRICT,
	CONSTRAINT fk_albums_publishers FOREIGN KEY (publisher_id) REFERENCES publishers(id) ON DELETE RESTRICT,
	CONSTRAINT fk_albums_collections FOREIGN KEY (collection_id) REFERENCES collections(id) ON DELETE SET NULL,
	CONSTRAINT fk_albums_bindings FOREIGN KEY (binding_id) REFERENCES bindings(id) ON DELETE SET NULL,
	CONSTRAINT fk_albums_images FOREIGN KEY (cover_id) REFERENCES images(id) ON DELETE SET NULL
);
CREATE INDEX ON albums(title);

CREATE TABLE album_prices (
	album_id INT UNSIGNED NOT NULL,
	date DATE NOT NULL,
	price FLOAT UNSIGNED NOT NULL,
	PRIMARY KEY(album_id, date),
	CONSTRAINT fk_album_prices FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE CASCADE
);

CREATE TABLE albums_images (
	album_id INT UNSIGNED NOT NULL,
	image_id INT UNSIGNED NOT NULL,
	PRIMARY KEY(album_id, image_id),
	CONSTRAINT fk_albums_images_album FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE CASCADE,
	CONSTRAINT fk_albums_images_image FOREIGN KEY (image_id) REFERENCES images(id) ON DELETE CASCADE
);

CREATE TABLE albums_artists (
	album_id INT UNSIGNED NOT NULL,
	artist_id INT UNSIGNED NOT NULL,
	PRIMARY KEY(album_id, artist_id),
	CONSTRAINT fk_albums_artists_album FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE CASCADE,
	CONSTRAINT fk_albums_artists_author FOREIGN KEY (artist_id) REFERENCES authors(id) ON DELETE CASCADE
);
CREATE TABLE albums_writers (
	album_id INT UNSIGNED NOT NULL,
	writer_id INT UNSIGNED NOT NULL,
	PRIMARY KEY(album_id, writer_id),
	CONSTRAINT fk_albums_writers_album FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE CASCADE,
	CONSTRAINT fk_albums_writers_author FOREIGN KEY (writer_id) REFERENCES authors(id) ON DELETE CASCADE
);
CREATE TABLE albums_colorists (
	album_id INT UNSIGNED NOT NULL,
	colorist_id INT UNSIGNED NOT NULL,
	PRIMARY KEY(album_id, colorist_id),
	CONSTRAINT fk_albums_colorists_album FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE CASCADE,
	CONSTRAINT fk_albums_colorists_author FOREIGN KEY (colorist_id) REFERENCES authors(id) ON DELETE CASCADE
);

CREATE TABLE tags (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(127) NOT NULL UNIQUE,
	fgcolour CHAR(7) DEFAULT NULL,
	bgcolour CHAR(7) DEFAULT NULL
);
CREATE INDEX ON tags(name);

CREATE TABLE albums_tags (
	album_id INT UNSIGNED NOT NULL,
	tag_id INT UNSIGNED NOT NULL,
	PRIMARY KEY(album_id, tag_id),
	CONSTRAINT fk_albums_tags_album FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE CASCADE,
	CONSTRAINT fk_albums_tags_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

CREATE TABLE sources (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(127) NOT NULL,
	owner VARCHAR(127) NULL,
	email VARCHAR(255) NULL,
	website VARCHAR(255) NULL,
	address VARCHAR(255) NULL,
	phone_number VARCHAR(31) NULL,
	history CLOB NULL
);

CREATE TABLE derivative_types (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(127) NOT NULL
);

CREATE TABLE derivatives (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	series_id INT UNSIGNED NOT NULL,
	album_id INT UNSIGNED NULL,
	artist_id INT UNSIGNED NULL,
	derivative_type_id INT UNSIGNED NOT NULL,
	colors SMALLINT UNSIGNED NULL,
	/* Source could be an editor, a library, the author himself. */
	source_id INT UNSIGNED NULL,
	number SMALLINT UNSIGNED NULL,
	total SMALLINT UNSIGNED NULL, 
	signed BOOLEAN DEFAULT 'true',
	authors_copy BOOLEAN DEFAULT 'false',
	restricted_sale BOOLEAN DEFAULT 'false',
	description CLOB NULL,
	width float UNSIGNED NULL,
	height float UNSIGNED NULL,
	depth float UNSIGNED NULL,
	purchase_date DATE NULL,
	purchase_price FLOAT NULL,
	CONSTRAINT fk_derivatives_series FOREIGN KEY (series_id) REFERENCES series(id) ON DELETE RESTRICT,
	CONSTRAINT fk_derivatives_albums FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE SET NULL,
	CONSTRAINT fk_derivatives_authors FOREIGN KEY (artist_id) REFERENCES authors(id) ON DELETE SET NULL,
	CONSTRAINT fk_derivatives_types FOREIGN KEY (derivative_type_id) REFERENCES derivative_types(id) ON DELETE RESTRICT,
	CONSTRAINT fk_derivatives_sources FOREIGN KEY (source_id) REFERENCES sources(id) ON DELETE SET NULL
);

CREATE TABLE derivative_prices (
	derivative_id INT UNSIGNED NOT NULL,
	date DATE NOT NULL,
	price FLOAT UNSIGNED NOT NULL,
	PRIMARY KEY(derivative_id, date),
	CONSTRAINT fk_derivative_prices FOREIGN KEY (derivative_id) REFERENCES derivatives(id) ON DELETE CASCADE
);

CREATE TABLE derivatives_images (
	derivative_id INT UNSIGNED NOT NULL,
	image_id INT UNSIGNED NOT NULL,
	PRIMARY KEY(derivative_id, image_id),
	CONSTRAINT fk_derivatives_images_derivative FOREIGN KEY (derivative_id) REFERENCES derivatives(id) ON DELETE CASCADE,
	CONSTRAINT fk_derivatives_images_image FOREIGN KEY (image_id) REFERENCES images(id) ON DELETE CASCADE
);

CREATE TABLE borrowers (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(127) NOT NULL,
	fname VARCHAR(127) NULL,
	email VARCHAR(255) NULL,
	UNIQUE(name, fname)
);

CREATE TABLE albums_borrowers (
	album_id INT UNSIGNED NOT NULL,
	borrower_id INT UNSIGNED NOT NULL,
	borrow_date DATE NOT NULL,
	return_date DATE NULL,
	PRIMARY KEY(album_id, borrower_id, borrow_date),
	CONSTRAINT fk_albums_borrowers_album FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE CASCADE,
	CONSTRAINT fk_albums_borrowers_borrower FOREIGN KEY (borrower_id) REFERENCES borrowers(id) ON DELETE CASCADE
);

CREATE TABLE searches (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	model VARCHAR(255) NOT NULL, /* The kind of item being searched. */
	status VARCHAR(63) NOT NULL, /* The status. Either temporary or saved. */
	name VARCHAR(255) NULL, /* User-specified name */
	criteria VARCHAR(4094) NOT NULL, /* The criteria for the search */
	parameters VARCHAR(4094) NULL, /* Some addition parameters for the search (e.g. how to display the result for albums) */
	last_used DATETIME NOT NULL, /* Last date of usage, for automated pruning */
	UNIQUE(model, status, name)
);
CREATE INDEX ON searches(model);
