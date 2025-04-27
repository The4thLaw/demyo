package org.demyo.model.enums;

/**
 * Defines ways to make variations between book labels.
 */
public enum TranslationLabelType {
	/** Comic issue-like label variants: issue, writer, artist... */
	COMIC_ISSUE,
	/** Comic issue-like label variants: volume, writer, artist... */
	COMIC_VOLUME,
	/** Graphic novel-like label variants: album, writer, artist... */
	GRAPHIC_NOVEL,
	/** Novel-label variants: book, author, illustrator. */
	NOVEL
}
