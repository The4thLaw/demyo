type HexColor = `#${string}`

interface AbstractModel {
	id?: number
	identifyingName: string
}

interface AbstractNamedModel extends AbstractModel {
	name: string
}

interface AbstractBasicLegalEntity extends AbstractNamedModel {
	website: string
	history: string
}

interface AbstractLegalEntity extends AbstractBasicLegalEntity {
	feed: string
	logo: Image
}

interface Binding extends AbstractNamedModel {}

interface Collection extends AbstractLegalEntity {
	publisher: Publisher
}

interface Image extends AbstractModel {}

interface Publisher extends AbstractLegalEntity {}

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
