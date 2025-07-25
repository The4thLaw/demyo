export function useRichTextTemplate(userValue: Ref<string | undefined>): Ref<string | undefined> {
	return computed(() => {
		if (!userValue.value) {
			return userValue.value
		}
		return userValue.value
			// Simple models without specific processing
			.replace(
				// eslint-disable-next-line max-len
				/(\[([^\]]*)\])?\((album|author|binding|bookType|collection|derivative|derivativeSource|derivativeType|publisher|series|universe):(\d+)\)/g,
				'<DeferredModelLink type="$3" model-id="$4" label="$2"/>')
			// Images
			.replace(/(\[([^\]]*)\])?\(image:(\d+)(:(small|large|full))?\)/g,
				'<DeferredImageThumb image-id="$3" alt="$2" variant="$5"/>')
			// Tags
			.replace(/(\[([^\]]*)\])?\(tag:(\d+)\)/g, '<DeferredTagLink tag-id="$3" label="$2"/>')
	})
}
