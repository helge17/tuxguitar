package org.herac.tuxguitar.util.properties;

public class TGPropertiesUtil {
	
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
		return getBooleanValue(properties, key,false);
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
}
