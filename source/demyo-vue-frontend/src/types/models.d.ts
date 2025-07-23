/** The type used for colors in the server-side configuration. */
type HexColor = `#${string}`

/** A processed tag is a Tag that has been extended with some additional properties. */
interface ProcessedTag extends Tag {
	/** The relative weight of this tag compared to other tags, based on its usage count. */
	relativeWeight: number
}

interface DerivativeQuery {
	toAlbum?: number
	toSeries?: number
	toArtist?: number
}

interface FilePondData {
	mainImage: string
	otherImages: string[]
}

type CombinationMode = 'AND' | 'OR'

interface AbstractModelFilter {
	mode?: CombinationMode
}

interface AlbumFilter extends AbstractModelFilter {
	/** The internal ID of the {@link Publisher}. */
	publisher?: number
	/** The internal ID of the {@link Collection}. */
	collection?: number
	/** The internal ID of the {@link BookType}. */
	bookType?: number
	/** The internal ID of the {@link Binding}. */
	binding?: number
	/** The internal ID of the {@link Tag}. */
	tag?: number
	/** The internal ID of the {@link Reader} that has this {@link Album} as favourite. */
	readerIdFavourite?: number
	/** The internal ID of the {@link Reader} that has this {@link Album} in their reading list. */
	readerIdReadingList?: number
}

interface DerivativeFilter extends AbstractModelFilter {
	/** The internal ID of the {@link Series}. */
	series?: number
	/** The internal ID of the {@link Album}. */
	album?: number
	/** The internal ID of the {@link org.demyo.model.Author artist}. */
	artist?: number
	/** The internal ID of the {@link DerivativeType}. */
	type?: number
	/** The internal ID of the {@link DerivativeSource}. */
	source?: number
}

/** The types of model that support deferred links. */
type DeferrableLinkModel = 'album' | 'author' | 'binding' | 'bookType' | 'collection' | 'derivative'
		| 'derivativeSource' | 'derivativeType' | 'publisher' | 'series' | 'universe'
