package org.demyo.web.jackson;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

/**
 * A Jackson deserializer to map blank Strings to <code>null</code>.
 * 
 * @see https://stackoverflow.com/a/53665706/109813
 */
class NullStringDeserializer extends StdDeserializer<String> {
	private static final long serialVersionUID = -6103391399673116611L;

	/**
	 * Creates the String dezerializer.
	 */
	public NullStringDeserializer() {
		super(String.class);
	}

	@Override
	public String deserialize(JsonParser parser, DeserializationContext context)
			throws IOException {
		String result = StringDeserializer.instance.deserialize(parser, context);
		if (StringUtils.isBlank(result)) {
			return null;
		}
		return result;
	}
}
