CREATE TABLE configuration (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	config_key VARCHAR(255) NOT NULL,
	config_value VARCHAR(2048) NULL,
);
CREATE INDEX ON configuration(config_key);

INSERT INTO configuration(config_key, config_value) VALUES
	('language', NULL),
	('header.quickLinks', '[ { "urlFromRoot": "albums/", "iconSpec": "open_book", "label": "menu.albums.browse" }, { "urlFromRoot": "authors/", "iconSpec": "person", "label": "menu.authors.browse" } ]'),
	('paging.textPageSize', '60'),
	('paging.imagePageSize', '15'),
	('paging.albumPageSize', '20'),
	('thumbnail.width', '220'),
	('thumbnail.height', '200');
