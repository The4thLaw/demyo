// https://groovy-lang.org/processing-xml.html
import groovy.xml.XmlParser
import groovy.xml.XmlUtil

import java.io.File
import javax.xml.namespace.QName

XmlParser destinationParser = new XmlParser()
Node destination = destinationParser.parse(args[0])

XmlParser sourceParser = new XmlParser()
Node source = sourceParser.parse(args[1])

// All mappings
def imageIdMapping = [:]
def publisherIdMapping = [:]
def collectionIdMapping = [:]
def bindingIdMapping = [:]
def authorIdMapping = [:]
def tagIdMapping = [:]
def seriesIdMapping = [:]
def albumIdMapping = [:]
def derivativeTypeIdMapping = [:]
def sourceIdMapping = [:]
def derivativeIdMapping = [:]

void copySimple(XmlParser destinationParser, NodeList sourceList, NodeList destList, Node copyTarget,
        Map idMapping, String type, Closure nodeProcessor = (s, c) -> { }) {
    println '\n----------\n'
    int newId = -1
    if (idMapping != null) {
        newId = destList.collect { it['@id'] as int }.max() + 1
        println "New entries of type ${type} will start at ID ${newId}"
    }
    int added = 0

    sourceList.each {
        source -> {
            Map attribs = source.attributes()
            if (idMapping != null) {
                idMapping[attribs.id] = newId
                attribs.id = newId
            }
            Node created = destinationParser.createNode(copyTarget, new QName(type), source.attributes())
            nodeProcessor(source, created)
            if (idMapping != null) {
                newId++
            }
            added++
        }
    }

    println "Added ${added} ${type}s"
}

void mergeSimple(XmlParser destinationParser, NodeList sourceList, NodeList destList, Node mergeTarget,
        Closure nodeMatcher, Map idMapping, String type, Closure nameExtractor,
        Closure nodeProcessor = (n) -> { }, Closure attributeProcessor = (n, a) -> { }) {
    println '\n----------\n'
    int newId = destList.collect { it['@id'] as int }.max() + 1
    int added = 0
    println "New entries of type ${type} will start at ID ${newId}\n"

    sourceList.each {
        source -> {
            Node destination = destList.find{dest -> nodeMatcher(source, dest)}
            if (destination == null) {
                // Clone to avoid changing the source document when mapping IDs
                Map attribs = source.attributes().clone()
                idMapping[attribs.id] = newId
                attribs.id = newId
                Node created = destinationParser.createNode(mergeTarget, new QName(type), attribs)
                nodeProcessor(created)
                newId++
                added++
                println "Adding new ${type} ${nameExtractor(source)}"
            } else {
                // println "Found a match: ${destination}"
                idMapping[source['@id']] = destination['@id']
                source.attributes().each {
                    key, value -> {
                        if (destination['@' + key] == null && value != null && !value.empty) {
                            destination['@' + key] = value
                            attributeProcessor(destination, '@' + key)
                            println "Adding missing attribute ${key} with value ${destination['@' + key]} to ${type} ${nameExtractor(source)}"
                        }
                    }
                }
            }
        }
    }

    println "\nAdded ${added} ${type}s"
}

void remap(Node node, String attrib, Map idMapping) {
    def value = idMapping[node[attrib]]
    if (value != null) {
        node[attrib] = value
    }
}

// Image
copySimple(destinationParser,
    source.images.image,
    destination.images.image,
    destination.images[0],
    imageIdMapping,
    'image')

// Publishers
mergeSimple(destinationParser,
    source.publishers.publisher,
    destination.publishers.publisher,
    destination.publishers[0],
    { s, d -> s['@name'] == d['@name'] },
    publisherIdMapping,
    'publisher',
    { p -> p['@name'] },
    { p -> remap(p, '@logo_id', imageIdMapping)},
    { p, a -> {
        if (a == '@logo_id') {
            remap(p, a, imageIdMapping)
        }
    }})

// Collections
mergeSimple(destinationParser,
    source.collections.collection,
    destination.collections.collection,
    destination.collections[0],
    { s, d -> {
            def mappedPublisherId = publisherIdMapping[s['@publisher_id']]
            return s['@name'] == d['@name'] && mappedPublisherId == d['@publisher_id']
        }
    },
    collectionIdMapping,
    'collection',
    { c -> c['@name'] },
    { c -> {
        remap(c, '@publisher_id', publisherIdMapping)
        remap(c, '@logo_id', imageIdMapping)
    }},
    { c, a -> {
        if (a == '@publisher_id') {
            remap(c, a, publisherIdMapping)
        }
        if (a == '@logo_id') {
            remap(c, a, imageIdMapping)
        }
    }})

// Bindings
mergeSimple(destinationParser,
    source.bindings.binding,
    destination.bindings.binding,
    destination.bindings[0],
    { s, d -> s['@name'] == d['@name'] },
    bindingIdMapping,
    'binding',
    { p -> p['@name'] })

