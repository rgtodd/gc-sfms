package sfms.rest;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class Throttle {

	public static final int MAX_OPERATIONS_PER_HOUR = 1000;
	public static final int MAX_OPERATIONS_PER_MINUTE = 10;

	private ZonedDateTime m_hourDateTime = null;
	private ZonedDateTime m_minuteDateTime = null;

	private int m_hourCount = 0;
	private int m_minuteCount = 0;

	public boolean increment() {
		ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
		ZonedDateTime currentHourDateTime = now.truncatedTo(ChronoUnit.HOURS);
		ZonedDateTime currentMinuteDateTime = now.truncatedTo(ChronoUnit.MINUTES);

		// Reset hour bucket if it is expired.
		//
		if (m_hourDateTime == null || !m_hourDateTime.equals(currentHourDateTime)) {
			m_hourDateTime = currentHourDateTime;
			m_hourCount = 0;
		}

		// Reset minute bucket if it is expired.
		//
		if (m_minuteDateTime == null || !m_minuteDateTime.equals(currentMinuteDateTime)) {
			m_minuteDateTime = currentMinuteDateTime;
			m_minuteCount = 0;
		}

		if (m_hourCount < MAX_OPERATIONS_PER_HOUR && m_minuteCount < MAX_OPERATIONS_PER_MINUTE) {
			m_hourCount += 1;
			m_minuteCount += 1;

			return true;
		} else {

			return false;
		}
	}
}
