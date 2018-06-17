package sfms.web.application;

import java.util.Set;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.annotation.NumberFormat.Style;
import org.springframework.format.number.CurrencyStyleFormatter;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.format.number.PercentStyleFormatter;

public final class SfmsNumberFormatAnnotationFormatterFactory implements AnnotationFormatterFactory<NumberFormat> {

	@Override
	public Set<Class<?>> getFieldTypes() {
		return new HashSet<Class<?>>(Arrays.asList(new Class<?>[] {
				Short.class, Integer.class, Long.class, Float.class, Double.class, BigDecimal.class,
				BigInteger.class }));
	}

	@Override
	public Printer<Number> getPrinter(NumberFormat annotation, Class<?> fieldType) {
		return createFormatter(annotation, fieldType);
	}

	@Override
	public Parser<Number> getParser(NumberFormat annotation, Class<?> fieldType) {
		return createFormatter(annotation, fieldType);
	}

	private Formatter<Number> createFormatter(NumberFormat annotation, Class<?> fieldType) {
		if (!annotation.pattern().isEmpty()) {
			return new NumberStyleFormatter(annotation.pattern());
		} else {
			Style style = annotation.style();
			if (style == Style.PERCENT) {
				return new PercentStyleFormatter();
			} else if (style == Style.CURRENCY) {
				return new CurrencyStyleFormatter();
			} else {
				return new NumberStyleFormatter();
			}
		}
	}
}