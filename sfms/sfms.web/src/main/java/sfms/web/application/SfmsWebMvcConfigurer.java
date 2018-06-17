package sfms.web.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import sfms.web.conversion.DateFormatter;
import sfms.web.conversion.LocalDateTimeFormatter;
import sfms.web.conversion.ZonedDateTimeFormatter;

@Configuration
public class SfmsWebMvcConfigurer implements WebMvcConfigurer {

	// @Autowired
	// private SfmsDateFormatAnnotationFormatterFactory
	// sfmsDateFormatAnnotationFormatterFactory;
	//
	// @Autowired
	// private SfmsNumberFormatAnnotationFormatterFactory
	// sfmsNumberFormatAnnotationFormatterFactory;

	@Autowired
	private DateFormatter sfmsDateFormatter;

	@Autowired
	private LocalDateTimeFormatter sfmsLocalDateTimeFormatter;

	@Autowired
	private ZonedDateTimeFormatter sfmsZonedDateTimeFormatter;

	/*
	 * Dispatcher configuration for serving static resources
	 */
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/images/**").addResourceLocations("/images/");
		registry.addResourceHandler("/css/**").addResourceLocations("/css/");
		registry.addResourceHandler("/js/**").addResourceLocations("/js/");
	}

	/*
	 * Add formatter for class {@link
	 * thymeleafexamples.stsm.business.entities.Variety} and {@link java.util.Date}
	 * in addition to the one registered by default
	 */
	@Override
	public void addFormatters(final FormatterRegistry registry) {
		registry.addFormatter(sfmsDateFormatter);
		registry.addFormatter(sfmsLocalDateTimeFormatter);
		registry.addFormatter(sfmsZonedDateTimeFormatter);
		// registry.addFormatterForFieldAnnotation(sfmsNumberFormatAnnotationFormatterFactory);
		// registry.addFormatterForFieldAnnotation(sfmsDateFormatAnnotationFormatterFactory);
	}

}