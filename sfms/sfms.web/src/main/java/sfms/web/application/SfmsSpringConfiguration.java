package sfms.web.application;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import sfms.web.conversion.DateFormatter;
import sfms.web.conversion.LocalDateTimeFormatter;
import sfms.web.conversion.ZonedDateTimeFormatter;
import sfms.web.mock.MockSpaceData;

@Configuration
public class SfmsSpringConfiguration implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages");
		return messageSource;
	}

	// @Bean
	// public SfmsDateFormatAnnotationFormatterFactory
	// sfmsDateFormatAnnotationFormatterFactory() {
	// return new SfmsDateFormatAnnotationFormatterFactory();
	// }
	//
	// @Bean
	// public SfmsNumberFormatAnnotationFormatterFactory
	// sfmsNumberFormatAnnotationFormatterFactory() {
	// return new SfmsNumberFormatAnnotationFormatterFactory();
	// }

	@Bean
	public DateFormatter sfmsDateFormatter() {
		return new DateFormatter();
	}

	@Bean
	public LocalDateTimeFormatter sfmsLocalDateTimeFormatter() {
		return new LocalDateTimeFormatter();
	}

	@Bean
	public ZonedDateTimeFormatter sfmsZonedDateTimeFormatter() {
		return new ZonedDateTimeFormatter();
	}

	/* **************************************************************** */
	/* THYMELEAF-SPECIFIC ARTIFACTS */
	/* TemplateResolver <- TemplateEngine <- ViewResolver */
	/* **************************************************************** */

	@Bean
	public SpringResourceTemplateResolver templateResolver() {

		// SpringResourceTemplateResolver automatically integrates with Spring's own
		// resource resolution infrastructure, which is highly recommended.
		//
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(this.applicationContext);
		templateResolver.setPrefix("classpath:/templates/");
		templateResolver.setSuffix(".html");

		// HTML is the default value, added here for the sake of clarity.
		//
		templateResolver.setTemplateMode(TemplateMode.HTML);

		// Template cache is true by default. Set to false if you want
		// templates to be automatically updated when modified.
		//
		templateResolver.setCacheable(true);

		return templateResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine() {

		// SpringTemplateEngine automatically applies SpringStandardDialect and
		// enables Spring's own MessageSource message resolution mechanisms.
		//
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();

		// Add dialect to support Java 8 date/time classes.
		//
		templateEngine.addDialect(new Java8TimeDialect());

		// Add layout dialect. See http://www.thymeleaf.org/doc/articles/layouts.html.
		//
		templateEngine.addDialect(new LayoutDialect());

		templateEngine.setTemplateResolver(templateResolver());

		// Enabling the SpringEL compiler with Spring 4.2.4 or newer can
		// speed up execution in most scenarios, but might be incompatible
		// with specific cases when expressions in one template are reused
		// across different data types, so this flag is "false" by default
		// for safer backwards compatibility.
		//
		templateEngine.setEnableSpringELCompiler(true);

		return templateEngine;
	}

	@Bean
	public ThymeleafViewResolver viewResolver() {

		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine());
		return viewResolver;
	}

	@Bean
	public MockSpaceData mockSpaceData() {
		return new MockSpaceData();
	}

}