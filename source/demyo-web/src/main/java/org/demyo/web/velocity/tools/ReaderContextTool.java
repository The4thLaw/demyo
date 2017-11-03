package org.demyo.web.velocity.tools;

import org.apache.velocity.tools.Scope;
import org.apache.velocity.tools.config.ValidScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.demyo.service.IReaderService;

/**
 * Velocity tool for functionalities linked to the current reader.
 */
@ValidScope(value = Scope.APPLICATION)
public class ReaderContextTool {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReaderContextTool.class);
	private IReaderService service;

	/**
	 * Default constructor.
	 */
	public ReaderContextTool() {
		LOGGER.debug("Creating instance");
	}

	/**
	 * Sets the reader service to use.
	 * 
	 * @param service The reader service.
	 */
	public void setReaderService(IReaderService service) {
		LOGGER.debug("Setting the reader service");
		this.service = service;
	}
}
