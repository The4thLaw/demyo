// TODO: generate part of this
type HexColor = `#${string}`

interface AbstractModel {
	id?: number
	identifyingName: string
}

interface AbstractPricedModel extends AbstractModel {}

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

interface Album extends AbstractPricedModel {
	title: string
	wishlist: boolean
}

interface Binding extends AbstractNamedModel {}

interface Collection extends AbstractLegalEntity {
	publisher: Publisher
}

interface Image extends AbstractModel {}

interface MetaSeries {
	album: Album
	series: Series
	albums: Album[]
}

interface Publisher extends AbstractLegalEntity {}

interface Series extends AbstractNamedModel {}

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
