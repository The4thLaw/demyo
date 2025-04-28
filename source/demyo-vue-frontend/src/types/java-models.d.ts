/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2025-04-28 15:48:08.

interface AuthorAlbums {
    asArtist: number[];
    asColorist: number[];
    asInker: number[];
    asTranslator: number[];
    asWriter: number[];
    albums: Album[];
}

interface MetaSeries {
    series: Series;
    album: Album;
    albums: Album[];
    title: string;
    actualSeries: boolean;
}

interface ReaderLists {
    favouriteSeries: number[];
    favouriteAlbums: number[];
    readingList: number[];
}

interface Album extends AbstractPricedModel<AlbumPrice, Album> {
    readersFavourites: Reader[];
    priceList: AlbumPrice[];
    readersReadingList: Reader[];
    bookType: BookType;
    series: Series;
    cycle: number;
    number: number;
    numberSuffix: string;
    title: string;
    originalTitle: string;
    publisher: Publisher;
    collection: Collection;
    firstEditionDate: Date;
    currentEditionDate: Date;
    printingDate: Date;
    markedAsFirstEdition: boolean;
    isbn: string;
    prices: AlbumPrice[];
    wishlist: boolean;
    location: string;
    binding: Binding;
    height: number;
    width: number;
    pages: number;
    cover: Image;
    summary: string;
    comment: string;
    tags: Tag[];
    writers: Author[];
    artists: Author[];
    colorists: Author[];
    inkers: Author[];
    translators: Author[];
    images: Image[];
    baseNameForImages: string;
    aggregatedLocation: string;
    pricesRaw: AlbumPrice[];
}

interface Series extends AbstractNamedModel {
    originalName: string;
    summary: string;
    comment: string;
    website: string;
    completed: boolean;
    location: string;
    relatedSeries: Series[];
    albumIds: number[];
}

interface Reader extends AbstractNamedModel {
    colour: string;
    configurationEntries: ConfigurationEntry[];
    configuration: ApplicationConfiguration;
    favouriteSeries: Series[];
    favouriteAlbums: Album[];
    readingList: Album[];
}

interface AlbumPrice extends AbstractPrice<AlbumPrice, Album> {
    album: Album;
    model: Album;
}

interface BookType extends AbstractNamedModel {
    labelType: TranslationLabelType;
    description: string;
    structuredFieldConfig: ModelField[];
    fieldConfig: string;
}

interface Publisher extends AbstractLegalEntity {
    collections: Collection[];
}

interface Collection extends AbstractLegalEntity {
    publisher: Publisher;
}

interface Binding extends AbstractNamedModel {
}

interface Image extends AbstractModel {
    url: string;
    description?: string;
    albumCovers?: Album[];
    albumOtherImages?: Album[];
    authors?: Author[];
    collections?: Collection[];
    derivatives?: Derivative[];
    publishers?: Publisher[];
    userFileName: string;
}

interface Tag extends AbstractNamedModel {
    fgColour: string;
    bgColour: string;
    description: string;
    usageCount: number;
}

interface Author extends AbstractModel {
    name: string;
    firstName: string;
    nickname: string;
    biography: string;
    portrait: Image;
    website: string;
    birthDate: Date;
    deathDate: Date;
    fullName: string;
}

interface AbstractNamedModel extends AbstractModel {
    name: string;
}

interface ConfigurationEntry extends AbstractModel {
    key: string;
    value: string;
    reader: Reader;
}

interface ApplicationConfiguration {
    language: string;
    currency: string;
    pageSizeForText: number;
    pageSizeForImages: number;
    pageSizeForCards: number;
    subItemsInCardIndex: number;
}

interface AbstractLegalEntity extends AbstractBasicLegalEntity {
    feed?: string;
    logo?: Image;
}

interface Derivative extends AbstractPricedModel<DerivativePrice, Derivative> {
    priceList: DerivativePrice[];
    series: Series;
    album: Album;
    artist: Author;
    type: DerivativeType;
    colours: number;
    source: DerivativeSource;
    number: number;
    total: number;
    signed: boolean;
    authorsCopy: boolean;
    restrictedSale: boolean;
    description: string;
    height: number;
    width: number;
    depth: number;
    prices: DerivativePrice[];
    images: Image[];
    mainImage: Image;
    baseNameForImages: string;
    pricesRaw: DerivativePrice[];
}

interface AbstractModel extends IModel {
}

interface AbstractPricedModel<P, M> extends AbstractModel {
    acquisitionDate: Date;
    purchasePrice: number;
    priceList: P[];
    prices: P[];
}

interface AbstractPrice<P, M> {
    date: Date;
    price: number;
}

interface AbstractBasicLegalEntity extends AbstractNamedModel {
    website?: string;
    history?: string;
}

interface DerivativePrice extends AbstractPrice<DerivativePrice, Derivative> {
    derivative: Derivative;
    model: Derivative;
}

interface DerivativeType extends AbstractNamedModel {
}

interface DerivativeSource extends AbstractBasicLegalEntity {
    owner: string;
    email: string;
    address: string;
    phoneNumber: string;
}

interface IModel {
    id: number;
    identifyingName: string;
}

type TranslationLabelType = "COMIC_ISSUE" | "COMIC_VOLUME" | "GRAPHIC_NOVEL" | "NOVEL";

type ModelField = "ALBUM_COLORIST" | "ALBUM_INKER" | "ALBUM_TRANSLATOR";
