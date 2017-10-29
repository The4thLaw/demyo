package org.demyo.service.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import org.demyo.model.Reader;
import org.demyo.service.IReaderContext;

/**
 * Implements the contract defined by {@link IReaderContext}.
 */
@Component
@Scope(scopeName = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ReaderContext implements IReaderContext {

	private Reader reader;

	@Override
	public Reader getCurrentReader() {
		return reader;
	}

	@Override
	public void setCurrentReader(Reader r) {
		if (r == null) {
			throw new IllegalArgumentException("null reader");
		}
		reader = r;
	}

	@Override
	public void clearCurrentReader() {
		reader = null;
	}

}
