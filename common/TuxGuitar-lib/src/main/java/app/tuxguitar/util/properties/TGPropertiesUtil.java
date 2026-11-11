package app.tuxguitar.util.properties;

public class TGPropertiesUtil {

	private static final String ARRAY_SEPARATOR = ";";

	public static String getStringValue(TGProperties properties, String key, String defaultValue) {
		try{
			String property = properties.getValue(key);
			return (property == null) ? defaultValue : property.trim();
		}catch(Throwable throwable){
			return defaultValue;
		}
	}

	public static String getStringValue(TGProperties properties, String key) {
		return getStringValue(properties, key, null);
	}

	public static int getIntegerValue(TGProperties properties, String key, int defaultValue) {
		try{
			String value = properties.getValue(key);
			return (value == null) ? defaultValue : Integer.parseInt(value.trim());
		}catch(Throwable throwable){
			return defaultValue;
		}
	}

	public static int getIntegerValue(TGProperties properties, String key) {
		return getIntegerValue(properties, key, 0);
	}

	public static float getFloatValue(TGProperties properties, String key, float defaultValue) {
		try{
			String value = properties.getValue(key);
			return (value == null) ? defaultValue : Float.parseFloat(value.trim());
		}catch(Throwable throwable){
			return defaultValue;
		}
	}

	public static float getFloatValue(TGProperties properties, String key) {
		return getFloatValue(properties, key, 0f);
	}

	public static double getDoubleValue(TGProperties properties, String key, double defaultValue) {
		try{
			String value = properties.getValue(key);
			return (value == null) ? defaultValue : Double.parseDouble(value.trim());
		}catch(Throwable throwable){
			return defaultValue;
		}
	}

	public static double getDoubleValue(TGProperties properties, String key) {
		return getDoubleValue(properties, key, 0.0);
	}

	public static boolean getBooleanValue(TGProperties properties, String key, boolean defaultValue) {
		try{
			String value = properties.getValue(key);
			return (value == null) ? defaultValue : Boolean.valueOf(value.trim()).booleanValue();
		}catch(Throwable throwable){
			return defaultValue;
		}
	}

	public static boolean getBooleanValue(TGProperties properties, String key) {
		return getBooleanValue(properties, key, false);
	}

	public static float[] getFloatArrayValue(TGProperties properties, String key, float[] defaultValue) {
		try{
			String value = properties.getValue(key);
			return (value == null) ? defaultValue : parseFloatArray(value);
		}catch(Throwable throwable){
			return defaultValue;
		}
	}

	private static float[] parseFloatArray(String value) {
		String[] stringValues = value.split(ARRAY_SEPARATOR);
		float[] floatValues = new float[stringValues.length];
		for(int i = 0; i < floatValues.length; i ++) {
			// a blank value stays 0
			if( stringValues[i].trim().length() > 0 ) {
				floatValues[i] = Float.parseFloat(stringValues[i].trim());
			}
		}
		return floatValues;
	}

	public static float[] getFloatArrayValue(TGProperties properties, String key) {
		return getFloatArrayValue(properties, key, new float[0]);
	}

	public static void setValue(TGProperties properties, String key, String value){
		properties.setValue(key, (value != null ? value : new String()) );
	}

	public static void setValue(TGProperties properties, String key, int value){
		properties.setValue(key,Integer.toString(value));
	}

	public static void setValue(TGProperties properties, String key, float value){
		properties.setValue(key,Float.toString(value));
	}

	public static void setValue(TGProperties properties, String key, double value){
		properties.setValue(key,Double.toString(value));
	}

	public static void setValue(TGProperties properties, String key, boolean value){
		properties.setValue(key,Boolean.toString(value));
	}

	public static void setValue(TGProperties properties, String key, float[] values){
		StringBuilder sb = new StringBuilder();
		for(Object value : values) {
			if( sb.length() > 0 ) {
				sb.append(ARRAY_SEPARATOR);
			}
			sb.append(value);
		}
		properties.setValue(key, sb.toString());
	}
}
