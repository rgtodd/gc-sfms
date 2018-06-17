package sfms.web.application;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.standard.TemporalAccessorParser;
import org.springframework.format.datetime.standard.TemporalAccessorPrinter;

public final class SfmsDateFormatAnnotationFormatterFactory implements AnnotationFormatterFactory<DateTimeFormat> {

	@Override
	public Set<Class<?>> getFieldTypes() {
		return new HashSet<Class<?>>(Arrays.asList(new Class<?>[] {
				LocalDateTime.class,
				ZonedDateTime.class }));
	}

	@Override
	public Printer<?> getPrinter(DateTimeFormat annotation, Class<?> fieldType) {
		return new TemporalAccessorPrinter(configureFormatterFrom(annotation,
				fieldType));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Parser<?> getParser(DateTimeFormat annotation, Class<?> fieldType) {
		DateTimeFormatter formatter = configureFormatterFrom(annotation, fieldType);
		return new TemporalAccessorParser((Class<? extends TemporalAccessor>) fieldType, formatter);
	}

	private DateTimeFormatter configureFormatterFrom(DateTimeFormat annotation, Class<?> fieldType) {
		if (!annotation.pattern().isEmpty()) {
			if (annotation.pattern().equals("###")) {
				return DateTimeFormatter.ofPattern("M/d/yyyy");
			} else {
				return DateTimeFormatter.ofPattern(annotation.pattern());
			}
		} else {
			String style = annotation.style();
			if (style == "LOCAL") {
				return DateTimeFormatter.ISO_DATE;
			} else {
				return DateTimeFormatter.ISO_DATE;
			}
		}
	}
}