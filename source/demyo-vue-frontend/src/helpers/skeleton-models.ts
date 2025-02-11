export function skeletonImage(): Image {
	return {
		id: null as unknown as number,
		identifyingName: '',
		url: '',
		userFileName: ''
	}
}

export function skeletonPublisher(): Publisher {
	return {
		id: null as unknown as number,
		identifyingName: '',
		name: '',
		collections: []
	}
}
