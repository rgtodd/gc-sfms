package sfms.web.conversion;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.Formatter;

public class ZonedDateTimeFormatter implements Formatter<ZonedDateTime> {

	@Autowired
	private MessageSource messageSource;

	@Override
	public ZonedDateTime parse(final String text, final Locale locale) throws ParseException {
		final DateTimeFormatter formatter = createFormatter(locale);
		return ZonedDateTime.parse(text, formatter);
	}

	@Override
	public String print(final ZonedDateTime object, final Locale locale) {
		final DateTimeFormatter formatter = createFormatter(locale);
		return formatter.format(object);
	}

	private DateTimeFormatter createFormatter(final Locale locale) {
		final String format = messageSource.getMessage("format.datetime", null, locale);
		return DateTimeFormatter.ofPattern(format);
	}

}