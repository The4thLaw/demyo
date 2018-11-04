package org.demyo.service.impl;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;

import org.demyo.test.AbstractPersistenceTest;

/**
 * Base class for Spring Data Repository tests.
 */
@ContextConfiguration({ "classpath:org/demyo/test/spring/unit-test-scope-adapter.xml",
		"classpath:org/demyo/dao/demyo-dao-context.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		TransactionalTestExecutionListener.class })
public abstract class AbstractServiceTest extends AbstractPersistenceTest {

}
