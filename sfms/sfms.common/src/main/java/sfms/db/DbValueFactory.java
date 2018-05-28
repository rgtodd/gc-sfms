package sfms.db;

import java.time.Instant;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.DoubleValue;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyValue;
import com.google.cloud.datastore.LongValue;
import com.google.cloud.datastore.NullValue;
import com.google.cloud.datastore.StringValue;
import com.google.cloud.datastore.TimestampValue;
import com.google.cloud.datastore.Value;

/**
 * Factory class for creating {@link Value} objects.
 *
 */
public class DbValueFactory {

	private DbValueFactory() {

	}

	public static Value<?> asValue(String value) {
		if (value == null) {
			return NullValue.of();
		}
		return StringValue.of(value);
	}

	public static Value<?> asUnindexedValue(String value) {
		if (value == null) {
			return NullValue.newBuilder().setExcludeFromIndexes(true).build();
		}
		return StringValue.newBuilder(value).setExcludeFromIndexes(true).build();
	}

	public static Value<?> asValue(Double value) {
		if (value == null) {
			return NullValue.of();
		}
		return DoubleValue.of(value);
	}

	public static Value<?> asUnindexedValue(Double value) {
		if (value == null) {
			return NullValue.newBuilder().setExcludeFromIndexes(true).build();
		}
		return DoubleValue.newBuilder(value).setExcludeFromIndexes(true).build();
	}

	public static Value<?> asValue(Long value) {
		if (value == null) {
			return NullValue.of();
		}
		return LongValue.of(value);
	}

	public static Value<?> asUnindexedValue(Long value) {
		if (value == null) {
			return NullValue.newBuilder().setExcludeFromIndexes(true).build();
		}
		return LongValue.newBuilder(value).setExcludeFromIndexes(true).build();
	}

	public static Value<?> asValue(Timestamp value) {
		if (value == null) {
			return NullValue.of();
		}
		return TimestampValue.of(value);
	}

	public static Value<?> asValue(Instant value) {
		if (value == null) {
			return NullValue.of();
		}
		return TimestampValue.of(Timestamp.ofTimeSecondsAndNanos(value.getEpochSecond(),
				value.getNano()));
	}

	public static Value<?> asValue(Key value) {
		if (value == null) {
			return NullValue.of();
		}
		return KeyValue.of(value);
	}

	public static Value<?> asUnindexedValue(Key value) {
		if (value == null) {
			return NullValue.newBuilder().setExcludeFromIndexes(true).build();
		}
		return KeyValue.newBuilder(value).setExcludeFromIndexes(true).build();
	}
}
