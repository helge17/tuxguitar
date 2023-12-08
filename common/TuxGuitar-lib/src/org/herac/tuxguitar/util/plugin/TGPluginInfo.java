package org.herac.tuxguitar.util.plugin;

import org.herac.tuxguitar.util.TGContext;
import org.herac.tuxguitar.util.properties.TGProperties;
import org.herac.tuxguitar.util.properties.TGPropertiesManager;
import org.herac.tuxguitar.util.properties.TGPropertiesUtil;

public class TGPluginInfo {
	
	public static final String RESOURCE = "plugin-info";
	
	private TGContext context;
	private TGProperties properties;
	
	public TGPluginInfo(TGContext context, String moduleId){
		this.context = context;
		this.initialize(moduleId);
	}
	
	public void initialize(String moduleId){
		this.properties = TGPropertiesManager.getInstance(this.context).createProperties();
		this.loadPluginInfo(moduleId);
	}
	
	public void loadPluginInfo(String moduleId){
		TGPropertiesManager.getInstance(this.context).readProperties(this.properties, RESOURCE, moduleId);
	}
	
	public TGProperties getProperties(){
		return this.properties;
	}
	
	public String getStringValue(String key) {
		return TGPropertiesUtil.getStringValue(this.properties, key);
	}
	
	public String getName(){
		return TGPropertiesUtil.getStringValue(this.properties, "plugin.name");
	}
	
	public String getDescription(){
		return TGPropertiesUtil.getStringValue(this.properties, "plugin.description");
	}
	
	public String getVersion(){
		return TGPropertiesUtil.getStringValue(this.properties, "plugin.version");
	}
	
	public String getAuthor(){
		return TGPropertiesUtil.getStringValue(this.properties, "plugin.author");
	}
}
