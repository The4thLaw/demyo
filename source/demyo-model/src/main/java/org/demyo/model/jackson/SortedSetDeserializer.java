package org.demyo.model.jackson;

import java.io.IOException;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.demyo.model.Image;
import org.demyo.model.util.IdentifyingNameComparator;

public class SortedSetDeserializer extends JsonDeserializer<SortedSet<Image>> {
	
	@Override
	public SortedSet<Image> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		TreeSet<Image> set = new TreeSet<>(new IdentifyingNameComparator());
		if (JsonToken.START_ARRAY != p.currentToken()) {
			throw new JsonParseException(p, "LOLOLOL", p.getCurrentLocation());
		}
		p.nextToken();
		Iterator<Image> i = p.getCodec().readValues(p, Image.class);
		while (i.hasNext()) {
			set.add(i.next());
		}
		return set;
	}
}