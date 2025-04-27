package org.demyo.model.enums;

/**
 * Defines ways to make variations between book labels.
 */
public enum TranslationLabelType {
	// TODO: maybe some of these are useless. We might only need GRAPHIC_NOVEL and NOVEL
	// Adapt the translations accordingly
	/** Comic issue-like label variants: issue, writer, artist... */
	COMIC_ISSUE,
	/** Comic issue-like label variants: volume, writer, artist... */
	COMIC_VOLUME,
	/** Graphic novel-like label variants: album, writer, artist... */
	GRAPHIC_NOVEL,
	/** Novel-label variants: book, author, illustrator. */
	NOVEL
}
