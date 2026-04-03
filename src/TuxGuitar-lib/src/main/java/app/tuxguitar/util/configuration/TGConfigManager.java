package app.tuxguitar.util.configuration;

import java.util.ArrayList;
import java.util.List;

import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.properties.TGProperties;
import app.tuxguitar.util.properties.TGPropertiesManager;
import app.tuxguitar.util.properties.TGPropertiesUtil;

public class TGConfigManager {

	public static final String RESOURCE = "config";

	private TGContext context;
	private TGProperties properties;
	private String module;

	public TGConfigManager(TGContext context, String module){
		this(context,module, null);
	}

	/* Since TuxGuitar 1.6.0, user configuration files are preserved when user upgrades TuxGuitar version
	 * Therefore, it is possible to find in a user configuration file some deprecated parameters
	 * It's also possible to find parameters created by future version of application
	 * (if user downgrades app version)
	 *
	 * To keep in user config file only properties with specific keys, use "validKeys" argument
	 */

	public TGConfigManager(TGContext context, String module, List<String> validKeys){
		List<String> toRemove = new ArrayList<String>();
		this.context = context;
		this.module = module;
		this.initialize();
		if ( (validKeys != null) && (this.properties.getStringKeys() != null) ) {
			for (String key : this.properties.getStringKeys() ) {
				if (!validKeys.contains(key) ) {
					toRemove.add(key);
				}
			}
		}
		for (String key : toRemove) {
			this.properties.remove(key);
		}
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

	public TGContext getContext() {
		return this.context;
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
