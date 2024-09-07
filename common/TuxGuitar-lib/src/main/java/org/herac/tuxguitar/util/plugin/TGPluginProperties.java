package org.herac.tuxguitar.util.plugin;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesManager;
import org.herac.tuxguitar.util.properties.TGPropertiesUtil;
import org.herac.tuxguitar.util.singleton.TGSingletonFactory;
import org.herac.tuxguitar.util.singleton.TGSingletonUtil;

public class TGPluginProperties {
	
	public static final String MODULE = "tuxguitar";
	public static final String RESOURCE = "plugin-settings";
	
	private static final String ENABLED_PROPERTY_SUFFIX = ".enabled";
	
	private TGContext context;
	private TGProperties properties;
	
	private TGPluginProperties(TGContext context){
		this.context = context;
		this.properties = TGPropertiesManager.getInstance(this.context).createProperties();
		this.load();
	}
	
	public void load(){
		TGPropertiesManager.getInstance(this.context).readProperties(this.properties, RESOURCE, MODULE);
	}
	
	public void save(){
		TGPropertiesManager.getInstance(this.context).writeProperties(this.properties, RESOURCE, MODULE);
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
	
	public static TGPluginProperties getInstance(TGContext context) {
		return TGSingletonUtil.getInstance(context, TGPluginProperties.class.getName(), new TGSingletonFactory<TGPluginProperties>() {
			public TGPluginProperties createInstance(TGContext context) {
				return new TGPluginProperties(context);
			}
		});
	}
}