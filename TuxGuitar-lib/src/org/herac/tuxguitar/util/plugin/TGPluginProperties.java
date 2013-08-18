/*
 * Created on 09-ene-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.util.plugin;

import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesManager;
import org.herac.tuxguitar.util.properties.TGPropertiesUtil;
/**
 * @author julian
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java - Code Style - Code Templates
 */
public class TGPluginProperties {
	
	public static final String MODULE = "tuxguitar";
	public static final String RESOURCE = "plugin-settings";
	
	private static final String ENABLED_PROPERTY_SUFFIX = ".enabled";
	
	private static TGPluginProperties instance;
	
	private TGProperties properties;
	
	public static TGPluginProperties instance(){
		if( instance == null ){
			instance = new TGPluginProperties();
		}
		return instance;
	}
	
	private TGPluginProperties(){
		this.properties = TGPropertiesManager.getInstance().createProperties();
		this.load();
	}
	
	public void load(){
		TGPropertiesManager.getInstance().readProperties(properties, RESOURCE, MODULE);
	}
	
	public void save(){
		TGPropertiesManager.getInstance().writeProperties(properties, RESOURCE, MODULE);
	}
	
	public void setEnabled(String moduleId, boolean enabled){
		this.setBooleanValue(createEnabledPropertyKey(moduleId),enabled);
		this.save();
	}
	
	public boolean isEnabled(String moduleId){
		return this.getBooleanValue(createEnabledPropertyKey(moduleId), true);
	}
	
	public String createEnabledPropertyKey(String moduleId){
		return (moduleId + ENABLED_PROPERTY_SUFFIX);
	}
	
	private boolean getBooleanValue(String key, boolean defaultValue) {
		return TGPropertiesUtil.getBooleanValue(this.properties, key, defaultValue);
	}
	
	private void setBooleanValue(String key,boolean value){
		TGPropertiesUtil.setValue(this.properties, key, value);
	}
}