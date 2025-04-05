import { contextRoot } from '@/myenv'

export function getEncodedImageName(image: Image): string {
	let encodedName = encodeURI(image.userFileName)
	// The # isn't url-encoded but can cause issues in e.g. srcset attributes
	encodedName = encodedName.replace(/#/, '')
	return encodedName
}

export function getBaseImageUrl(image?: Image): string | undefined {
	if (!image?.id) {
		return undefined
	}
	return `${contextRoot}images/${image.id}/file/${getEncodedImageName(image)}`
}
