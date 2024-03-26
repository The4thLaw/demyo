type HexColor = `#${string}`

interface AbstractModel {
	id?: number
	getIdentifyingName: string
}

interface AbstractNamedModel extends AbstractModel {
	name: string
}

interface ApplicationConfiguration {
	currency: string
	language: string
	pageSizeForCards: number
	pageSizeForImages: number
	pageSizeForText: number
	subItemsInCardIndex: number

}

declare interface Reader extends AbstractNamedModel {
	colour?: HexColor,
	configuration: ApplicationConfiguration
}
