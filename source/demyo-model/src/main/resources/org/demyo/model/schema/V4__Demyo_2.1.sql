/*
 * Ensure it is possible to insert larger number/total
 * https://github.com/The4thLaw/demyo/issues/20
 */
ALTER TABLE derivatives
	MODIFY COLUMN number INT UNSIGNED NULL;

ALTER TABLE derivatives
	MODIFY COLUMN total INT UNSIGNED NULL;
