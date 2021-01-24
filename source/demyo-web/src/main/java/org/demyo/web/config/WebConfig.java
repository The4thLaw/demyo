package org.demyo.web.config;

import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

// TODO: When migrating to Spring 5, switch the XML config to the annotation-based equivalent
@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);

	// See https://stackoverflow.com/a/54412744
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		LOGGER.debug("Configuring Jackson...");
		for (HttpMessageConverter<?> converter : converters) {
			if (converter instanceof AbstractJackson2HttpMessageConverter) {
				ObjectMapper mapper = ((AbstractJackson2HttpMessageConverter) converter).getObjectMapper();

				// Add Hibernate 5 module
				Hibernate5Module module = new Hibernate5Module();
				module.disable(Hibernate5Module.Feature.USE_TRANSIENT_ANNOTATION);
				mapper.registerModule(module);
				mapper.setSerializationInclusion(Include.NON_EMPTY);

				// Configure the date format
				mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

				LOGGER.debug("... successful");
			}
		}
	}
}
