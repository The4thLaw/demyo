package org.demyo.common.exception;

/**
 * Exhaustive list of error codes in Demyo.
 */
public enum DemyoErrorCode {
	/*
	 * 10xxx: system errors
	 * 11xxx: import errors
	 * 12xxx: image error
	 */
	/** Cannot determine Demyo application directory. */
	SYS_APPDIR_NOT_FOUND(10000),
	/** Cannot find the default system configuration. */
	SYS_CONFIG_NO_DEFAULT(10001),
	/** Cannot read the system configuration (but the file exists). */
	SYS_CONFIG_NOT_READABLE(10002),
	/** The given path for a Demyo directory already exists, but is not a directory. */
	SYS_DIR_PATH_ALREADY_TAKEN(10003),
	/** Failed to create a directory required to operate. */
	SYS_DIR_CANNOT_CREATE(10004),
	// 10005 is reserved
	/** Generic error during application startup. */
	SYS_STARTUP_ERROR(10006),
	/** The Spring configuration is inconsistent. */
	SYS_SPRING_CONFIG_INCONSISTENT(10007),
	/** The H2 version that is needed for a migration is missing. */
	SYS_MISSING_H2_FOR_MIGRATION(10008),
	/** The database version couldn't be found. */
	SYS_MISSING_DB_SCHEMA_VERSION(10009),
	/** This import format is not supported. */
	IMPORT_FORMAT_NOT_SUPPORTED(11000),
	/** The system encountered an I/O error during import. */
	IMPORT_IO_ERROR(11001),
	/** The system encountered a parse error during import. */
	IMPORT_PARSE_ERROR(11002),
	/** The system encountered an error while restoring the images. */
	IMPORT_IMAGES_ERROR(11003),
	/** This version of the schema is not supported by Demyo. */
	IMPORT_WRONG_SCHEMA(11004),
	/** The accessed image is trying to perform a directory traversal. */
	IMAGE_DIRECTORY_TRAVERSAL(12000),
	/** The accessed image does not exist. */
	IMAGE_NOT_FOUND(12001),
	/** Error while uploading an image. */
	IMAGE_UPLOAD_ERROR(12002),
	/** Error while processing an image. */
	IMAGE_IO_ERROR(12003),
	/** The temporary image from FilePond doesn't exist. */
	IMAGE_FILEPOND_MISSING(12004),
	/** If there is more than one image, alwaysNumber should be set to <code>true</code>. */
	IMAGE_FILEPOND_RENUMBER_MORE_THAN_ONE(12005),
	/** Thumbnail generation failed and no surrogate could be provided. */
	IMAGE_THUMBNAIL_TIMEOUT(12006),
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
	QUICK_TASKS_MISSING_LABEL(15001),
	/** The system encountered an I/O error during export. */
	EXPORT_IO_ERROR(16000),
	/** An assumption about the database could not be met. */
	EXPORT_DB_CONSISTENCY_ERROR(16001),
	/** The system encountered a parse error during export. */
	EXPORT_XML_ERROR(16001),
	/** ORM: A mapped property is invalid. */
	ORM_INVALID_PROPERTY(17000),
	/** ORM: The PreSave method doesn't have a valid signature. */
	ORM_INVALID_PRESAVE(17001),
	/** Web: Forms: The select options are of an unknown type. */
	WEB_FORM_INVALID_OPTIONS(18000),
	/** Cannot delete the last reader. */
	READER_CANNOT_DELETE_LAST(19000),
	/** Generic I/O operation error. */
	IO_GENERIC_ERROR(20000),
	/** There was an issue during a file compression. */
	IO_COMPRESSION_ERROR(20001),
	/** Book type management is already enabled and cannot be re-enabled. */
	BOOK_TYPE_MGMT_ALREADY_ENABLED(21000),
	/** Book type management is enabled, so it's needed on albums. */
	BOOK_TYPE_MGMT_ENABLED_REQ_ON_ALBUM(21001);

	private final int numericCode;

	DemyoErrorCode(int numericCode) {
		this.numericCode = numericCode;
	}

	@Override
	public String toString() {
		return "DEMYO-ERR-" + numericCode + ": " + name();
	}
}
