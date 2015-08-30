package org.demyo.utils.web;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Static accessor for the Application Context.
 * 
 * @author $Author: xr $
 * @version $Revision: 1070 $
 */
@Component
public class SpringContext implements ApplicationContextAware {
	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext contextArg) throws BeansException {
		context = contextArg;
	}

	/**
	 * Gets the global application context.
	 * 
	 * @return the application context.
	 */
	public static ApplicationContext getContext() {
		return context;
	}
}
