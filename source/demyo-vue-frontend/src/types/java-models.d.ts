/* tslint:disable */
/* eslint-disable */

interface AbstractBasicLegalEntity extends AbstractNamedModel {
    history?: string;
    website?: string;
}

interface AbstractLegalEntity extends AbstractBasicLegalEntity {
    feed?: string;
    logo?: Image;
}

interface AbstractModel extends IModel {
}

interface AbstractNamedModel extends AbstractModel {
    name: string;
}

interface AbstractPrice<P, M> {
    date: Date;
    price: number;
}

interface AbstractPricedModel<P, M> extends AbstractModel {
    acquisitionDate: Date;
    priceList: P[];
    prices: P[];
    purchasePrice: number;
}

interface Album extends AbstractPricedModel<AlbumPrice, Album> {
    artists: Author[];
    baseNameForImages: string;
    binding: Binding;
    bookType: BookType;
    collection: Collection;
    colorists: Author[];
    comment: string;
    cover: Image;
    coverArtists: Author[];
    currentEditionDate: Date;
    cycle: number;
    firstEditionDate: Date;
    height: number;
    images: Image[];
    inkers: Author[];
    isbn: string;
    location: string;
    markedAsFirstEdition: boolean;
    number: number;
    numberSuffix: string;
    originalTitle: string;
    pages: number;
    priceList: AlbumPrice[];
    prices: AlbumPrice[];
    pricesRaw: AlbumPrice[];
    printingDate: Date;
    publisher: Publisher;
    readersFavourites: Reader[];
    readersReadingList: Reader[];
    series: Series;
    summary: string;
    tags: Tag[];
    title: string;
    translators: Author[];
    universe: Universe;
    width: number;
    wishlist: boolean;
    writers: Author[];
}

interface AlbumPrice extends AbstractPrice<AlbumPrice, Album> {
    album: Album;
    model: Album;
}

interface ApplicationConfiguration {
    currency: string;
    language: string;
    pageSizeForCards: number;
    pageSizeForImages: number;
    pageSizeForText: number;
    subItemsInCardIndex: number;
}

interface Author extends AbstractModel {
    biography: string;
    birthDate: Date;
    country: string;
    deathDate: Date;
    firstName: string;
    fullName: string;
    name: string;
    nameWithPseudonym: string;
    nickname: string;
    portrait: Image;
    pseudonymOf: Author;
    pseudonyms: Author[];
    website: string;
}

interface AuthorAlbums {
    albumPseudonyms: Record<string, number>;
    albums: Album[];
    asArtist: number[];
    asColorist: number[];
    asCoverArtist: number[];
    asInker: number[];
    asTranslator: number[];
    asWriter: number[];
    pseudonyms: Record<string, Author>;
}

interface Binding extends AbstractNamedModel {
}

interface BookType extends AbstractNamedModel {
    description: string;
    fieldConfig: string;
    labelType: TranslationLabelType;
    structuredFieldConfig: ModelField[];
}

interface Collection extends AbstractLegalEntity {
    publisher: Publisher;
}

interface ConfigurationEntry extends AbstractModel {
    key: string;
    reader: Reader;
    value: string;
}

interface Derivative extends AbstractPricedModel<DerivativePrice, Derivative> {
    album: Album;
    artist: Author;
    authorsCopy: boolean;
    baseNameForImages: string;
    colours: number;
    depth: number;
    description: string;
    height: number;
    images: Image[];
    mainImage: Image;
    number: number;
    priceList: DerivativePrice[];
    prices: DerivativePrice[];
    pricesRaw: DerivativePrice[];
    restrictedSale: boolean;
    series: Series;
    signed: boolean;
    source: DerivativeSource;
    total: number;
    type: DerivativeType;
    width: number;
}

interface DerivativePrice extends AbstractPrice<DerivativePrice, Derivative> {
    derivative: Derivative;
    model: Derivative;
}

interface DerivativeSource extends AbstractBasicLegalEntity {
    active: boolean;
    address: string;
    email: string;
    owner: string;
    phoneNumber: string;
}

interface DerivativeType extends AbstractNamedModel {
}

interface IModel {
    id: number;
    identifyingName: string;
}

interface Image extends AbstractModel {
    albumCovers?: Album[];
    albumOtherImages?: Album[];
    authors?: Author[];
    collections?: Collection[];
    derivatives?: Derivative[];
    description?: string;
    publishers?: Publisher[];
    url: string;
    userFileName: string;
}

interface MetaSeries {
    actualSeries: boolean;
    album: Album;
    albums: Album[];
    series: Series;
    title: string;
}

interface Publisher extends AbstractLegalEntity {
    collections: Collection[];
}

interface Reader extends AbstractNamedModel {
    colour: string;
    configuration: ApplicationConfiguration;
    configurationEntries: ConfigurationEntry[];
    favouriteAlbums: Album[];
    favouriteSeries: Series[];
    readingList: Album[];
}

interface ReaderLists {
    favouriteAlbums: number[];
    favouriteSeries: number[];
    readingList: number[];
}

interface Series extends AbstractNamedModel {
    albumIds: number[];
    comment: string;
    completed: boolean;
    location: string;
    originalName: string;
    summary: string;
    universe: Universe;
    website: string;
}

interface Tag extends AbstractNamedModel {
    bgColour: string;
    description: string;
    fgColour: string;
    usageCount: number;
}

interface Universe extends AbstractNamedModel {
    albums: Album[];
    description: string;
    images: Image[];
    logo: Image;
    series: Series[];
    website: string;
}

type ModelField = "ALBUM_ARTIST" | "ALBUM_COLORIST" | "ALBUM_INKER" | "ALBUM_TRANSLATOR" | "ALBUM_COVER_ARTIST";

type TranslationLabelType = "GRAPHIC_NOVEL" | "NOVEL";
