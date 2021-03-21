package org.demyo.web.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * A Jackson module to map blank Strings to <code>null</code> when deserializing.
 * 
 * @see https://stackoverflow.com/a/53665706/109813
 */
public class NullStringModule extends SimpleModule {
	private static final long serialVersionUID = 5727822785826230276L;

	public NullStringModule() {
		addDeserializer(String.class, new NullStringDeserializer());
	}
}