// Authors
mergeSimple(destinationParser,
    source.authors.author,
    destination.authors.author,
    destination.authors[0],
    { s, d -> s['@name'] == d['@name'] && s['@fname'] == d['@fname']},
    authorIdMapping,
    'author',
    { a -> "${a['@fname']} ${a['@name']}" },
    { a -> remap(a, '@portrait_id', imageIdMapping)},
    { p, a -> {
        if (a == '@portrait_id') {
            remap(p, a, imageIdMapping)
        }
    }})

// Tags
mergeSimple(destinationParser,
    source.tags.tag,
    destination.tags.tag,
    destination.tags[0],
    { s, d -> s['@name'] == d['@name'] },
    tagIdMapping,
    'tag',
    { t -> t['@name'] })

// Series
mergeSimple(destinationParser,
    source['series-list'].series,
    destination['series-list'].series,
    destination['series-list'][0],
    { s, d -> s['@name'] == d['@name'] },
    seriesIdMapping,
    'series',
    { s -> s['@name'] })

// Related series
source['series-list'].series.each { s -> {
    def related = s['related_series-list']['related_series']
    related.each { r -> {
        def mappedSeriesFromId = seriesIdMapping[s['@id']]
        // Note that we must work on .children() to include the added nodes
        def mappedSeriesFrom = destination['series-list'][0].children().find { it['@id'] as int == mappedSeriesFromId }
        if (mappedSeriesFrom != null) {
            // Only work on added series, not existing ones
            def relParent = mappedSeriesFrom.children().find{it.name() as String == 'related_series-list'}
            if (relParent == null) {
                relParent = destinationParser.createNode(mappedSeriesFrom, new QName('related_series-list'), [:])
            }
            destinationParser.createNode(relParent, new QName('related_series'), [ref: seriesIdMapping[r['@ref']]])
        }
    }}
}}

// Albums
void remapNested(XmlParser destinationParser, Node original, Node created, String collectionName, String itemName, Map mapping) {
    if (original[collectionName][itemName].size() > 0) {
        Node parent = destinationParser.createNode(created, new QName(collectionName), [:])
        original[collectionName][itemName].each {
            destinationParser.createNode(parent, new QName(itemName), [ref: mapping[it['@ref']]])
        }
    }
}
copySimple(destinationParser,
    source.albums.album,
    destination.albums.album,
    destination.albums[0],
    albumIdMapping,
    'album',
    { original, created -> {
        // Simple attributes
        remap(created, '@series_id', seriesIdMapping)
        remap(created, '@publisher_id', publisherIdMapping)
        remap(created, '@collection_id', collectionIdMapping)
        remap(created, '@binding_id', bindingIdMapping)
        remap(created, '@cover_id', imageIdMapping)
        // Authors writers/writer, etc
        remapNested(destinationParser, original, created, 'writers', 'writer', authorIdMapping)
        remapNested(destinationParser, original, created, 'artists', 'artist', authorIdMapping)
        remapNested(destinationParser, original, created, 'colorists', 'colorist', authorIdMapping)
        remapNested(destinationParser, original, created, 'translators', 'translator', authorIdMapping)
        // Tags album-tags/album-tag
        remapNested(destinationParser, original, created, 'album-tags', 'album-tag', tagIdMapping)
        // Images album-images/album-image
        remapNested(destinationParser, original, created, 'album-images', 'album-image', imageIdMapping)
    }})

// Album prices
copySimple(destinationParser,
    source.album_prices.album_price,
    destination.album_prices.album_price,
    destination.album_prices[0],
    null,
    'album_price',
    { s, d -> {
        d['@album_id'] = albumIdMapping[s['@album_id']]
    }})

// Derivative types
mergeSimple(destinationParser,
    source.derivative_types.derivative_type,
    destination.derivative_types.derivative_type,
    destination.derivative_types[0],
    { s, d -> s['@name'] == d['@name'] },
    derivativeTypeIdMapping,
    'derivative_type',
    { dt -> dt['@name'] })

// Sources
mergeSimple(destinationParser,
    source.sources.source,
    destination.sources.source,
    destination.sources[0],
    { s, d -> s['@name'] == d['@name'] },
    sourceIdMapping,
    'source',
    { s -> s['@name'] })

// Derivatives
copySimple(destinationParser,
    source.derivatives.derivative,
    destination.derivatives.derivative,
    destination.derivatives[0],
    derivativeIdMapping,
    'derivative',
    { original, created -> {
        // Simple attributes
        remap(created, '@series_id', seriesIdMapping)
        remap(created, '@album_id', albumIdMapping)
        remap(created, '@artist_id', authorIdMapping)
        remap(created, '@derivative_type_id', derivativeTypeIdMapping)
        remap(created, '@source_id', sourceIdMapping)
        remapNested(destinationParser, original, created, 'album-tags', 'album-tag', tagIdMapping)
        // Images derivative-images/derivative-image
        remapNested(destinationParser, original, created, 'derivative-images', 'derivative-image', imageIdMapping)
    }})

// Derivative prices
copySimple(destinationParser,
    source.derivative_prices.derivative_price,
    destination.derivative_prices.derivative_price,
    destination.derivative_prices[0],
    null,
    'derivative_price',
    { s, d -> {
        d['@derivative_id'] = derivativeIdMapping[s['@derivative_id']]
    }})

String asText = XmlUtil.serialize(destination)
new File(args[2]).text = asText
