package org.demyo.common.function;

import org.demyo.common.exception.DemyoException;

/**
 * This functional interface is essentially a {@link Runnable} that can throw {@link DemyoException}s.
 * */
@FunctionalInterface
public interface CheckedRunnable {
	/**
	 * Invokes a behaviour that takes no argument, returns no value and may throw an exception.
	 * @throws DemyoException In case of processing issue.
	 */
	void run() throws DemyoException;
}
