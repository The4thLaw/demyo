package org.demyo.common.config;

import ch.qos.logback.core.PropertyDefinerBase;
import ch.qos.logback.core.spi.PropertyDefiner;

/**
 * A logback {@link PropertyDefiner} that can extract data from the {@link SystemConfiguration}.
 */
public class SystemConfigurationPropertyDefiner extends PropertyDefinerBase {

	/** The name of the system configuration property to retrieve. */
	private String propertyName;

	@Override
	public String getPropertyValue() {
		if ("userDirectory".equals(propertyName)) {
			return SystemConfiguration.getInstance().getUserDirectory().toString();
		}

		addError("The \"propertyName\" property must be set to a known value (currently " + propertyName + ")");
		return null;
	}

	/**
	 * Gets the name of the system configuration property to retrieve.
	 *
	 * @return the name of the system configuration property to retrieve
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Sets the name of the system configuration property to retrieve.
	 *
	 * @param propertyName the new name of the system configuration property to retrieve
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

}
