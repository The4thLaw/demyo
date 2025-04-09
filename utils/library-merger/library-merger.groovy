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

void copySimple(XmlParser destinationParser, NodeList sourceList, NodeList destList, Node copyTarget,
        Map idMapping, String type, Closure nodeProcessor = (n) -> { }) {
    println '\n----------\n'
    int newId = sourceList.collect { it['@id'] as int }.max() + 1
    int added = 0
    println "New entries of type ${type} will start at ID ${newId}"

    sourceList.each {
        source -> {
            Map attribs = source.attributes()
            idMapping[attribs.id] = newId
            attribs.id = newId
            Node created = destinationParser.createNode(copyTarget, new QName(type), source.attributes())
            nodeProcessor(created)
            newId++
            added++
        }
    }

    println "Added ${added} ${type}s"
    println '\n----------'
}

void mergeSimple(XmlParser destinationParser, NodeList sourceList, NodeList destList, Node mergeTarget,
        Closure nodeMatcher, Map idMapping, String type, Closure nameExtractor,
        Closure nodeProcessor = (n) -> { }, Closure attributeProcessor = (n, a) -> { }) {
    println '\n----------\n'
    int newId = sourceList.collect { it['@id'] as int }.max() + 1
    int added = 0
    println "New entries of type ${type} will start at ID ${newId}\n"

    sourceList.each {
        source -> {
            Node destination = destList.find{dest -> nodeMatcher(source, dest)}
            if (destination == null) {
                Map attribs = source.attributes()
                idMapping[attribs.id] = newId
                attribs.id = newId
                Node created = destinationParser.createNode(mergeTarget, new QName(type), source.attributes())
                nodeProcessor(created)
                newId++
                added++
                println "Adding new ${type} ${nameExtractor(source)}"
            } else {
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
    println '\n----------'
}

void remap(Node node, String attrib, Map idMapping) {
    node[attrib] = idMapping[node[attrib]]
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
    { s, d -> s['@name'] == d['@name'] },
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
int maxInitialSeriesId = destination['series-list'].series.collect { it['@id'] as int }.max() + 1
println "Max series ID is ${maxInitialSeriesId}"
mergeSimple(destinationParser,
    // TODO: series-list
    source.series.series,
    destination['series-list'].series,
    destination['series-list'][0],
    { s, d -> s['@name'] == d['@name'] },
    seriesIdMapping,
    'series',
    { p -> p['@name'] })
// TODO: process related series afterwards

// TODO: albums
// TODO: album_prices
// TODO: derivative_types
// TODO: sources
// TODO: derivatives
// TODO: derivative_prices

String asText = XmlUtil.serialize(destination)
new File(args[2]).text = asText
