import { contextRoot } from '@/myenv'

export function getEncodedImageName(image) {
	let encodedName = encodeURI(image.userFileName)
	// The # isn't url-encoded but can cause issues in e.g. srcset attributes
	encodedName = encodedName.replace(/#/, '')
	return encodedName
}

export function getBaseImageUrl(image) {
	return `${contextRoot}images/${image.id}/file/${getEncodedImageName(image)}`
}
