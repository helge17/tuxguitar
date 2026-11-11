package app.tuxguitar.util.properties;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Pins how a float array is read from a property (TGPropertiesUtil.getFloatArrayValue): values separated by ";",
 * each trimmed, a blank value reads as 0, trailing separators are ignored. A missing property, a value that is not
 * a number, or a failing store gives the default.
 */
public class TestFloatArrayProperty {

	private static final float[] DEFAULT = new float[] {9f};

	private final Map<String, String> values = new HashMap<String, String>();

	private final TGProperties properties = new TGProperties() {
		public String getValue(String key) {
			return values.get(key);
		}

		public void setValue(String key, String value) {
			values.put(key, value);
		}

		public void remove(String key) {
			values.remove(key);
		}

		public void clear() {
			values.clear();
		}

		public void update(Properties newProperties) {
			throw new UnsupportedOperationException();
		}

		public Set<String> getStringKeys() {
			return values.keySet();
		}
	};

	private float[] read(String value) {
		this.values.put("key", value);
		return TGPropertiesUtil.getFloatArrayValue(this.properties, "key", DEFAULT);
	}

	@Test
	public void readsSeparatedTrimmedValues() {
		assertArrayEquals(new float[] {1f, 2.5f, 3f}, read("1; 2.5 ; 3"));
		assertArrayEquals(new float[] {-4f}, read("-4"));
	}

	@Test
	public void readsABlankValueAsZero() {
		assertArrayEquals(new float[] {1f, 0f, 3f}, read("1; ;3"));
		assertArrayEquals(new float[] {0f}, read(""));
	}

	@Test
	public void ignoresTrailingSeparators() {
		assertArrayEquals(new float[] {1f, 2f}, read("1;2;;"));
	}

	@Test
	public void givesTheDefaultForAMissingOrInvalidValue() {
		assertSame(DEFAULT, read(null));
		assertSame(DEFAULT, read("1;x"));
		assertSame(DEFAULT, TGPropertiesUtil.getFloatArrayValue(null, "key", DEFAULT));
	}

	@Test
	public void defaultsToAnEmptyArray() {
		assertArrayEquals(new float[0], TGPropertiesUtil.getFloatArrayValue(this.properties, "missing"));
	}

	@Test
	public void readsBackWhatWasWritten() {
		TGPropertiesUtil.setValue(this.properties, "key", new float[] {0.5f, -1f, 2f});
		assertArrayEquals(new float[] {0.5f, -1f, 2f},
			TGPropertiesUtil.getFloatArrayValue(this.properties, "key", DEFAULT));
	}
}
