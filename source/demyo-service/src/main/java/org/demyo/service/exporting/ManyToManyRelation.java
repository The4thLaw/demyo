package org.demyo.service.exporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.demyo.common.exception.DemyoErrorCode;
import org.demyo.common.exception.DemyoRuntimeException;

/**
 * Representation of an arbitrary Many-to-Many relation.
 */
class ManyToManyRelation {
	private final String relationTag;
	private final String entryTag;
	private final Map<Number, List<Number>> entries;

	/**
	 * Constructor.
	 * 
	 * @param relationtag The name of the group of relations in the XML output.
	 * @param entryTag The name of a single relation in the XML output.
	 * @param primaryKey The name of the column containing the identifier of the model responsible for the relation.
	 * @param secondaryKey The name of the column containing the identifier of the child model.
	 * @param relations The relations, as a raw list of records.
	 */
	public ManyToManyRelation(String relationtag, String entryTag, String primaryKey, String secondaryKey,
			List<Map<String, Object>> relations) {
		this.relationTag = relationtag;
		this.entryTag = entryTag;
		entries = new HashMap<>();

		for (Map<String, Object> relation : relations) {
			Number primaryValue = (Number) relation.get(primaryKey);
			Number secondaryValue = (Number) relation.get(secondaryKey);
			if (primaryValue == null) {
				throw new DemyoRuntimeException(DemyoErrorCode.EXPORT_DB_CONSISTENCY_ERROR,
						"null primary value for key " + primaryKey + " in relation " + relation.toString());
			}
			if (secondaryValue == null) {
				throw new DemyoRuntimeException(DemyoErrorCode.EXPORT_DB_CONSISTENCY_ERROR,
						"null secondary value for key " + secondaryKey + " in relation " + relation.toString());
			}
			List<Number> secondaryValues = entries.computeIfAbsent(primaryValue, n -> new ArrayList<>());
			secondaryValues.add(secondaryValue);
		}
	}

	/**
	 * Writes a specific set of relation to an open XML stream.
	 * 
	 * @param primaryValue The identifier of the parent model.
	 * @param writer The XML stream to write to.
	 * @throws XMLStreamException in case of error while writing to the stream.
	 */
	public void writeRelationToStream(Number primaryValue, XMLStreamWriter writer) throws XMLStreamException {
		if (entries.isEmpty()) {
			// No data at all
			return;
		}
		List<Number> secondaryValues = entries.get(primaryValue);
		if (secondaryValues == null || secondaryValues.isEmpty()) {
			// No associations for this primary key
			return;
		}
		writer.writeStartElement(relationTag);
		for (Number sec : secondaryValues) {
			writer.writeEmptyElement(entryTag);
			writer.writeAttribute("ref", sec.toString());
		}
		writer.writeEndElement();
	}

	/**
	 * Checks if the current map contains relations for a specific parent.
	 * 
	 * @param primaryValue The identifier of the parent model.
	 * @return <code>true</code> if there is at least one relation. <code>false</code> otherwise.
	 */
	public boolean hasRelations(Number primaryValue) {
		List<Number> secondaryValues = entries.get(primaryValue);
		return secondaryValues != null && !secondaryValues.isEmpty();
	}
}
