package org.demyo.model.jackson;

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.annotations.SortComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;

/**
 * A Jackson deserializer that is able to build {@link SortedSet}s of non-comparables by providing the relevant
 * comparators.
 * 
 * @param <T> The element type
 * @param <C> The comparator type
 */
public class SortedSetDeserializer<T, C extends Comparator<T>> extends JsonDeserializer<SortedSet<T>>
		implements ContextualDeserializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(SortedSetDeserializer.class);

	private Class<C> compClass;
	private Class<T> elementClass;
	private Comparator<T> compInstance;

	public SortedSetDeserializer() throws InstantiationException, IllegalAccessException {
		this(null, null);
	}

	SortedSetDeserializer(Class<C> compClass, Class<T> elementClass)
			throws InstantiationException, IllegalAccessException {
		this.compClass = compClass;
		this.elementClass = elementClass;
		this.compInstance = compClass == null ? null : compClass.newInstance();

		LOGGER.debug("Created a new SortedSetDeserializer({}, {})", compClass, elementClass);
	}

	@Override
	public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
			throws JsonMappingException {

		// Detect comparator
		SortComparator sc = property.getAnnotation(SortComparator.class);
		if (sc == null) {
			throw new JsonMappingException(ctxt.getParser(),
					"No SortComparator found for " + property.getFullName());
		}
		Class<? extends Comparator<?>> scCompClass = sc.value();

		// Detect
		JavaType type = property.getType();
		if (!(type instanceof CollectionType)) {
			throw new JsonMappingException(ctxt.getParser(),
					"Annotated type isn't a Collection, but a " + type);
		}
		CollectionType cType = (CollectionType) type;
		Class<?> elClass = cType.getContentType().getRawClass();

		// Check current configuration
		if (compClass == scCompClass && elementClass == elClass) {
			return this;
		}

		try {
			return createSerializer(scCompClass, elClass);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new JsonMappingException(ctxt.getParser(),
					"Failed to create a new SortedSetdeserializer instance", e);
		}
	}

	// I hate suppressing warning about types but we don't really have a choice here due to the context
	// At least it's limited to a single line (which is why this is extracted to a specific method)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static JsonDeserializer<?> createSerializer(Class<?> scCompClass, Class<?> elClass)
			throws InstantiationException, IllegalAccessException {
		return new SortedSetDeserializer(scCompClass, elClass);
	}

	@Override
	public SortedSet<T> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		Objects.requireNonNull(compInstance);
		Objects.requireNonNull(elementClass);

		if (JsonToken.START_ARRAY != p.currentToken()) {
			throw new JsonParseException(p,
					"Unexpected structure for the SortedSet deserialization (not an array but a "
							+ p.currentToken() + ")",
					p.getCurrentLocation());
		}
		p.nextToken();

		TreeSet<T> set = new TreeSet<>(compInstance);

		Iterator<T> i = p.getCodec().readValues(p, elementClass);
		while (i.hasNext()) {
			set.add(i.next());
		}

		return set;
	}
}
