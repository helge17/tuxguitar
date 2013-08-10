/*
 * Created on 09-ene-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.herac.tuxguitar.app.system.plugins;

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
	
	private static TGPluginProperties instance;
	
	private TGProperties properties;
	
	public static TGPluginProperties instance(){
		if( instance == null ){
			instance = new TGPluginProperties();
		}
		return instance;
	}
	
	public void save(){
		TGPropertiesManager.getInstance().writeProperties(properties, RESOURCE, MODULE);
	}
	
	public void load(){
		TGPropertiesManager.getInstance().readProperties(properties, RESOURCE, MODULE);
	}
	
	private TGPluginProperties(){
		this.properties = TGPropertiesManager.getInstance().createProperties();
		this.load();
	}
	
	public String getStringValue(String key) {
		return TGPropertiesUtil.getStringValue(this.properties, key);
	}
	
	public boolean getBooleanValue(String key, boolean defaultValue) {
		return TGPropertiesUtil.getBooleanValue(this.properties, key, defaultValue);
	}
	
	public void setValue(String key, String value) {
		TGPropertiesUtil.setValue(this.properties, key, value);
	}
	
	public void setValue(String key,boolean value){
		TGPropertiesUtil.setValue(this.properties, key, value);
	}
}