package org.herac.tuxguitar.util.configuration;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesManager;
import org.herac.tuxguitar.util.properties.TGPropertiesUtil;

public class TGConfigManager {
	
	public static final String RESOURCE = "config";
	
	private TGContext context;
	private TGProperties properties;
	private String module;
	
	public TGConfigManager(TGContext context, String module){
		this.context = context;
		this.module = module;
		this.initialize();
	}
	
	public void initialize(){
		this.properties = TGPropertiesManager.getInstance(this.context).createProperties();
		this.load();
	}
	
	public void save(){
		TGPropertiesManager.getInstance(this.context).writeProperties(this.properties, RESOURCE, this.module);
	}
	
	public void load(){
		TGPropertiesManager.getInstance(this.context).readProperties(this.properties, RESOURCE, this.module);
	}
	
	public TGProperties getProperties(){
		return this.properties;
	}
	
	public String getStringValue(String key) {
		return TGPropertiesUtil.getStringValue(this.properties, key);
	}
	
	public String getStringValue(String key, String defaultValue) {
		return TGPropertiesUtil.getStringValue(this.properties, key, defaultValue);
	}
	
	public int getIntegerValue(String key, int defaultValue) {
		return TGPropertiesUtil.getIntegerValue(this.properties, key, defaultValue);
	}
	
	public int getIntegerValue(String key) {
		return TGPropertiesUtil.getIntegerValue(this.properties, key);
	}
	
	public float getFloatValue(String key,float defaultValue) {
		return TGPropertiesUtil.getFloatValue(this.properties, key, defaultValue);
	}
	
	public float getFloatValue(String key) {
		return TGPropertiesUtil.getFloatValue(this.properties, key);
	}
	
	public double getDoubleValue(String key,double defaultValue) {
		return TGPropertiesUtil.getDoubleValue(this.properties, key, defaultValue);
	}
	
	public double getDoubleValue(String key) {
		return TGPropertiesUtil.getDoubleValue(this.properties, key);
	}
	
	public boolean getBooleanValue(String key) {
		return TGPropertiesUtil.getBooleanValue(this.properties, key);
	}
	
	public boolean getBooleanValue(String key, boolean defaultValue) {
		return TGPropertiesUtil.getBooleanValue(this.properties, key, defaultValue);
	}
	
	public float[] getFloatArrayValue(String key) {
		return TGPropertiesUtil.getFloatArrayValue(this.properties, key);
	}
	
	public float[] getFloatArrayValue(String key, float[] defaultValue) {
		return TGPropertiesUtil.getFloatArrayValue(this.properties, key, defaultValue);
	}
	
	public void setValue(String key, String value) {
		TGPropertiesUtil.setValue(this.properties, key, value);
	}

	public void setValue(String key, int value){
		TGPropertiesUtil.setValue(this.properties, key, value);
	}
	
	public void setValue(String key, float value){
		TGPropertiesUtil.setValue(this.properties, key, value);
	}
	
	public void setValue(String key, double value){
		TGPropertiesUtil.setValue(this.properties, key, value);
	}
	
	public void setValue(String key, boolean value){
		TGPropertiesUtil.setValue(this.properties, key, value);
	}
	
	public void setValue(String key, float[] value){
		TGPropertiesUtil.setValue(this.properties, key, value);
	}
	
	public void remove(String key){
		this.properties.remove(key);
	}
	
	public void clear(){
		this.properties.clear();
	}
}
