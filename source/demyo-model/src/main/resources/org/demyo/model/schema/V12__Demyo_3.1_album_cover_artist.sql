-- Issue #17: Add a field for cover illustrators
CREATE TABLE albums_cover_artists (
	album_id INT NOT NULL,
	cover_artist_id INT NOT NULL,
	PRIMARY KEY(album_id, cover_artist_id),
	CONSTRAINT fk_albums_cover_artists_album FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE CASCADE,
	CONSTRAINT fk_albums_cover_artists_author FOREIGN KEY (cover_artist_id) REFERENCES authors(id) ON DELETE CASCADE
);

