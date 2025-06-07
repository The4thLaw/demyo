export function useRichTextTemplate(userValue: Ref<string | undefined>): Ref<string | undefined> {
	return computed(() => {
		if (!userValue.value) {
			return userValue.value
		}
		// const initial = userValue.value
		const processed = userValue.value
			// Authors
			.replace(/(\[([^\]]*)\])?\((author|series):(\d+)\)/g,
				'<DeferredModelLink type="$3" model-id="$4" label="$2"/>')
			// TODO: album, publisher, collection, derivative source, binding, derivative type,
			// derivative, image (thumbnail, large or full), book type
			// Images
			.replace(/(\[([^\]]*)\])?\(image:(\d+)(:(small|large|full))?\)/g,
				'<DeferredImageThumb image-id="$3" alt="$2" variant="$5"/>')
			// Tags
			.replace(/(\[([^\]]*)\])?\(tag:(\d+)\)/g, '<DeferredTagLink tag-id="$3" label="$2"/>')
		// console.debug('Processed rich text field for model links', { initial, processed })
		return processed
	})
}
