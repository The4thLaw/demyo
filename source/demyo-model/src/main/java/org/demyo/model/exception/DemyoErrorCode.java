package org.demyo.model.exception;

/**
 * Exhaustive list of error codes in Demyo.
 * 
 * @author $Author: xr $
 * @version $Revision: 1070 $
 */
public enum DemyoErrorCode {
	/*
	 * 10xxx: system errors
	 * 11xxx: import errors
	 * 12xxx: image error
	 */
	/** Cannot determine Demyo application directory. */
	SYS_APPDIR_NOT_FOUND(10000),
	/** Cannot read the system configuration (but the file exists). */
	SYS_CONFIG_NOT_READABLE(10001),
	/** The given path for a Demyo directory already exists, but is not a directory. */
	SYS_DIR_PATH_ALREADY_TAKEN(10002),
	/** Failed to create a directory required to operate. */
	SYS_DIR_CANNOT_CREATE(10003),
	/** Generic I/O operation error. */
	SYS_IO_ERROR(10004),
	/** This import format is not supported. */
	IMPORT_FORMAT_NOT_SUPPORTED(11000),
	/** The system encountered an I/O error during import. */
	IMPORT_IO_ERROR(11001),
	/** The system encountered a parse error during import. */
	IMPORT_PARSE_ERROR(11002),
	/** The system encountered an error while restoring the images. */
	IMPORT_IMAGES_ERROR(11003),
	/** The accessed image is trying to perform a directory traversal. */
	IMAGE_DIRECTORY_TRAVERSAL(12000),
	/** The accessed image does not exist. */
	IMAGE_NOT_FOUND(12001),
	/** The system could not save the configuration. */
	CONFIG_CANNOT_SAVE(13000),
	/** The system could not load the configuration. */
	CONFIG_CANNOT_LOAD(13001),
	/** The configuration template cannot be found. */
	CONFIG_TEMPLATE_NOT_FOUND(13002),
	/** The system could not copy the configuration template to the appropriate location. */
	CONFIG_TEMPLATE_CANNOT_COPY(13003),
	/** The current model does not support "startsWith" jumps in pagination. */
	PAGING_NO_STARTSWITH(14000),
	/** The quick task configuration is invalid. */
	QUICK_TASKS_INVALID_CONFIG(15000),
	/** The quick task configuration is invalid: the task is missing its label. */
	QUICK_TASKS_MISSING_LABEL(15001);

	private final int numericCode;

	private DemyoErrorCode(int numericCode) {
		this.numericCode = numericCode;
	}

	@Override
	public String toString() {
		return "DEMYO-ERR-" + numericCode + ": " + name();
	}
}
