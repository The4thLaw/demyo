// https://groovy-lang.org/processing-xml.html
// TODO: slf4j ?

import groovy.xml.XmlParser
import groovy.xml.XmlUtil

import java.io.File
import javax.xml.namespace.QName

XmlParser destinationParser = new XmlParser()
Node destination = destinationParser.parse(args[0])

XmlParser sourceParser = new XmlParser()
Node source = sourceParser.parse(args[1])

// All mappings
def publisherIdMapping = [:]
def collectionIdMapping = [:]
def tagIdMapping = [:]
def bindingIdMapping = [:]

void mergeSimple(XmlParser destinationParser, NodeList sourceList, NodeList destList, Node mergeTarget,
        Closure nodeMatcher, Map idMapping, String type, Closure nameExtractor,
        Closure nodeProcessor = (n) -> { }) {
    println '\n----------'
    int newId = sourceList.collect { it['@id'] as int }.max() + 1
    println "New entries of type ${type} will start at ID ${newId}"

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
                println "Adding new ${type} ${nameExtractor(source)}"
            } else {
                idMapping[source['@id']] = destination['@id']
                source.attributes().each {
                    key, value -> {
                        if (destination['@' + key] == null && value != null && !value.empty) {
                            destination['@' + key] = value
                            println "Adding missing attribute ${key} with value ${value} to ${type} ${nameExtractor(source)}"
                        }
                    }
                }
            }
        }
    }

    println '\n----------'
}

void remap(Node node, String attrib, Map idMapping) {
    node[attrib] = idMapping[node[attrib]]
}

// TODO: Just copy and renumber: Images

// Publishers
mergeSimple(destinationParser,
    source.publishers.publisher,
    destination.publishers.publisher,
    destination.publishers[0],
    { s, d -> s['@name'] == d['@name'] },
    publisherIdMapping,
    'publisher',
    { p -> p['@name'] }
    /* TODO: image mapping */)

// Collections
mergeSimple(destinationParser,
    source.collections.collection,
    destination.collections.collection,
    destination.collections[0],
    { s, d -> s['@name'] == d['@name'] },
    collectionIdMapping,
    'collection',
    { p -> p['@name'] },
    { n -> remap(n, '@publisher_id', publisherIdMapping)
    /* TODO: image mapping */ })

// Bindings
mergeSimple(destinationParser,
    source.bindings.binding,
    destination.bindings.binding,
    destination.bindings[0],
    { s, d -> s['@name'] == d['@name'] },
    bindingIdMapping,
    'binding',
    { p -> p['@name'] })

// TODO: authors

// Tags
mergeSimple(destinationParser,
    source.tags.tag,
    destination.tags.tag,
    destination.tags[0],
    { s, d -> s['@name'] == d['@name'] },
    tagIdMapping,
    'tag',
    { p -> p['@name'] })

// TODO: series-list/series
// TODO: albums
// TODO: album_prices
// TODO: derivative_types
// TODO: sources
// TODO: derivatives
// TODO: derivative_prices

String asText = XmlUtil.serialize(destination)
new File(args[2]).text = asText
