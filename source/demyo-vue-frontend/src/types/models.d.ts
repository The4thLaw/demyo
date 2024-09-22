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
