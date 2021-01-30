package org.demyo.service.impl;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

import org.demyo.dao.config.DaoConfig;
import org.demyo.test.AbstractPersistenceTest;
import org.demyo.test.config.TestConfiguration;

/**
 * Base class for Spring Data Repository tests.
 */
@ContextConfiguration(classes = { DaoConfig.class, TestConfiguration.class })
@TestExecutionListeners(
{ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		TransactionalTestExecutionListener.class })
public abstract class AbstractServiceTest extends AbstractPersistenceTest {

}
