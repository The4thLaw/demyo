package org.demyo.web.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

// TODO: When switching to Java 8 and Spring 5, drop the adapter and implement the interface:
// the interface has default methods
// TODO: When migrating to Spring 5, switch the XML config to the annotation-based equivalent
@EnableWebMvc
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);

	// See https://stackoverflow.com/a/54412744
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		LOGGER.error("Adding the Hibernate5Module to Jackson...");
		for (HttpMessageConverter<?> converter : converters) {
			LOGGER.error("Converter: {}", converter);
			LOGGER.error("Types: {}", converter.getSupportedMediaTypes());
			if (converter instanceof AbstractJackson2HttpMessageConverter) {
				ObjectMapper mapper = ((AbstractJackson2HttpMessageConverter) converter).getObjectMapper();
				mapper.registerModule(new Hibernate5Module());
				LOGGER.error("NON_EMPTY");
				mapper.setSerializationInclusion(Include.NON_EMPTY);
				LOGGER.error("... successful");
			}
		}
	}
}