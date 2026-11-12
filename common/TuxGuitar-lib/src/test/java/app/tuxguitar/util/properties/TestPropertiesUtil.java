package app.tuxguitar.util.properties;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Pins how TGPropertiesUtil reads typed values from properties: the value is trimmed; a missing property, a value
 * that is not a number, or a failing store gives the default (0, false, null or an empty array when none is given).
 * A float array is separated by ";", a blank value in it reads as 0, trailing separators are ignored.
 */
public class TestPropertiesUtil {

	private static final float[] DEFAULT = new float[] {9f};

	private final MapProperties properties = new MapProperties();

	private final Map<String, String> values = this.properties.values;

	private float[] read(String value) {
		this.values.put("key", value);
		return TGPropertiesUtil.getFloatArrayValue(this.properties, "key", DEFAULT);
	}

	// every typed value of the key, read with a default of 7 (or "d", or true)
	private String readAll(TGProperties properties, String key) {
		return TGPropertiesUtil.getStringValue(properties, key, "d")
			+ " " + TGPropertiesUtil.getIntegerValue(properties, key, 7)
			+ " " + TGPropertiesUtil.getFloatValue(properties, key, 7f)
			+ " " + TGPropertiesUtil.getDoubleValue(properties, key, 7.0)
			+ " " + TGPropertiesUtil.getBooleanValue(properties, key, true);
	}

	@Test
	public void readsTypedValues() {
		this.values.put("number", " 42 ");
		this.values.put("true", " true ");
		this.values.put("word", " x ");

		assertEquals("42 42 42.0 42.0 false", readAll(this.properties, "number"));
		assertEquals("true 7 7.0 7.0 true", readAll(this.properties, "true"));
		assertEquals("x 7 7.0 7.0 false", readAll(this.properties, "word"));
		assertEquals("d 7 7.0 7.0 true", readAll(this.properties, "missing"));
		assertEquals("d 7 7.0 7.0 true", readAll(null, "number"));
	}

	@Test
	public void readsTypedValuesWithoutDefault() {
		assertEquals("null 0 0.0 0.0 false", TGPropertiesUtil.getStringValue(this.properties, "missing")
			+ " " + TGPropertiesUtil.getIntegerValue(this.properties, "missing")
			+ " " + TGPropertiesUtil.getFloatValue(this.properties, "missing")
			+ " " + TGPropertiesUtil.getDoubleValue(this.properties, "missing")
			+ " " + TGPropertiesUtil.getBooleanValue(this.properties, "missing"));
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
	public void writesValuesAsText() {
		TGPropertiesUtil.setValue(this.properties, "text", "v");
		TGPropertiesUtil.setValue(this.properties, "null", (String) null);
		TGPropertiesUtil.setValue(this.properties, "int", 5);
		TGPropertiesUtil.setValue(this.properties, "float", 1.5f);
		TGPropertiesUtil.setValue(this.properties, "double", 2.5);
		TGPropertiesUtil.setValue(this.properties, "boolean", true);

		assertEquals("v,,5,1.5,2.5,true", String.join(",", this.values.get("text"), this.values.get("null"),
			this.values.get("int"), this.values.get("float"), this.values.get("double"), this.values.get("boolean")));
	}

	@Test
	public void readsBackWhatWasWritten() {
		TGPropertiesUtil.setValue(this.properties, "key", new float[] {0.5f, -1f, 2f});
		assertArrayEquals(new float[] {0.5f, -1f, 2f},
			TGPropertiesUtil.getFloatArrayValue(this.properties, "key", DEFAULT));
	}
}
