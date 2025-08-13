-- Issue #102: Add a field to indicate whether a Derivative Source is still active
ALTER TABLE sources
	ADD COLUMN active BOOLEAN DEFAULT 'true';
