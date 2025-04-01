/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/explicit-module-boundary-types */

function isIModel(object: any): object is IModel {
	return 'id' in object
		&& 'identifyingName' in object
}

function isAbstractModel(object: any): object is AbstractModel {
	return isIModel(object)
}

/**
 * Custom guard for processed tags
 */
export function isProcessed(object: any): object is ProcessedTag {
	return 'relativeWeight' in object
}

export function isImage(object: any): object is Image {
	return isAbstractModel(object)
		&& 'url' in object
		&& 'userFileName' in object
}
