package org.demyo.web.config;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import org.demyo.common.desktop.DesktopCallbacks;
import org.demyo.dao.config.DaoConfig;
import org.demyo.service.i18n.BrowsableResourceBundleMessageSource;
import org.demyo.web.jackson.NullStringModule;

/**
 * Configuration for the Web layer.
 */
@Configuration
@ComponentScan("org.demyo")
@Import(
{ DaoConfig.class, WebSecurityConfig.class })
@EnableWebMvc
@EnableCaching
@EnableScheduling
public class WebConfig implements WebMvcConfigurer {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);

	public WebConfig() {
		LOGGER.info("Parsing configuration");
	}

	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/css/**").addResourceLocations("/css/", "classpath:/public/css/");
		registry.addResourceHandler("/fonts/**").addResourceLocations("/fonts/", "classpath:/public/fonts/");
		registry.addResourceHandler("/js/**").addResourceLocations("/js/", "classpath:/public/js/");
		registry.addResourceHandler("/icons/**").addResourceLocations("/icons/");
		registry.addResourceHandler("/favicon.ico").addResourceLocations("/favicon.ico");
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		LOGGER.debug("Configuring Jackson...");
		for (HttpMessageConverter<?> converter : converters) {
			if (converter instanceof AbstractJackson2HttpMessageConverter) {
				ObjectMapper mapper = ((AbstractJackson2HttpMessageConverter) converter).getObjectMapper();

				// See https://stackoverflow.com/a/54412744
				// Add Hibernate 5 module
				Hibernate5Module module = new Hibernate5Module();
				module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
				mapper.registerModule(module);

				// Filter out empty values when serializing
				mapper.setSerializationInclusion(Include.NON_EMPTY);

				// Filter out empty values when deserializing
				mapper.registerModule(new NullStringModule());

				// Configure the date format
				mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

				LOGGER.debug("... successful");
			}
		}
	}

	/*
	 * Cache management
	 * We can rely on a pure memory implementation for now, without advanced features:
	 *  - It's very fast
	 *  - It's easy to set up
	 *  - We don't really need persistence between application executions. At least not with the current use cases.
	 */
	@Bean
	public CacheManager cacheManager() {
		SimpleCacheManager manager = new SimpleCacheManager();
		manager.setCaches(Arrays.asList(
				// This cache is used to store lists of model (e.g. results of findAll).
				// It can be very useful in constrained environments like a Raspberry Pi
				new ConcurrentMapCache("ModelLists"),
				// Same rationale, but for reference data (e.g. translations)
				new ConcurrentMapCache("ReferenceData")));
		return manager;
	}

	@Bean({ "messageSource", "demyo.core.messageSource" })
	public MessageSource messageSource() {
		BrowsableResourceBundleMessageSource ms = new BrowsableResourceBundleMessageSource();
		ms.setBasenames("org/demyo/i18n/core",
				"org/demyo/i18n/desktop",
				"org/demyo/i18n/fields",
				"org/demyo/i18n/menu",
				"org/demyo/i18n/pages",
				"org/demyo/i18n/javascript",
				"org/demyo/i18n/special",
				"org/demyo/i18n/widgets");
		return ms;
	}

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		return new CommonsMultipartResolver();
	}

	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/jsp/");
		resolver.setSuffix(".jsp");
		return resolver;
	}

	@Bean
	public DesktopCallbacks desktopCallbacks() throws NamingException {
		JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
		bean.setJndiName("org.demyo.services.desktop");
		bean.afterPropertiesSet();
		return (DesktopCallbacks) bean.getObject();
	}
}
